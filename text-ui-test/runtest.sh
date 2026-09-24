#!/usr/bin/env bash

set -e

INPUT_FILE="${1:-input.txt}"
EXPECTED_FILE="${2:-EXPECTED.TXT}"
BIN_DIR="../bin"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

if [ ! -f "$INPUT_FILE" ] || [ ! -f "$EXPECTED_FILE" ]
then
    echo "Usage: ./runtest.sh <input-file> <expected-file>"
    echo "Both files must contain the Sage console test input/output."
    exit 2
fi

mkdir -p "$BIN_DIR"

# delete output from previous run
if [ -e "./ACTUAL.TXT" ]
then
    rm ACTUAL.TXT
fi

# Compile Sage's console classes into the bin folder.
javac -Xlint:none -d "$BIN_DIR" $(find ../src/main/java/sage \
    -name '*.java' ! -name 'SageGui.java' ! -name 'Launcher.java' \
    ! -name 'MainWindow.java' ! -name 'DialogBox.java')

# Run Sage with the supplied input and capture its output.
TEST_DIR="$(mktemp -d)"
trap 'rm -rf "$TEST_DIR"' EXIT
(cd "$TEST_DIR" && java -classpath "$SCRIPT_DIR/$BIN_DIR" sage.Sage < "$SCRIPT_DIR/$INPUT_FILE") > ACTUAL.TXT

# convert to UNIX format
sed 's/\r$//' "$EXPECTED_FILE" > EXPECTED-UNIX.TXT
sed -i '' 's/\r$//' ACTUAL.TXT

# compare the output to the expected output
diff ACTUAL.TXT EXPECTED-UNIX.TXT
if [ $? -eq 0 ]
then
    echo "Test result: PASSED"
    exit 0
else
    echo "Test result: FAILED"
    exit 1
fi
