#!/usr/bin/env bash
# Reports possible breaches of the se-education Java coding standard (intermediate level).
# https://se-education.org/guides/conventions/java/intermediate.html
#
# Usage: bash check-conventions.sh File1.java File2.java ...
#
# The output is a list of suspects, not a verdict. Every line needs a human check,
# because the tests are text patterns, not a Java parser. Exit code 1 means that
# the script printed at least one suspect.

if [ "$#" -eq 0 ]; then
    echo "usage: $0 <java files>" >&2
    exit 2
fi

# Most checks below use grep -P, which refuses to run in a locale it cannot
# classify as unibyte or UTF-8. An unset locale, which is what Git Bash on
# Windows gives, is one of those. Every -P check then fails quietly and the
# script reports a clean file that was never actually read. Pick a UTF-8 locale
# that exists here, and say so plainly when none does.
for candidate in "$LC_ALL" C.UTF-8 en_US.UTF-8; do
    [ -z "$candidate" ] && continue
    if LC_ALL="$candidate" grep -qP 'a' <<< 'a' 2>/dev/null; then
        export LC_ALL="$candidate"
        break
    fi
done
if ! grep -qP 'a' <<< 'a' 2>/dev/null; then
    echo "warning: grep -P does not work in this locale, so most checks below" >&2
    echo "         cannot run. Treat a clean result as meaningless." >&2
fi

found=0
work="${TMPDIR:-/tmp}/check-conventions.$$"
trap 'rm -f "$work"' EXIT

# report <file> <"line:text" stream> <message>
report() {
    while IFS= read -r hit; do
        [ -z "$hit" ] && continue
        printf '%s:%s | %s\n' "$1" "${hit%%:*}" "$3"
        found=1
    done <<< "$2"
}

