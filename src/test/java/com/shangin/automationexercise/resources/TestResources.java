package com.shangin.automationexercise.resources;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class TestResources {
    private TestResources() {}

    public static Path testTextFile() {
        return resource("files/TextFile.txt");
    }

    //

    private static Path resource(String path) {
        try {
            return Paths.get(
                    Objects.requireNonNull(TestResources.class.getClassLoader().getResource(path))
                            .toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Resource not found: " + path, e);
        }
    }
}
