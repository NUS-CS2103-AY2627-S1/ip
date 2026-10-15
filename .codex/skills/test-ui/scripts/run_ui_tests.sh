#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "$0")/../../../../" && pwd)"
plan="$repo_root/test/ui-test-plan.md"
build_dir="$(mktemp -d "${TMPDIR:-/tmp}/jelly-ui-test.XXXXXX")"
trap 'rm -rf "$build_dir"' EXIT

if [[ ! -f "$plan" ]]; then
  echo "Missing test plan: $plan" >&2
  exit 2
fi

javac -d "$build_dir" $(find "$repo_root/src/main/java" -name '*.java' -print)

python3 - "$plan" "$build_dir" <<'PY'
import re
import subprocess
import sys

plan, build_dir = sys.argv[1:]
text = open(plan, encoding="utf-8").read()
cases = re.findall(
    r"^## Test case: (.+?)\n\n### Aim\n(.*?)\n\n### Input\n```text\n(.*?)```\n\n### Expected output\n```text\n(.*?)```",
    text, re.MULTILINE | re.DOTALL,
)
if not cases:
    raise SystemExit("No valid test cases found in " + plan)

def normalize(value):
    return "\n".join(line.rstrip() for line in value.replace("\r\n", "\n").splitlines()).strip()

for index, (name, aim, user_input, expected) in enumerate(cases, 1):
    user_input = user_input.strip("\n") + "\n"
    result = subprocess.run(
        ["java", "-cp", build_dir, "jelly.Jelly"], input=user_input,
        text=True, capture_output=True, cwd=build_dir,
    )
    actual = result.stdout + result.stderr
    print(f"\n=== Test case {index}: {name.strip()} ===")
    print("--- console input ---")
    print(user_input, end="")
    print("--- console output ---")
    print(actual, end="" if actual.endswith("\n") else "\n")
    if normalize(actual) != normalize(expected):
        print("--- FAILED ---")
        print("Expected output:")
        print(expected, end="" if expected.endswith("\n") else "\n")
        raise SystemExit(1)
    print("--- PASSED ---")

print(f"\nAll {len(cases)} UI test case(s) passed.")
PY
