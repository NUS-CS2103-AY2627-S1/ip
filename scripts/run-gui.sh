#!/usr/bin/env sh

# Starts Duchess's JavaFX interface from any working directory.
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
cd "$SCRIPT_DIR/.."
exec ./gradlew run "$@"
