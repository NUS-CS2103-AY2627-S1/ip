#!/usr/bin/env bash
#
# PostToolUse hook: runs the text UI test plan after the chatbot's sources change.
#
# Claude Code passes the tool call as JSON on standard input.  This script picks
# the edited file out of that JSON, does nothing unless it is a Java source file
# under src/, and otherwise runs the test plan.  A failing test case is reported
# back to Claude with exit code 2, so the failure is raised at the moment the
# change is made rather than being noticed later.
#
# Exit codes: 0 nothing to do, or every test case passed; 2 a test case failed.

set -uo pipefail

# The repository root is found from this script's own location, so the hook works
# no matter which directory Claude Code happens to run it from.
root="${CLAUDE_PROJECT_DIR:-$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)}"
runner="$root/.claude/skills/test-ui/scripts/run-ui-tests.py"
plan="$root/test/ui-test-plan.md"

# Stay quiet if the skill or the plan is not present, so a checkout without them
# is not blocked by a hook it cannot satisfy.
[ -f "$runner" ] && [ -f "$plan" ] || exit 0

# Read the edited file's path out of the hook's JSON input.  Write and Edit report
# it in different places, so both are tried.
edited=$(python3 -c '
import json, sys
try:
    payload = json.load(sys.stdin)
except ValueError:
    sys.exit(0)
tool_input = payload.get("tool_input") or {}
tool_response = payload.get("tool_response") or {}
print(tool_input.get("file_path") or tool_response.get("filePath") or "")
' 2>/dev/null)

# Only a change to the program under test is worth re-running the tests for.
case "$edited" in
    "$root"/src/*.java|src/*.java) ;;
    *) exit 0 ;;
esac

# Java is installed through sdkman here, which puts it on the PATH from the shell
# profile that a hook does not read.  Add it back when it is missing.
if ! command -v javac >/dev/null 2>&1; then
    export PATH="$HOME/.sdkman/candidates/java/current/bin:$PATH"
fi

output=$(cd "$root" && python3 "$runner" 2>&1)
status=$?

if [ "$status" -eq 0 ]; then
    # Nothing to say on success: the edit stands and the turn carries on.
    exit 0
fi

# Exit code 2 sends this text back to Claude as the reason the hook objected.
{
    echo "The text UI tests no longer pass after this change to $edited."
    echo
    echo "$output"
    echo
    echo "Decide which side is wrong before going further: the code, if this change"
    echo "broke behaviour by accident, or test/ui-test-plan.md, if the change was"
    echo "intended and the plan still records the old expected output."
} >&2
exit 2
