# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Sufficient programming experience (>10000 lines of code written).
* IDE and level of expertise: VSCode, familiar with it.

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
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Wrap every commit message body line at 72 characters or fewer.
Explicitly invoke and follow the project-specific `$seedu-git-standard` skill before proposing, creating, or amending a commit, or before creating or renaming a branch.
Do not commit or push unless explicitly asked.

## Project Instructions

Avon is a Shakespearen-themed chatbot. This theme is chosen by the user as part of customisation efforts. Instructions sent to the chatbot may contain the original, uncustomised messages. Please do not change the Shakespeare theme unless otherwise stated.

Additional instructions (if applicable)
- Strict Standard Adherence: Explicitly invoke and follow the project-specific `$seedu-java-coding-standard` skill for every task that reads, reviews, creates, or changes Java code.
- Effective OOP Structure: Demonstrate a reasonable use of OOP by utilizing inheritance and dividing code into specialized classes with clear responsibilities, such as Ui, Storage, Parser, Todo, and Deadline.
- Adherence to SLAP: Apply the Single Level of Abstraction Principle (SLAP) to ensure methods are not excessively long and to avoid deeply nested code.
- Code Neatness and Naming: Keep code neat by removing all commented-out code and ensuring variables and methods have clear, meaningful naming.
- Robust Error Handling: Use Exceptions to handle errors systematically rather than allowing the program to crash.
- Comprehensive Documentation: Provide Javadoc header comments for every non-private class, constructor, and method, and for every non-trivial private method. Use `/** {@inheritDoc} */` when an inherited method contract applies exactly.
- Thoughtful Refactoring: Before refactoring existing code, investigate the original design rationale (applying Chesterton’s Fence Principle) through commit history and documentation.

## Required Java code workflow

Use the following sequence for every task that reads or changes Java code.

Explicitly invoke `$seedu-java-coding-standard` and read `.agents/skills/seedu-java-coding-standard/SKILL.md` completely before inspecting or editing Java code.

### 1. Inspect before editing

Run these commands from the repository root.

```bash
git status --short --branch
git log -5 --oneline -- src/main/java
rg --files src/main/java | sort
sed -n '1,260p' src/main/java/<FileName>.java
rg -n '<symbol-or-command>' src/main/java
```

Read every Java file directly involved in the requested change. Before refactoring existing behavior, inspect the relevant commit with `git show <commit> -- <files>`.

### 2. Edit Java source files

Use `apply_patch` for focused Java source edits. Do not write source files with `cat`, shell redirection, or Python.
Keep unrelated user changes untouched.

### 3. Review each edit

After editing, run:

```bash
git diff --check
git diff -- src/main/java test/ui-test-plan.md
git status --short
```

Confirm that only intended files changed and that no build output or temporary files were added.

## Mandatory UI testing after code updates

After every code update:

1. Read `test/ui-test-plan.md` completely.
2. Decide whether the changed commands, state transitions, text, spacing, task formatting, startup output, or shutdown output require test-plan changes.
3. If observable console behavior changed or a new behavior was added, update the relevant test cases or add new cases. Every case must specify its aim, input commands, and exact expected output.
4. If no plan update is needed because behavior is unchanged, explicitly record that assessment in the final response.
5. Explicitly invoke the repository skill as `$test-ui`, read `.agents/skills/test-ui/SKILL.md`, and follow it. The deterministic command is:

   ```bash
   python3 .agents/skills/test-ui/scripts/run_ui_tests.py
   ```

6. Stop immediately if a UI test fails. Report the failing case's expected and actual outputs; do not proceed to commit or declare completion.
7. Report the passing test count and console-session record in the final response.

Do not treat compilation alone as sufficient verification. Do not commit a code update until the test plan has been assessed and the `test-ui` run passes, unless the user explicitly directs otherwise.
