# Week 5 iP Increments Design

## Goal

Complete the Week 5 iP work that is still missing while preserving the Git and
GitHub evidence required by the course dashboard. The implementation will add
Java assertions, improve code quality, exercise Java streams, and add the
`D-Notes` extension.

## Authoritative Requirements

The work follows these course sources:

- Week 5 iP instructions:
  <https://nus-cs2103-ay2627-s1.github.io/website/admin/ip-w5.html>
- Project Duke increment definitions:
  <https://nus-cs2103-ay2627-s1.github.io/website/projectDuke/index.html>
- Java coding standard:
  <https://se-education.org/guides/conventions/java/basic.html>
- Git conventions:
  <https://se-education.org/guides/conventions/git.html>

The exact branch and tag names specified by the course will be used. Tags will
be lightweight tags. Pull requests will be created within the student's fork
and merged with merge commits.

## Increment Design

### A-Assertions

Add Java language assertions to document internal assumptions that indicate a
programming error if violated. Suitable invariants include a `TaskList` never
receiving a null backing collection or null task, and a parsed task index being
valid before `Habi` accesses the collection.

Assertions will not validate commands, file contents, or other user-controlled
data. Those cases will continue to use `HabiException`, because Java assertions
can be disabled at runtime.

JUnit tests will run with assertions enabled and will verify representative
invariants by expecting `AssertionError` for programmer misuse.

### A-CodeQuality

Refactor command parsing without changing external behavior. `Parser` repeats
command keywords, separators such as `/by`, `/from`, and `/to`, and arithmetic
based on their string lengths. Replace those magic literals and duplicated
substring operations with named constants and focused helper methods.

Existing parser tests will act as regression tests. Additional tests will cover
any extracted behavior that is not already protected.

### A-Streams

The project already uses streams in search and save operations. To produce a
separate Week 5 increment and parallel pull request, rewrite task loading as a
clear stream pipeline that filters blank lines and reconstructs tasks in file
order. The public behavior and storage format remain unchanged.

### D-Notes

Add a `Note` entity that is separate from `Task`. A note has text but no done
status, deadline, or event period.

The supported commands are:

- `note DESCRIPTION` adds and persists a note.
- `notes` lists saved notes in insertion order with one-based numbering.
- `delete-note INDEX` removes and persists the selected note.

An empty note description, missing index, malformed index, or out-of-range
index produces a `HabiException` message consistent with existing commands.

Notes share the existing human-editable data file. A stored note uses a new
record type beginning with `N`, while existing `T`, `D`, and `E` records remain
backward compatible. A small data container returned by `Storage` separates
loaded tasks and notes. Saving writes both collections without changing their
relative order within each collection.

The GUI needs no dedicated layout changes because it already sends commands to
`Habi.getResponse` and displays the returned text.

## Git and GitHub Workflow

1. Create `branch-A-Assertions`, `branch-A-CodeQuality`, and
   `branch-A-Streams` from the same `master` commit.
2. Implement and test one increment on each branch. Each non-trivial commit has
   a subject in imperative mood and a body explaining what and why, wrapped at
   72 characters.
3. Push all three branches and open pull requests from those branches to
   `master` within `JiayiZhai/ip`.
4. Merge the assertions pull request using **Create a merge commit**, pull the
   updated remote `master`, and add the lightweight `A-Assertions` tag to the
   merge commit.
5. Merge the new `master` into each remaining increment branch, test, and push
   the synchronized branches again.
6. Repeat the merge-and-synchronize process for code quality and streams, using
   the `A-CodeQuality` and `A-Streams` tags.
7. Create `branch-D-Notes` from the resulting `master`, implement the extension,
   test it, push it, and merge its pull request with a merge commit. Add the
   lightweight `BCD-Extension` tag to that merge commit.
8. Add `A-FullCommitMessage` to one of the Week 5 implementation commits. At
   least three pushed commits will contain compliant message bodies.
9. Push all tags and all post-merge branch states. Confirm every increment
   branch is fully merged into `origin/master`.

## Verification

Use Java 25 (`25.0.3.fx-zulu`) for all Gradle work. For each increment, run the
focused JUnit test first and then the full test suite. Before finalizing, run:

- all JUnit tests;
- Checkstyle, if configured by the project;
- the project UI tests using `test/ui-test-plan.md` and the `test-ui` skill;
- the Shadow JAR build and a size check confirming the JavaFX dependencies are
  bundled;
- Git ancestry checks for every required branch and tag.

The UI test plan will be extended for `note`, `notes`, and `delete-note` before
claiming their console behavior is correct.

## Out of Scope

`A-CI` is an optional increment and is not one of the overdue dashboard items,
so it is excluded from this catch-up scope. Week 6 enhancements, the published
user guide, `Ui.png`, and the final GitHub release are separate follow-up work.
