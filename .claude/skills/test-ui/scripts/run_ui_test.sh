#!/usr/bin/env bash
# Runs one UI test case: feeds an input file to the compiled Ace program as
# stdin, captures stdout, and diffs it against an expected-output file.
#
# Usage: run_ui_test.sh <input-file> <expected-file> [classes-dir] [main-class]
#
# Exit code 0 = actual output matched expected exactly. Exit code 1 = mismatch
# (diff is printed). This script does not decide whether to continue to the
# next test case — the caller (the skill) stops the session on the first
# non-zero exit, per the project's "fail fast" testing requirement.

set -uo pipefail

input_file="$1"
expected_file="$2"
classes_dir="${3:-out/production/ip}"
main_class="${4:-ace.Ace}"

if [[ ! -f "$input_file" ]]; then
    echo "run_ui_test.sh: input file not found: $input_file" >&2
    exit 2
fi
if [[ ! -f "$expected_file" ]]; then
    echo "run_ui_test.sh: expected-output file not found: $expected_file" >&2
    exit 2
fi

# Every test case assumes a fresh, empty task list. Ace now persists tasks to
# data/Ace.txt (see Storage.java), so a file left over from a previous run
# would silently start this one with a non-empty list — deleting it first is
# what makes "fresh" actually true.
rm -f data/Ace.txt

actual=$(java -cp "$classes_dir" "$main_class" < "$input_file" 2>&1)
expected=$(cat "$expected_file")

echo "----- input (fed to the program as stdin, one command per line) -----"
cat "$input_file"
echo
echo "----- actual output -----"
echo "$actual"
echo

if [[ "$actual" == "$expected" ]]; then
    echo "RESULT: PASS"
    exit 0
fi

echo "RESULT: FAIL"
echo
echo "----- diff (- expected, + actual) -----"
diff -u <(echo "$expected") <(echo "$actual")
exit 1
