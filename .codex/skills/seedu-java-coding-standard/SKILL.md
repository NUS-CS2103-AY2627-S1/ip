---
name: seedu-java-coding-standard
description: "Review and write Java code in this project using the SE-EDU basic-plus-intermediate coding standard."
---

# Seedu Java Coding Standard

Apply this skill to every new or modified Java source file in this project, including
tests. Preserve behavior and public APIs unless the user explicitly requests a
behavioral change. Use the source guide for details not repeated here:
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).

## Naming

- Keep package names lowercase; use the project name followed by logical package
  names (for example, `herta.parser`).
- Use English names. Name classes and enums as PascalCase nouns, methods as
  camelCase verbs, variables as camelCase, and constants as
  `SCREAMING_SNAKE_CASE`.
- Test methods may use up to three underscore-separated parts in the form
  `featureUnderTest_testScenario_expectedBehavior`.
- Do not write acronyms as consecutive capitals in identifiers (`xmlParser`, not
  `XMLParser`). Use longer names for values with wider scope and short names only
  for local scratch values. Use `i`, `j`, or `k` for iterators, with `j` and `k`
  reserved for nested loops.
- Prefix boolean fields, local variables, parameters, and record components with
  a predicate such as `is`, `has`, `can`, `should`, or `was`. Avoid vague names
  such as `all`, `done`, `valid`, or `requested`. Boolean methods should also
  read as predicates; action methods that return an operation status are an
  exception when their Javadoc explains what the status represents. Use the
  same convention for boolean setter parameters, and use plural names for
  collections. Give related constants a shared prefix.

## Layout and whitespace

- Use four spaces for indentation; never use tabs.
- Keep lines at or below 120 characters, aiming for 110 or fewer. When wrapping,
  indent continuation lines by eight spaces beyond the parent line and break at
  readable, higher-level boundaries (usually after commas or before operators).
  Keep method names attached to their opening parenthesis.
- Use K&R braces. Keep method, constructor, conditional, loop, switch, and
  try/catch declarations in the standard multiline form. Put `else` and
  `catch` on the closing-brace line.
- Surround operators and binary/ternary colons with spaces; put spaces after
  reserved words, commas, and `for` semicolons. Separate logical units in a
  block with one blank line.

## Statements and declarations

- Put every class in a package. Keep imports explicit, minimal, and consistently
  ordered. In this project, use static imports first, then `java`/`javax`,
  `org`, and other third-party imports alphabetically within each group.
- Attach array brackets to the type (`String[] values`). Initialize variables at
  declaration when practical and keep them in the smallest scope possible.
- Keep class fields non-public, except for constants and behavior-free data
  classes.
- Always use braces around loop and conditional bodies, including one-statement
  bodies. Put a conditional body on separate lines. Mark intentional switch
  fall-through with an explicit `// Fallthrough` comment.

## Comments and Javadoc

- Write comments in English using American spelling, without local slang. Indent
  comments with the code they describe.
- Add descriptive Javadoc to every public class and public method, except
  getters/setters, applicable overrides, and test code. Start with a short
  present-tense summary such as `Returns ...` or `Adds ...`; document parameters,
  return values, and thrown exceptions when they add useful information. Keep the
  Javadoc immediately above its declaration with standard leading asterisks and
  spacing.
- Add concise Javadoc to non-obvious class members.

## Verification

After changing Java code, review the relevant tests and run the project checks
with Java 25. Use Checkstyle as the automated conformance check; do not weaken
the project Checkstyle configuration to hide a violation. For topics this guide
does not cover, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
Review the CS2103 code-quality guidance as well, especially method length,
class size, and keeping each method at one level of abstraction.
