---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard (basic and intermediate rules) to all Java production and test code in this project.
metadata:
  short-description: Apply this project's Java coding standard
---

# SE-EDU Java Coding Standard

Apply this skill whenever creating, modifying, reviewing, or formatting Java code in this project. Treat its rules as mandatory unless the user explicitly gives conflicting instructions. The source is the [SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html); for topics not covered there, follow the Google Java Style Guide.

## Names and packages

- Put every class in a lowercase package rooted at `neil`, followed by logical package names.
- Use PascalCase nouns for classes and enums, camelCase verbs for methods, camelCase for variables, and English names throughout.
- Name boolean variables and methods as predicates, preferably beginning with `is`, `has`, `was`, `can`, or `should`. Boolean setters use `setX(boolean isX)`.
- Use plural names for collections. Use short iterator names only for tightly scoped loop indices; reserve `j`, `k`, and so on for nested loops.
- Write constants in SCREAMING_SNAKE_CASE. Give related constants a shared prefix.
- Test names may use `featureUnderTest_testScenario_expectedBehavior`, omitting parts only when the test scope remains clear. Acronyms embedded in names are ordinary words, e.g. `parseHtml`, not `parseHTML`.

## Layout and statements

- Indent with four spaces; never tabs. Keep lines within 120 characters and normally within 110. For wrapped lines, indent eight spaces beyond the parent line and break after commas or before operators/dots when that improves readability.
- Use K&R braces. Place whitespace around binary and ternary operators, after keywords and commas, and after `for` semicolons. Separate distinct logical units with one blank line.
- Use braces and a separate line for every conditional and loop body, including single statements. Document intentional fall-through in a traditional `switch` with `// Fallthrough`.
- Import individual classes only. Keep imports consistently grouped: static imports, `java.*`, `javax.*`, third-party imports, then project imports; separate nonempty groups with one blank line.
- Attach array brackets to the type (`String[] values`). Declare variables in the smallest useful scope and initialize them where declared. Do not expose mutable class fields publicly.

## Documentation

- Write all comments in clear American English and indent them to the surrounding code level. Avoid comments that merely repeat the code.
- Add descriptive Javadoc to every public class and public method. Getters, setters, test code, and overrides whose inherited Javadoc applies unchanged are exempt.
- Begin Javadoc summaries with a third-person verb such as `Returns`, `Adds`, or `Parses`. Include a blank line before `@param`, `@return`, and `@throws` tags, and end tag descriptions with punctuation. Omit all `@param` tags only when every parameter name is self-explanatory.
- Document non-obvious fields and nontrivial private methods when their purpose is not apparent from the code.

## Before finishing Java work

1. Review changed Java files for these rules, including test names and Javadoc.
2. Check no Java line exceeds 120 characters.
3. Run the relevant Gradle tests using Java 25 (`25.0.3.fx-zulu` on this macOS workspace).
