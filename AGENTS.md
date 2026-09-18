# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Slightly over a year with some experience in designing and rolling out some programs.
* IDE and level of expertise: Have been using IDEs for over a year now.

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

## Coding standard

All Java code in this project MUST follow the SE-EDU Java coding standard
(intermediate level), as recorded in the `seedu-java-coding-standard` skill.
This is mandatory, not advisory.

* Invoke the `seedu-java-coding-standard` skill before writing or modifying any
  Java file, and follow it for every line you add or change.
* Header comments are required for every class and every public method. They may
  be omitted only for getters and setters, for overriding methods whose parent
  Javadoc applies as is, and for test code.
* Do not leave a file less compliant than you found it. When editing a file that
  already violates the standard, fix the violations you touch, and mention the
  rest rather than silently leaving them.
* The standard supersedes any conflicting formatting habit, including the
  Javadoc guidance under "Guidance for interacting with users" above, which it
  extends rather than replaces.

## Git

All commits and branches in this project MUST follow the SE-EDU Git
conventions, as recorded in the `seedu-git-standard` skill. This is mandatory,
not advisory.

* Invoke the `seedu-git-standard` skill before writing or proposing any commit
  message, and before naming a new branch.
* Subject lines are imperative, capitalised, no trailing period, and 50
  characters where possible and 72 at the very most.
* Non-trivial commits need a body wrapped at 72 characters, following the
  guide's structure: the present situation, why it must change, what is being
  done, and why it is done that way.
* Explain WHAT and WHY, not HOW. If a message grows too long to follow that
  shape, split the commit rather than stretching the message.
* Branch names use kebab-case keywords, or `issueNumber-keywords-from-title`
  when tied to an issue.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
