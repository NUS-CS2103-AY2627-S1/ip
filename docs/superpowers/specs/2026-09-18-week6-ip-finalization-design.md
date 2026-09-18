# Week 6 iP Finalization Design

## Goal

Complete the Week 6 iP tasks listed on the course project page, retaining the
existing HABI application and its prior increment history.

## Scope

Execute the course tasks in this order:

1. Preserve the already-completed `A-BetterGui` and `A-Personality` increments,
   then complete `A-MoreErrorHandling` and `A-MoreTesting`.
2. Finalize HABI's common-error handling while retaining its existing product
   name and command language.
3. Replace the placeholder user guide in `docs/README.md` and add a genuine
   full-window `docs/Ui.png` screenshot.
4. Enable GitHub Pages from the `master` branch's `docs` directory and verify
   the published guide.
5. Build a Java 25 fat JAR, smoke-test it from an empty directory, create a
   public GitHub release, and upload only that JAR as its asset.

## Delivery Rules

- Work through the tasks one at a time, using the smallest change that meets
  the stated course requirement.
- Use a separate `branch-<increment>` branch for each named increment. For
  each, add focused tests before the implementation when behavior changes,
  verify it, commit it, push it, open and merge a pull request, and create the
  matching lightweight tag on the merge commit.
- Preserve existing commands and the human-editable data format unless a
  course requirement needs a compatible validation improvement.
- Reject arguments for `list`, `notes`, and `bye`; require exactly one
  deadline/event separator of each required kind; and reject tabs in user
  text because tabs delimit stored fields.
- Treat malformed persisted records as a failed load, report the failure in
  both UIs, and prevent mutations from overwriting the unreadable data file.
- Cover task entities, collections, parsing, storage, and command responses
  with focused JUnit tests, including valid boundaries and invalid inputs.
- Update the console UI test plan whenever console text or behavior changes;
  run the project UI-test workflow before treating that behavior as verified.
- Use Java 25 for every Gradle build and test.

## Verification

For each increment, run focused JUnit tests followed by the full Gradle test
suite and Checkstyle. Before release, run the complete console UI plan,
`clean shadowJar`, JAR packaging inspection, and a launch smoke test from a
fresh empty directory. Confirm the final Pages URL and GitHub release are
publicly reachable.

## Out of Scope

No command-language redesign, unrelated new features, broad refactoring, or
artificial screenshot/test evidence will be added. The screenshot will be
captured from one actual HABI JavaFX window.
