---
name: code-quality-self-check
description: Run the manual code-quality checks that Checkstyle does not cover, before handing any Java or Git change back to the user.
---

# Code quality self-check

The course runs an automated script over this repository and reports style
problems back to the student. Checkstyle (`./gradlew checkstyleMain
checkstyleTest`) already catches some of them, but several aspects are checked
only by the course script or by a human marker. This skill lists the gaps and
how to verify each one.

Run this check after the code change is complete and the tests pass, but before
reporting the change as done.

## What Checkstyle already covers

`config/checkstyle/checkstyle.xml` enforces tab characters, line length, brace
usage, member/method/parameter naming, modifier order, import order,
declaration order, and Javadoc presence on types and methods. Running the
Gradle checkstyle tasks is enough for these.

```
./gradlew checkstyleMain checkstyleTest
```

## What you must check by hand

### 1. Boolean names must read as predicates

Checkstyle only validates the casing of a name, not its meaning. Every boolean
variable, field, parameter, and method must start with a predicate prefix such
as `is`, `has`, `can`, `should`, or `was`.

```
grep -rn "boolean " src/main/java src/test/java | grep -vE "boolean (is|has|can|should|was)[A-Z]"
```

The second `grep` drops the names that already follow the rule, so anything it
prints needs a look. `boolean escaped` is wrong; `boolean isEscaped` is right.

One legitimate exception survives the filter: a method that performs an action
and returns whether it succeeded keeps its verb name, the way `TaskStorage.save`
and the JDK's `File.delete` do. A predicate prefix would misdescribe it, because
calling it changes something. The rule binds every boolean that answers a
question rather than doing a job.

### 2. Class size

A class much beyond roughly 300 lines is usually doing more than one job.

```
wc -l src/main/java/sevensix/*.java | sort -rn | head
```

When a class is oversized, look for groups of methods that share a single
responsibility and could move to their own class. In this project the natural
seams are: console input and output, command parsing, individual command
behaviour, and persistence. Propose the split to the user rather than
performing a large refactor unasked, and explain which responsibility is moving
and why.

### 3. Method length

A method that does not fit on one screen, roughly 30 lines, is a candidate for
extraction. Prefer extracting a named helper whose name states the step, which
also keeps every statement in the method at the same level of abstraction.

### 4. Single level of abstraction

No command finds this one; it is a reading check. For each method you added or
changed, read it top to bottom and ask whether every statement sits at the same
level of abstraction. If the reader has to switch between "what is happening"
and "how it happens" partway down, the method mixes levels.

The common symptom in this repository is a loop or a string-building block
sitting next to a call to a well-named helper. Extract the low-level fragment
into a method named for the step it performs, so the caller reads as a sequence
of steps.

The `seedu-java-coding-standard` skill has the full rule and a worked example.
Check the method you changed and its immediate caller, since extracting a step
often leaves the caller mixing levels instead.

### 5. Dead code

Commented-out code and unused private members must be deleted rather than left
behind. Version control preserves the history, so there is no reason to keep a
disabled copy in the file.

```
grep -rn "^\s*//\s*[A-Za-z_].*[;{)]\s*$" src/main/java src/test/java
```

Per `AGENTS.md`, only remove dead code that your own change created. Report any
pre-existing dead code to the user instead of deleting it.

### 6. Header comments

Every class needs a Javadoc header saying what it is responsible for, and every
nontrivial method needs one saying what it does. Checkstyle checks that the
comment exists; you must check that it is informative rather than a restatement
of the method name.

### 7. Binary files

Build output and binaries must never be committed.

```
git status --porcelain
```

Confirm nothing under `build/`, `out/`, `.gradle/`, or any `.class`, `.jar`, or
image file is staged unless the user asked for it.

## Commit messages

Commit message wrapping is the most frequently reported problem in this
repository. Before committing, write the message to a file and verify it:

```
awk 'length > 72 {print FILENAME ":" NR ": " length " chars"}' /tmp/commitmsg.txt
```

The subject line must be at or below 72 characters, and every body line must be
at or below 72 characters. See the `seedu-git-standard` skill for the rest of
the format. Commit with `git commit -F /tmp/commitmsg.txt` so that the wrapping
you verified is the wrapping that is stored.