# Blanks every comment but keeps the line count, so that a line number stays true.
# The code checks run on this copy. A word such as "class" inside a comment then
# raises no false alarm.
strip_comments() {
    awk '
        {
            line = $0
            if (inblock) {
                if (line ~ /\*\//) { sub(/^.*\*\//, "", line); inblock = 0 } else { line = "" }
            }
            if (!inblock) {
                gsub(/\/\*([^*]|\*[^\/])*\*\//, "", line)   # whole block on one line
                if (line ~ /\/\*/) { sub(/\/\*.*$/, "", line); inblock = 1 }
                sub(/\/\/.*$/, "", line)                    # line comment, and trailing comment
            }
            print line
        }' "$1" | sed -E 's/"[^"]*"/""/g'
}

for f in "$@"; do
    [ -f "$f" ] || continue
    strip_comments "$f" > "$work"

    # Whitespace and length: run on the original file, comments included.
    report "$f" "$(grep -n -P '^\t' "$f")"           "tab indentation; use 4 spaces"
    report "$f" "$(grep -n -P '^.{121,}$' "$f")"     "line over 120 characters (hard limit)"
    report "$f" "$(grep -n -P '[ \t]+$' "$f")"       "trailing whitespace"

    # Code checks: run on the comment-free copy.
    report "$f" "$(grep -n -P '^\s*import .*\.\*;' "$work")" "wildcard import; list each class"
    report "$f" "$(grep -n -P '^\s*\{\s*$' "$work")"         "opening brace on its own line; put it on the previous line"
    report "$f" "$(grep -n -P '\b(if|for|while|switch|catch)\(' "$work")" "missing space after the keyword"
    report "$f" "$(grep -n -P '\b(int|long|short|byte|char|boolean|float|double|String|[A-Z]\w*)\s+\w+\s*\[\s*\]' "$work")" "C-style array; write int[] a"
    report "$f" "$(grep -n -P '^\s*public\s+(?!.*\b(class|interface|enum|record|static\s+final)\b)[\w<>\[\], .]+\s+\w+\s*(=|;)' "$work")" "public field; make it private and add an accessor"
    report "$f" "$(grep -n -P '\bstatic\s+final\b[\w<>\[\], .]*\s+(?![A-Z0-9_]+\s*[=;])\w+\s*[=;]' "$work")" "static final field with a lower-case name; rename it UPPER_CASE if it is a true constant"
    report "$f" "$(grep -n -P '^\s*((public|protected|private|abstract|final|static|sealed)\s+)*(class|interface|enum|record)\s+(?![A-Z])\w+' "$work")" "type name is not PascalCase"

    # Method declaration whose name does not start with a lower-case letter.
    # The second grep drops constructors, because a constructor repeats the type name.
    report "$f" "$(grep -n -P '^\s*(public|protected|private)\s+(?!.*\b(class|interface|enum|record)\b)[\w<>\[\], .]+\s+(?![a-z])\w+\s*\(' "$work" \
        | grep -v -P '^\d+:\s*(public|protected|private)\s+('"$(basename "$f" .java)"')\s*\(')" \
        "method name is not camelCase"

    # An abbreviation inside a name is not written in capitals: the standard
    # asks for exportHtmlSource, not exportHTMLSource.
    #
    # Three capitals in a row is an abbreviation. Two is only an abbreviation
    # when the name ends there, because a name such as leapDayInALeapYear puts
    # two capitals together out of an article and an ordinary word. Only names
    # this project declares are judged: a JDK type such as IOException cannot
    # be renamed.
    report "$f" "$(grep -n -P '^\s*(?:(?:public|protected|private|static|final|abstract|synchronized)\s+)+[\w<>\[\], .]+\s+\w*(?:[A-Z]{3}|[A-Z]{2}(?=\s*\())\w*\s*\(' "$work")" \
        "name holds an abbreviation in capitals; write it as Xml, not XML"
    report "$f" "$(grep -n -P '^\s*(?:(?:public|protected|private|static|final|abstract)\s+)*(?:class|interface|enum|record)\s+\w*[A-Z]{3}' "$work")" \
        "type name holds an abbreviation in capitals; write it as Xml, not XML"

    # A brace opens after a space: someMethod() { and not someMethod(){
    report "$f" "$(grep -n -P '\)\{|\belse\{|\btry\{|\bdo\{' "$work")"         "put a space before the opening brace"

    # The body of a method or a control statement goes on its own lines.
    report "$f" "$(grep -n -P '\)\s*\{.*;.*\}' "$work")"         "body is on the same line as the declaration; put it between the braces on its own lines"

    # A binary operator is surrounded by spaces. Only the operators that cannot
    # be confused with a unary sign or with generics are measured.
    report "$f" "$(grep -n -P '[^\s+]\+[^\s+=]|[^\s]&&|&&[^\s]|[^\s]\|\||\|\|[^\s]|[^\s!<>=]==|==[^\s]' "$work")"         "put spaces around the operator"

    # A method is named for what it does, so its name starts with a verb:
    # printDescription, not printingDescription.
    #
    # Only the first word of a declared method name is judged. Anything looser
    # fires on ordinary English inside a longer name, such as StringBuilder,
    # LeadingZeros or afterRemovingTheSamePosition, all of which are correct.
    # The listed words are ordinary verbs and nouns that end in ing.
    report "$f" "$(grep -n -P '^\s*(?:public|protected|private)\s+[\w<>\[\], .]+\s+(?!(?:ping|bring|string|sing|ring|swing|wing|cling|fling|spring|sling|king)[A-Z_(])[a-z]+ing(?:[A-Z_]|(?=\s*\())\w*\s*\(' "$work")" \
        "method name reads as a participle; name it for the action, such as printDescription"

    # A line that continues the statement above it is indented 8 spaces further
    # than the line that started the statement. A statement ends at ; { } or :
    # so the next line starts a new one. Blank lines, comments and annotations
    # break the chain, because none of them continues a statement.
    report "$f" "$(awk '
        BEGIN { stmt = -1 }
        {
            s = $0
            sub(/^[ \t]*/, "", s)
            sub(/[ \t]*$/, "", s)
            if (s == "" || s ~ /^@/) { stmt = -1; next }
            indent = match($0, /[^ ]/) - 1
            # A line that only closes what an earlier line opened may sit at the
            # indent of the statement it closes. That is normal Java layout, so
            # it is not measured.
            closer = (s ~ /^[)}\]]/)
            if (stmt >= 0 && !closer && indent != stmt + 8) {
                printf "%d:%s\n", NR, s
            }
            last = substr(s, length(s), 1)
            if (last == ";" || last == "{" || last == "}" || last == ":") {
                stmt = -1
            } else if (stmt < 0) {
                stmt = indent
            }
        }' "$work")" "wrapped line: indent it 8 spaces past the line that starts the statement"

    # Every @param, @return and @throws description ends with a period. A
    # description can run over several lines, so the period belongs at the end of
    # the last line of the block, not at the end of the tag line. A block ends at
    # the next tag, at a blank comment line, or at the end of the Javadoc.
    # This check needs the comments, so it reads the original file.
    report "$f" "$(awk '
        function flush() {
            if (num > 0 && text !~ /[.!?][ \t]*$/) { printf "%d:%s\n", num, text }
            num = 0
        }
        {
            s = $0
            if (s ~ /^[ \t]*\*[ \t]*@(param|return|throws)([ \t]|$)/) { flush(); num = NR; text = s; next }
            if (num > 0) {
                if (s ~ /^[ \t]*\*\//)      { flush(); next }   # closes the Javadoc
                if (s ~ /^[ \t]*\*[ \t]*$/) { flush(); next }   # blank comment line
                if (s ~ /^[ \t]*\*/)        { num = NR; text = s; next }
                flush()
            }
        }
        END { flush() }' "$f")" "Javadoc tag description does not end with a period"

    # The standard asks for American spelling. Only the words this project has
    # actually used, plus the usual suspects, are listed: a pattern loose enough
    # to catch every -ise verb also catches raise, wise and otherwise.
    # A user-facing string may keep its British spelling, so read each hit.
    report "$f" "$(grep -n -i -E 'capitalis|realis|recognis|organis|apologis|summaris|prioritis|customis|normalis|specialis|utilis|minimis|maximis|initialis|serialis|analys|paralys|colour|behaviour|favour|neighbour|honour|centre|defence|licence|catalogue|dialogue|cancelled|travelled|labelled|modelling' "$f")" \
        "British spelling; the standard asks for American spelling in comments (a user-facing string is exempt)"

    # A boolean sounds like a question: isMarked, hasData, wasFound.
    report "$f" "$(grep -n -P '^\s*(?:(?:public|protected|private|static|final|volatile|transient)\s+)+boolean\s+(?!(?:is|has|was|can|should|does|will|must|are)[A-Z_])\w+\s*[=;(]' "$work")" \
        "boolean name with no is/has/was prefix"

    # A public class or public method needs a header comment. The line above the
    # declaration must close a Javadoc block, or must carry an annotation such as
    # @Override. This check needs the comments, so it reads the original file.
    report "$f" "$(awk '
        {
            line = $0
            sub(/^[ \t]+/, "", line)
            if (line ~ /^public[ \t]/ && line !~ /^public[ \t]+(static[ \t]+)?final[ \t]/ && line ~ /[({]/) {
                if (prev !~ /\*\/$/ && prev !~ /^@/) {
                    printf "%d:%s\n", NR, line
                }
            }
            if (line != "") { prev = line }
        }' "$f")" "public class or method with no Javadoc above it"
done

if [ "$found" -eq 0 ]; then
    echo "No suspects found. Read the diff for the rules that a pattern cannot check."
fi
exit "$found"
