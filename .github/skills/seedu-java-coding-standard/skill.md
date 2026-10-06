# SE-EDU Java coding standard

Use this project-specific skill for all Java code in this repository.

The authoritative rule set is the SE-EDU Java coding standard:
https://se-education.org/guides/conventions/java/intermediate.html

Apply these rules consistently in all code we write or modify:

- Package names must be in lower case, for example `com.gihun456.ui`.
- Type names (classes, enums, interfaces) must use PascalCase.
- Method names and variable names must use camelCase.
- Constant names must use SCREAMING_SNAKE_CASE.
- Use 4-space indentation. Do not use tabs.
- Keep lines at or below 120 characters where practical; wrap long lines neatly.
- Use a consistent, readable layout with clear separation between blocks.
- Prefer simple, readable code over clever or overly condensed solutions.
- Name things clearly and descriptively; avoid abbreviations that make the code harder to follow.
- Use English comments and Javadoc for public classes and non-trivial public methods.
- Keep fields private unless there is a clear reason to expose them.
- Prefer `final` for values that do not change after initialisation.
- Follow standard Java conventions for braces, switch statements, and imports.
- Keep imports organised and remove unused imports.

When changing code in this repository, treat the SE-EDU standard as mandatory. If a change conflicts with a more general repository instruction, follow the project-specific rule in this file and then the repository's AGENTS.md guidance.
