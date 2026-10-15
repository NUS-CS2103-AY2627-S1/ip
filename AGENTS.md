# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Strong in C++ and Rust; basic Java and OOP concepts.
* IDE and level of expertise: IntelliJ IDEA, comfortable

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 or newer is used when running the application or build tasks. This machine has OpenJDK 26 installed and no sdkman, so switch with `java_home` instead of `sdk use`:

    export JAVA_HOME="$(/usr/libexec/java_home -v 26)"

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Project-specific skills

Apply `.codex/skills/seedu-java-coding-standard/SKILL.md` to all Java code in this project.
Apply `.codex/skills/seedu-git-standard/SKILL.md` to all commit messages in this project.
