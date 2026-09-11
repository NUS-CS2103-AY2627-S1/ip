#!/usr/bin/env bash
# Run each documented console scenario in an isolated temporary data directory.
set -euo pipefail

SKILL_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_DIR="$(cd "$SKILL_DIR/../../.." && pwd)"
MANIFEST="$PROJECT_DIR/test/ui-test-cases.tsv"
JAR="$PROJECT_DIR/build/libs/lynn.jar"
WORK_DIR="$(mktemp -d)"

cleanup() {
    rm -rf "$WORK_DIR"
}
trap cleanup EXIT

cd "$PROJECT_DIR"
./gradlew shadowJar --console=plain >/dev/null

run_case() {
    local case_id="$1"
    local group="$2"
    local inputs="$3"
    local expected="$4"
    local case_directory="$WORK_DIR/$group"
    local output_file="$WORK_DIR/${case_id}.out"
    local expected_item

    mkdir -p "$case_directory"
    printf '\n=== %s ===\n' "$case_id"
    printf '%s\n' '--- Console input ---'
    printf '%b\n' "$inputs"
    printf '%s\n' '--- Console output ---'
    printf '%b\n' "$inputs" | (cd "$case_directory" && java -jar "$JAR") >"$output_file"
    cat "$output_file"

    IFS=';;' read -r -a expected_items <<< "$expected"
    for expected_item in "${expected_items[@]}"; do
        if ! grep -Fq "$(printf '%b' "$expected_item")" "$output_file"; then
            printf '\nFAIL: %s\n' "$case_id" >&2
            printf '%s\n' '--- Expected output to contain ---' >&2
            printf '%b\n' "$expected_item" >&2
            printf '%s\n' '--- Actual output ---' >&2
            cat "$output_file" >&2
            exit 1
        fi
    done
    printf 'PASS: %s\n' "$case_id"
}

while IFS=$'\t' read -r case_id group inputs expected; do
    [[ -z "$case_id" || "$case_id" == \#* ]] && continue
    run_case "$case_id" "$group" "$inputs" "$expected"
done < "$MANIFEST"


printf '\nAll UI test cases passed.\n'
