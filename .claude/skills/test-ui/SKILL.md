---
name: test-ui
description: Run the text UI tests for AlfredTheButler. Compiles the project, replays each test case from test/ui-test-plan.md through a fresh run of the program, and compares the console output against the expected output, stopping at the first failure. Use when asked to test the UI, run the UI tests, run the text-ui tests, check the chatbot output, or verify that a command still prints the right thing. Also use after changing anything that affects console output.
---

# test-ui

Runs the test cases in `test/ui-test-plan.md` against the real program and
reports the console session.

## The contract with the test plan

`test/ui-test-plan.md` holds every test case. Each case is a `## TC<n>: <title>`
heading, a prose **Aim**, then fenced code blocks, each introduced by a bold
label naming what it is. The label decides what the block means, so blocks are
never counted or identified by position.

Every case has an **Expected output** block — the entire console output of the
case, from the greeting banner to the last farewell message — and one of:

* **Input**, for a one-run case: the lines typed into the program, one command
  per line.
* **First input** and **Second input**, for a two-run case: two runs, the second
  started after the first has exited, against the save file the first left
  behind. Its expected output holds both runs end to end, so the second greeting
  appears in the middle of the block.

Any case may also carry a **Save file** block, whose contents become
`data/alfred.txt` before the case runs. That is how a case tests what the
program does with a file it did not write.

Each case starts from a **fresh program**, and from an empty save file unless it
supplied one, so cases never share task state. Within a two-run case the save
file is _not_ cleared between the two runs — carrying it over is the whole point
of the case.

Read the plan's own "Rules for writing a test case" section before adding cases.

## Procedure

Use the Bash tool (Git Bash) for all of the steps below. Set up the paths once,
in the same command as the compile, since shell state does not persist between
calls:

```bash
WORK="$TMPDIR/test-ui"          # or any scratch dir outside the repo
rm -rf "$WORK" && mkdir -p "$WORK/build"
```

### 1. Compile

```bash
CLASSPATH=$(./gradlew --quiet printClasspath | tail -1)
javac -cp "$CLASSPATH" -d "$WORK/build" $(find src/main/java -name '*.java')
```

The sources sit in packages under `src/main/java`, so `find` collects the file
list. A `src/main/java/*.java` glob matches only the top level, which now holds
no sources at all, so it would compile nothing.

Some of those sources are the JavaFX window in `alfred.gui`, which does not
compile without the JavaFX jars. Gradle downloaded them, and the
`printClasspath` task in `build.gradle` prints where they are, so `javac` gets
the same classpath the Gradle build uses. Compile every source, the window
included: a text UI test that skipped part of the program would pass while the
program failed to build.

Running a case needs no such classpath, because the text version of the program
uses nothing from JavaFX.

If compilation fails, stop. Report the compiler errors and do not run any case —
a failure to build is not a test failure, and running stale class files would
report the wrong result.

### 2. Extract the cases from the plan

Pull each case's fenced blocks straight out of the Markdown, rather than
retyping them. Copying by hand risks changing a space and testing the wrong
thing.

Each block is routed by the **bold label above it**, not by its position, so
that a case can leave a block out or add one without shifting the rest:

```bash
awk -v out="$WORK" '
  /^## TC/ { tc++; kind = ""; next }
  tc == 0  { next }
  !infence && /^\*\*[A-Za-z ]+:\*\*/ {
      kind = $0
      sub(/^\*\*/, "", kind); sub(/:\*\*.*$/, "", kind)
      gsub(/ /, "-", kind); kind = tolower(kind)
      next
  }
  /^```/ { infence = !infence; if (infence) file = out "/tc" tc "." kind; next }
  infence { print > file }
' test/ui-test-plan.md
```

That gives one file per labeled block:

| Label in the plan | File | Meaning |
| --- | --- | --- |
| `**Input:**` | `tc<n>.input` | the only run's input |
| `**First input:**` | `tc<n>.first-input` | first run of a two-run case |
| `**Second input:**` | `tc<n>.second-input` | second run of a two-run case |
| `**Save file:**` | `tc<n>.save-file` | seeds `data/alfred.txt` before the run |
| `**Expected output:**` | `tc<n>.expected-output` | what every run printed, end to end |

`**Aim:**` is a label too, but no fence follows it, so it routes nothing.

```bash
if [ -f "$WORK/tc$n.first-input" ]; then
  inputs="$WORK/tc$n.first-input $WORK/tc$n.second-input"
else
  inputs="$WORK/tc$n.input"
