---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard to all Java source, test, and review work in this project. Use when creating, editing, reviewing, naming, formatting, documenting, packaging, or testing Java code.
---

# SEEDU Java Coding Standard

Apply these rules to every Java change in this repository. Treat the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) as the source of truth; use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) for topics not covered here.

## Naming

- Put package names in lower case. Keep the current application package as `bro` unless a new subpackage is justified.
- Name classes and enums with PascalCase nouns.
- Name variables with camelCase and constants with SCREAMING_SNAKE_CASE.
- Name methods with camelCase verbs. Do not write abbreviations or acronyms as uppercase parts of names.
- Use English names and American spelling. Use long names for large scopes and short names only for nearby scratch variables or loop indices.
- Name booleans so they read as predicates, using prefixes such as `is`, `has`, `was`, or `can`. Name boolean setters `setFound(boolean isFound)`.
- Use plural names for collections. Reserve `i`, `j`, and `k` for iterators, with `j` and `k` for nested loops.
- Give associated constants a common prefix.
- In tests, allow underscores only in the three-part form `featureUnderTest_testScenario_expectedBehavior()` when that improves the test name.

## Layout and whitespace

- Use four spaces for indentation; never use tabs.
- Keep lines at or below 120 characters and aim for fewer than 110. Wrap continuation lines with eight additional spaces.
- Break after commas and before operators. Keep a method or constructor name attached to its opening parenthesis, and prefer breaks at higher expression levels.
- Use K&R braces for classes, methods, conditionals, loops, switches, and try-catch blocks.
- Surround operators, reserved-word parentheses, commas, and binary or ternary colons with the appropriate spaces.
- Separate logical units in a block with one blank line.
- Include `// Fallthrough` for an intentional switch fallthrough.
- Organize class members as documentation, declaration, class variables, instance variables, constructors, and methods; order variables by visibility within each group.
- Put the access modifier first in a method declaration, followed by `static`, `abstract`, `synchronized`, unusual modifiers, and `final` where applicable.

## Statements and types

- Put every class in a package and keep imports consistently ordered, explicit, and minimal. Do not use wildcard imports.
- Attach array brackets to the type, such as `int[] values`.
- Initialize variables where they are declared and keep declarations in the smallest valid scope.
- Do not expose class variables as public fields, except constants or behavior-free data classes.
- Wrap every loop and conditional body in braces, including one-statement bodies. Put a conditional body on its own lines.

## Comments and Javadoc

- Write comments in English, using American spelling and avoiding local slang.
- Add descriptive header Javadocs to public classes and public methods. A getter, setter, override whose inherited documentation applies exactly, or test method may omit one.
- Start each Javadoc summary with a short action-oriented sentence such as `Returns ...`, `Adds ...`, or `Creates ...`.
- Put `/**` on its own line, align the `*` markers, leave no blank line before the declaration, and add a blank line before `@param`, `@return`, or `@throws` tags when tags are present.
- End parameter and exception descriptions with punctuation. Add only tags that clarify the API; either document all parameters or none when the names and summary already explain them.
- Document protected and package-private methods when they form part of the project-facing API, while keeping exact inherited overrides concise with `@inheritDoc` when appropriate.
- Indent inline comments with the code they explain, and keep comments beside the relevant logical unit.

## Change workflow

1. Inspect the affected class and the callers relevant to the changed behavior
   or contract. A comment or formatting edit does not require a caller survey.
2. Preserve behavior while applying the naming, package, layout, visibility, and documentation rules.
3. Check package declarations, imports, braces, line lengths, and public API Javadocs in every changed Java file.
4. For behavior or contract changes, run the relevant project and console tests
   with Java 25. Formatting or comment changes need proportionate checks. Report
   environment limitations and fix failures caused by the requested change.
