# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

- Prior knowledge: Basic Java and OOP concepts.
- Level of programming experience: 6 years of software engineering
- IDE and level of expertise: expert in vscode, expert using codex

# Guidance for interacting with users

- Explain the rationale for significant actions: what you did and why.
- Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:
  - When suggesting a Git command, briefly explain what it does.
  - Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  - Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  - When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

If you were to ever make a commit, you must follow the below conventions listed out by the course:

Every commit must have a well-written commit message subject line.

Try to limit the subject line to 50 characters (hard limit: 72 chars)
Rationale: Some tools show only a limited number of characters from the commit message.
Use the imperative mood in the subject line.

Good: Add README.md
Bad: Added README.md
Bad: Adding README.md
Capitalize the first letter of the subject line.

Good: Move index.html file to root
Bad: move index.html file to root
Do not end the subject line with a period.

Good: Update sample data
Bad: Update sample data.
You may add a <scope>: or <category>: in front, when applicable.

e.g. Person class: Remove static imports
Main.java: Remove blank lines
bug fix: Add space after name
chore: Update release date
