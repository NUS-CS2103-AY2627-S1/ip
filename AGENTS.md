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

## Java coding standard

For every Java code creation, edit, refactor, or review in this repository, invoke the
`$seedu-java-coding-standard` skill and follow its rules. Apply the standard to both application
and test code. Treat the SE-EDU basic + intermediate Java coding standard as authoritative, and
use the Google Java Style Guide only for topics that standard does not cover.

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## JUnit test coverage

Maintain JUnit tests for approximately the top 50% highest-value methods in the codebase. Prioritize methods containing complex, core, or critical business logic over trivial getters, simple constructors, or presentation-only methods. Treat this as a method-selection target: tests should cover the most consequential behavior and its reasonable success, boundary, and failure cases rather than inflate coverage with low-value assertions.

After every application code change, review the affected methods and update or add JUnit tests as needed to remain compliant with this 50% target. After every application or test-code change, run the complete JUnit suite using the project's Gradle test task.

## After every code update

After each update to application or test code:

1. Review the JUnit coverage of affected methods and update or add tests when needed to maintain the 50% highest-value-method target.
2. Run the complete JUnit suite with the project's Gradle test task.
3. Review `test/ui-test-plan.md` and update its test cases or execution configuration when the code update changes behavior, inputs, expected outputs, or relevant UI-test coverage. If no update is needed, state why in the final response.
4. Invoke the `$test-ui` skill and run the UI test plan after the plan review. Do not substitute an ad hoc test command for the skill workflow.
5. If a test fails, terminate that test session immediately and report the actual and expected outputs as required by the skill. Fixes may be followed by a new test session.
6. Include the JUnit result, console input/output record, and final UI-test result in the response that hands off the code update.

## Git

For every future commit in this repository, invoke the `$seedu-git-standard` skill before proposing
or creating the commit message, and follow its commit-message rules. Also use the skill when naming
branches. This requirement does not authorize creating a commit, branch, tag, or push without the
user's explicit request.

Use lightweight tags unless the user requests an annotated tag.
Do not commit or push unless explicitly asked.
