#!/usr/bin/env sh

# Runs the repository's exact console UI regression plan.
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR/.."
exec python3 .codex/skills/test-ui/scripts/run_ui_tests.py "$@"
