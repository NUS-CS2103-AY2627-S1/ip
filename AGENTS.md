  # Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Somewhat of a beginner
* IDE and level of expertise: Used intelliJ and VSCode prior to this

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

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, follow the SE-EDU Git conventions:

https://se-education.org/guides/conventions/git.html

In particular, use the imperative mood, write a concise and specific subject line,
use a suitable action-oriented prefix where appropriate (such as `Add`, `Fix`,
`Update`, `Refactor`, `Remove`, or `Document`), and explain the purpose of the
change when a longer message is needed.

Commit messages should closely follow the guide even when the user does not
explicitly repeat this requirement in a prompt.

Do not commit or push unless explicitly asked.

## Coding standard

All Java code in this repository must closely follow the SE-EDU Java coding
standard (basic + intermediate rules):

https://se-education.org/guides/conventions/java/intermediate.html

For topics not covered by the SE-EDU guide, follow the Google Java Style Guide.

Important requirements include:

- Put every class in a package.
- Use PascalCase for class and enum names, camelCase for methods and variables,
  and SCREAMING_SNAKE_CASE for constants.
- Name methods with verbs and name boolean variables and methods with prefixes
  such as `is`, `has`, `can`, `should`, or `was` where appropriate.
- Use plural names for collections and English names throughout the codebase.
- Use four spaces for indentation, K&R-style braces, and consistent whitespace.
- Keep lines within 120 characters where reasonably possible, wrapping long
  lines to preserve readability.
- Keep imports consistently ordered.
- Separate logical units within a block with blank lines where appropriate.
- Use the prescribed three-part naming format for test methods when applicable:
  `featureUnderTest_testScenario_expectedBehavior()`.
- Add Javadoc to classes and to non-trivial methods and fields when their
  purpose or behavior is not obvious.

These coding-standard requirements apply to all new and modified Java code,
including tests, unless the user explicitly requests an exception.

The reusable project skill at `.codex/skills/seedu-java-coding-standard` contains
the operational checklist for applying this standard. Use it for every Java
code change.

## Git commit standard

All future commits must follow the SE-EDU Git conventions:

https://se-education.org/guides/conventions/git.html

The reusable project skill at `.codex/skills/seedu-git-standard` contains the
operational checklist for proposing and writing commit messages. Use it for
every commit, and keep unrelated changes in separate commits.

## UI testing workflow

After any code change that affects the program's console behavior (a command, prompt, message, or output format), before treating the change as done:

1. Update `test/ui-test-plan.md` if the change adds, removes, or alters any user-visible command or output. Keep every case's aim, input, and expected output current.
2. Invoke the `test-ui` skill (`.codex/skills/test-ui`) to run the console UI test plan against the updated program.
3. If a test case fails, stop immediately and report that case's actual and expected output. Fix the code or the plan, then rerun the full test plan from the beginning.

Skip this workflow for changes with no observable effect on console input/output, such as internal refactors, build/config edits, or non-UI unit tests.
