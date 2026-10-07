"""Exercise CI failure reporting and evidence isolation without external services."""
import contextlib
import io
import json
import os
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
from unittest.mock import patch

import run_ci


class CiRunnerTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.wrapper = self.root / 'mvnw'
        self.wrapper.write_text(f'''#!{sys.executable}
import os, sys
from pathlib import Path
p = next(a.split('=', 1)[1] for a in sys.argv if a.startswith('-Dtest.reports.directory='))
reports = Path(p)
reports.mkdir(parents=True)
if os.getenv('FAKE_XML'):
    (reports / 'TEST-suite.xml').write_text(os.environ['FAKE_XML'])
print('diagnostic evidence')
sys.exit(int(os.getenv('FAKE_EXIT', '0')))
''')
        self.wrapper.chmod(0o755)
        self.env = patch.dict(os.environ, {}, clear=True)
        self.env.start()
        self.addCleanup(self.env.stop)
        self.root_patch = patch.object(run_ci, 'ROOT', self.root)
        self.root_patch.start()
        self.addCleanup(self.root_patch.stop)
        version = subprocess.CompletedProcess([], 0, '', 'openjdk version "17.0.20"')
        revision = subprocess.CompletedProcess([], 0, 'test-revision\n', '')
        self.commands = patch.object(run_ci.subprocess, 'run', side_effect=[revision, version] * 4)
        self.commands.start()
        self.addCleanup(self.commands.stop)

    def execute(self, suite='api', **kwargs):
        with contextlib.redirect_stdout(io.StringIO()):
            return run_ci.run(suite, **kwargs)

    def outputs(self):
        return sorted((self.root / 'target/ci').iterdir())

    def test_success_contains_revision_counts_and_console(self):
        os.environ['FAKE_XML'] = '<testsuite tests="14" failures="0" errors="0" skipped="0"/>'
        self.assertEqual(self.execute(), 0)
        output = self.outputs()[0]
        data = json.loads((output / 'run.json').read_text())
        self.assertEqual(data['revision'], 'test-revision')
        self.assertEqual(data['counts']['tests'], 14)
        self.assertEqual(data['execution'], 'real')
        self.assertIn('diagnostic evidence', (output / 'console.log').read_text())

    def test_failure_is_preserved_and_another_suite_can_run(self):
        os.environ.update(FAKE_EXIT='7', FAKE_XML='<testsuite tests="1" failures="1"/>')
        self.assertEqual(self.execute(), 7)
        os.environ.update(FAKE_EXIT='0', FAKE_XML='<testsuite tests="1"/>')
        self.assertEqual(self.execute('ui-smoke'), 0)
        statuses = [json.loads((p / 'run.json').read_text())['status'] for p in self.outputs()]
        self.assertCountEqual(statuses, ['failed', 'passed'])

    def test_missing_results_cannot_pass(self):
        self.assertEqual(self.execute(), 1)

    def test_failed_skipped_empty_and_malformed_results_cannot_pass(self):
        for xml in ['<testsuite tests="0"/>', '<testsuite tests="1" skipped="1"/>',
                    '<testsuite tests="1" errors="1"/>', '<broken']:
            with self.subTest(xml=xml):
                os.environ['FAKE_XML'] = xml
                self.assertEqual(self.execute(), 1)

    def test_repeats_do_not_consume_stale_evidence(self):
        os.environ['FAKE_XML'] = '<testsuite tests="1"/>'
        self.assertEqual(self.execute(), 0)
        del os.environ['FAKE_XML']
        self.assertEqual(self.execute(), 1)
        self.assertEqual(len(self.outputs()), 2)

    def test_discovery_is_explicitly_labelled(self):
        os.environ['FAKE_XML'] = '<testsuite tests="1"/>'
        self.assertEqual(self.execute('bdd-smoke', discovery=True), 0)
        data = json.loads((self.outputs()[0] / 'run.json').read_text())
        self.assertEqual(data['execution'], 'discovery')
        self.assertIn('-Dcucumber.execution.dry-run=true', data['command'])

    def test_wrong_java_version_fails_before_maven(self):
        revision = subprocess.CompletedProcess([], 0, 'test-revision\n', '')
        java = subprocess.CompletedProcess([], 0, '', 'openjdk version "26.0.1"')
        with patch.object(run_ci.subprocess, 'run', side_effect=[revision, java]):
            self.assertEqual(self.execute(), 1)
        output = self.outputs()[0]
        data = json.loads((output / 'run.json').read_text())
        self.assertIn('requires Java 17', data['error'])
        self.assertFalse((output / 'surefire-reports').exists())

    def test_github_run_attempt_and_summary_are_retained(self):
        summary = self.root / 'github-summary.md'
        os.environ.update(GITHUB_RUN_ID='123', GITHUB_RUN_ATTEMPT='2',
                          GITHUB_REPOSITORY='owner/repo', GITHUB_STEP_SUMMARY=str(summary),
                          FAKE_XML='<testsuite tests="1"/>')
        self.assertEqual(self.execute(), 0)
        data = json.loads((self.outputs()[0] / 'run.json').read_text())
        self.assertEqual(data['run_url'], 'https://github.com/owner/repo/actions/runs/123')
        self.assertEqual(data['attempt'], '2')
        self.assertIn('Tests: 1', summary.read_text())

    def test_launch_failure_has_downloadable_metadata(self):
        self.wrapper.unlink()
        self.assertEqual(self.execute(), 1)
        data = json.loads((self.outputs()[0] / 'run.json').read_text())
        self.assertEqual(data['status'], 'failed')
        self.assertTrue(data['error'])


if __name__ == '__main__':
    unittest.main()
