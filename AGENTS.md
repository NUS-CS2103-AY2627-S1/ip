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

## Java switch statement style

Use arrow-form switch statements or switch expressions when appropriate:

```java
switch (condition) {
    case ABC -> method("1");
    case DEF -> method("2");
    default -> method("0");
}
```

Indent `case` and `default` one level inside the `switch`, and indent their statements one additional level. For traditional colon-form switches, include `break;` at the end of every case, including `default`, whenever the case can reach it. If a case intentionally falls through without a break, add an explicit `// Fallthrough` comment before the next case.

## SE-EDU Java coding standard

All Java code in this project must follow the project-specific
`seedu-java-coding-standard` skill, based on the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
This includes naming, four-space indentation, K&R braces, the 120-character
line limit, consistent explicit imports, braced control-flow bodies, and
descriptive Javadoc comments. Apply the standard to production and test code
while preserving behavior and avoiding unrelated formatting changes.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

All future commits and branches in this project must follow the project-specific
`seedu-git-standard` skill, based on the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).
Commit subjects must be imperative, capitalized, period-free, and at most 72
characters; non-trivial commits must include a WHAT/WHY body wrapped at 72
characters. Use meaningful kebab-case branch names, including the issue-number
format when applicable. Review the staged diff and do not stage unrelated files.

## JUnit test coverage

Aim to cover approximately the highest-value 50% of methods with JUnit tests, prioritizing complex, core, or business-critical logic over trivial wrappers.
After every code change, review and update the relevant JUnit tests so that they continue to cover the highest-value methods and comply with this 50% target.

## UI testing after code updates

After every code update, review `test/ui-test-plan.md` and update it when the change adds or changes observable command-line UI behavior. Then invoke the project-local `$test-ui` skill using the updated plan, even when no plan changes are needed. Treat the test session as part of the code update: report its console transcript and stop at the first failure. Do not claim the code update is complete without reporting the test result or any setup blocker.
