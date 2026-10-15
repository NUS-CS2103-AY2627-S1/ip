#!/usr/bin/env bash

set -euo pipefail

test_dir="$(cd "$(dirname "$0")" && pwd)"
project_dir="$(cd "$test_dir/.." && pwd)"
build_dir="$(mktemp -d)"
run_dir="$(mktemp -d)"
error_run_dir="$(mktemp -d)"
corrupt_run_dir="$(mktemp -d)"
legacy_run_dir="$(mktemp -d)"
empty_run_dir="$(mktemp -d)"
trap 'rm -rf "$build_dir" "$run_dir" "$error_run_dir" "$corrupt_run_dir" "$legacy_run_dir" "$empty_run_dir"' EXIT

javac -Xlint:none -d "$build_dir" "$project_dir"/src/main/java/*.java
(cd "$run_dir" && java -classpath "$build_dir" Michael < "$test_dir/input.txt" > "$test_dir/ACTUAL.TXT")
(cd "$run_dir" && java -classpath "$build_dir" Michael < "$test_dir/input-load.txt" > "$run_dir/ACTUAL-LOAD.TXT")
(cd "$empty_run_dir" && java -classpath "$build_dir" Michael < "$test_dir/input-list.txt" > "$empty_run_dir/ACTUAL-EMPTY-STORAGE.TXT")
(cd "$error_run_dir" && java -classpath "$build_dir" Michael < "$test_dir/input-errors.txt" > "$error_run_dir/ACTUAL-ERRORS.TXT")
mkdir -p "$corrupt_run_dir/data"
printf 'T|2|not-valid-base64\n' > "$corrupt_run_dir/data/michael.txt"
(cd "$corrupt_run_dir" && java -classpath "$build_dir" Michael < "$test_dir/input-list.txt" > "$corrupt_run_dir/ACTUAL-CORRUPT-STORAGE.TXT")
mkdir -p "$legacy_run_dir/data"
printf '[T][X] read book\n[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)\n' > "$legacy_run_dir/data/michael.txt"
(cd "$legacy_run_dir" && java -classpath "$build_dir" Michael < "$test_dir/input-list.txt" > "$legacy_run_dir/ACTUAL-LEGACY-STORAGE.TXT")

diff -u "$test_dir/EXPECTED.TXT" "$test_dir/ACTUAL.TXT"
diff -u "$test_dir/EXPECTED-STORAGE.TXT" "$run_dir/data/michael.txt"
diff -u "$test_dir/EXPECTED-LOAD.TXT" "$run_dir/ACTUAL-LOAD.TXT"
diff -u "$test_dir/EXPECTED-EMPTY-STORAGE.TXT" "$empty_run_dir/ACTUAL-EMPTY-STORAGE.TXT"
test ! -e "$empty_run_dir/data"
diff -u "$test_dir/EXPECTED-ERRORS.TXT" "$error_run_dir/ACTUAL-ERRORS.TXT"
diff -u "$test_dir/EXPECTED-CORRUPT-STORAGE.TXT" "$corrupt_run_dir/ACTUAL-CORRUPT-STORAGE.TXT"
diff -u "$test_dir/EXPECTED-LOAD.TXT" "$legacy_run_dir/ACTUAL-LEGACY-STORAGE.TXT"
echo "Test result: PASSED"
