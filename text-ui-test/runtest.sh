#!/usr/bin/env bash

# create bin directory if it doesn't exist
if [ ! -d "../bin" ]
then
    mkdir ../bin
fi

# delete output from previous run
if [ -e "./ACTUAL.TXT" ]
then
    rm ACTUAL.TXT
fi

# delete the save file left behind by a previous run.
# Goat now loads its tasks at start-up, so a leftover file would make the
# first session start with old tasks and the test would not be repeatable.
rm -rf ./data

# compile the code into the bin folder, terminates if error occurred
# The sources now sit in package folders under the source root, so they are
# gathered with find rather than a single *.java glob. The source root itself
# is no longer put on the classpath: javac locates a class from its package
# name, and leaving stale .class files there on the classpath could shadow a
# freshly compiled one.
if ! javac -Xlint:none -d ../bin $(find ../src/main/java -name "*.java")
then
    echo "********** BUILD FAILURE **********"
    exit 1
fi

# FIRST RUN: feed commands from input.txt and redirect the output to ACTUAL.TXT
java -classpath ../bin goat.Goat < input.txt > ACTUAL.TXT

# show what was written to the hard disk, so the saved format is tested too
echo "===== SAVED FILE =====" >> ACTUAL.TXT
cat ./data/goat.txt >> ACTUAL.TXT

# SECOND RUN: start the program again without adding anything.
# If the list still has the tasks from the first run, saving and loading work.
echo "===== RESTART =====" >> ACTUAL.TXT
java -classpath ../bin goat.Goat < input-restart.txt >> ACTUAL.TXT

# THIRD RUN: damage the save file, then start the program again.
# Reading has to cope with a file someone has edited by hand and got wrong,
# so the test checks that Goat says which line is at fault instead of crashing.
# The fault used here is a date in the wrong format, which is now the easiest
# mistake to make by hand, and it proves dates are validated on loading too.
echo "===== DAMAGED FILE =====" >> ACTUAL.TXT
printf 'T | 1 | read book\nD | 0 | return book | 2 Dec 2019\n' > ./data/goat.txt
cat ./data/goat.txt >> ACTUAL.TXT
echo "===== RECOVERY =====" >> ACTUAL.TXT
java -classpath ../bin goat.Goat < input-corrupt.txt >> ACTUAL.TXT

# FOURTH RUN: delete only the save file, keeping the ./data folder.
# The requirement names two separate first-run situations - no folder at all
# (the very first run above) and a folder with no file in it, which is what
# someone gets after deleting their save file. Both must start cleanly.
echo "===== FILE DELETED, FOLDER KEPT =====" >> ACTUAL.TXT
rm -f ./data/goat.txt
java -classpath ../bin goat.Goat < input-nofile.txt >> ACTUAL.TXT
echo "===== FILE REMADE =====" >> ACTUAL.TXT
cat ./data/goat.txt >> ACTUAL.TXT

# convert to UNIX format.
# dos2unix is not installed on macOS by default, so fall back to stripping
# the carriage returns with tr, which needs no extra tools.
cp EXPECTED.TXT EXPECTED-UNIX.TXT
if command -v dos2unix > /dev/null
then
    dos2unix ACTUAL.TXT EXPECTED-UNIX.TXT
else
    for file in ACTUAL.TXT EXPECTED-UNIX.TXT
    do
        tr -d '\r' < "$file" > "$file.tmp" && mv "$file.tmp" "$file"
    done
fi

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
