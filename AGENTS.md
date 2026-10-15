# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Medium
* IDE and level of expertise: Medium

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Coding convention

Make sure your code follows the OOP principle. e.g., at least some use of inheritance, with code divided into classes in a sensible way

Make sure your code follows the Java coding standard in the next section.

Make sure ALL the public methods/classes have Javadoc comments.

You must provide reasonable code quality as follows:
* No blatant violations of the coding standard (both Java and Git conventions).
* The code is neat, e.g., no chunks of commented-out code.
* Reasonable use of SLAP, e.g., no very long methods or deeply nested code.

Make sure at least some errors are handled using exceptions.

## Java coding standard

# Java Coding Standard — Basic + Intermediate

For topics not specified here, follow the **Google Java Style Guide**.

# Naming

* Packages: lowercase; school projects should use the group/project name as the root.
* Classes/enums: noun-based `PascalCase`.
* Variables: `camelCase`.
* Constants: `SCREAMING_SNAKE_CASE`; related constants should share a prefix.
* Methods: verb-based `camelCase`.
* Tests may use `featureUnderTest_scenario_expectedBehavior`.
* Acronyms inside identifiers use normal casing rather than all caps.
* Use English names.
* Use longer variable names for wider scopes; short scratch/index names are acceptable locally.
* Boolean names should read as boolean predicates, preferably using `is`, `has`, `was`, `can`, `should`, etc.
* Collection names should generally be plural.
* Iterator names such as `i`, `j`, and `k` are acceptable, with later letters typically used for nested loops.

# Layout

* Indent using **4 spaces**, never tabs.
* Aim below **110 characters per line**; do not exceed **120**.
* Wrapped lines use an additional **8 spaces**.
* Wrap for readability: generally after commas and before operators; keep method names attached to `(`.
* Prefer breaking expressions at higher syntactic levels.
* Use **K&R/Egyptian braces**.
* Always use conventional block formatting for methods, conditionals, loops, `switch`, and `try/catch/finally`.
* Explicitly mark intentional `switch` fall-through.
* Put spaces around operators and after Java keywords, commas, and `for` semicolons.
* Separate logical units with blank lines. 

# Statements

* Do not change the locations of any Java file.
* Keep import ordering consistent and list imports explicitly; avoid wildcard imports.
* Write arrays as `int[] values`, not `int values[]`.
* Initialize variables at declaration when possible and keep their scope minimal.
* Avoid public mutable fields except where appropriate for pure data classes; constants are exempt.
* Always use braces around loop and conditional bodies, even for one statement.
* Put conditional bodies on separate lines rather than writing one-line `if` statements. 

# Comments and Javadoc

* Write comments in English, using American spelling and avoiding local slang.
* Document classes and public methods, with exceptions such as obvious getters/setters, applicable inherited documentation, and test code.
* Javadoc should begin with a concise summary sentence, use standard aligned formatting, and document parameters, returns, and exceptions when they add useful information.
* `@inheritDoc` may be used when extending inherited documentation.
* Short member documentation may use single-line Javadoc.
* Indent comments consistently with surrounding code; trailing comments are allowed. 

