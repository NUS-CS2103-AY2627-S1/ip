---
name: seedu-java-coding-standard
description: Use when writing, refactoring, reviewing, or auditing Java code in this repository against the required SE-EDU Basic and Intermediate Java coding standard.
---

# Follow the SE-EDU Java Coding Standard

Apply the cumulative Basic + Intermediate SE-EDU Java coding standard to Java
work in this repository.

Authoritative sources:

- Basic: <https://se-education.org/guides/conventions/java/basic.html>
- Basic + Intermediate: <https://se-education.org/guides/conventions/java/intermediate.html>

The Intermediate page is cumulative: it includes both Basic and Intermediate
rules. Advanced rules are optional and are outside this skill unless the user or
current course instructions require them. For a Java topic not covered by the
SE-EDU standard, use the Google Java Style Guide as the fallback. Do not use the
Google guide to replace an SE-EDU rule.

Preserve the force of the source wording: `must` and explicit requirements are
mandatory; `should`, `try`, and `as much as possible` are recommendations; and
`may` or `can` indicate permitted alternatives. Current repository and course
requirements take precedence when they deliberately constrain or override a
general convention.

## Naming

- Write package names in lowercase. For school projects, use the project or
  group name as the root, followed by logical group names; do not use a package
  implying the code is produced by NUS, such as `edu.nus.comp.*`.
- Use noun-based PascalCase names for classes and enums.
- Use camelCase for variables.
- Use SCREAMING_SNAKE_CASE for constants.
- Use verb-based camelCase names for methods.
- Test method names may use underscores in the form
  `featureUnderTest_testScenario_expectedBehavior()`. The expected-behavior
  part, or both the scenario and expected-behavior parts, may be omitted when
  the test's scope makes them unnecessary.
- Do not capitalize an abbreviation or acronym when it is part of a name; use
  forms such as `exportHtmlSource()` and `openDvdPlayer()`.
- Write all names in English.
- Give large-scope variables descriptive names. Short names are acceptable for
  scratch values whose use is confined to a few nearby lines.
- Name boolean variables and methods so they read as booleans, preferably with
  prefixes such as `is`, `has`, or `was`. A boolean setter must use a form such
  as `setFound(boolean isFound)`.
- Use plural names for collections of objects.
- Iterator variables may be named `i`, `j`, or `k`; reserve `j`, `k`, and later
  letters for nested loops.
- Give associated constants a common prefix.

## Layout

- Indent with 4 spaces, never tabs.
- Aim to keep lines below 110 characters and never exceed the 120-character
  hard limit. Wrap a line when needed.
- Indent a wrapped continuation 8 spaces more than its parent line.
- Choose line breaks for readability rather than accepting IDE formatting
  blindly. In general, break after a comma and before an operator, including
  `.`, `&` in type bounds, and `|` in catch clauses. Keep a method or constructor
  name attached to its opening parenthesis, and prefer higher-level breaks. A
  ternary expression may stay on one line or place `?` and `:` on separate
  continuation lines.
- Use K&R (Egyptian) braces: put the opening brace at the end of the controlling
  line. Format methods, `if`/`else`, loops, `switch`, and `try`/`catch`/`finally`
  consistently with that style.
- In a traditional `switch`, add `// Fallthrough` whenever execution
  intentionally continues from one case into the next without a `break`.
- Put spaces around operators, after Java reserved words, after commas, around
  a colon used as a binary or ternary operator, and after semicolons in a `for`
  header. The colon spacing rule does not apply to a `switch` label.
- Separate logical units inside a block with one blank line.

## Statements

- Put every class in a package.
- Keep import ordering consistent. List imported classes explicitly, keep the
  list minimal, and do not use wildcard imports.
- Attach an array specifier to the type, as in `int[] values`, not to the
  variable.
- Initialize variables where they are declared and declare them in the smallest
  possible scope. If no valid initial value exists, leave the variable
  uninitialized rather than assigning a phony value.
- Do not expose class variables as `public` unless the class is a behavior-free
  data class. This restriction does not apply to constants.
- Always wrap loop bodies in braces, including single-statement bodies.
- Put a conditional and its body on separate lines, and always wrap the body in
  braces, including single-statement bodies.

## Comments and Javadoc

- Write comments in English, use American spelling, and avoid local slang.
- Write descriptive header comments for every class and every public method.
  They may be omitted for getters and setters, test classes and methods, and an
  overriding method when the inherited Javadoc applies exactly as written.
- Use a Javadoc block beginning with `/**` on its own line. Align subsequent
  `*` characters and include one space after each `*`.
- Start with a short summary sentence. For methods, begin it with a
  third-person verb such as `Returns`, `Sends`, or `Adds`.
- Put one blank comment line between the description and block tags. End each
  parameter description with punctuation, and do not put a blank source line
  between the Javadoc block and the declaration.
- `@return` may be omitted for a `void` method or when the return value is
  already obvious from the description.
- Omit `@param` tags only when every parameter name is self-explanatory or all
  parameters are already explained in the description. Include tags for all
  parameters or for none of them.
- Use the `@inheritDoc` tag when inherited documentation is applicable but needs
  further explanation for the overriding method.
- A short class-member Javadoc may be written on one line.
- Indent comments relative to the surrounding code. Trailing comments are
  permitted when they remain readable.

## Apply and verify

When writing or changing Java, apply these rules as part of the change rather
than as unrelated cleanup. When reviewing, report concrete violations with file
and line evidence. Before completion, inspect all affected Java lines for these
rules and run the repository's relevant formatting, build, and test checks.
