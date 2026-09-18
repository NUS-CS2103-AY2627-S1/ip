# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Competent
* IDE and level of expertise: IntelliJ IDEA 2025

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## SE-EDU Java coding standard

All Java code in this repository MUST follow every rule in `.codex/skills/seedu-java-coding-standard/SKILL.md`, based on the complete SE-EDU basic + intermediate Java coding standard. This includes naming, package/import ordering, explicit imports, indentation, line length and wrapping, K&R braces, whitespace, blank-line separation, variable scope and initialization, encapsulation, braces around all loops and conditionals, intentional switch fall-through comments, and English/American-spelling comments. Header Javadocs are required for every class and every non-private method, plus every non-trivial private method; project-specific requirements take precedence over the source guide's exemptions.

## Code-update verification

After every code update:

1. Review `test/ui-test-plan.md` and update it when the change adds, removes, or changes observable command-line UI behavior, inputs, commands, prerequisites, or expected output.
2. Invoke the project-local `$test-ui` skill to run the UI test plan. Include the resulting console input/output session record and report any failure with its actual and expected output.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
For every proposed or requested commit, apply the project-specific `$seedu-git-standard` skill. Use a meaningful kebab-case branch name, and for issue-related branches use `<issue-number>-<keywords-from-issue-title>`.
Commit subjects must be imperative, capitalized, concise (aim for 50 characters and never exceed 72 characters), and must not end with a period. Non-trivial commits must have a blank-line-separated body wrapped at 72 characters that explains what changed and why, not how. Keep commits focused and split overly broad changes when practical.
Do not commit or push unless explicitly asked.
