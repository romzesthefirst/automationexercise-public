# Automation Exercise

A Java 17 test automation framework using Selenium, TestNG, Cucumber, REST Assured,
and Allure against [Automation Exercise](https://automationexercise.com).

## Prerequisites

- JDK 17; set `JAVA_HOME` to the JDK directory.
- Internet access for Maven downloads and the external test website.
- Chrome for the UI and BDD commands below. Selenium Manager resolves its driver.
- On Unix, a shell, `curl` or `wget`, and `unzip` for the Maven Wrapper.

The committed Maven Wrapper pins Maven 3.9.16 and checks its distribution SHA-256
on download. Use `./mvnw` on Unix or `mvnw.cmd` on Windows. An installed Maven
3.9.16 can also run these commands by replacing `./mvnw` with `mvn`.

## Test entry points

Run commands from the repository root. Choose one suite profile per invocation;
`smoke` is a modifier that can be combined with any one suite.

| Command | Selected tests | Browser |
| --- | --- | --- |
| `./mvnw clean test` | API tests only (default) | No |
| `./mvnw clean test -Papi` | All TestNG API tests | No |
| `./mvnw clean test -Pui -Dbrowser=chrome -Dheadless=true` | TestNG UI tests only | Yes |
| `./mvnw clean test -Pbdd -Dbrowser=chrome -Dheadless=true` | Cucumber scenarios only | Yes |
| `./mvnw clean test -Psmoke` | API product-list test | No |
| `./mvnw clean test -Papi,smoke` | API product-list test | No |
| `./mvnw clean test -Pui,smoke -Dbrowser=chrome -Dheadless=true` | TestNG Test Cases page test | Yes |
| `./mvnw clean test -Pbdd,smoke -Dbrowser=chrome -Dheadless=true` | Cucumber Test Cases page scenario | Yes |

No selection requires a database. Default execution makes external HTTP requests,
including account tests using generated test data. It does not start a browser
or run the equivalent TestNG and Cucumber UI suites together. The smoke selections
only read products or navigate pages; they do not create accounts or orders.

Surefire selects the suite by package. TestNG's `testGroups` can further narrow
API/UI tests, and `cucumber.filter.tags` can narrow BDD scenarios:

```sh
./mvnw test -Papi -DtestGroups=smoke
./mvnw test -Pbdd -Dcucumber.filter.tags=@TC_07 -Dheadless=true
./mvnw test -Pbdd -DthreadCount=1 -Dheadless=true
```

`threadCount` controls Cucumber's parallel data provider (default: 4); it does
not enable parallel execution for ordinary API/UI methods. `-Dtest=Class#method`
is an advanced Surefire override that replaces package selection; prefer the
profiles above. An empty class/group selection or Cucumber tag selection fails
the build. Unknown Maven profile names only produce a Maven warning, so use the
listed names. The obsolete `testng.xml` was removed; UI listener registration
remains on `BaseTest`.

## Discovery without external execution

TestNG dry-run discovers API/UI methods without invoking them or browser setup.
Cucumber dry-run resolves scenarios and steps without executing hooks or steps.
These are discovery checks, not evidence that website tests passed.

```sh
./mvnw clean test -Papi -Dtestng.mode.dryrun=true
./mvnw clean test -Pui -Dtestng.mode.dryrun=true
./mvnw clean test -Pbdd -Dcucumber.execution.dry-run=true
./mvnw clean test -Papi,smoke -Dtestng.mode.dryrun=true
./mvnw clean test -Pui,smoke -Dtestng.mode.dryrun=true
./mvnw clean test -Pbdd,smoke -Dcucumber.execution.dry-run=true
```

Expected discovery at this revision: API 14, UI 26, BDD 35 (including Examples),
and one test/scenario for each smoke selection. Use a separate run or `clean`
to avoid mixing discovery results with real execution results.

## Browser launch settings

All three browsers use `headless`, `browser.width`, and `browser.height` from
`src/main/resources/config.properties`. The committed defaults are headed mode
(`headless=false`) and a 1920 by 1080 window. `-Dheadless=true` or
`-Dheadless=false` overrides the file for any browser. Startup arguments and a
shared WebDriver window-size operation apply the configured dimensions before
either runner receives the browser. These are outer window dimensions; browser
chrome can make the page viewport height differ between browsers.

Firefox also accepts Selenium's `-Dwebdriver.firefox.bin` binary override.
On macOS 27, direct execution can fail before profile selection because Firefox
cannot access its app-data directory; see the
[Mozilla startup issue](https://bugzilla.mozilla.org/show_bug.cgi?id=2062988).
The optional `bin/firefox-macos` launcher starts a signed Firefox application
through macOS LaunchServices. It keeps the launch process alive until the browser
exits while geckodriver owns the temporary profile, Marionette session, and
shutdown. It verifies the application signature and does not disable browser
sandboxing or change macOS privacy permissions.

Use the installed `/Applications/Firefox.app`, or point `AUTOMATION_FIREFOX_APP`
at another signed official Firefox application:

```sh
AUTOMATION_FIREFOX_APP="/path/to/Firefox.app" ./mvnw test -Pui \
  -Dbrowser=firefox -Dheadless=true -Dwebdriver.firefox.bin="$PWD/bin/firefox-macos"
AUTOMATION_FIREFOX_APP="/path/to/Firefox.app" ./mvnw test -Pbdd \
  -Dbrowser=firefox -Dheadless=true -Dwebdriver.firefox.bin="$PWD/bin/firefox-macos"
```

Without `AUTOMATION_FIREFOX_APP`, the launcher uses `/Applications/Firefox.app`.
A damaged signature requires a valid browser copy; the launcher does not alter
or re-sign the installed application. Other platforms use normal Selenium
Firefox launch without this macOS launcher.

## Browser lifecycle and failure evidence

The driver factory configures window size, advertisement blocking, and all three
Selenium timeouts before publishing a browser to either runner. If initialization
fails, it attempts to close the new browser and preserves the initialization
error, with a shutdown error suppressed when present. Driver teardown always
clears its worker's thread-local reference, even when browser shutdown fails.

TestNG captures browser evidence from an invocation listener before teardown;
Cucumber captures it in its failure hook before the driver shutdown hook, using
the scenario UUID rather than the current Allure fixture UUID.
Allure's lifecycle listener is a fallback. A single shared collector adds at most
one URL and screenshot per report, plus browser diagnostics. Failed URL or
screenshot commands do not replace the original error or prevent the other
attachment from being attempted. Setup evidence belongs to the Allure setup
report; API failures without a browser need no browser attachments.

WebDriver responses are bounded by the largest configured page-load, script,
or implicit wait timeout plus ten seconds (40 seconds with the current file). This also bounds stalled
screenshot and shutdown commands; transport failures retain the original test
error and allow teardown to proceed.

Focused regression checks do not call the external website:

```sh
./mvnw test -Dtest=DriverLifecycleTest,DriverTransportTimeoutTest,DriverReportingTest,CucumberFailureReportingTest,AccountCleanupTest
```

Focused assertion regression checks also run without a browser or external service:

```sh
./mvnw test -Dtest=AssertionCorrectnessTest
```

They exercise the actual Cucumber text, quantity, and search steps against a
controlled DOM and reject empty positive searches, wrong prices, quantities,
text, missing products, displayed line totals, and checkout totals. Cart and
checkout line totals are read from the page separately from unit price and
quantity; a correct checkout total cannot hide an incorrect line total.

The real browser probes are skipped by default. Enable them to verify
intentionally failed TestNG and Cucumber tests against local HTML pages,
including real PNG and URL attachments:

```sh
./mvnw test -Dtest=DriverReportingTest,CucumberFailureReportingTest \
  -Ddriver.lifecycle.live=true -Dbrowser=chrome -Dheadless=true
```

The harness expects its inner failures and passes only when their original
errors, evidence, and teardown behavior are verified. Probe Allure reports are
written under `target/driver-lifecycle-validation/`.

The Cucumber regression also executes the actual Allure Cucumber adapter in
fixture context with healthy and failed evidence commands. To check actual
session headless state and window dimensions, including the file default and
its system-property override, run:

```sh
./mvnw test -Dtest=BrowserConfigurationLiveTest -Ddriver.lifecycle.live=true -Dbrowser=chrome
```

Repeat for `edge` and `firefox`; add the macOS Firefox launcher when needed.
This probe opens and closes both headed and headless sessions.

## Dependency audit

All dependencies used only by tests have test scope. Selenium remains a compile
dependency because page objects and driver infrastructure are in `src/main`.
Build lifecycle plugins are pinned in `pom.xml`.

Generate the resolved dependency graph (including test scope), then query the
current [OSV database](https://google.github.io/osv.dev/api/) with Python 3:

```sh
./mvnw org.apache.maven.plugins:maven-dependency-plugin:3.7.0:tree \
  -DoutputType=json -DoutputFile=target/dependencies.json
python3 tools/audit_dependencies.py target/dependencies.json
```

The audit writes `target/dependency-audit.json` and exits nonzero for known
vulnerabilities or request errors. Package coordinates and versions are sent to
OSV. This covers resolved project dependencies, not Maven's own libraries,
build-plugin dependencies, browser binaries, or vulnerabilities absent from OSV.
A successful compile is not a security audit.

The 2026-10-05 audit initially flagged Jackson core/databind 3.2.1 and Allure's
transitive FreeMarker 2.3.33. Jackson was updated to 3.2.3 and FreeMarker is managed
at 2.3.35, the fixed versions recorded in the corresponding OSV advisories:
[FreeMarker](https://osv.dev/vulnerability/GHSA-27j2-h3m2-8237),
[Jackson core](https://osv.dev/vulnerability/GHSA-7hhh-6rmp-j9qf), and
[Jackson databind](https://osv.dev/vulnerability/GHSA-cxp5-3px4-pw24).
The repeated audit queried 97 dependencies and found no affected packages.

Validation on Java 17.0.20 passed all discovery selections in a fresh candidate
copy, all 14 real API tests, and one real smoke check in each of TestNG UI and
Cucumber with headless Chrome. Chrome 154 reported a Selenium CDP version warning;
both smoke checks passed. Subsequent full browser regression results are recorded
in roadmap item 4. Windows Wrapper and hosted Jenkins execution remain unverified.

Test results are written to `target/surefire-reports`; Allure results are written
to `target/allure-results`. Full report presentation and portable CI setup are
tracked separately in the roadmap.

## Test account cleanup

Each account-enabled TestNG invocation and Cucumber scenario owns a separate registry containing
only freshly generated test identities. Register an identity with
`accounts.newUser()` (TestNG) or `context.newUser()` (Cucumber) before any
UI creation attempt. `accounts.createUser()` and Cucumber's `ApiUserSteps.createUser()` register before the API request,
so an interrupted request or failed setup still leaves credentials for teardown.
Assigning an arbitrary user to `ScenarioContext` does not grant cleanup ownership.

`BaseTest` manages the browser without account infrastructure. UI classes that
create accounts explicitly extend `AccountUiTestBase`; API account tests extend
`AccountTestBase`. Both compose the same `AccountFixture` for ownership and
reporting. UI classes that never use accounts continue to extend `BaseTest`.

Cleanup runs for every outcome and does not depend on `@user`. It attempts every
owned account, validates HTTP and application codes, and confirms absence with
an independent email lookup. A deletion 404 with an existing account is a failure.
Cleanup errors appear in report attachments and are suppressed onto an existing
TestNG error; a failed Cucumber scenario keeps its original failure. Cleanup failure
after a successful test/scenario fails the build. External service outages may
prevent removal; the report records that failure instead of claiming success.
Abrupt process termination cannot execute teardown.

Run the autonomous cleanup regression without a browser or external requests:

```sh
./mvnw test -Dtest=AccountCleanupTest
```

The tests deliberately run failing nested TestNG fixtures to verify teardown and
reporting. Those fixture failures are expected in Allure; the ten enclosing
regression checks must pass. Use separate result directories to avoid mixing them
with ordinary suite reports.

For an explicitly selected real-site acceptance check:

```sh
./mvnw test -Dtest=AccountCleanupLiveTest
```

This creates fresh synthetic accounts and checks their absence after a passing
run, deliberate setup/test failures, and a deliberately lost creation response.
The outer check passes only when each account is confirmed absent; expected
fixture failures remain visible in Allure. UI registration/cart/order/invoice
coverage still requires the corresponding real `ui` and `bdd` runs. Current
external acceptance status is recorded in roadmap item 3.

Focused cleanup acceptance on Java 17.0.20/headless Chrome passed real API fault
injection and absence checks. Selected UI tests passed 12/13 (registration failed
before account creation on the page title assertion); selected BDD scenarios
passed 11/11, including invoice without `@user`. Additional deliberate browser
setup/test/scenario failures also left their owned accounts absent. These checks
verify cleanup, not a clean full-suite UI/BDD regression. Reports and the subsequent registration/readiness fix are recorded in roadmap
item 3. After adding explicit form readiness and separating duplicate-email
attempts, affected UI checks passed 7/7 and BDD checks passed 6/6 in real Chrome;
all thirteen owned accounts were confirmed absent.
