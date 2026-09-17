---
name: cs2103-ip-increments
description: Implement and verify CS2103 individual-project increments using the course Java, Git, branch, tag, naming, and commit conventions. Use for scheduled iP increments; do not use for unrelated Java repositories.
---

# CS2103 iP increments

Implement course increments as traceable, independently verifiable units while preserving the student's existing work.

## Establish the requirements

- Read the applicable page on the current semester's CS2103 website before changing code; schedules and increment details can change.
- Follow links to the named increment, official SE-EDU tutorial, Java coding standard, and Git conventions when relevant.
- Inspect `git status`, branches, tags, recent graph, build configuration, and project instructions. Do not rewrite earlier history or overwrite unrelated work.
- Use the Java version required by the repository. For this project, run build and application tasks with Java 25.

## Implement an increment branch

When the schedule says to add an increment as a branch:

1. Start from the latest local `master` and create the exact branch name `branch-<Increment>`, preserving the increment's capitalization and punctuation.
2. Implement only that increment on its branch. Prefer the simplest course-supported approach, especially when an official tutorial supplies build configuration.
3. Apply the SE-EDU basic + intermediate Java coding standard. Use lower-case packages, PascalCase noun type names, camelCase variable names and verb method names, SCREAMING_SNAKE_CASE constants, four-space indentation, K&R braces, explicit imports, and a 120-character hard line limit.
4. Add Javadoc to classes and public non-test methods unless the standard permits omission. Document non-obvious private behavior where it improves understanding.
5. Run the relevant Gradle tests and static checks using Java 25. Fix genuine failures before recording the increment.
6. Create focused commits. Write imperative, capitalized subjects without a trailing period; aim for 50 characters and never exceed 72. For non-trivial changes, add a wrapped body explaining what and why.
7. Switch to `master`, merge with `--no-ff` so a merge commit exists, and create the exact increment name as a lightweight tag on that merge commit.
8. Keep the increment branch after merging. Verify that the branch tip is an ancestor of `master` and that the tag points to the merge commit.

Do not push unless the user explicitly asks. When pushing is authorized, push `master`, every required increment branch, and every required tag; pushing `master` alone does not publish the other refs.

## Naming outside prescribed increments

Course-prescribed branch and tag names take precedence. For ordinary branches, use meaningful kebab-case keywords; use `issueNumber-keywords` when tied to an issue. Keep identifiers and comments in English.

## Handoff

Report the implementation outcome, checks run, exact branch/tag graph, any tasks that require the student's own account or judgment, and whether refs remain local. Link the current-semester course and SE-EDU pages used.
