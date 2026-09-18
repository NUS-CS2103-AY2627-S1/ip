#!/usr/bin/env bash

set -eu

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <commit-message-file>"
    exit 2
fi

message_file="$1"
if [ ! -f "$message_file" ]; then
    echo "ERROR: Commit message file does not exist: $message_file"
    exit 2
fi

subject=$(sed -n '1p' "$message_file")
subject_length=${#subject}
has_error=0

if [ -z "$subject" ]; then
    echo "ERROR: Commit subject must not be empty."
    has_error=1
elif [ "$subject_length" -gt 72 ]; then
    echo "ERROR: Commit subject is $subject_length characters; the hard limit is 72."
    has_error=1
elif [ "$subject_length" -gt 50 ]; then
    echo "WARNING: Commit subject is $subject_length characters; aim for 50 or fewer."
fi

case "$subject" in
    *.)
        echo "ERROR: Commit subject must not end with a period."
        has_error=1
        ;;
esac

if tail -n +2 "$message_file" | grep -q '[^[:space:]]'; then
    second_line=$(sed -n '2p' "$message_file")
    if [ -n "$second_line" ]; then
        echo "ERROR: Separate the subject and body with a blank line."
        has_error=1
    fi
fi

if awk 'NR > 1 && length($0) > 72 {
            print "ERROR: Body line " NR " is " length($0) " characters; the limit is 72."
            found = 1
        }
        END { exit !found }' "$message_file"; then
    has_error=1
fi

if [ "$has_error" -ne 0 ]; then
    exit 1
fi

echo "Mechanical SE-EDU commit-message checks passed."
