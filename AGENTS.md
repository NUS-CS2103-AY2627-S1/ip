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

## Java Coding Standard

All Java source and test code in this repository MUST strictly follow the SE-EDU Java Coding Standard (Basic + Intermediate rules) defined in `skills/seedu-java-coding-standard/SKILL.md` (based on https://se-education.org/guides/conventions/java/intermediate.html). For any topics not covered, adhere to the Google Java Style Guide.

Key mandatory rules include:
* **Naming**: Packages in all lowercase (`panda.*`), classes/enums in PascalCase nouns without uppercase acronyms (`Ui` instead of `UI`), variables and methods in camelCase, constants in `SCREAMING_SNAKE_CASE` with shared prefixes for associated constants, test methods in `featureUnderTest_testScenario_expectedBehavior()`, and boolean fields/methods named to sound like booleans (`isMarked`, `hasData`).
* **Layout & Braces**: 4-space basic indentation (no tabs), line length <= 120 characters, continuation line indentation 8 spaces, K&R (Egyptian) braces on all control flow statements (`if`, `else`, `for`, `while`, `switch`, `try-catch-finally`) even for single-line bodies. Explicit `// Fallthrough` comment for fallthrough switch cases.
* **Imports**: Consistent ordering with `import static` first, followed by `java.*`/`javax.*`, third-party, and project imports in sorted groups separated by blank lines. Explicit imports only (no `*` wildcard imports).
* **Encapsulation & Types**: Array brackets attached to types (`int[] a`), class fields never `public` (except constants), variables declared and initialized in the smallest possible scope.
* **Javadoc**: Required for all public classes, interfaces, enums, and public methods/constructors. Summaries must use third-person present tense verbs, and tag descriptions (`@param`, `@return`, `@throws`) must be capitalized and terminate with a period.

## Git

Use lightweight tags unless the user requests an annotated tag.
For every proposed, reviewed, or created commit or commit message, read and
follow the project-specific `seedu-git-standard` skill in
`skills/seedu-git-standard/SKILL.md`. This requirement is mandatory for all
future commits.
Do not commit or push unless explicitly asked.
