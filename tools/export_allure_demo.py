"""Export only the explicitly synthetic report demo; never use on arbitrary runs."""
import argparse
import json
from pathlib import Path
import shutil


def export(source, destination):
    results = [json.loads(p.read_text()) for p in source.glob('*-result.json')]
    expected = {'Browse the public product catalog with HTTP evidence',
                'Deliberate UI assertion failure on a synthetic local page',
                'Diagnose a deliberate failure on a synthetic browser page'}
    if len(results) != 3 or {r['name'] for r in results} != expected:
        raise ValueError('Expected exactly the three real demo results; refusing other reports')
    if sorted(r['status'] for r in results) != ['failed', 'failed', 'passed']:
        raise ValueError('Expected a passed catalog and two deliberate assertion failures')
    if any(p.get('name') == 'Execution' and p.get('value') != 'Real execution'
           for r in results for p in r.get('parameters', [])):
        raise ValueError('Discovery is not demo execution evidence')
    destination.mkdir(parents=True, exist_ok=False)
    labels = {'epic', 'feature', 'story', 'parentSuite', 'suite', 'subSuite', 'framework', 'language'}
    parameters = {'Execution', 'Source revision', 'Java', 'UI environment', 'API environment', 'Browser', 'Headless'}

    def evidence(node):
        if 'statusDetails' in node:
            node['statusDetails'].pop('trace', None)
        node['parameters'] = [p for p in node.get('parameters', []) if p['name'] in parameters]
        for attachment in node.get('attachments', []):
            filename = attachment['source']
            if Path(filename).name != filename:
                raise ValueError('Invalid attachment path')
            shutil.copyfile(source / filename, destination / filename)
        for step in node.get('steps', []):
            evidence(step)

    for result in results:
        result['labels'] = [l for l in result.get('labels', []) if l['name'] in labels]
        result.pop('links', None)
        evidence(result)
        (destination / (result['uuid'] + '-result.json')).write_text(json.dumps(result, indent=2) + '\n')
    shutil.copyfile(source / 'environment.properties', destination / 'environment.properties')
    # No fixture containers: omit local feature URIs, worker names and driver paths.
    # Test-level assertion messages, steps, screenshots, URLs and HTTP exchange remain.


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('source', type=Path)
    parser.add_argument('destination', type=Path)
    args = parser.parse_args()
    export(args.source, args.destination)
