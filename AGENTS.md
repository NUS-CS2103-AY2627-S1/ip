# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: [to be filled]
* IDE and level of expertise: [to be filled]

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
- Subject line: imperative mood, capitalized, no trailing period, concise (~50 chars).
- Separate subject from body with a blank line.
- Commit message body: hard-wrap all lines at 72 characters maximum. Explain the rationale (what and why).
- Do not edit or rewrite past commits (to preserve milestone detection timestamps).
Do not commit or push unless explicitly asked.
Before committing (or proposing a commit), always invoke the `present-changes-visually` skill (`python3 .agents/skills/present-changes-visually/scripts/generate-split-view-diff.py . HEAD WORKTREE _temp/visual-diff.html`) and link to the generated diff page so changes can be reviewed visually.

## Documentation and User Guide

After each increment or when new features are added (or modified):
1. **Update User Guide**: Ensure `docs/README.md` is updated to accurately document any new or modified commands, syntax, descriptions, and example outputs.

## Testing and Quality Assurance

After each code update or increment:
1. **Update Test Plan**: Ensure `test/ui-test-plan.md` is updated (if needed) with test cases reflecting the new or modified behavior.
2. **Invoke `test-ui` Skill**: Always invoke the `test-ui` skill (`python3 .agents/skills/test-ui/scripts/run-ui-tests.py`) to execute all UI tests and ensure all test cases pass cleanly before concluding the turn.

## Code Style and Standards

Ensure all Java code complies with the SE-EDU Java Coding Standard (Intermediate) detailed in `.agents/skills/seedu-java-coding-standard/SKILL.md` (e.g. 4-space indentation, explicit imports, K&R braces, constant and variable naming conventions).

