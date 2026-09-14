#!/usr/bin/env bash

# Compiles Stuart and runs three scenarios (all from the project root, so
# its "./data/..." save path resolves correctly):
#   1. input.txt against a clean slate, checking console output
#      (EXPECTED.TXT) and the resulting save file (EXPECTED_DATA.TXT) -
#      exercises writing, including several invalid-input edge cases.
#   2. input_load.txt against a pre-seeded, well-formed save file
#      (seed_data.txt), checking console output (EXPECTED_LOAD.TXT) -
#      exercises loading.
#   3. input_load.txt against a pre-seeded save file with corrupted lines
#      mixed in (seed_data_corrupted.txt), checking console output
#      (EXPECTED_LOAD_CORRUPTED.TXT) - exercises loading resilience.
# Run from anywhere; paths are resolved relative to this script's location.

cd "$(dirname "$0")"

cd ..
mkdir -p out/production/ip
javac -d out/production/ip $(find src/main/java -name "*.java")
if [ $? -ne 0 ]; then
    echo "********** BUILD FAILURE **********"
    exit 1
fi

WRITE_CONSOLE_OK=0
WRITE_DATA_OK=0
LOAD_CONSOLE_OK=0
LOAD_CORRUPTED_OK=0

# --- Scenario 1: writing, from a clean slate ---
rm -rf data

# Run from the project root, matching how a normal user (or IntelliJ) would
# launch Stuart, so its hard-coded "./data/stuart.txt" path resolves here.
java -ea -classpath out/production/ip stuart.Stuart < text-ui-test/input.txt > text-ui-test/ACTUAL.TXT

cd text-ui-test

# Normalize line endings (in case EXPECTED.TXT has Windows line endings).
cp EXPECTED.TXT EXPECTED-UNIX.TXT
if command -v dos2unix >/dev/null 2>&1; then
    dos2unix ACTUAL.TXT EXPECTED-UNIX.TXT >/dev/null 2>&1
fi

diff ACTUAL.TXT EXPECTED-UNIX.TXT
if [ $? -eq 0 ]; then
    WRITE_CONSOLE_OK=1
else
    echo "********** Console output does not match EXPECTED.TXT **********"
fi

diff ../data/stuart.txt EXPECTED_DATA.TXT
if [ $? -eq 0 ]; then
    WRITE_DATA_OK=1
else
    echo "********** Saved data/stuart.txt does not match EXPECTED_DATA.TXT **********"
fi

# --- Scenario 2: loading, from a pre-seeded, well-formed save file ---
cd ..
rm -rf data
mkdir -p data
cp text-ui-test/seed_data.txt data/stuart.txt

java -ea -classpath out/production/ip stuart.Stuart < text-ui-test/input_load.txt > text-ui-test/ACTUAL_LOAD.TXT

cd text-ui-test

diff ACTUAL_LOAD.TXT EXPECTED_LOAD.TXT
if [ $? -eq 0 ]; then
    LOAD_CONSOLE_OK=1
else
    echo "********** Console output does not match EXPECTED_LOAD.TXT **********"
fi

# --- Scenario 3: loading, from a save file with corrupted lines mixed in ---
cd ..
rm -rf data
mkdir -p data
cp text-ui-test/seed_data_corrupted.txt data/stuart.txt

java -ea -classpath out/production/ip stuart.Stuart < text-ui-test/input_load.txt > text-ui-test/ACTUAL_LOAD_CORRUPTED.TXT

cd text-ui-test

diff ACTUAL_LOAD_CORRUPTED.TXT EXPECTED_LOAD_CORRUPTED.TXT
if [ $? -eq 0 ]; then
    LOAD_CORRUPTED_OK=1
else
    echo "********** Console output does not match EXPECTED_LOAD_CORRUPTED.TXT **********"
fi

if [ $WRITE_CONSOLE_OK -eq 1 ] && [ $WRITE_DATA_OK -eq 1 ] && [ $LOAD_CONSOLE_OK -eq 1 ] && [ $LOAD_CORRUPTED_OK -eq 1 ]; then
    echo "Test passed!"
    exit 0
else
    echo "Test failed!"
    exit 1
fi
