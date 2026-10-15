---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding standard when creating, reviewing, or modifying Java source and test code in this project.
---

# SE-EDU Java Coding Standard

Make all Java source and test code follow the
[SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
Use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) for topics
the SE-EDU standard does not cover. Preserve project-specific requirements when they are stricter.

## Naming

- Use lowercase package names organized under the project name.
- Name classes and enums with English nouns in PascalCase.
- Name methods with English verbs in camelCase and variables in camelCase.
- Name constants in SCREAMING_SNAKE_CASE. Give related constants a common prefix.
- Keep acronyms lowercase within names, such as `exportHtmlSource` rather than
  `exportHTMLSource`.
- Give wider-scope variables descriptive names; reserve short names for obvious, short-lived
  scratch values and loop indices. Use `j`, `k`, and later letters only for nested loops.
- Prefix boolean names with words such as `is`, `has`, `can`, `was`, or `should` so they read as
  boolean statements. Name boolean setters in the form `setFound(boolean isFound)`.
- Use plural names for collections and arrays.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`, omitting parts only when
  the remaining name still describes the test.

## Layout

- Indent with 4 spaces, never tabs. Indent wrapped lines 8 spaces beyond their parent line.
- Prefer lines shorter than 110 characters and never exceed 120 characters.
- Break after commas and before operators, including `.`, `&` in type bounds, and `|` in multi-catch
  clauses. Keep a method name attached to its opening parenthesis and prefer higher-level breaks.
- Use K&R braces. Put `else`, `catch`, and `finally` on the same line as the preceding closing brace.
- Indent `case` and `default` one level inside `switch`, and indent their statements one further
  level. Mark intentional fall-through with `// Fallthrough`.
- Put spaces around operators, after Java keywords and commas, and after semicolons in `for`
  headers. Surround a ternary colon with spaces.
- Separate logical units within a block with one blank line.

## Statements and declarations

- Put every class in a package.
- Order imports consistently, list every imported class explicitly, and remove unused imports.
- Attach array brackets to the type, as in `int[] values`.
- Initialize variables when declared and declare them in the smallest useful scope. Leave a value
  uninitialized instead of assigning a fake default when immediate valid initialization is
  impossible.
- Do not expose class variables publicly unless the class is a behavior-free data class; constants
  are exempt.
- Always use braces around loop and conditional bodies, even for one statement. Put the body on a
  separate line.

## Comments and Javadoc

- Write comments in English using American spelling and avoid slang.
- Add descriptive Javadoc to every class and public method. It may be omitted for getters/setters,
  test code, and overrides when inherited documentation applies exactly.
- Begin Javadoc with a short summary sentence using third-person verbs such as `Returns`, `Adds`,
  or `Sends`.
- Put `/**` on its own line, align each `*`, include a space after it, and leave no blank line between
  the comment and declaration.
- Separate the description from tags with one blank Javadoc line. End tag descriptions with
  punctuation. Include either all useful `@param` tags or none when every parameter is already
  self-explanatory. Omit `@return` for `void` methods or when it adds no information.
- Indent comments with the code they describe. Use trailing comments only when they remain clear.

## Review checklist

When reviewing or changing Java code, check every affected file for naming, line length,
indentation, braces, whitespace, imports, variable scope, encapsulation, and Javadoc. Keep behavior
unchanged when the task is only a standards cleanup, and run the repository-required tests after
edits.
