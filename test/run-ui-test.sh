#!/usr/bin/env bash
set -euo pipefail

# Keep UI test persistence isolated from the user's real task data.
mkdir -p build/ui-test-data
rm -f build/ui-test-data/data/larry.txt
cd build/ui-test-data
exec java -ea -Dlarry.currentDate=2026-08-26 -cp ../classes/java/main larry.Larry
