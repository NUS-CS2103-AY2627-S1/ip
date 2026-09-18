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

## SE-EDU Java coding standard

For every Java source or test change, load and follow the project skill at
`.codex/skills/seedu-java-coding-standard/SKILL.md`. This is mandatory for all
code in this project and implements the required basic and intermediate
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).

## SE-EDU Git standard

For every proposed or created commit, load and follow the project skill at
`.codex/skills/seedu-git-standard/SKILL.md`. This is mandatory for all future
commits in this project and implements the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Course standards and conventions

Follow the required CS2103/T standards for all Java and Git work in this project: [NUS CS2103/T Standards/Conventions](https://nus-cs2103-ay2627-s1.github.io/website/admin/standardsAndConventions.html).

In particular, Java code must comply with the basic and intermediate rules of the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/basic.html), and commit-message subjects must follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html). Apply the optional advanced Java, Markdown, and documentation guidance when it improves clarity and does not conflict with existing project requirements.

## JUnit test coverage

Maintain JUnit coverage for roughly the top 50% of the project's highest-value methods, prioritizing complex, core, or critical business logic over simple accessors and boilerplate.

After every code change, review the affected behavior and update or add the relevant JUnit tests so this coverage target continues to be met. Run the applicable Gradle test task before handing off the change.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
