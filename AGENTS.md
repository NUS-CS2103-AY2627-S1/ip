# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Some experience coding and using git in a 2-man project
* IDE and level of expertise: IntelliJ, not very good with intelliJ (Used VScode previously)

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Plana's voice and personality

Plana should sound like a friendly, capable anime girl: warm, casual, lightly playful, and encouraging. Her wording should feel like a natural conversation with a helpful classmate, not a caricature or roleplay performance.

* Prefer short, conversational phrases such as "Hi hi!", "Yay!", "Got it!", and "No worries!" when they fit the situation.
* Use contractions and first-person phrasing where natural, while keeping instructions and error messages clear.
* Keep enthusiasm gentle and varied. Do not use baby talk, excessive honorifics, repeated catchphrases, dramatic roleplay, or lots of emojis/symbols.
* Be kind when correcting input: explain what is missing and suggest the expected format without sounding scolding.
* Preserve command names, task markers, dates, and other machine-readable details exactly unless the user explicitly asks to change them.
* When adding or changing user-facing text, update the UI test plan's expected output in the same change.

# Project-specific requirements

## SEEDU Java coding standard

All Java source and test code in this project must follow the project-specific
`seedu-java-coding-standard` skill, based on the SE-EDU Java basic and
intermediate coding standard:

<https://se-education.org/guides/conventions/java/intermediate.html>

Use the skill when reviewing, adding, or modifying Java code. Preserve existing
behavior and user-facing output while applying its naming, layout, statements,
imports, variable, control-flow, and documentation rules.

## JUnit test coverage target:

Maintain JUnit tests for at least 50% of the highest-value non-trivial methods,
prioritizing complex, core, or critical business logic. After every code change,
update or add the relevant JUnit tests so that the test coverage target remains
fulfilled and the changed behavior is verified.

## Post-code-update UI validation:

After every update to source code or other user-visible application behavior:

1. Review `test/ui-test-plan.md` and update it when the change adds, removes, or changes a user-visible command, input, or expected console output. Keep each affected test case's aim, inputs, and expected output accurate.
2. Invoke the project-specific `test-ui` skill after the code update by running its test session from the repository root:

   ```powershell
   pwsh -NoProfile -File .codex/skills/test-ui/scripts/run_ui_tests.ps1
   ```

   Preserve the console input/output record in the report. If a test fails, stop immediately, report the actual and expected outputs, and do not continue to later test cases.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## SEEDU Git standard

All future commits in this project must follow the project-specific
`seedu-git-standard` skill, based on the SE-EDU Git conventions:

<https://se-education.org/guides/conventions/git.html>

Use the skill when preparing any commit. Commit subjects must use its
imperative, capitalization, length, and punctuation rules. Non-trivial commits
must include a properly separated, wrapped body explaining what and why.
Branches must use meaningful kebab-case names, with issue-number prefixes when
applicable. Continue to commit only when the user explicitly requests it.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
