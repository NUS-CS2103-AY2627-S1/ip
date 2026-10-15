# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: some coding experience. 
* IDE and level of expertise: IDE experience with Visual Studio Code.

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Noms personality and response style

Noms is a cute, hungry little monster that gobbles up tasks. Its responses should
be playful and lightly food-themed, enthusiastic when accepting tasks, and
encouraging when users make mistakes. Error messages must still clearly explain
the problem and, where useful, show the correct command format.

Use food or monster references sparingly so that messages remain easy to
understand. Follow the style of these examples:

* `Yum! Noms has gobbled up your new todo.`
* `Nom nom! Noms has added your deadline to the menu.`
* `Oops! This todo is missing its main ingredient: a description.`
* `Grrr... Noms couldn't understand that command.`
* `Noms couldn't find that task on the menu.`

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Code style

All Java code in this project (both `src/main/java` and `src/test/java`) MUST
follow the project-specific `seedu-java-coding-standard` skill, which encodes the
se-education intermediate Java conventions. Invoke that skill whenever you write,
review, or refactor Java, and self-check every change against its review checklist
before considering the work done. These conventions are graded, so apply them from
the start rather than only at refactoring increments.

In addition, all code MUST follow the project-specific `seedu-code-quality`
skill, which encodes the CS2103 code-quality conventions. It currently covers
naming (nouns vs. verbs, self-explaining names, spelling, word order), with more
areas to be added over time. Where the two skills overlap on naming,
`seedu-java-coding-standard` governs the mechanical form (casing) and
`seedu-code-quality` governs whether the name communicates. Invoke it whenever
you write, review, or refactor code, and self-check against its review checklist
before considering the work done.

@docs/style-guide.md

## Git

All commits and branch names in this project MUST follow the project-specific
`seedu-git-standard` skill, which encodes the se-education Git conventions
(imperative subject, 50/72 length limits, WHAT-and-WHY body, kebab-case
branches). Invoke that skill whenever you propose or create a commit message or
name a branch, and self-check against its review checklist first.

Project-specific policy on top of that skill:

- Use lightweight tags unless the user requests an annotated tag.
- When proposing or creating a commit message, include enough detail to explain the rationale for the change.
- Do not commit or push unless explicitly asked.

## PR review comments

When drafting or posting review comments on a pull request (e.g. via
`/code-review --comment`), follow the se-education PR-reviewing guidelines
(https://se-education.org/guides/guidelines/PRs-reviewing.html):

- Attach comments at the specific lines they refer to, not as one lump summary.
- Phrase feedback as questions or suggestions ("Could this be extracted?")
  rather than directives.
- Lead with what works before raising concerns, and keep compliments genuine.
- Consolidate a recurring issue: flag it at a couple of spots, then note that it
  applies throughout instead of repeating the same comment everywhere.
- Keep the tone collegial; avoid condescension (including an over-used "please").

This is about how findings are communicated. The technical substance of a review
still comes from the `code-review` skill.

## JUnit test coverage

Maintain automated JUnit tests covering approximately the top 50% highest-value
methods in the codebase, prioritizing complex, core, or business-critical logic
(e.g. parsing, task and date handling, and persistence). Thin accessors, simple
display formatting, and console-facing orchestration are the lower-value half and
need not be covered.

Tests live under `src/test/java`, mirroring the package of the class under test
(e.g. `noms.parser.Parser` -> `src/test/java/noms/parser/ParserTest.java`), and
run via `./gradlew test`.

After every code change, update the JUnit tests so this target continues to hold:

1. Add tests for any new high-value method, and update existing tests whose
   expected behavior the change affects.
2. Run `./gradlew test` and confirm it passes before considering the change done.

If a change touches only lower-value code (or is itself test-only), no new tests
may be needed, but still run `./gradlew test` to confirm nothing regressed. Do not
silently skip this step.

## Console UI regression testing

After every code update:

1. Review `test/ui-test-plan.md` and update it when the change adds, removes, or changes observable console UI behavior, inputs, or expected outputs.
2. Invoke the project-specific `test-ui` skill using the resulting test plan. Run the planned cases in order, show the console input/output transcript, and stop immediately on the first failure.

If the code update has no effect on the console UI, still invoke `test-ui` and record that the existing plan was reviewed and remains applicable. Do not silently skip this step.
