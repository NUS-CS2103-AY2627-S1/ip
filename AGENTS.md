# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: IntelliJ and intermediate level of expertise

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25.0.3.fx-zulu is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to this SDK if needed.

## UI testing

Keep `test/ui-test-plan.md` as the source of truth for command-driven UI tests. After any code update that changes the console UI, update the plan if needed and run the `test-ui` skill before reporting completion.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Prepare implementation, tests, documentation, and visual reviews without additional conversational checkpoints.
Ask the user to confirm the completed change set before creating its increment commit.
After confirmation, carry out the already agreed merge, tagging, and push workflow; required tool approvals still apply.
Leave unrelated user edits uncommitted unless the user asks to include them.
Do not push outside the level workflow below unless explicitly asked.

## Level development workflow

When implementing a course level:
* Create `branch-Level-N` from the latest `master` before changing level code.
* Break the level into meaningful, independently verifiable increments rather than one large final commit.
* After each increment passes the relevant tests and the user confirms its commit, create a detailed commit
  and push `branch-Level-N` to the fork.
  Good intervals include a completed model/storage layer, its UI integration, and a stretch feature; avoid commits for
  incomplete or unverified code merely because time has passed.
* Update the root `README.md` with a concise `Level-N` implementation summary during every level. Also update
  `docs/README.md` whenever user-visible commands, formats, setup, or recovery behavior changes.
* Before merging, run all relevant checks with the required Java SDK and commit and push the final documentation
  and test updates to `branch-Level-N`.
* Merge the level branch into `master` with `--no-ff` so there is a merge commit.
* Add the lightweight `Level-N` tag to that merge commit on `master`.
* Push `master`, the level branch, and the `Level-N` tag to the user's fork.
* Keep the level branch after merging; the course checks require the merged branch to remain available.