fi
expected="$WORK/tc$n.expected-output"
```

Before running anything, check that every case produced an `expected-output` and
at least one input. A case that did not is mislabeled, and the plan should be
fixed first.

### 3. Run the cases, in the order they appear in the plan

Run them in a loop that **stops at the first failure** and prints a per-case
pass/fail line, so the failing case is always identifiable. Never run them in a
way that swallows individual results or keeps going after a failure.

1. **Remove the whole `data` folder**, so the case starts with an empty task
   list — or seed the file, if the case supplied a `**Save file:**` block:

   ```bash
   rm -rf data
   if [ -f "$WORK/tc$n.save-file" ]; then
     mkdir -p data && cp "$WORK/tc$n.save-file" data/alfred.txt
   fi
   ```

   This matters as much as compiling. The program reads `data/alfred.txt` at
   startup, so without it the tasks from one case reappear in the next, and
   every case after the first compares against the wrong list. Do this once per
   **case**, never between the two runs of a two-run case.

   The **folder** goes, not just the file, so that every unseeded case starts
   from what a fresh copy of the project on someone else's computer looks like:
   no save file and no folder to put one in. That path is required to work, and
   deleting only the file would leave it untested.

   A `**Save file:**` block is how a case tests what the program does with a
   file it did not write — a hand-edited or damaged one. It is the only way to
   reach that code, since the program never saves a line it cannot read back.
   `rm -rf` rather than `rm -f` because an earlier manual test may have left a
   directory of that name behind.
1. Run the program once per input block, appending each run's output to the
   same actual-output file so a two-run case is compared as one session:

   ```bash
   : > "$WORK/tc1.actual"
   r=0
   for in in $inputs; do
     r=$((r + 1))
     java -ea -cp "$WORK/build" alfred.AlfredTheButler < "$in" > "$WORK/tc1.r$r.actual" 2>&1
     cat "$WORK/tc1.r$r.actual" >> "$WORK/tc1.actual"
   done
   ```

   Each run's output is kept on its own as well as appended, because the
   transcript in step 5 has to be built per run.

   Give each run a short timeout (10s is plenty). If it times out, treat it as a
   failure: it usually means the input did not end with `bye`, so the program is
   still waiting for a command that will never come.

   `-ea` turns on the assertions in the program, which `java` leaves off unless
   you ask for them. The assertions state what the code takes for granted, so a
   run without them tests less than it looks like it does: a broken assumption
   surfaces as a wrong reply further on, or not at all. With them on, an
   `AssertionError` and its stack trace land in the output, and the case fails
   where the assumption broke.
1. Normalize both files and diff them. Normalizing strips carriage returns,
   trailing whitespace, and blank lines at the end of the file — differences
   that come from the platform, not from the program:

   ```bash
   norm() {
     sed -e 's/\r$//' -e 's/[[:space:]]*$//' "$1" |
       awk '{ line[n++] = $0 }
            END { last = n - 1
                  while (last >= 0 && line[last] == "") last--
                  for (i = 0; i <= last; i++) print line[i] }'
   }
   norm "$expected"        > "$WORK/tc1.expected.norm"
   norm "$WORK/tc1.actual" > "$WORK/tc1.actual.norm"
   diff -u "$WORK/tc1.expected.norm" "$WORK/tc1.actual.norm"
   ```

   `diff` exits 0 when the files match. Any other exit status is a failure.
1. If the case passed, move on to the next one. If it failed, go to
   "Reporting a failure" below and stop.

Putting those together:

```bash
for n in $(seq 1 "$CASES"); do
  if [ -f "$WORK/tc$n.first-input" ]; then
    inputs="$WORK/tc$n.first-input $WORK/tc$n.second-input"
  else
    inputs="$WORK/tc$n.input"
  fi
  expected="$WORK/tc$n.expected-output"
  rm -rf data
  [ -f "$WORK/tc$n.save-file" ] && { mkdir -p data; cp "$WORK/tc$n.save-file" data/alfred.txt; }
  : > "$WORK/tc$n.actual"
  r=0
  for in in $inputs; do
    r=$((r + 1))
    timeout 10 java -ea -cp "$WORK/build" alfred.AlfredTheButler < "$in" > "$WORK/tc$n.r$r.actual" 2>&1
    cat "$WORK/tc$n.r$r.actual" >> "$WORK/tc$n.actual"
  done
  norm "$expected"          > "$WORK/tc$n.e.norm"
  norm "$WORK/tc$n.actual"  > "$WORK/tc$n.a.norm"
  if diff -u "$WORK/tc$n.e.norm" "$WORK/tc$n.a.norm" > "$WORK/tc$n.diff"; then
    echo "TC$n PASS"
  else
    echo "TC$n FAIL"; cat "$WORK/tc$n.diff"; break
  fi
