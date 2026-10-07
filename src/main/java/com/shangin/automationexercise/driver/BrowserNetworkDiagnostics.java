package com.shangin.automationexercise.driver;

import com.shangin.automationexercise.config.ConfigReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.module.Network;
import org.openqa.selenium.bidi.network.Header;
import org.openqa.selenium.json.Json;

/** Passive, worker-local first-party network evidence for failed browser tests. */
public final class BrowserNetworkDiagnostics {
    private static final ThreadLocal<BrowserNetworkDiagnostics> CURRENT = new ThreadLocal<>();
    private final List<Map<String, Object>> responses = new ArrayList<>();
    private final String origin =
            URI.create(ConfigReader.getBaseUrl()).getScheme()
                    + "://"
                    + URI.create(ConfigReader.getBaseUrl()).getRawAuthority();
    private Network network;
    private String collectionError = "";
    private int omittedSuccessfulResponses;

    private BrowserNetworkDiagnostics() {}

    public static void start(WebDriver driver) {
        close();
        if (!ConfigReader.isNetworkDiagnosticsEnabled()) {
            return;
        }
        var recorder = new BrowserNetworkDiagnostics();
        CURRENT.set(recorder);
        try {
            recorder.network = new Network(driver);
            recorder.network.onResponseStarted(
                    event -> {
                        if (!recorder.isFirstParty(event.getRequest().getUrl())) {
                            return;
                        }
                        var response = event.getResponseData();
                        Map<String, Object> entry = new LinkedHashMap<>();
                        entry.put("timestamp", event.getTimestamp());
                        entry.put("navigation", event.getNavigationId());
                        entry.put("requestUrl", event.getRequest().getUrl());
                        entry.put("method", event.getRequest().getMethod());
                        entry.put("requestHeaders", headers(event.getRequest().getHeaders()));
                        entry.put("responseUrl", response.getUrl());
                        entry.put("status", response.getStatus());
                        entry.put("protocol", response.getProtocol());
                        entry.put("fromCache", response.isFromCache());
                        entry.put("responseHeaders", headers(response.getHeaders()));
                        recorder.record(entry, response.getStatus() >= 400);
                    });
            recorder.network.onFetchError(
                    event -> {
                        if (recorder.isFirstParty(event.getRequest().getUrl())) {
                            recorder.record(
                                    Map.of(
                                            "timestamp",
                                            event.getTimestamp(),
                                            "requestUrl",
                                            event.getRequest().getUrl(),
                                            "fetchError",
                                            event.getErrorText()),
                                    true);
                        }
                    });
        } catch (RuntimeException failure) {
            recorder.collectionError = failure.toString();
        }
    }

    private boolean isFirstParty(String url) {
        return url.equals(origin) || url.startsWith(origin + "/");
    }

    private static List<Map<String, Object>> headers(List<Header> headers) {
        return headers.stream().map(Header::toMap).toList();
    }

    private synchronized void record(Map<String, Object> entry, boolean error) {
        // Retain every error, including the first 520, when limiting ordinary responses.
        if (responses.size() < 1000 || error) {
            responses.add(entry);
        } else {
            omittedSuccessfulResponses++;
        }
    }

    public static String snapshot() {
        var recorder = CURRENT.get();
        if (recorder == null) {
            return null;
        }
        synchronized (recorder) {
            return new Json()
                    .toJson(
                            Map.of(
                                    "origin",
                                    recorder.origin,
                                    "responses",
                                    new ArrayList<>(recorder.responses),
                                    "collectionError",
                                    recorder.collectionError,
                                    "omittedSuccessfulResponses",
                                    recorder.omittedSuccessfulResponses));
        }
    }

    public static void close() {
        var recorder = CURRENT.get();
        CURRENT.remove();
        if (recorder != null && recorder.network != null) {
            try {
                recorder.network.close();
            } catch (RuntimeException ignored) {
                /* Evidence cleanup must not replace a test failure. */
            }
        }
    }
}
