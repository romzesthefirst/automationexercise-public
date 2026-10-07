#!/usr/bin/env python3
"""Compare individual UI tests and BDD scenarios from separate Allure run directories."""
import argparse
import json
from pathlib import Path


def read_cases(directory):
    cases = {}
    for path in sorted(directory.glob('*-result.json')):
        result = json.loads(path.read_text())
        result['_result_path'] = path.resolve()
        # Scenario outlines share a source location, but their example values differ.
        parameters = tuple(sorted((p['name'], p['value']) for p in result.get('parameters', [])
                                  if not p.get('excluded', False)))
        key = (result.get('fullName', result['name']), parameters)
        if key in cases:
            raise ValueError(f'Duplicate case in {directory}: {key}; use a fresh results directory')
        cases[key] = result
    if not cases:
        raise ValueError(f'No Allure results in {directory}')
    return cases


def compare(root):
    lines = ['# Individual browser comparison', '',
             'Each cell is the recorded Allure outcome; missing cases are explicit.', '',
             '| Suite / test or scenario | Chrome | Firefox | Edge |',
             '| --- | --- | --- | --- |']
    differences = []
    for suite in ('ui', 'bdd'):
        runs = {browser: read_cases(root / f'{browser}-{suite}' / 'allure-results')
                for browser in ('chrome', 'firefox', 'edge')}
        keys = set().union(*(run.keys() for run in runs.values()))
        for key in sorted(keys):
            name = next(run[key]['name'] for run in runs.values() if key in run)
            if key[1]:
                name += ' (' + ', '.join(f'{k}={v}' for k, v in key[1]) + ')'
            statuses = [runs[b].get(key, {}).get('status', 'missing') for b in runs]
            lines.append('| ' + ' | '.join([suite + ' / ' + name.replace('|', '\\|')] + statuses) + ' |')
            if len(set(statuses)) > 1 or any(s != 'passed' for s in statuses):
                differences.append((suite, name, key, runs))
    lines += ['', '## Failures and differences', '']
    for suite, name, key, runs in differences:
        lines += [f'### {suite}: {name}', '']
        for browser, run in runs.items():
            result = run.get(key, {})
            if result.get('status') != 'passed':
                message = result.get('statusDetails', {}).get('message') or 'No failure message (see Allure result)'
                lines.append(f'- {browser}: {result.get("status", "missing")}: {message.splitlines()[0]}')
                if '_result_path' in result:
                    path = result['_result_path']
                    lines.append(f'  [Allure result](<{path}>)')
                    for attachment in result.get('attachments', []):
                        if attachment.get('name') in ('Current URL', 'Screenshot on failure'):
                            artifact = path.parent / attachment['source']
                            lines.append(f'  [{attachment["name"]}](<{artifact}>)')
        lines.append('')
    if not differences:
        lines.append('All individual cases passed in all three browsers.')
    return '\n'.join(lines) + '\n'


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('root', type=Path, help='Root containing chrome-ui/allure-results, firefox-bdd/allure-results, etc.')
    parser.add_argument('--output', type=Path)
    args = parser.parse_args()
    report = compare(args.root)
    if args.output:
        args.output.parent.mkdir(parents=True, exist_ok=True)
        args.output.write_text(report)
    else:
        print(report, end='')
