# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate programming experience; limited and rusty experience with Java.
* IDE and level of expertise: IntelliJ IDEA; beginner, still learning the IDE and its workflow.

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to classes and public methods as required by the Java coding standard, and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Java coding standard

For every Java code creation, change, review, or documentation task, invoke the project-specific
`$seedu-java-coding-standard` skill and follow it. Do not consider Java work complete until the modified code
complies with that skill.

Follow the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html). In particular:

* Use lowercase package names. Use PascalCase noun names for classes and enums, camelCase verb names for methods, camelCase names for variables, and SCREAMING_SNAKE_CASE names for constants. Write names in English, treat acronyms as words (for example, `exportHtmlSource`), name booleans to read as boolean expressions (prefer prefixes such as `is`, `has`, or `can`), and use plural names for collections.
* Test method names may use `featureUnderTest_testScenario_expectedBehavior`; omit parts only when the remaining name still describes the test accurately.
* Indent with 4 spaces, never tabs. Prefer lines shorter than 110 characters and never exceed 120 characters. Indent wrapped lines by 8 additional spaces and place breaks where they improve readability, normally after commas and before operators.
* Use K&R braces. Always use braces for loop and conditional bodies, including single-statement bodies, and put the body on a separate line. Mark intentional traditional `switch` fall-through with `// Fallthrough`.
* Use standard Java whitespace: spaces around operators, after Java keywords and commas, and after semicolons in `for` headers. Separate logical units within a block with one blank line.
* Put every class in a package. Keep import ordering consistent, list imported classes explicitly, and do not use wildcard imports.
* Attach array brackets to the type (for example, `int[] values`). Declare variables in the smallest practical scope and initialize them at declaration when a valid initial value is available. Do not expose mutable class fields publicly unless the class is intentionally a behavior-free data class; constants are exempt.
* Write comments in English using American spelling. Add descriptive Javadoc to all classes and public methods, except getters/setters, test code, and overrides whose inherited documentation applies exactly. Start Javadoc summaries with a concise third-person verb such as `Returns`, use standard Javadoc layout and punctuation, and omit tags only when they add no information. Indent comments consistently with the surrounding code.

Advanced rules from the standard are optional. Do not modify otherwise-correct existing code solely to adopt an optional rule. For Java topics the SE-EDU standard does not cover, follow the conventions already established in this codebase; if none exist, use the Google Java Style Guide as the upstream standard recommends.

## Testing after code updates

After every update to source code:

1. Review and update the JUnit tests to maintain coverage of approximately the top 50% highest-value methods, prioritizing complex, core, and critical business logic. Include all reasonable success, boundary, invalid-input, and failure cases for the selected methods, and run the Gradle JUnit test suite.
2. Review `test/ui-test-plan.md` and update it when the changed or newly added behavior is not adequately covered. Each added test case must state its aim, inputs, and exact expected output.
3. Invoke the project-specific `$test-ui` skill and run the UI test plan. Do not consider the code update complete until both the JUnit and UI test sessions pass, or until a failure has been reported to the user with the actual and expected outputs.

When adding a feature or command, identify its invalid-input and failure cases, implement clear error handling for them, and add corresponding negative test cases to `test/ui-test-plan.md` as needed. Negative tests must verify both the error message and that the chatbot remains in a valid state afterward.

## Git

Before proposing, reviewing, or creating any commit or commit message, invoke the project-specific
`$seedu-git-standard` skill and follow it. Also invoke it when naming a branch. Do not consider a commit ready
until its message complies with that skill.

Use lightweight tags unless the user requests an annotated tag.
Every proposed or created commit message must have a well-written subject line that:

* uses the imperative mood;
* starts with a capital letter;
* has no trailing period; and
* aims for at most 50 characters and never exceeds 72 characters.

An applicable `<scope>:` or `<category>:` prefix may be used. Commit bodies are optional. If a body is used, separate it from the subject with a blank line, wrap it at 72 characters, separate paragraphs with blank lines, and explain what changed and why rather than how. Include enough detail for the reader to understand the rationale without inspecting the diff.

Never rewrite, amend, rebase, or otherwise modify past commits merely to make their messages comply. Rewriting a past commit can change its timestamp and cause the school's progress-tracking scripts to attribute earlier work to the week in which the history was rewritten, potentially affecting progress marks.

Do not commit or push unless explicitly asked.
