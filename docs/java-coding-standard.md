# Java Coding Standard

Use this checklist when creating, reviewing, or changing Java code in this
repository. Project-specific rules and the SE-EDU conventions take precedence.
For topics they do not cover, follow the Google Java Style Guide.

Sources:

- [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)

## Packages and source files

- Put every class in a lowercase package. Use `shan` as this project's root
  package.
- Place the package declaration first, followed by explicit imports. Do not use
  wildcard imports.
- Keep one top-level class in each source file and match the filename to the
  class name.
- Remove unused imports and use a consistent import order.

## Naming

- Name classes and enums with nouns in `PascalCase`.
- Name methods with verbs in `camelCase`.
- For test methods, underscores may separate the feature, scenario, and expected
  behavior, e.g., `parse_blankInput_exceptionThrown()`.
- Capitalize acronyms as normal words when they form part of a name, e.g.,
  `exportHtmlSource()` rather than `exportHTMLSource()`.
- Name variables in `camelCase` and constants in `SCREAMING_SNAKE_CASE`.
- Give booleans names that read as true-or-false conditions, such as `isDone`,
  `hasData`, or `wasLoaded`.
- Use plural names for collections, such as `tasks` and `lines`.
- Prefer descriptive English names. Avoid unexplained abbreviations; short names
  such as `i` and `j` are acceptable for small local loops.

## Layout and whitespace

- Indent with four spaces; never use tabs.
- Keep lines below 110 characters where practical. The hard limit is 120
  characters.
- Indent continuation lines by eight spaces relative to the parent line.
- Use K&R braces: put an opening brace at the end of the declaration or control
  statement and the closing brace on its own line.
- Always use braces for conditionals and loops, including one-line bodies.
- Put one statement on each line.
- Use blank lines to separate logical sections and class members.
- Put spaces around binary operators, after commas, and between a control-flow
  keyword and its opening parenthesis.
- When wrapping an expression, prefer high-level breaks. Break after commas and
  before operators, method-chain dots, and the `|` in a multi-catch clause.

## Variables and fields

- Declare and initialize a variable together when practical.
- Give variables the smallest useful scope.
- Declare only one variable in each declaration.
- Attach array brackets to the type, for example `String[] arguments`.
- Keep fields non-public and expose only the operations callers need.
- Use `final` when a field or local variable should not be reassigned and doing
  so improves clarity.

## Control flow and exceptions

- Format `if`, loop, `switch`, `try`, and `catch` blocks consistently with the
  brace rules above.
- Do not allow silent `switch` fallthrough. If fallthrough is intentional, mark
  it with a `// Fallthrough` comment.
- Use specific exception types that describe the failure. Give caught exceptions
  meaningful names; use `ignored` only when ignoring one is deliberate.

## Javadoc and comments

- Write comments and identifiers in clear English, use American spelling, and
  avoid slang.
- Add Javadoc to every public class, constructor, and method, except obvious
  getters, setters, and methods whose inherited documentation is sufficient.
- Start a Javadoc summary with a short third-person verb, such as `Returns`,
  `Adds`, or `Parses`.
- Format Javadoc with aligned `/**` and `*` markers and leave a blank line before
  block tags.
- Describe `@param`, `@return`, and `@throws` tags with capitalized, punctuated
  phrases.
- Use comments to explain intent or a non-obvious reason. Do not restate code
  that is already clear.

## Before finishing a Java change

- Confirm package declarations, filenames, names, indentation, braces, imports,
  line lengths, and Javadoc follow this checklist.
- Preserve existing user-visible behavior unless the requirement calls for a
  behavior change.
- Compile with Java 25 and enable compiler warnings:

  ```bash
  javac -Xlint:all -d out -sourcepath src/main/java \
      src/main/java/shan/Shan.java
  ```

- Check Javadoc when public APIs changed:

  ```bash
  javadoc -quiet -Xdoclint:all -d /tmp/shan-javadocs \
      -sourcepath src/main/java -subpackages shan
  ```

- Run the relevant automated and UI tests.
- Check for whitespace errors with `git diff --check`.

This project intentionally follows SE-EDU's four-space indentation and
120-character hard limit, even where Google Java Style uses different defaults.
