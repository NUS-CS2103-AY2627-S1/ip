#!/usr/bin/env bash

set -eu

project_root="${1:-.}"
java_files=$(find "$project_root/src/main/java" "$project_root/src/test/java" \
    -type f -name '*.java' 2>/dev/null | sort)

if [ -z "$java_files" ]; then
    echo "No Java files found under src/main/java or src/test/java."
    exit 1
fi

has_error=0

while IFS= read -r java_file; do
    if LC_ALL=C grep -n $'\t' "$java_file"; then
        echo "ERROR: Tabs found in $java_file"
        has_error=1
    fi

    if awk 'length($0) > 120 { print FNR ":" length($0) ":" $0; found = 1 }
            END { exit !found }' "$java_file"; then
        echo "ERROR: Lines longer than 120 characters found in $java_file"
        has_error=1
    fi

    if grep -nE '^import .*[.]\*;' "$java_file"; then
        echo "ERROR: Wildcard import found in $java_file"
        has_error=1
    fi

    if ! grep -qE '^package [a-z][a-z0-9]*(\.[a-z][a-z0-9]*)*;' "$java_file"; then
        echo "ERROR: Missing or invalid lowercase package declaration in $java_file"
        has_error=1
    fi
done <<EOF
$java_files
EOF

if [ "$has_error" -ne 0 ]; then
    exit 1
fi

echo "Mechanical SE-EDU Java style checks passed."
