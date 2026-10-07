#!/usr/bin/env python3
"""Query OSV for every resolved project dependency, including test scope."""
import argparse
import json
from datetime import datetime, timezone
import urllib.request
from pathlib import Path


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('tree', type=Path, help='Maven dependency:tree JSON output')
    parser.add_argument('--output', type=Path, default=Path('target/dependency-audit.json'))
    args = parser.parse_args()
    tree = json.loads(args.tree.read_text())
    args.output.unlink(missing_ok=True)
    packages = set()

    def visit(node):
        packages.add((node['groupId'] + ':' + node['artifactId'], node['version']))
        for child in node.get('children', []):
            visit(child)

    for child in tree.get('children', []):
        visit(child)
    packages = sorted(packages)
    if not packages:
        raise RuntimeError('No resolved dependencies found; check the dependency tree input')
    findings = []
    for offset in range(0, len(packages), 100):
        batch = packages[offset:offset + 100]
        payload = {'queries': [
            {'package': {'name': name, 'ecosystem': 'Maven'}, 'version': version}
            for name, version in batch
        ]}
        request = urllib.request.Request(
            'https://api.osv.dev/v1/querybatch', data=json.dumps(payload).encode(),
            headers={'Content-Type': 'application/json'})
        with urllib.request.urlopen(request, timeout=60) as response:
            results = json.load(response)['results']
        if len(results) != len(batch) or any(result.get('next_page_token') for result in results):
            raise RuntimeError('Incomplete OSV response; audit cannot be considered complete')
        for (name, version), result in zip(batch, results):
            if result.get('vulns'):
                findings.append({'package': name, 'version': version, 'vulnerabilities': result['vulns']})
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps({'source': 'https://api.osv.dev/v1/querybatch',
                                      'checked_at': datetime.now(timezone.utc).isoformat(),
                                      'packages': len(packages), 'findings': findings}, indent=2) + '\n')
    print(f'Queried {len(packages)} resolved dependencies; {len(findings)} affected packages.')
    for finding in findings:
        print(f"{finding['package']}:{finding['version']}: " + ', '.join(
            v['id'] for v in finding['vulnerabilities']))
    return 1 if findings else 0


if __name__ == '__main__':
    raise SystemExit(main())
