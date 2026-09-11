# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner to early-intermediate; comfortable with basic Java syntax and small programs, but still learning software engineering workflow and design.
* IDE and level of expertise: IntelliJ IDEA, beginner to intermediate.

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, this machine has JDK 25 installed; verify it with `java -version` before compiling or running the application.

## Current project scope

The chatbot is implemented through Level-9 (finding tasks by a keyword).
Keep changes small, incremental, and aligned with the current increment requirements.
Preserve the existing command-line behavior unless the current increment explicitly changes it.

## Testing workflow

After each code update that can affect command-line behavior or stored data:
1. Update `test/ui-test-plan.md` and `test/ui-test-cases.tsv` if the behavior or relevant edge cases changed.
2. Invoke the project-local `test-ui` skill at `.agents/skills/test-ui/SKILL.md`, which runs its test script in an isolated temporary directory.
3. Report the console input/output produced by the skill. If a test fails, stop and report the expected and actual output before continuing.
4. Do not commit until the updated behavior has been tested and the results have been reported.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
