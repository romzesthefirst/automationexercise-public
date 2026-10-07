# Allure report demonstration

This checked-in example comes from real Java 17/headless Chrome execution on
2026-10-06. It contains exactly three tests: one passed public catalog API request,
one deliberate TestNG UI assertion failure, and one deliberate Cucumber assertion
failure. Both browser failures use synthetic `data:` pages, with no accounts or
payments. These failures demonstrate diagnostics; they are not product defects
or a green regression run. The source revision parameter identifies the base
commit plus working-tree changes used for this implementation.

Generate and view the checked-in sample (Java 17 and the Maven Wrapper):

```sh
./mvnw allure:report -Dallure.results.directory="$PWD/examples/allure/results"
./mvnw allure:serve -Dallure.results.directory="$PWD/examples/allure/results"
```

The shell examples use `$PWD` to pass an absolute input directory to the report
plugin (relative report paths resolve under `target/`).

The project pins Allure Maven 2.16.1 and Allure CLI 2.30.0. The first generation
requires dependency downloads; later runs can reuse the cache. The HTML output
is `target/site/allure-maven-plugin/index.html`; use `allure:serve` to open it,
because loading this report through `file:` can block its data requests.

In **Overview**, inspect the environment and 3-test summary. In **Behaviors**,
open the catalog test and expand its business steps and Request and HTTP/1.1 200 OK response HTML
attachments. Open either failed browser test to see its original assertion,
Current URL, Screenshot on failure, Browser diagnostics, Java/browser version,
and revision. The Cucumber scenario appears once, with Gherkin steps.

Reproduce the raw demo in a new results directory:

```sh
./mvnw test -Dtest=AllureDemoTest,AllureDemoCucumberTest \
  -Dbrowser=chrome -Dheadless=true -DthreadCount=1 \
  -Dallure.results.directory=target/report-demo/allure-results \
  -Dtest.reports.directory=target/report-demo/surefire-reports
```

**Expected Maven exit: 1**, with 3 tests, 2 assertion failures, no errors/skips.
Any other outcome must be investigated; do not ignore all build failures.
These demo classes are excluded from the normal API/UI/BDD selectors.

To export a new sample, choose a destination that does not already exist:

```sh
python3 tools/export_allure_demo.py target/report-demo/allure-results target/report-demo/sample
./mvnw allure:serve -Dallure.results.directory="$PWD/target/report-demo/sample"
```

The exporter accepts only the exact three demo results and expected statuses.
It removes host/thread labels, stack traces, fixture containers and local feature
URIs, preserving test steps, assertion messages and their actual attachments.
Review the exported attachments before publication. It is **not a general-purpose
sanitizer**: full HTTP diagnostics in arbitrary account tests can contain test
credentials. This catalog sample has no authorization headers or generated user
data. Live internal reports retain complete diagnostics.

Do not combine discovery output with this example or any real run. TestNG and
Cucumber discovery use separate directories and `[DISCOVERY ONLY]` labels;
reported discovery statuses do not prove that any test passed.
