# Java coding standards

This project follows the [SE Education Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
The course requires the basic and intermediate rules; the advanced rules are
optional. This file turns the required rules and the project's code-quality
expectations into a checklist for day-to-day development.

## Before submitting Java changes

1. Use JDK 25.
2. Run `./gradlew checkstyleMain checkstyleTest test` from the project root.
3. If the user-visible console behavior changed, update
   [`test/ui-test-plan.md`](../test/ui-test-plan.md) and run the UI test plan.
4. Review the diff with `git diff --check` and remove unintended files or
   generated output.

Do not consider a Java change complete while any of these checks fail.

## Naming

- Use lowercase package names.
- Use PascalCase nouns for classes and enums.
- Use camelCase verbs for methods and camelCase names for variables.
- Use `SCREAMING_SNAKE_CASE` for constants.
- Name boolean fields and methods with prefixes such as `is`, `has`, `was`,
  `can`, or `should`.
- Use plural names for collections and arrays.
- Keep acronyms in normal camel case, such as `parseJson`, not `parseJSON`.
- Use the three-part form
  `featureUnderTest_testScenario_expectedBehavior()` for JUnit test names
  when the scenario is not obvious from the method name alone.
- Write names and comments in English using American spelling.

## Layout and formatting

- Use four spaces for indentation; never use tabs.
- Keep every line at 120 characters or fewer. Prefer fewer than 110
  characters and wrap long lines at readable boundaries.
- Use K&R braces and braces for every conditional and loop body, including
  one-line bodies.
- Put spaces around operators, after commas, and after Java keywords such as
  `if` and `for`.
- Separate logical units in a method with one blank line.
- Use explicit imports in a consistent order; never use wildcard imports.
- Attach array brackets to the type, for example `String[] names`.
- Keep class members in this order: documentation, class variables, instance
  variables, constructors, and methods.

## Design and code quality

- Put every class in a named package.
- Keep fields private and expose behavior through methods.
- Declare variables in the smallest scope possible and initialize them where
  they are declared when practical.
- Prefer methods with one clear responsibility. When a method grows beyond
  about 30 lines, first consider extracting a well-named helper.
- Avoid more than three levels of nesting. Use guard clauses and early
  returns to keep the normal path easy to follow.
- Avoid complicated expressions. Calculate meaningful intermediate values
  with descriptive names instead of hiding several conditions in one line.
- Replace unexplained numeric, string, and character literals with named
  constants or enums.
- Keep code at one level of abstraction within a method and group related
  statements in a logical order.
- Do not leave unused parameters, dead code, duplicated logic, or placeholder
  comments in the codebase.
- Handle errors explicitly. Catch the narrowest useful exception and document
  intentional recovery or ignoring of malformed input.
- Choose the simplest design that satisfies the requirements. Do not add
  abstractions or optimisations without a clear benefit.

## Documentation

- Add a Javadoc comment to every class and public method. Getters, setters,
  test methods, and overriding methods may omit Javadoc when the inherited or
  obvious behavior is sufficient.
- Add Javadoc to nontrivial private methods and fields whose purpose is not
  obvious.
- Start a method summary with a third-person verb such as `Returns`, `Adds`,
  `Calculates`, or `Validates`.
- Put a blank line between the summary and the tag section.
- End every `@param`, `@return`, and `@throws` description with punctuation.
- Document behavior and constraints, not implementation details that can be
  read directly from the code.
- Add `// Fallthrough` whenever a switch case intentionally falls through.

When this document does not cover a topic, follow the [Google Java Style
Guide](https://google.github.io/styleguide/javaguide.html).

## Testing expectations

- Keep JUnit coverage for at least the 50% highest-value methods, prioritising
  core, complex, and critical business logic.
- Add or update tests whenever behavior changes, especially for invalid input
  and boundary cases.
- Keep console output stable unless the behavior change intentionally includes
  a new transcript; update the UI test plan in that case.
- Run the relevant tests after every code change, then run the complete Gradle
  command listed above before submitting.

## References

- [NUS CS2103/T standards and conventions](https://nus-cs2103-ay2627-s1.github.io/website/admin/standardsAndConventions.html)
- [SE Education Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
- [CS2103/T code quality guide](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/codeQuality.html)
