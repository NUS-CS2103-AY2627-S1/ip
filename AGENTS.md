# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

- Prior knowledge: Basic Java and OOP concepts.
- Level of programming experience: Advanced
- IDE and level of expertise: Advanced

# Guidance for interacting with users

- Explain the rationale for significant actions: what you did and why.
- Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:
  - When suggesting a Git command, briefly explain what it does.
  - Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  - Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  - When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

For every Java creation, edit, formatting pass, or code review in this project,
invoke and follow the project-specific `seedu-java-coding-standard` skill at
`.claude/skills/seedu-java-coding-standard/SKILL.md`. Treat its SE-EDU basic +
intermediate rules as mandatory for all Java code in this repository.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Testing after code changes

After every change to the code under `src/`, before reporting the work as done:

1. **Update `test/ui-test-plan.md` if the change affects the console UI.** Add a
   test case for new behaviour (new command, new output format), and revise the
   expected output of existing cases whose output the change deliberately alters.
   The plan is the single source of truth for expected UI behaviour, so it is
   updated *before* the tests are run, not after seeing what the program prints.
   Never rewrite an expected output just to make a failing case pass — a
   mismatch is a result to report, and the expected output only changes when the
   new behaviour is the intended one.
2. **Invoke the `test-ui` skill** to run the whole plan against the changed code,
   and report the outcome. If a case fails, say which behaviour is wrong and
   where in the source it comes from rather than silently moving on.

Skip both steps only for changes that cannot affect the console UI (for example
comments or Javadoc alone), and say so when skipping.

## Git

For every commit-message proposal, commit review, commit creation, or branch-name
proposal or creation in this project, invoke and follow the project-specific
`seedu-git-standard` skill at
`.claude/skills/seedu-git-standard/SKILL.md`. Treat its SE-EDU Git conventions as
mandatory for all commits and branches in this repository.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
