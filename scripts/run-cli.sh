#!/usr/bin/env sh

# Starts Duchess's command-line interface from any working directory.
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR/.."
exec ./gradlew runCli "$@"
