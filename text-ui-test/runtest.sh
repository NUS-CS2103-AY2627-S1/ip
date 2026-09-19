#!/usr/bin/env bash

set -euo pipefail

cd "$(dirname "$0")/.."

test_backup_dir=$(mktemp -d)
test_had_saved_data=false
if [[ -f data/duke.txt ]]; then
    cp data/duke.txt "$test_backup_dir/duke.txt"
    test_had_saved_data=true
fi

cleanup() {
    rm -f data/duke.txt
    if [[ "$test_had_saved_data" == true ]]; then
        mkdir -p data
        cp "$test_backup_dir/duke.txt" data/duke.txt
        rm -f "$test_backup_dir/duke.txt"
    else
        rmdir data 2>/dev/null || true
    fi
    rmdir "$test_backup_dir"
}
trap cleanup EXIT

mkdir -p out/production/ip
javac -d out/production/ip \
    src/main/java/mochi/*.java \
    src/main/java/mochi/*/*.java

rm -f data/duke.txt text-ui-test/ACTUAL.TXT \
    text-ui-test/ACTUAL-LOADED.TXT text-ui-test/ACTUAL-CORRUPTED.TXT
rmdir data 2>/dev/null || true
java -cp out/production/ip mochi.Mochi \
    < text-ui-test/input.txt \
    > text-ui-test/ACTUAL.TXT

diff -u text-ui-test/EXPECTED.TXT text-ui-test/ACTUAL.TXT
diff -u text-ui-test/EXPECTED-DATA.TXT data/duke.txt

java -cp out/production/ip mochi.Mochi \
    < text-ui-test/input-loaded.txt \
    > text-ui-test/ACTUAL-LOADED.TXT

diff -u text-ui-test/EXPECTED-LOADED.TXT text-ui-test/ACTUAL-LOADED.TXT
diff -u text-ui-test/EXPECTED-DATA.TXT data/duke.txt

cp text-ui-test/CORRUPTED-DATA.TXT data/duke.txt
java -cp out/production/ip mochi.Mochi \
    < text-ui-test/input-loaded.txt \
    > text-ui-test/ACTUAL-CORRUPTED.TXT

diff -u text-ui-test/EXPECTED-CORRUPTED.TXT text-ui-test/ACTUAL-CORRUPTED.TXT

echo "Text UI, persistence, and storage error-handling tests passed."
