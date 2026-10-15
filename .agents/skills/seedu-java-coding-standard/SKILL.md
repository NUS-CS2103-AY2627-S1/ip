# SE-EDU Java Coding Standard

Follow the SE-EDU Java coding conventions for all Java code in this project.

## Naming

- Use `UpperCamelCase` for class and interface names.
- Use `lowerCamelCase` for methods, parameters, and local variables.
- Use `SCREAMING_SNAKE_CASE` for constants.
- Use meaningful and descriptive names.
- Avoid abbreviations unless they are widely understood.

## Formatting

- Use 4 spaces for indentation.
- Do not use tabs for indentation.
- Place opening braces on the same line as the declaration.
- Place the closing brace on its own line.
- Keep lines within 120 characters.
- Use one statement per line.
- Use blank lines to separate logically distinct sections.

## Classes and methods

- Use an appropriate access modifier for classes, methods, and fields.
- Keep fields private unless there is a clear reason otherwise.
- Prefer `final` for fields and variables that are not reassigned.
- Keep methods short and focused on one responsibility.
- Avoid unnecessary duplication.
- Prefer simple designs that are sufficient for the requirements.

## Javadoc

- Add Javadoc comments to public classes and public methods.
- Document parameters using `@param`.
- Document return values using `@return` where applicable.
- Document checked exceptions using `@throws`.
- Keep comments accurate and update them when code changes.
- Do not add comments that merely restate obvious code.

## Java best practices

- Use braces consistently for control structures.
- Avoid unnecessary casts and exception handling.
- Do not catch exceptions unless the program can meaningfully handle them.
- Prefer clear control flow over unnecessarily clever code.
- Use constants for values that have a meaningful fixed purpose.

## Future code

All new and modified Java code in this project must follow this coding standard.
