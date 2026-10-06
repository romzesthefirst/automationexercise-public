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
both smoke checks passed. Full UI/BDD regression, Windows Wrapper execution, and
hosted Jenkins execution remain unverified.

Test results are written to `target/surefire-reports`; Allure results are written
to `target/allure-results`. Full report presentation and portable CI setup are
tracked separately in the roadmap.
