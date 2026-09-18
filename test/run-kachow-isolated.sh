#!/bin/sh

# Copy fixtures into a disposable directory so even write-failure tests protect repository data.
classes="$PWD/_temp/ui-test-console-classes"
test_directory=$(mktemp -d "${TMPDIR:-/tmp}/kachow-ui-test.XXXXXX") || exit 1
trap 'chmod -R u+w "$test_directory"; rm -rf "$test_directory"' EXIT
trap 'exit 1' HUP INT TERM

if [ "$#" -ge 1 ]; then
    cp -R "$PWD/$1/." "$test_directory" || exit 1
fi
if [ "${2:-}" = "--read-only" ]; then
    chmod a-w "$test_directory/data/kachow.txt" || exit 1
fi

cd "$test_directory" || exit 1
java -cp "$classes" com.benthecat.kachow.Kachow
