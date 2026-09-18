#!/usr/bin/env bash
# Checks a commit message against the se-education git convention.
# https://se-education.org/guides/conventions/git.html
#
# Usage: bash check-commit-message.sh <message file>
#        bash check-commit-message.sh -   (reads the message from stdin)
#
# Write the message to a file first, then check the file, then commit with
# `git commit -F <file>`. That way you check the same text that you commit.
# Exit code 1 means that the script printed at least one breach.

if [ "$#" -ne 1 ]; then
    echo "usage: $0 <message file | ->" >&2
    exit 2
fi

# Some checks below use grep -P, which refuses to run in a locale it cannot
# classify as unibyte or UTF-8. An unset locale, which is what Git Bash on
# Windows gives, is one of those, and those checks then fail quietly. Pick a
# UTF-8 locale that exists here, and say so plainly when none does.
for candidate in "$LC_ALL" C.UTF-8 en_US.UTF-8; do
    [ -z "$candidate" ] && continue
    if LC_ALL="$candidate" grep -qP 'a' <<< 'a' 2>/dev/null; then
        export LC_ALL="$candidate"
        break
    fi
done
if ! grep -qP 'a' <<< 'a' 2>/dev/null; then
    echo "warning: grep -P does not work in this locale, so some checks below" >&2
    echo "         cannot run. Treat a clean result as incomplete." >&2
fi

if [ "$1" = "-" ]; then
    msg=$(cat)
else
    [ -f "$1" ] || { echo "no such file: $1" >&2; exit 2; }
    msg=$(cat "$1")
fi

bad=0
say() { printf '%s | %s\n' "$1" "$2"; [ "$1" = "BREACH" ] && bad=1; return 0; }

subject=$(printf '%s\n' "$msg" | sed -n '1p')
second=$(printf '%s\n' "$msg" | sed -n '2p')
lines=$(printf '%s\n' "$msg" | wc -l)

# --- Subject line ---
if [ -z "${subject// }" ]; then
    say BREACH "the subject line is empty"
fi

len=${#subject}
if [ "$len" -gt 72 ]; then
    say BREACH "subject is $len characters; the hard limit is 72"
elif [ "$len" -gt 50 ]; then
    say WARN   "subject is $len characters; keep it at 50 if you can"
fi

# The first letter after an optional scope prefix, such as "Task class: ".
head_word=$(printf '%s' "$subject" | sed -E 's/^[A-Za-z][A-Za-z0-9 ]*: *//')
if printf '%s' "$head_word" | grep -q -P '^[a-z]'; then
    say BREACH "capitalize the first letter of the subject"
fi

if printf '%s' "$subject" | grep -q -P '\.\s*$'; then
    say BREACH "do not end the subject line with a period"
fi

# Imperative mood. The test is the first word, so it catches the common slips
# only. "Add" is right. "Added" and "Adding" are wrong.
verb=$(printf '%s' "$head_word" | awk '{print $1}')
case "$verb" in
    *ed|*ing)
        say WARN "\"$verb\" may be past tense or a gerund; the subject needs the imperative, such as \"Add\"" ;;
esac

# --- Body ---
if [ "$lines" -gt 1 ]; then
    if [ -n "${second// }" ]; then
        say BREACH "put a blank line between the subject and the body"
    fi

    n=0
    while IFS= read -r line; do
        n=$((n + 1))
        [ "$n" -le 2 ] && continue
        if [ "${#line}" -gt 72 ]; then
            # A single long word, such as a URL, cannot be wrapped.
            if printf '%s' "$line" | grep -q -P '^\S{72,}$'; then
                say WARN "line $n is one long token; leave it unwrapped"
            else
                say BREACH "line $n is ${#line} characters; wrap the body at 72"
            fi
        fi
    done <<< "$msg"
else
    say NOTE "no body. A commit that is more than a rename needs a body that states what and why."
fi

if [ "$bad" -eq 0 ]; then
    echo "OK | the message meets the convention. Read it once more: does the body say WHAT and WHY, not HOW?"
fi
exit "$bad"
