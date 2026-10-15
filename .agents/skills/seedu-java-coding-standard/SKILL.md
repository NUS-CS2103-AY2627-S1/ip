---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard to all Java source and test code in this project.
---

# SE-EDU Java coding standard

Use this skill for every Java file in this project, including tests. It is
based on the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/index.html).
For topics not covered there, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Required conventions

- Use lower-case package names; use English and American spelling in names and
  comments.
- Name classes and enums as nouns in `PascalCase`; name variables and methods
  in `camelCase`. Methods should be verbs. Use `SCREAMING_SNAKE_CASE` for
  constants, and use names such as `isOpen`, `hasData`, or `canEvaluate` for
  booleans. Collection variables should be plural. Test methods may use the
  `feature_scenario_expectedBehavior` form.
- Use four spaces for indentation, never tabs. Keep lines at or below 120
  characters, aiming for 110 or fewer. Wrap long expressions at readable
  boundaries, with continuation indentation of eight spaces relative to the
  parent line. Use K&R braces and put spaces around operators, after commas,
  and after Java keywords such as `if`, `for`, and `while`.
- Put every class in a package. Keep imports explicit and consistently ordered:
  static imports, standard Java imports, `org` imports, then other third-party
  imports, with each group separated by a blank line. Never use wildcard
  imports.
- Organize a class as documentation, the class declaration, static fields,
  instance fields, constructors, and methods. Put the access modifier first
  and keep fields private unless public access is required for a constant or a
  deliberately behavior-free data class. Declare and initialize variables in
  the smallest suitable scope. Attach array brackets to the type. Avoid
  unnecessary `this`; retain it when a parameter shadows a field.
- Always use braces for loops and conditionals, including single-statement
  bodies. Put conditional bodies on separate lines. Mark intentional switch
  fall-through with an explicit `// Fallthrough` comment. Format try-catch and
  try-catch-finally blocks consistently.
- Add descriptive Javadocs to all classes and public methods. Getters,
  setters, test methods, and overriding methods may omit Javadocs when the
  inherited documentation applies. Add header comments to non-trivial private
  methods and document non-obvious fields. Start a method summary with a
  concise verb such as `Returns`, `Adds`, or `Sends`; include useful `@param`,
  `@return`, and `@throws` tags.

## Verification

Use Java 25 for builds and run the relevant project checks, especially
`gradlew.bat checkstyleMain checkstyleTest`. Fix style findings in the source
instead of suppressing or weakening the project checks unless the requirement
is demonstrably inapplicable.
