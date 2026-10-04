---
name: seedu-java-coding-standard
description: Apply the SE-EDU intermediate Java coding standard when writing, editing, or reviewing Java code in this project.
---

# SE-EDU Java coding standard

Use this skill for every Java production or test-code change in this repository.
It summarizes the [SE-EDU intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
When a project requirement is stricter, follow the project requirement.

## Naming and declarations

- Use `UpperCamelCase` for classes and interfaces, `lowerCamelCase` for methods
  and variables, and `UPPER_SNAKE_CASE` for constants.
- Name booleans with a readable prefix such as `is`, `has`, or `was`; use
  matching names for boolean parameters, for example `setFound(boolean isFound)`.
- Use plural names for collections. Use `i`, `j`, and `k` only as loop indexes;
  reserve `j` and `k` for nested loops.
- Put every class in a package. Use explicit, minimal imports—never wildcard
  imports—and keep the chosen import ordering consistent.
- Attach array brackets to the type, for example `int[] values`.
- Declare and initialize variables in the smallest practical scope. Do not make
  mutable class fields public unless the class is a behavior-free data class.

## Layout and control flow

- Indent with four spaces; do not use tabs. Keep lines at or below 120
  characters, and aim for 110 or fewer where practical.
- Use K&R braces and always use braces for loop and conditional bodies.
- Use spaces around binary and ternary operators, after commas, and after Java
  keywords. Separate distinct logical units in a block with one blank line.
- Wrap long expressions to preserve readability: break after commas and before
  operators or chained dots, retaining the method name with its opening `(`.
- For a deliberate fall-through in a traditional `switch`, add `// Fallthrough`.

## Comments and documentation

- Write all comments in clear English using American spelling and no local
  slang.
- Add Javadoc to every public class and public method. Getters, setters,
  methods whose inherited Javadoc applies unchanged, and test code may omit it.
- Begin Javadoc summaries with a third-person verb such as `Returns`, `Adds`,
  or `Creates`. Explain non-obvious parameters, return values, and exceptions.
- Keep comments aligned with the code they explain. Prefer code whose names and
  structure make most implementation comments unnecessary.

## Before finishing

Review changed Java files for naming, package/imports, line length, spacing,
braces, documentation, and comments. Run the relevant tests or build task.
