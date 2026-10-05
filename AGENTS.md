# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: IntelliJ IDEA, beginner

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

### Java coding standard

All Java source and test code in this repository must follow the project-specific
`.codex/skills/seedu-java-coding-standard/SKILL.md`, based on the
[SE-EDU intermediate Java conventions](https://se-education.org/guides/conventions/java/intermediate.html).
Review changed Java files against that skill before finishing any code change.

### UI testing

After every code update, review and update `test/ui-test-plan.md` when the console behavior or test coverage changes, then invoke the project-specific `test-ui` skill. The skill must run all listed cases in order, show each console input/output transcript, and stop immediately on the first failure.

### JUnit testing

Keep JUnit tests under `src/test/java` and target approximately the top 50% of test-worthy methods, prioritizing core business logic, validation, state changes, persistence, and other behavior with meaningful branches or user impact. After each code change, review and update the relevant JUnit tests so that they continue to cover this target.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

All future commits and branch names in this repository must follow
`.codex/skills/seedu-git-standard/SKILL.md`, based on the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).
Review every commit subject, body, and branch name against that skill before
creating it.

Before creating any commit, verify the exact commit message rather than relying
on visual wrapping in an editor. Every subject and body line must be at most 72
characters, and the subject should aim for 50 characters. This verification is
mandatory for all commits, including merges. Do not create the commit until the
message passes this check.

When merging a completed branch into `master`, use `git merge --no-ff` so the
merge produces a dedicated commit that can be tagged. Do not fast-forward
completed-branch merges.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
