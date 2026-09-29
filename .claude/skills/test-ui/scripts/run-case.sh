#!/usr/bin/env bash
#
# Compiles Emma and runs one UI test case against it.
#
# Usage: run-case.sh <inputs-file> <raw-out> <normalised-out>
#
#   <inputs-file>     one command per line, exactly as the user would type them
#   <raw-out>         receives the full console transcript (what the tester sees)
#   <normalised-out>  receives just Emma's responses, for comparing against
#                     the "Expected output" block in the test plan
#
# A line reading exactly "--- restart ---" ends one chatbot session and starts
# another in the same directory. That is how a case tests whether tasks saved by
# one run are still there on the next run.
#
# Normalisation removes, in order:
#   - ANSI colour codes
#   - carriage returns (Windows line endings)
#   - the "Emma" / "user" speaker labels
#   - blank lines
#   - the startup banner and greeting of each session
#
# Trailing whitespace on each line is also stripped so that invisible
# differences never fail a test.

set -euo pipefail

if [ "$#" -ne 3 ]; then
    echo "usage: run-case.sh <inputs-file> <raw-out> <normalised-out>" >&2
    exit 2
fi

inputs="$(cd "$(dirname "$1")" && pwd)/$(basename "$1")"
raw_out="$2"
norm_out="$3"

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../../.." && pwd)"
classes="$(mktemp -d)"
sessions="$(mktemp -d)"
# Emma saves to ./data/emma.json relative to the working directory, so each case
# runs in its own empty directory. That keeps cases from inheriting each other's
# saved tasks, and leaves the repo's own data/emma.json untouched. Sessions
# within a case share this directory, so saved tasks carry across a restart.
sandbox="$(mktemp -d)"
trap 'rm -rf "$classes" "$sessions" "$sandbox"' EXIT

javac -d "$classes" "$repo_root"/src/main/java/emma/*.java "$repo_root"/src/main/java/emma/command/*.java

awk -v dir="$sessions" '
    BEGIN { n = 1 }
    /^--- restart ---$/ { n++; next }
    { print > (dir "/" n ".session") }
' "$inputs"

: > "$raw_out"
: > "$norm_out"

for session in $(ls "$sessions" | sort -n); do
    transcript="$sessions/$session.out"
    # The program may exit non-zero if it crashes; keep the transcript either
    # way so the failure report can show what actually happened.
    (cd "$sandbox" && java -cp "$classes" emma.Emma < "$sessions/$session") \
        > "$transcript" 2>&1 || true

    cat "$transcript" >> "$raw_out"

    # Normalise each session on its own, so every session's banner and greeting
    # is dropped rather than only the first one's.
    sed -e 's/\x1b\[[0-9;]*m//g' -e 's/[[:space:]]*$//' "$transcript" \
        | tr -d '\r' \
        | awk '
            /^Emma$/ { emmaLabels++; next }
            /^user$/ { next }
            /^$/     { next }
            emmaLabels >= 2 { print }
        ' >> "$norm_out"
done
