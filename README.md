# Automation Exercise

A Java 17 test automation framework using Selenium, TestNG, Cucumber, REST Assured,
and Allure against [Automation Exercise](https://automationexercise.com).

[![Quick checks](https://github.com/romzesthefirst/automationexercise-public/actions/workflows/ci.yml/badge.svg)](https://github.com/romzesthefirst/automationexercise-public/actions/workflows/ci.yml)

## Prerequisites

- JDK 17; set `JAVA_HOME` to the JDK directory.
- Internet access for Maven downloads and the external test website.
- Chrome, Firefox, or Microsoft Edge for UI/BDD execution. Selenium Manager
  resolves the matching driver.
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

TestNG and Cucumber use the same driver factory and configuration reader.
Every key below resolves in this order: Java system property (`-Dkey=value`),
environment variable, then `src/main/resources/config.properties`. Environment
names use `AE_` plus the uppercase key with dots replaced by underscores.
An empty override is an error, rather than falling back to another source.
Values are checked before browser startup or an API request; errors name the key
and its environment variable. Booleans accept only `true` or `false` (case insensitive),
and numeric settings must be positive integers. Implicit waits always remain zero.

| System property | Environment variable | File default |
| --- | --- | --- |
| `base.url` | `AE_BASE_URL` | `https://automationexercise.com/` |
| `api.base.url` | `AE_API_BASE_URL` | `https://automationexercise.com/api` |
| `browser` | `AE_BROWSER` | `chrome` (`chrome`, `firefox`, `edge`) |
| `headless` | `AE_HEADLESS` | `false` |
| `incognito` | `AE_INCOGNITO` | `false` |
| `browser.width` | `AE_BROWSER_WIDTH` | `1920` |
| `browser.height` | `AE_BROWSER_HEIGHT` | `1080` |
| `page.load.timeout` | `AE_PAGE_LOAD_TIMEOUT` | `30` seconds |
| `script.timeout` | `AE_SCRIPT_TIMEOUT` | `30` seconds |
| `explicit.wait` | `AE_EXPLICIT_WAIT` | `10` seconds |
| `ads.handling.enabled` | `AE_ADS_HANDLING_ENABLED` | `true` |
| `download.directory` | `AE_DOWNLOAD_DIRECTORY` | `target/downloads` |
| `download.timeout` | `AE_DOWNLOAD_TIMEOUT` | `10` seconds |
| `download.mime.types` | `AE_DOWNLOAD_MIME_TYPES` | `text/plain,application/octet-stream` |

URLs must be absolute HTTP(S) URLs without embedded credentials, query, or
fragment. Use a trailing slash for the home URL to match the website's canonical
URL. Relative download roots resolve against the working directory; each session
creates and removes only its own child directory. Automatic download prompts are
disabled. Both runners wait up to `download.timeout` for a completed invoice. Firefox uses the MIME list for automatic saving; Chromium handles
attachment responses without a MIME allowlist. Inline documents and browser/OS
security restrictions can still prevent automatic downloads.

All browsers apply the requested outer window dimensions before the runner
receives its driver. Browser chrome can make viewport heights differ. Chrome
uses `--incognito`, Edge uses `--inprivate`, and Firefox enables permanent private
browsing in its temporary profile. Each browser has a fresh session profile even
when private mode is disabled. All use eager page loading with explicit page
readiness waits. The transport response limit is the larger page/script timeout
plus ten seconds. Chromium download routing also uses
[`Page.setDownloadBehavior`](https://chromedevtools.github.io/devtools-protocol/tot/Page/#method-setDownloadBehavior)
for the current page context, including private sessions. Rerun the download
probe after browser/driver upgrades.

```sh
AE_BROWSER=edge AE_HEADLESS=true ./mvnw test -Pui,smoke \
  -Dbrowser.width=1440 -Dbrowser.height=900 -Dincognito=true
AE_API_BASE_URL=https://automationexercise.com/api ./mvnw test -Papi,smoke
./mvnw test -Dtest=ConfigReaderTest,DriverLifecycleTest,DriverTransportTimeoutTest,ParallelIsolationTest
./mvnw test -Dtest=BrowserConfigurationLiveTest -Ddriver.lifecycle.live=true \
  -Dbrowser=edge -Dbrowser.width=1440 -Dbrowser.height=900 \
  -Dpage.load.timeout=25 -Dscript.timeout=20 -Ddownload.directory=target/custom-downloads
```

The live configuration probe exercises headed/headless and ordinary/private
sessions, actual outer dimensions and timeouts, and a local HTTP attachment
download. Add the Firefox launcher settings below on affected macOS systems.

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

## Page readiness and interaction waits

The implicit wait is always zero and is not configurable. Page objects use the
configured `explicit.wait` for visible and
clickable controls. Clicks scroll targets into view and retry temporary
interception until the explicit deadline; pointer hovering also waits for the
target to intersect the viewport. Navigation returns the instance whose readiness was checked.
Search waits for the old result section to detach and for the new results heading,
so repeated and empty-result searches do not depend on a changed title.
Contact and review messages are read only after they become visible. Chrome and
Firefox use eager document loading; explicit readiness conditions await usable
page controls without waiting for unrelated advertisement resources to finish.

Container components resolve their root locator on every access, including after
navigation or DOM replacement. Product cards and cart rows retain their item
identity as element snapshots. Reacquire these items from the page after a DOM
replacement; using an obsolete snapshot fails immediately with a descriptive
error instead of polling it until timeout. Closing the cart modal waits for its
shopping button to disappear before the next interaction.

Payment confirmation is transient: the website shows it and redirects to the
order page. A JavaScript `MutationObserver` captures the visible text in
same-origin `sessionStorage` before the redirect. The payment method clears any
previous capture, explicitly waits for a nonempty new capture, waits for the
order page, then removes the stored message. Missing confirmation fails with an
explicit-wait timeout; landing on the order page alone is insufficient.

Advertisement handling defaults to enabled. Set `-Dads.handling.enabled=false`
to disable all framework ad handling for a run, or change the file default.
Enabled handling blocks selected Google ad requests through CDP in Chrome and
Edge. Firefox has no equivalent request blocking in this implementation. All
three browsers remove matching ad elements on pages that invoke `removeAds()`
and install a DOM observer to restore original words in Google annotation links
and remove injected suggestion chips. Link navigation retries the original
link once if a Google vignette consumes the first click. This changes the
page compared with normal browsing and does not guarantee removal of every ad.
Changing the setting affects new sessions; it cannot undo already blocked
requests or restore removed elements in an existing page.

Run controlled delayed-DOM checks plus real-site API-page/category/brand
navigation and repeated searches:

```sh
./mvnw test -Dtest=PageReadinessLiveTest -Dreadiness.live=true \
  -Dbrowser=chrome -Dheadless=true
```

Repeat for `edge` and `firefox`; on macOS add the Firefox launcher described
above. This opt-in test is outside the default suite selectors. Its local HTTP
fixture injects delayed DOM updates without sleeps in the test code, verifies
confirmation across an actual redirect, checks stale component behavior, and
checks the ad-handling toggle. Actual website submissions remain covered by
`ContactUsTest`, `ProductsPageTest#shouldAddReviewOnProduct`, and
`PlaceOrderTest#shouldPlaceOrderLoginBeforeCheckout`.

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

Monetary values use one parser for expected prices, displayed cart line totals,
checkout totals, and invoice expectations. Supported input is a nonnegative
number with an optional `Rs.` prefix, a dot as the decimal separator, and optional
commas in groups of three for thousands (`Rs. 1,234.50`). Outer whitespace and
whitespace after the prefix are allowed. Signs, other currencies, decimal commas,
scientific notation, incomplete numbers, and malformed text are rejected with
`IllegalArgumentException`; characters are never silently discarded.

Calculations use `BigDecimal` without rounding. Price and total assertions compare
numeric values, so `12.50` and `12.500` are equal regardless of the currency prefix.
Invoice expectations use plain decimal text without a prefix, grouping, scientific
notation, or insignificant trailing zeros: `500.00` becomes `500`, and `12.50`
becomes `12.5`. Display strings remain available for diagnostics.

Run the monetary parser, arithmetic, formatting, checkout DOM, and BDD invoice
regression checks without a browser or external service:

```sh
./mvnw test -Dtest=MonetaryValuesTest,AssertionCorrectnessTest
```

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
to `target/allure-results`. CI uses separate directories described below; full
Allure report presentation is tracked separately in the roadmap.

## Continuous integration

[Quick checks](https://github.com/romzesthefirst/automationexercise-public/actions/workflows/ci.yml)
runs on pushes, pull requests, and manual dispatch on fresh Ubuntu 24.04 runners
with Temurin Java 17 and the installed Chrome. It runs three independent jobs:
all 14 API tests, one TestNG UI smoke test, and one Cucumber smoke scenario.
The matrix keeps running after a job fails. Jobs require no repository secrets;
checkout credentials are not persisted and token permissions are read-only.

[External-site regression](https://github.com/romzesthefirst/automationexercise-public/actions/workflows/regression.yml)
is a separate manual workflow for full Chrome headless TestNG UI and Cucumber
suites. Each suite runs in its own job with one worker and a 60-minute timeout.
Use **Actions → External-site regression → Run workflow** and select the revision.
Quick checks have a 20-minute timeout per job.

Both workflows use the same portable runner as Jenkins. Python 3.9 or newer is
required, in addition to the prerequisites above. Local equivalents are:

```sh
python3 tools/run_ci.py api
python3 tools/run_ci.py ui-smoke
python3 tools/run_ci.py bdd-smoke
# Broader regression, separately from quick checks:
python3 tools/run_ci.py ui
python3 tools/run_ci.py bdd --threads 1
```

On Windows use `python` instead of `python3`; the runner invokes `mvnw.cmd`.
Set `JAVA_HOME` to Java 17. The runner calls the Wrapper with `test -Papi`,
`test -Pui,smoke`, `test -Pbdd,smoke`, `test -Pui`, or `test -Pbdd` respectively,
plus browser/headless/worker settings and unique report/download directories.
It rejects a different Java major version. It returns Maven's failure code and
also fails when reports are missing, empty, malformed, failed, or skipped.
`--discovery` is only for local selection validation: it labels outputs as
**discovery**, and neither CI workflow uses it.

Every invocation creates a new `target/ci/<run>-<attempt>-<suite>-<unique-id>/`.
It never reads previous results. Each directory contains `run.json` (source
revision, CI run URL, timestamps, exact command, Java version, exit status and
counts), `summary.md`, `console.log`, Surefire XML/text, and Allure results with
existing HTTP/browser failure attachments. Run the local commands in a fresh
checkout for CI-equivalent compilation; do not run concurrent Maven invocations
in one workspace. Avoid `clean` between suites because it deletes their evidence.

GitHub exposes the counts in the job summary. Download the corresponding
`quick-<suite>-<run-id>-<attempt>` or `regression-<suite>-<run-id>-<attempt>` artifact
from that workflow run. Upload runs on failure as well as success; retention is
14 days. Compilation/startup failures can have metadata and logs without test
XML or Allure results. Runner loss or forced cancellation can prevent upload.
Artifacts contain full test diagnostics and generated synthetic test data.
They are execution evidence; use the separate publication-sample process when
preparing curated public examples.

Jenkins starts with a fresh checkout, disables concurrent builds in the same job,
and runs API plus both smoke selections by default. Enable `REGRESSION` for API
plus full UI/BDD. A failed suite marks the build and stage failed while allowing
the remaining suites to run; cancellation and timeout still stop execution.
The `always` post block archives `target/ci/**`, publishes Surefire XML through
JUnit, and builds Allure from all current invocation directories. Jenkins retains
20 builds and artifacts for the latest 10. Agent requirements: Java 17 configured
through `JAVA_HOME`/PATH, Python (`python3` on Unix, `python` on Windows), Git,
Wrapper download prerequisites, and the selected browser. Required Jenkins
plugins: Pipeline, Git, JUnit, and Allure; configure an Allure command-line
installation in Jenkins tools. No Homebrew installation or machine-specific
PATH is assumed. Windows support is implemented but awaits a Windows CI run.

All these checks use the external Automation Exercise website. API tests and
full regression create fresh synthetic accounts and attempt owned-account
cleanup; the two browser smoke selections only navigate pages. Network outages,
Cloudflare errors, rate limits, site changes, and cleanup failures remain visible
as failures. CI does not automatically retry or reinterpret them as success.
A passing quick workflow proves only its API and smoke selections; full UI/BDD
coverage requires the separate regression workflow.

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

## Parallel execution and replay

Cucumber scenarios use `-DthreadCount=N` (default 4). TestNG UI/API methods are
sequential by default; opt into method parallelism with
`-Dparallel=methods -DthreadCount=N`. The Cucumber data provider already schedules scenarios in parallel;
do not also enable method parallelism for that runner.

Each managed browser session downloads into a unique directory below
`target/downloads/session-*`. Chrome, Edge, and Firefox use the same owned
location. `DownloadHelper` rejects paths outside that directory, waits for a
nonempty file whose size and modification time remain stable for 600 ms, and
waits while `.crdownload`, `.part`, or `.tmp` files exist. This is a filesystem
completion heuristic, not a checksum or browser download-event guarantee;
a producer that pauses without a temporary suffix for longer than the stability
window can still appear complete. Teardown removes only the owned directory,
including after initialization/test/shutdown failure. Read the invoice before
teardown; its path is retained in Allure for isolation diagnostics.

Requests receive new REST Assured specifications and logging/Allure filters.
Account registries and driver references remain worker-local; Cucumber context
is scenario-scoped. Datafaker instances use invocation-local randomness rather
than shared generators. Account emails always contain fresh UUIDs so replay
never reuses or deletes an account from another invocation.

Add `-Dtest.seed=824` to replay generated data and random product selection for
one invocation with the same code, call order, and available product list.
Without an override each invocation gets a new seed. Console and Allure record
the seed and randomly selected product link, without credentials. Product
selection has a separate random stream, so unrelated generated text does not
change the product sequence. Seed replay does not reproduce external-site state.

Autonomous regression (local HTTP server, no external site or browser):

```sh
./mvnw test -Dtest=ParallelIsolationTest,AccountCleanupTest,DriverLifecycleTest \
  -Dallure.results.directory=target/parallel-validation/offline-allure
```

Repeat real TestNG invoice purchases with one and four workers; each invocation
checks its own invoice text and cleans its accounts:

```sh
./mvnw test -Dtest=ParallelInvoiceLiveTest#repeatedInvoice -Dheadless=true \
  -Dinvoice.invocations=2 -DthreadCount=1
./mvnw test -Dtest=ParallelInvoiceLiveTest#repeatedInvoice -Dheadless=true \
  -Dinvoice.invocations=4 -DthreadCount=4
python3 tools/prepare_invoice_acceptance.py --copies 4
./mvnw test -Pbdd -Dheadless=true -DthreadCount=4 \
  -Dcucumber.features=target/parallel-validation/invoices.feature
```

The helper copies the existing invoice scenario into an ignored acceptance
feature, leaving normal scenario discovery unchanged. Use `--copies 2` and
`-DthreadCount=1` for its sequential comparison. Use separate Allure/report
directories for each run.

For Edge, add `-Dbrowser=edge` to the invoice commands. For Firefox, add
`-Dbrowser=firefox`; on affected macOS systems use `bin/firefox-macos` and
`AUTOMATION_FIREFOX_APP` as documented above. A one-worker run discovers two
invocations; a four-worker run discovers four and checks each invoice against
its own generated user.

The 2026-10-06 invoice acceptance passed Edge 12/12. Firefox's initial matrix
passed 6/12; explicit repeats of failed selections passed 7/10. Its failed
TestNG runs had Cloudflare 520 after successful invoice checks, and two initial
BDD scenarios stopped before download on the site's queue-full page. The
repeated four-worker BDD run passed 4/4. All 32 invoices reached across these
additional runs passed their content checks and used distinct directories;
account and directory cleanup succeeded. Exact results and limitations are in
roadmap item 8; local evidence is `target/parallel-validation/cross-browser/report.md`.

## Compare complete browser runs

Run each full suite into a fresh, separate Allure directory, using the same
configuration, worker count, and seed. Preserve failures and do not skip cases.
For example, repeat these commands with `chrome`, `firefox`, and `edge`:

```sh
./mvnw test -Pui -Dbrowser=chrome -Dheadless=true -Dtest.seed=824 \
  -Dallure.results.directory=target/browser-comparison/chrome-ui/allure-results
./mvnw test -Pbdd -Dbrowser=chrome -Dheadless=true -Dtest.seed=824 -DthreadCount=1 \
  -Dallure.results.directory=target/browser-comparison/chrome-bdd/allure-results
python3 tools/compare_browser_results.py target/browser-comparison \
  --output target/browser-comparison/comparison.md
```

The comparison joins UI methods and BDD source locations plus example parameters,
reports missing cases, and includes the original error for each failure. It
rejects duplicate cases from mixed runs. Compare individual outcomes before
claiming equivalent browser support; external-site failures require investigation
and cannot be excused solely because another run passed.

The 2026-10-06 Java 17/macOS 27 acceptance checked Chrome 154, Firefox 157.0,
and Edge 154 in all four headed/headless × ordinary/private modes. Live
configuration/download probes passed for all twelve sessions. Both-runner smoke
passed 23/24 initially; one Firefox 520 page was retained, and that mode's
separate repeat passed 2/2. Complete headless/ordinary UI/BDD comparison passed
Chrome 26/26 + 35/35, Edge 26/26 + 35/35, and Firefox 22/26 + 32/35. Every
Firefox full-run difference displayed a Cloudflare 520 page; focused repeats
passed except BDD logout, which displayed 520 again. This is not a green full
Firefox claim. Roadmap item 9 records the scope; local detailed evidence is
`target/configuration-validation/report.md` and `comparison.md`. Windows/Linux
and full headed/private suites were not run.
