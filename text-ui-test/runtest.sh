#!/usr/bin/env bash
#
# Compiles GOAT, feeds it input.txt, and compares the output with EXPECTED.TXT.
#
# Usage:
#   ./text-ui-test/runtest.sh            run the test and report PASSED/FAILED
#   ./text-ui-test/runtest.sh --bless    accept the current output as the new expected

set -u

cd "$(dirname "$0")/.." || exit 1

PROJECT_DIR="$PWD"
TEST_DIR="$(mktemp -d)" || exit 1
trap 'rm -rf "$TEST_DIR"' EXIT

# Compiled through Gradle rather than by calling javac directly, because the GUI classes
# need the JavaFX jars that Gradle already resolves. The text interface itself loads none
# of those classes, so running it below still needs nothing beyond the compiled output.
if ! ./gradlew --quiet compileJava; then
    echo "BUILD FAILURE"
    exit 1
fi

(
    cd "$TEST_DIR" || exit 1
    java -ea -cp "$PROJECT_DIR/build/classes/java/main" seedu.goat.Goat \
        < "$PROJECT_DIR/text-ui-test/input.txt" > "$PROJECT_DIR/text-ui-test/ACTUAL.TXT"
)

if [ "${1:-}" = "--bless" ]; then
    cp text-ui-test/ACTUAL.TXT text-ui-test/EXPECTED.TXT
    echo "EXPECTED.TXT updated from this run. Read the diff above before committing it."
    exit 0
fi

if diff -u text-ui-test/EXPECTED.TXT text-ui-test/ACTUAL.TXT; then
    echo "Test result: PASSED"
    exit 0
else
    echo "Test result: FAILED"
    exit 1
fi
