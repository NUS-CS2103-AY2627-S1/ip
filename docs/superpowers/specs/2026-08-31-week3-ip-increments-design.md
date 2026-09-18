# Week 3 iP Increments Design

## Goal

Complete every mandatory Week 3 iP increment for HABI in the order and Git
topology required by the course website. Preserve all Week 2 behavior, avoid
optional JavaFX and stretch goals, and publish each completed increment using
the required branch, commit, lightweight tag, and push sequence.

## Sources

- [Week 3 project requirements](https://nus-cs2103-ay2627-s1.github.io/website/schedule/week3/project.html)
- [Level 7 requirement](https://github.com/NUS-CS2103-AY2627-S1/website/blob/master/projectDuke/Level-7-fragment.md)
- [Level 8 requirement](https://github.com/NUS-CS2103-AY2627-S1/website/blob/master/projectDuke/Level-8-fragment.md)
- [Git conventions](https://se-education.org/guides/conventions/git.html)
- [Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)

## Scope

### Included

- `Level-7`: automatic task persistence using a relative, portable file path.
- `Level-8`: parsed and formatted deadline dates using `LocalDate`.
- `A-MoreOOP`: extraction of `Ui`, `Storage`, `Parser`, and `TaskList`.
- `A-Packages`: placement of Java classes in the `habi` package.
- `A-Gradle`: Gradle build and run support based on `add-gradle-support`.
- `A-JUnit`: JUnit tests for at least two non-trivial methods in two classes.
- `A-Jar`: generation of an executable `habi.jar` without committing it.
- `A-JavaDoc`: Javadoc for all non-private classes and methods.
- `A-CodingStandard`: compliance with the specified Java coding standard.
- `Level-9`: keyword search within task descriptions.
- Java 25, automated UI tests, Gradle tests, and executable-JAR verification.
- The exact course branch, merge, lightweight-tag, push, and upstream-PR flow.

### Excluded

- The optional JavaFX head start.
- Corrupted-file recovery beyond reporting the loading error and starting with
  an empty task list.
- Searching by date, reminders, flexible date formats, case-insensitive search,
  command-class hierarchies, or any other stretch goal or additional feature.
- Rewriting or amending commits and tags from earlier weeks.

## User-Visible Behavior

All existing Week 2 commands and responses remain available. Week 3 adds only
the behavior required by `Level-7`, `Level-8`, and `Level-9`.

### Persistence

HABI stores tasks in `data/habi.txt`, resolved relative to the directory from
which the application is run. It creates the `data` directory and file when
they do not exist. It loads stored tasks at startup and saves immediately after
successful commands that change task state: adding, marking, unmarking, and
deleting. Read-only and invalid commands do not rewrite the file.

The storage format is one task per line with the type, completion status,
description, and type-specific fields separated by ` | `. The exact format is
an implementation detail but must round-trip todos, deadlines, events, and
their completion states without data loss.

### Dates

Deadline commands accept dates in `yyyy-MM-dd` format:

```text
deadline return book /by 2026-09-15
```

The `Deadline` object stores the value as `LocalDate` and displays it using an
English `MMM d yyyy` formatter:

```text
[D][ ] return book (by: Sep 15 2026)
```

An invalid date is rejected through the existing `HabiException` error path
without adding a task. Event start and end values remain strings because the
minimal Level 8 requirement applies to deadline dates.

### Find

The new command is:

```text
find KEYWORD
```

It selects tasks whose descriptions contain the supplied keyword using direct,
case-sensitive substring matching. Results use one-based numbering within the
matching result list. An empty keyword is rejected without changing tasks.

## Architecture

The implementation evolves in the prescribed increment order. Before
`A-MoreOOP`, the existing structure receives only the minimum changes needed
for persistence and dates. `A-MoreOOP` then establishes these responsibilities:

- `Habi`: constructs the components and coordinates the command loop.
- `Ui`: reads input and prints greetings, responses, errors, and task lists.
- `Storage`: converts tasks to and from the relative data file.
- `Parser`: validates command syntax and converts command arguments into task
  objects or indexes.
- `TaskList`: owns the task collection and provides add, status update, delete,
  list, and later find operations.
- `Task`, `Todo`, `Deadline`, `Event`, and `TaskType`: model task state and
  user-facing formatting.
- `HabiException`: carries recoverable command, date, and storage errors.

After `A-Packages`, these classes live under `src/main/java/habi` with package
name `habi`. Tests live under `src/test/java/habi`.

## Gradle, JUnit, and JAR

The remote `origin/add-gradle-support` branch is merged as required. Its Gradle
configuration is adjusted only where needed to use Java 25, `habi.Habi` as the
application entry point, JUnit 5, and `habi.jar` as the Shadow JAR filename.

JUnit covers at least two non-trivial methods in two classes, including normal
and invalid cases. Suitable targets are parser date handling and task-list
operations. Existing exact-output UI tests remain the primary regression check
for complete console interactions.

The JAR is built into `build/libs/habi.jar`, copied to an empty temporary
directory, and verified with `java -jar habi.jar`. Generated JAR and build
output remain ignored and uncommitted.

## Error Handling

- A missing data directory or file is created automatically.
- An invalid deadline date produces a user-facing `HabiException` message and
  leaves the task list unchanged.
- A loading or saving I/O failure is reported without exposing a stack trace to
  the user. A loading failure starts HABI with an empty task list.
- Existing validation for malformed commands and indexes remains unchanged.
- `find` with no keyword produces a corrective error response.

## Testing Strategy

Each behavior change follows red-green-refactor:

1. Add a focused automated test and update `test/ui-test-plan.md` whenever the
   console interaction changes or needs a new scenario.
2. Run the test and confirm it fails for the missing behavior.
3. Implement only enough code to satisfy the current increment.
4. Run the project `test-ui` skill and relevant Java or Gradle tests.
5. Refactor only while the full suite remains green.

Final verification uses Java 25 and covers Gradle build, Gradle test, UI tests,
first-run file creation, persistence across two launches, date formatting,
find output, and execution of the generated JAR from an empty directory.

## Git Sequence

Commit subjects use imperative mood, begin with a capital letter, omit a final
period, and stay within 72 characters. Non-trivial commits include a wrapped
body explaining what and why. Tags are lightweight.

1. Create `branch-Level-7`, implement and verify persistence, commit, return to
   `master`, merge with `--no-ff`, tag the merge commit `Level-7`, and push the
   branch, `master`, and tag.
2. Repeat for `branch-Level-8` and tag `Level-8`.
3. Complete `A-MoreOOP`, `A-Packages`, `A-Gradle`, `A-JUnit`, and `A-Jar` in
   order on `master`; after each passes, commit, apply its matching lightweight
   tag, and push `master` plus the tag.
4. From the same post-`A-Jar` commit, create `branch-A-JavaDoc`,
   `branch-A-CodingStandard`, and `branch-Level-9`. Complete and commit each
   branch without first merging either of the others.
5. Merge the three branches into `master` one at a time, resolving conflicts
   without removing required behavior. Tag each corresponding merge commit and
   push `master`, all three retained branches, and all three tags.
6. Create or confirm a public PR from `JiayiZhai/ip:master` to
   `NUS-CS2103-AY2627-S1/ip:master`, titled `[JiayiZhai] iP`.

