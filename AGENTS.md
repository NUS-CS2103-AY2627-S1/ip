# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: Intermediate

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

All Java code written or edited in this project (new code and edits to existing code) must follow the `seedu-java-coding-standard` skill (`.claude/skills/seedu-java-coding-standard/`), based on the SE-EDU intermediate Java conventions. Invoke it before writing or reviewing Java source, and fix non-compliant code you touch even if the change wasn't otherwise about style.

This standard is also enforced mechanically via Checkstyle (`config/checkstyle/`, wired into `build.gradle`). Run `./gradlew checkstyleMain checkstyleTest` after changing Java source and before committing it; fix any reported violations (see `build/reports/checkstyle/main.html` / `test.html` for details) rather than suppressing them, unless the user says otherwise.

## Git

Use lightweight tags unless the user requests an annotated tag.
Every commit message (subject and, where needed, body) must follow the `seedu-git-standard` skill (`.claude/skills/seedu-git-standard/`), based on the SE-EDU Git conventions. Invoke it before proposing or creating a commit message.
Do not commit or push unless explicitly asked.

## Testing

After any change to the program's source (e.g. `Eve.java`, `Task.java`, or new classes):

1. Update `test/ui-test-plan.md` if the change added, removed, or altered the behavior of a command -- verify any new or changed expected output by running the program manually before writing it down.
2. Invoke the `test-ui` skill (`.claude/skills/test-ui/`) to compile the program and run the full test plan against it. Report the printed session record and result; if a test case fails, do not proceed (e.g. to committing) until it is resolved.
