---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard to all Java code in this project.
---

# SE-EDU Java coding standard

Use the SE-EDU Java coding standard at https://se-education.org/guides/conventions/java/intermediate.html for all Java code in this project.

Follow these rules as mandatory requirements unless a more specific project instruction overrides them:

Naming:
- Use lower-case package names and keep the package structure logical and consistent with the project.
- Keep the project root package logical, e.g. use your project or group name followed by subpackages such as `todobuddy.ui` or `todobuddy.file`.
- Use nouns in PascalCase for classes and enums.
- Use verbs in camelCase for methods.
- Use camelCase for variables and parameters.
- Use SCREAMING_SNAKE_CASE for constants.
- Use descriptive names and avoid unclear abbreviations unless they are standard in the domain.
- For test methods, use the `featureUnderTest_testScenario_expectedBehavior()` convention where practical.

Layout and formatting:
- Use 4-space indentation; do not use tabs.
- Keep line length to 120 characters maximum, aiming for shorter lines when practical.
- Wrap long lines at sensible points, typically after commas or before operators.
- Keep statements readable and avoid awkward auto-formatting that reduces clarity.
- Use one statement per line where possible.

Statements and structure:
- Put every class in a package.
- Keep imports explicit and ordered consistently; avoid wildcard imports.
- Keep class variables non-public unless the class is a true data holder; prefer encapsulation.
- Declare variables where they are initialized and keep their scope as small as possible.
- Place array brackets with the type (e.g. `String[] names`), not the variable.
- Use braces for all control structures, even for single-statement loops and conditionals.
- Put the condition on a separate line and keep the body inside braces.
- Keep loop bodies enclosed in braces regardless of size.
- Return early when it makes the logic clearer.
- Prefer explicit, readable control flow over complex nested expressions.
- Keep methods short, focused, and easy to read.
- Prefer clear, single-purpose code over clever or overly condensed solutions.
- Keep class responsibilities narrow and avoid unrelated logic in the same class.
- Prefer constants over magic numbers.

Comments and Javadoc:
- Write all comments in English using American spelling; avoid local slang.
- Write descriptive header comments for all public classes and public methods.
- Header comments may be omitted for getters/setters, overriding methods when the parent Javadoc applies as-is, and classes or methods used for testing.
- Use Javadoc in the standard form, with the first sentence starting in the form "Returns ...", "Sends ...", "Adds ...", etc., as appropriate.
- Add `@param`, `@return`, and `@throws` tags when relevant.
- Do not add redundant or obvious Javadoc that merely restates the code.
- Avoid redundant comments; comments should explain intent, not restate the code.

Project conventions:
- Preserve existing project structure and naming conventions when they are already established.

For tests, also follow the repository's testing conventions:

- Place JUnit tests under `src/test/java` mirroring the production package structure.
- Name test classes after the class under test with a `Test` suffix.
- Prefer explicit static imports for JUnit assertions.
- Cover both positive and negative cases when adding or modifying behavior.
