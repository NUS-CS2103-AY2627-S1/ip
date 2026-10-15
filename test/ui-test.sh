#!/usr/bin/env bash
# UI test harness for Atlas
# Usage: test/ui-test.sh
# Runs every test/cases/*.in against Atlas and checks the expected substrings.
# Fails fast on the first failing case (per the course test-ui skill spec).

set -u
cd "$(dirname "$0")/.."
REPO_ROOT=$(pwd)

# Compile to a temp dir so the repo never accumulates .class files
BIN=$(mktemp -d)
WORK=$(mktemp -d)
trap 'rm -rf "$BIN" "$WORK"' EXIT

# GUI sources import javafx.*, so put the JavaFX jars from the Gradle cache
# on the compile classpath. Run a Gradle build first if this find is empty.
JAVAFX_CP=$(find "$HOME/.gradle/caches/modules-2/files-2.1/org.openjfx" -name '*.jar' 2>/dev/null | tr '\n' ':')

if ! find "$REPO_ROOT/src/main/java" -name '*.java' -print0 \
    | xargs -0 javac -Xlint:none -cp "$JAVAFX_CP" -d "$BIN"; then
    echo "********** BUILD FAILURE **********"
    exit 1
fi

pass=0

for input in "$REPO_ROOT"/test/cases/*.in; do
    name=$(basename "$input" .in)
    expected_file="$REPO_ROOT/test/cases/$name.expected"
    second_input="$REPO_ROOT/test/cases/$name.in2"
    # Run each case in its own temp working dir so Level-7 data files are
    # isolated and every case starts with a fresh task list.
    RUN_DIR=$(mktemp -d "$WORK/case.XXXXXX")
    # A case that needs files in place before it starts, e.g. a data file that
    # cannot be read, puts them in test/cases/<name>.setup/ and they are copied
    # into the run directory first, keeping their layout.
    setup_dir="$REPO_ROOT/test/cases/$name.setup"
    if [ -d "$setup_dir" ]; then
        cp -R "$setup_dir/." "$RUN_DIR/"
    fi
    output=$(cd "$RUN_DIR" && java -cp "$BIN" atlas.Atlas < "$input" 2>&1)
    status=$?
    # A second input file (<name>.in2) runs again in the same working dir to
    # test persistence across restarts.
    if [ $status -eq 0 ] && [ -f "$second_input" ]; then
        output2=$(cd "$RUN_DIR" && java -cp "$BIN" atlas.Atlas < "$second_input" 2>&1)
        status=$?
        output="$output"$'\n'"$output2"
    fi
    if [ $status -ne 0 ]; then
        echo "[FAIL] $name: program exited with code $status"
        echo "----- console input -----"
        cat "$input"
        [ -f "$second_input" ] && cat "$second_input"
        echo "----- actual output -----"
        echo "$output"
        echo "********** TEST SESSION TERMINATED **********"
        exit 1
    fi
    while IFS= read -r expected; do
        [ -z "$expected" ] && continue
        if ! grep -qF -- "$expected" <<<"$output"; then
            echo "[FAIL] $name: expected to find: $expected"
            echo "----- console input -----"
            cat "$input"
            [ -f "$second_input" ] && cat "$second_input"
            echo "----- actual output -----"
            echo "$output"
            echo "********** TEST SESSION TERMINATED **********"
            exit 1
        fi
    done < "$expected_file"
    pass=$((pass + 1))
    echo "[PASS] $name"
done

echo "--------------------------------"
echo "All $pass case(s) passed."
