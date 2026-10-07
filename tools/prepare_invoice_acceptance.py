#!/usr/bin/env python3
"""Copy the existing invoice scenario for parallel acceptance without changing discovery."""
import argparse
from pathlib import Path

parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument("--copies", type=int, default=4)
parser.add_argument("--output", type=Path, default=Path("target/parallel-validation/invoices.feature"))
args = parser.parse_args()
if args.copies < 1:
    parser.error("--copies must be positive")
root = Path(__file__).resolve().parents[1]
source = (root / "src/test/resources/features/order/PlaceOrder.feature").read_text()
lines = source.splitlines()
start = next(i for i, line in enumerate(lines) if line.strip() == "@TC_24")
end = next((i for i in range(start + 1, len(lines)) if lines[i].lstrip().startswith("@")), len(lines))
scenario = "\n".join(lines[start:end])
output = args.output if args.output.is_absolute() else root / args.output
output.parent.mkdir(parents=True, exist_ok=True)
output.write_text("@ui\nFeature: Repeated isolated invoices\n\n" + "\n\n".join(
    scenario.replace("Scenario: Download Invoice after purchase order", f"Scenario: Isolated invoice {i}")
    for i in range(args.copies)
) + "\n")
print(output)
