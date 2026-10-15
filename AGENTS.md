# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Beginner
* IDE and level of expertise: IntelliJ IDEA and Beginner

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

All Java code in this project (new or edited) must follow the project-specific `seedu-java-coding-standard` skill, which is based on the [SE-EDU intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html). This applies to naming, layout, brace/whitespace style, import order, and Javadoc format — not just to code written for a specific increment.

Checkstyle (`config/checkstyle/checkstyle.xml`, copied from AddressBook-Level3) mechanically enforces the objectively-checkable subset of this same standard. Run `./gradlew checkstyleMain checkstyleTest` (or `./gradlew check`/`./gradlew build`, which include it) before considering Java changes done — it catches things like switch/case indentation depth and line-wrap placement that are easy to miss by eye. See README.md's "Developer setup" section for the pre-commit hook that runs it automatically.

## Git

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.

All commits in this project must follow the project-specific `seedu-git-standard` skill, which is based on the [SE-EDU git conventions guide](https://se-education.org/guides/conventions/git.html) (commit subject/body format, branch naming). When proposing or creating a commit message, include enough detail to explain the rationale for the change, per that skill's guidance on commit body content.

## UI regression testing

After each code update, update `test/ui-test-plan.md` if the planned console behaviour changed, then invoke the project-specific `test-ui` skill. The skill's runner must pass before handing off the change.

As the chatbot evolves, handle errors introduced by each new feature. Error messages should state what is wrong and how the user can correct the command.