done
```

### 4. Reporting a failure

Terminate the session immediately — do not run the remaining cases, and do not
try to fix the code unless asked. Report, in this order:

* which case failed (number, title, and its aim),
* the exact input that was fed to the program,
* the **expected** output, in full,
* the **actual** output, in full,
* the `diff -u` output, which points at the first differing line,
* a one-line reading of what the difference suggests (for example: an extra
  blank line, a changed word, an exception stack trace where a reply was
  expected).

Then say clearly that the remaining cases were not run.

Do not "fix" the plan to match the program. A mismatch means either the program
or the expected output is wrong, and deciding which is the user's call.

### 5. Reporting success

When every case passes, show the console session so it can be read like a real
run. Piping input means the commands are not echoed, so put them back: every
reply is wrapped in a pair of divider lines, so after each _closing_ divider
insert the next command, prefixed with `> ` as if it had been typed at the
prompt. Blocks pair up 1:1 with input lines after the greeting.

A block is a response to a command, _except_ for the blocks the program prints
before it reads anything: the greeting, and the startup messages it prints when
it has restored tasks, when it has skipped damaged lines, and when it could not
read the save file at all. Those have to be passed over, or every command lands
one block early. There can be more than one of them, so they are counted rather
than recognized by their wording:

```bash
transcript() {   # $1 = actual output file, $2 = input file
  blocks=$(($(grep -cE '^ *_{20,}$' "$1") / 2))
  commands=$(awk 'END { print NR }' "$2")

  # Every command is answered by exactly one block, and the greeting is one
  # more. Whatever is left over was printed before the first command was read.
  extra=$((blocks - commands - 1))
  if [ "$extra" -lt 0 ]; then
    echo "transcript: $blocks blocks answer $commands commands; not interleaving" >&2
    cat "$1"
    return 1
  fi

  awk -v inputs="$2" -v extra="$extra" '
    { print }
    /^ *_{20,}$/ {
        if (++dividers % 2 == 0) {
            if (passed < extra) { passed++ }
            else if ((getline cmd < inputs) > 0) print "> " cmd
        }
    }
  ' "$1"
}
transcript "$WORK/tc1.r1.actual" "$WORK/tc1.input"
```

Counting is what keeps this working when a message is reworded. An earlier
version looked for the startup messages by their text and allowed for only one
of them, so TC18 — which restores tasks _and_ reports damaged lines — came out
a block early, with `> bye` printed in the middle of a reply. Arithmetic needs
no list of wordings to keep up to date, and a run whose blocks and commands
cannot be reconciled now says so and prints the raw output instead of a
plausible wrong one.

**For a two-run case, transcribe each run separately** against its own input
file, and show the two one after the other with a line saying the program was
restarted in between:

```bash
transcript "$WORK/tc1.r1.actual" "$WORK/tc1.first-input"
echo "--- program restarted, same save file ---"
transcript "$WORK/tc1.r2.actual" "$WORK/tc1.second-input"
```

Do **not** run the combined output against the two input files concatenated.
The second run prints its own greeting, which is a divider pair with no command
in front of it, so every command from that point on is inserted one block early.
The result looks plausible and is wrong, which is worse than not interleaving at
all.

Counting divider lines rather than splitting on blank lines matters: the
greeting banner has a blank line inside it, so blank-line splitting would run
one command out of step.

Check the result before showing it. The tell that it is right is that the last
`>` line is the case's last command and no commands are left over; the tell that
it is wrong is a `> bye` sitting in the middle with replies after it.

If the interleaving ever comes out wrong — commands left over at the end, or a
`>` line in the middle of a reply — fall back to showing the input list and then
the raw output, and say that the transcript could not be interleaved.

Put each case's transcript in a fenced code block under its heading, and finish
with a one-line summary: how many cases ran and that all passed.

## Adding a test case

When asked to add a case:

1. Append a new `## TC<n>` section to `test/ui-test-plan.md` with its aim and
   Input block.
1. Get the expected output by **running** the program with that input, then
   paste the real output into the Expected output block — after checking line by
   line that the output is actually correct. Writing expected output by hand is
   how typos in indentation end up baked into the plan; running it and reviewing
   it is both faster and more accurate.
1. Re-run the whole plan to confirm nothing else broke.

## When the output changes on purpose

Some changes alter output that many cases share — renaming the chatbot, editing
the banner, fixing the "1 tasks" wording. The greeting is repeated in full in
every expected block, so such a change invalidates all of them at once.

Regenerate those blocks by running each case and pasting in the real output.
Do not hand-edit them: retyping the same banner six times is how one block ends
up with a wrong space that then looks like a genuine test failure. Read the
regenerated output before pasting it, to confirm the change is the one intended
and nothing else moved.

## Notes

* Java 25 is required (`java -version` to check).
* Class files go to a scratch directory, never into the repository.
* `data/` is the one repository path this skill writes to and deletes, because
  the program writes there and it has to be cleared between cases. It is ignored
  by Git, so a run leaves the working tree clean. Whatever the last case saved is
  left behind afterwards.
* This skill only reads and runs; it never edits `src/main/java`.
