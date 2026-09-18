# CP3108A Remaining Work Design

## Goal

Complete the remaining CP3108A coursework that is currently actionable while
preserving the commit, branch, tag, pull request, and testing evidence required
by the course.

## Scope

The work consists of four independently verifiable streams:

1. Complete JavaFX Tutorial Parts 1–4 and at least half of Part 5 in a fork of
   `se-edu/javafx-tutorial`.
2. Complete four named optional iP increments: `A-Checkstyle`, `A-CI`,
   `A-MoreErrorHandling`, and `A-MoreTesting`.
3. Confirm exposure to the required basic tools: Gradle, Checkstyle, Codecov,
   Codex, PlantUML, and an IDE.
4. Complete two AB3 tutorials after the W10-3 organization team repository becomes
   available. Until then, record this as an external dependency rather than
   fabricating submission evidence.

## JavaFX Tutorial Repository

Fork the official tutorial starter repository under the student's GitHub
account, `JiayiZhai`.

Use the student's GitHub account and retain the tutorial's prescribed project
structure. Complete each tutorial part as a distinct progression:

- Part 1: configure and launch the starter JavaFX application.
- Part 2: build the basic Duke chat interface.
- Part 3: connect user input to application responses.
- Part 4: move the interface to FXML.
- Part 5: complete at least half of the documented GUI tweaks.

Each completed part receives its own commit history and lightweight tag named
`Tutorial-Part1` through `Tutorial-Part5`. Push both commits and tags to the
student fork. Verify the build after each part and inspect the final GUI.

## iP Increment Workflow

Implement each increment independently from the latest `master` branch:

1. Create the exact course branch `branch-<increment>`.
2. Add tests first for behavior changes and observe the expected failure.
3. Make the smallest implementation that satisfies the increment.
4. Run Java 25 Gradle tests and relevant manual or UI tests.
5. Commit with an imperative subject and a rationale body wrapped at 72
   characters.
6. Push the branch, create a pull request, merge it into `master`, and push the
   fully merged branch again.
7. Add the exact lightweight increment tag to the merge commit and push it.

The four increments are designed as follows:

### A-Checkstyle

Add the course-compatible Checkstyle configuration and Gradle plugin. Run the
checker over production and test code, then fix reported violations without
changing behavior. Verification is `checkstyleMain`, `checkstyleTest`, and the
full test suite.

### A-CI

Add a GitHub Actions Gradle workflow based on the course-recommended Duke
workflow. It must use the repository's Gradle wrapper and a compatible Java 25
runtime, run tests and Checkstyle, and upload useful test results when
appropriate. Verify the workflow syntax locally where possible and confirm the
GitHub Actions run after pushing.

### A-MoreErrorHandling

Strengthen predictable user-facing failures without redesigning the command
language. Priorities are repeated separators, malformed extra arguments,
impossible dates, storage corruption, and inaccessible storage. Tests must
capture each chosen failure before production code changes.

### A-MoreTesting

Expand JUnit coverage across parser, task list, storage, notes, and command
handling code that can be tested without driving JavaFX. Avoid tests that only
repeat implementation details. The completed suite must run cleanly under
Java 25.

## Tool Evidence

The repository already demonstrates Gradle and Codex use. A-Checkstyle and
A-CI provide Checkstyle and GitHub Actions evidence. Add Codecov only if its
course setup can be completed without a missing team or service authorization.
PlantUML familiarity can be demonstrated with a small source diagram if the
course provides no separate tracked exercise. IDE use is a learning activity
without a repository deliverable.

## AB3 Dependency

The two submitted AB3 tutorials require the W10-3 team repository:

- Tutorial 1 requires an issue titled `Tutorial: tracing code` containing two
  or three genuine screenshots from the tracing exercise.
- Tutorial 2 requires a `tutorial-adding-command` branch in the student's fork
  and a pull request to the team repository's `master` branch.

The team organization is not currently available. Recheck it once Week 6 setup
is complete. Do not create substitute screenshots or a PR against an unrelated
repository.

## Verification and Safety

- Preserve unrelated untracked files and existing branches.
- Use Java 25 (`25.0.3.fx-zulu`) for every build and test.
- Update `test/ui-test-plan.md` whenever console behavior changes and run the
  project UI-testing workflow afterward.
- Never rewrite existing iP commit timestamps or published history.
- Confirm all branches, merge commits, tags, remote branch tips, PRs, Gradle
  checks, and GitHub Actions results before reporting completion.
