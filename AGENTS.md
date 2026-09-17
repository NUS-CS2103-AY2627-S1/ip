# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Moderate to high, you are working with the same student who has done your past projects
* IDE and level of expertise: IntelliJ IDEA

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Java coding standard

For every Java creation, edit, refactor, or review, read and follow the project-specific `$seedu-java-coding-standard` skill at `.codex/skills/seedu-java-coding-standard/SKILL.md`. This is mandatory for production and test code.

## Git

Before naming a branch or proposing, reviewing, or creating a commit, read and follow the project-specific `$seedu-git-standard` skill at `.codex/skills/seedu-git-standard/SKILL.md`. This is mandatory for all future commits.
Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Exceptions and Errors
Always throw a new CrystalException with the message that the user should see as the parameter, and print .getUserMessage() unless otherwise stated.

## JUnit unit testing

After every application code update:

1. Review the corresponding JUnit tests under `src/test/java` and update them to cover every changed nontrivial public behavior and all reasonable normal, boundary, and error cases.
2. Follow Gradle and JUnit conventions: mirror the production package beneath `src/test/java` and name a test for `Example` as `ExampleTest`.
3. Select Java 25 and run `./gradlew test` from the project root.
4. Stop at the first failing unit test. Report the failure and do not claim the code update is complete until it is resolved or the user directs otherwise.

## Console UI regression testing

After every application code update:

1. Review `test/ui-test-plan.md` and update its test cases or expected output when observable console behavior changes or new behavior needs coverage.
2. Invoke the project-specific `$test-ui` skill at `.codex/skills/test-ui/SKILL.md` before reporting completion.
3. Stop at the first failing UI test. Report the actual and expected output and do not claim the code update is complete until the failure is resolved or the user directs otherwise.
