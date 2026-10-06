"""Run one CI suite with fresh, attributable evidence (Python standard library)."""
import argparse
from datetime import datetime, timezone
import json
import os
from pathlib import Path
import re
import subprocess
import sys
import uuid
import xml.etree.ElementTree as ET

PROFILES = {"api": "api", "ui-smoke": "ui,smoke", "bdd-smoke": "bdd,smoke",
            "ui": "ui", "bdd": "bdd"}
ROOT = Path(__file__).resolve().parents[1]


def run(suite, browser="chrome", headless="true", threads=1, discovery=False):
    run_id = (os.getenv("GITHUB_RUN_ID") or os.getenv("BUILD_NUMBER") or
              datetime.now(timezone.utc).strftime("%Y%m%dT%H%M%SZ"))
    attempt = os.getenv("GITHUB_RUN_ATTEMPT", "1")
    # A unique directory also protects local repeats and Jenkins stage retries.
    name = re.sub(r"[^A-Za-z0-9_.-]", "_", f"{run_id}-{attempt}-{suite}-{uuid.uuid4().hex[:8]}")
    output = ROOT / "target" / "ci" / name
    output.mkdir(parents=True, exist_ok=False)
    reports = output / "surefire-reports"
    allure = output / "allure-results"
    wrapper = ["cmd", "/c", str(ROOT / "mvnw.cmd")] if os.name == "nt" else [str(ROOT / "mvnw")]
    command = wrapper + ["--batch-mode", "--no-transfer-progress", "test", f"-P{PROFILES[suite]}",
                         f"-Dbrowser={browser}", f"-Dheadless={headless}", f"-DthreadCount={threads}",
                         f"-Dtest.reports.directory={reports}", f"-Dallure.results.directory={allure}",
                         f"-Ddownload.directory={output / 'downloads'}"]
    if discovery:
        command += ["-Dcucumber.execution.dry-run=true" if suite.startswith("bdd")
                    else "-Dtestng.mode.dryrun=true"]
    metadata = {"suite": suite, "execution": "discovery" if discovery else "real",
                "started_utc": datetime.now(timezone.utc).isoformat(),
                "run_id": run_id, "attempt": attempt, "command": command, "status": "running"}
    revision = subprocess.run(["git", "rev-parse", "HEAD"], cwd=ROOT, capture_output=True, text=True)
    metadata["revision"] = revision.stdout.strip()
    metadata["run_url"] = (f"{os.getenv('GITHUB_SERVER_URL', 'https://github.com')}/"
                           f"{os.getenv('GITHUB_REPOSITORY')}/actions/runs/{run_id}"
                           if os.getenv("GITHUB_REPOSITORY") else os.getenv("BUILD_URL", ""))
    metadata_path = output / "run.json"
    metadata_path.write_text(json.dumps(metadata, indent=2) + "\n", encoding="utf-8")
    exit_code = 1
    error = ""
    try:
        java_home = os.getenv("JAVA_HOME")
        java = str(Path(java_home) / "bin" / ("java.exe" if os.name == "nt" else "java")) if java_home else "java"
        version = subprocess.run([java, "-version"], capture_output=True, text=True, check=True)
        metadata["java"] = version.stdout + version.stderr
        if not re.search(r'version "17[.\"]', metadata["java"]):
            raise RuntimeError("CI requires Java 17; configure JAVA_HOME before running.")
        with (output / "console.log").open("w", encoding="utf-8") as log:
            with subprocess.Popen(command, cwd=ROOT, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
                                  text=True, encoding="utf-8", errors="replace") as process:
                for line in process.stdout:
                    log.write(line)
                    log.flush()
                    print(line, end="", flush=True)
                exit_code = process.wait()
        counts = dict(tests=0, failures=0, errors=0, skipped=0)
        for report in reports.glob("TEST-*.xml"):
            result = ET.parse(report).getroot()
            for key in counts:
                counts[key] += int(result.get(key, "0"))
        metadata["counts"] = counts
        if exit_code == 0 and (counts["tests"] == 0 or counts["failures"] or counts["errors"]
                               or counts["skipped"]):
            error = "Missing, empty, failing, or skipped test results; this is not a passing CI suite."
            exit_code = 1
    except (OSError, RuntimeError, subprocess.SubprocessError, ET.ParseError, ValueError) as exc:
        error = str(exc)
        exit_code = 1
    finally:
        metadata.update(exit_code=exit_code, status="passed" if exit_code == 0 else "failed",
                        finished_utc=datetime.now(timezone.utc).isoformat(), error=error)
        metadata_path.write_text(json.dumps(metadata, indent=2) + "\n", encoding="utf-8")
        counts = metadata.get("counts", {})
        summary = (f"### {suite} ({metadata['execution']}) — {metadata['status']}\n\n"
                   f"Revision: `{metadata['revision']}`; run: `{run_id}`; attempt: `{attempt}`.\n\n"
                   f"Tests: {counts.get('tests', 0)}, failures: {counts.get('failures', 0)}, "
                   f"errors: {counts.get('errors', 0)}, skipped: {counts.get('skipped', 0)}.\n\n"
                   f"Exit code: {exit_code}. {error}\n\nEvidence: `{output.relative_to(ROOT)}`.\n")
        (output / "summary.md").write_text(summary, encoding="utf-8")
        if os.getenv("GITHUB_STEP_SUMMARY"):
            with open(os.environ["GITHUB_STEP_SUMMARY"], "a", encoding="utf-8") as handle:
                handle.write(summary)
        print(summary)
    return exit_code


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("suite", choices=PROFILES)
    parser.add_argument("--browser", choices=["chrome", "firefox", "edge"], default="chrome")
    parser.add_argument("--headless", choices=["true", "false"], default="true")
    parser.add_argument("--threads", type=int, choices=range(1, 9), default=1)
    parser.add_argument("--discovery", action="store_true", help="Labelled discovery only; does not execute tests")
    args = parser.parse_args()
    sys.exit(run(args.suite, args.browser, args.headless, args.threads, args.discovery))
