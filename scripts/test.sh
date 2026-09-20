#!/usr/bin/env sh

# Runs compilation, JUnit tests, and Checkstyle.
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR/.."
exec ./gradlew check "$@"
