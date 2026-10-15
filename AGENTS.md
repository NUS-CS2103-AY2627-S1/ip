# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: Intermediate (IntelliJ IDEA)

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java coding standard

For every Java source or test change, you must load and follow the
project-specific `$seedu-java-coding-standard` skill at
`.codex/skills/seedu-java-coding-standard/SKILL.md`. It implements the
SE-EDU intermediate Java coding standard and is mandatory for all Java code in
this project.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Console UI testing

After every code update:

1. Review `test/ui-test-plan.md` and add or update test cases when the change affects the console UI or its expected output. Each case must state its aim, inputs, and complete expected output.
2. Invoke the project-specific `$test-ui` skill and run its documented test session before reporting the work as complete.
3. If a test fails, stop at the first failure and report the test input along with the expected and actual output. Do not continue with later test cases.

## JUnit test coverage

Maintain JUnit tests for approximately the top 50% of methods by value, prioritising complex, core, and critical business logic over simple accessors or framework code.

After every code change, review and update the relevant JUnit tests to keep this coverage target satisfied. Add or revise tests for changed high-value methods, including their important success, boundary, and error cases.

## Git

Before creating a branch, proposing a commit message, or preparing a commit,
you must load and follow the project-specific `$seedu-git-standard` skill at
`.codex/skills/seedu-git-standard/SKILL.md`. It implements the SE-EDU Git
conventions and is mandatory for all future commits in this project.

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
When the user asks for “CTP”, commit the current in-scope changes, create a lightweight tag named after the current Codex task title (for example, `Level-6`), and push both the commit and tag without requesting further confirmation.
Likewise, when the user asks for "CP", commit the current in-scope changes and push the commit (without a tag).

Branch procedure:

1. Merge the branch back to the master branch (remember to create a merge commit, i.e., no fast-forward).
2. Git tag the merge commit in the master branch as usual (i.e., add the tag based on the Codex task title).
3. Push the following three things to your fork:
(a) the master branch,
(b) the current feature branch,
(c) the tag.

The user will use the acronym "BP" to ask you to implement the above steps.
