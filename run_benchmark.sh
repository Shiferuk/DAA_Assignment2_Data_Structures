#!/usr/bin/env bash
# One command: compile, run all workloads with seed 42, write results/results.csv and the plots.
set -euo pipefail
cd "$(dirname "$0")"
rm -rf target/classes && mkdir -p target/classes
javac -d target/classes $(find src/main/java -name '*.java')
java -cp target/classes daa.benchmark.BenchmarkRunner results/results.csv
if python3 -c "import matplotlib" 2>/dev/null; then
  python3 results/make_plots.py
else
  echo "matplotlib not found: skipping plots (pip install matplotlib)"
fi
