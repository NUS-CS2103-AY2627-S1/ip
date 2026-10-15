#!/usr/bin/env bash
set -e

mkdir -p /tmp/puke-out
./scripts/my-javac -d /tmp/puke-out src/main/java/puke/*.java
echo "Build complete."
