# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate (Written 1000-5000 LoC)
* IDE and level of expertise: IntelliJ and intermediate.

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

## Coding conventions

Follow the required basic and intermediate rules in the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). Apply the optional [advanced Java rules](https://se-education.org/guides/conventions/java/index.html) when they improve consistency and clarity without adding unnecessary complexity.

In particular, use logical lower-case packages, descriptive conventional names, four-space indentation, a 120-character hard line limit, explicit and consistently ordered imports, braces around all loop and conditional bodies, and descriptive Javadocs for public production APIs. Javadoc parameter descriptions should use sentence case and terminal punctuation.

## JUnit testing

Maintain JUnit tests after every code change so that the test suite continues to cover the top approximately 50% highest-value methods. Prioritize complex, core, and critical business logic over trivial getters or constructors. For each selected class, test every method that warrants unit testing, including reasonable normal, boundary, and invalid-input cases.

Follow Gradle and JUnit conventions: place tests under `src/test/java` in the same package as the production class, name each test class `<ClassName>Test`, and run the full Gradle `test` task after updating tests.

## Console UI testing

After each code update:

1. Review `test/ui-test-plan.md` and update it when the intended console behavior changes or new behavior needs coverage.
2. Invoke the project-specific `test-ui` skill and report its result. Stop at the first failed UI test and include the actual and expected output.

When commands are added or changed, cover relevant malformed and boundary inputs. Interleave invalid commands with valid commands and later `list` checks so tests verify that rejected inputs do not alter task state.

## Git

Use lightweight tags unless the user requests an annotated tag.
Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) when proposing or creating branches and commits. Write commit subjects in the imperative mood, capitalize the first letter, omit the trailing period, aim for at most 50 characters, and never exceed 72 characters. Use meaningful kebab-case names for non-default branches.
When a non-trivial commit needs a body, separate it from the subject with a blank line, wrap it at 72 characters, and explain what changed and why rather than restating the implementation.
Do not commit or push unless explicitly asked.
