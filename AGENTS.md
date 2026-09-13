# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate.
* IDE and level of expertise: VS Code, intermediate.

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

All Java production and test code must follow `skills/seedu-java-coding-standard/SKILL.md`,
which is based on the SE-EDU Java basic and intermediate coding standard. Apply it when
creating, modifying, or reviewing Java code, including package naming, imports, layout,
braces, line length, naming, and JavaDoc.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## UI testing

After every code update:

1. Review `test/ui-test-plan.md` and update it whenever the intended console inputs, outputs, or behaviour have changed.
2. Invoke the project-local `test-ui` skill by following `skills/test-ui/SKILL.md` and running `python skills/test-ui/scripts/run_ui_tests.py`.
3. Do not continue after a UI-test failure. Report the failing test case together with its expected and actual output.

## Weekly iP execution workflow

When the user asks Codex to complete a weekly iP, follow this workflow:

1. Read the current official weekly project page and all relevant dropdowns, panels,
   linked increment instructions, and AI guidance before changing files.
2. Convert the instructions into a checklist that records required and requested
   optional increments, exact branch and tag names, ordering constraints, tests,
   documentation, and any work the student must do personally.
   Treat every branch and tag name specified by the course website as a
   case-sensitive identifier: copy it verbatim, including capitalization,
   punctuation, and hyphens. A website-prescribed name overrides the general Git
   naming convention below. If the website leaves a branch name up to the student,
   follow the general Git naming convention instead.
3. Inspect the repository, remotes, branches, tags, and working tree before starting.
   Preserve unrelated user changes and existing product behavior.
4. Complete increments one at a time. Follow the prescribed Git topology and order
   exactly, including every intermediate branch synchronization and push. Do not
   shortcut a training step merely because the final tree would be equivalent.
5. Preserve Nelson's chess-themed personality and existing user-facing behavior
   unless the increment explicitly requires a change.
6. After each code update, follow the UI-testing requirements above. Before pushing
   an increment, also run the relevant JUnit and Checkstyle checks with Java 25 and
   generate the fat JAR when required.
7. Before merging a PR, wait for all CI checks to complete. Read inline review
   comments, general comments, and reviews after CI settles, then check them again
   immediately before merging so late automated feedback is not missed. Address or
   explicitly evaluate every comment.
8. Push only to the user's confirmed fork through `origin`. Never push or create a
   PR against `upstream` unless the user explicitly requests and confirms it.
   Immediately before each push or PR, compare the local branch name character for
   character with the checklist. Immediately after pushing, verify that `origin`
   contains that exact case-sensitive branch ref and that the PR uses it as its
   head branch.
9. Create each required lightweight tag on the exact prescribed commit and push it
   immediately. Record the corresponding commit hash for the final report.
10. Perform a fresh final audit against the live weekly instructions. Verify the
    local and remote commit graph, PR destinations and merge methods, required tags,
    exact case-sensitive branch and tag refs, review comments, CI results, tests,
    documentation, JAR manifest and size, and a clean synchronized working tree.
11. Do not claim completion until the audit passes. Report any deviation candidly;
    do not rewrite published history unless the user explicitly requests it and the
    risks have been explained.

The user's request to complete a weekly iP authorizes normal implementation edits
and local verification within this repository. Committing, pushing, creating or
merging PRs, and publishing tags still require explicit user authorization. If the
course requires personal authorship, manual review, acknowledgement of AI use, or
another student-only action, pause and leave that action to the user.

## Git

All future branch names and commits must follow
`skills/seedu-git-standard/SKILL.md`, based on the SE-EDU Git
conventions. Before proposing or creating a commit, verify the complete
message line by line. Every physical line, including the subject and
each body line, must contain at most 72 characters.

Prefer a subject of 50 characters or fewer. Write it in the imperative
mood, capitalize its first letter, and omit a final period. Separate the
subject from the body with one blank line. Non-trivial commits must
include a concise body that explains what changed and why. Use blank
lines or bullets to separate distinct points. Reflow the body before
committing rather than relying on a Git client or rendered view to wrap
it visually.

Apply these rules to future commits only. Do not amend, rebase, or
otherwise rewrite published commits solely to correct commit-message
formatting unless the user explicitly requests it after the
history-rewriting risks are explained.

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.
