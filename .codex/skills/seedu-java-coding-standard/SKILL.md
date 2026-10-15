---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java coding conventions when creating, editing, or reviewing Java code in this project.
---

# Seedu Java Coding Standard

Use this skill for every Java source and test change in this repository. It is based on the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
For topics not covered here, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Naming

- Put package names in lowercase; use the project name followed by logical subpackages.
- Name classes and enums as nouns in PascalCase. Name variables in camelCase.
- Name methods as verbs in camelCase. Boolean names should read as booleans, using prefixes
  such as `is`, `has`, `was`, or `can` where appropriate.
- Name constants with SCREAMING_SNAKE_CASE. Use plural names for collections.
- Keep names in English and write acronyms in normal word case when they are part of a name
  (for example, `exportHtmlSource`, not `exportHTMLSource`).
- Test methods may use underscores in the form
  `featureUnderTest_testScenario_expectedBehavior`.
- Use names that reflect scope: descriptive names for long-lived variables and short names
  only for small-scope scratch variables or loop indices.

## Layout and statements

- Use four spaces for indentation, never tabs. Use K&R braces for classes, methods, and control
  statements.
- Keep lines at or below 120 characters; prefer wrapping before operators or after commas with
  continuation indentation of eight spaces relative to the parent line.
- Keep method or constructor names attached to their opening parenthesis when wrapping.
- Put logical units in a block on separate lines with one blank line between them where it
  improves readability.
- Always use braces around loop and conditional bodies, including single-statement bodies.
- Keep `if`, `else`, `for`, `while`, `switch`, and `try-catch` formatting consistent with the
  standard examples.
- Add an explicit `// Fallthrough` comment when a switch case intentionally falls through.
- Surround operators, commas, and relevant colons with appropriate whitespace.

## Packages, imports, and variables

- Put every class in a package.
- Keep import ordering consistent within the project, list imported classes explicitly, and
  remove unused imports.
- Attach array brackets to the type, such as `String[] names`.
- Initialize variables at declaration when practical and declare them in the smallest scope
  that needs them.
- Keep class variables private unless they are constants or the class is a behavior-free data
  class; use methods to preserve encapsulation.

## Comments and Javadoc

- Write comments in English using American spelling and avoid local slang.
- Write descriptive header comments for all classes and public methods. Getters, setters,
  overridden methods whose inherited documentation applies exactly, and test methods may omit
  comments when the exception is genuinely appropriate.
- Use `/** ... */` for Javadoc. Put the opening delimiter on its own line, begin with a concise
  summary sentence, and keep the comment directly above its declaration.
- Add `@param`, `@return`, and `@throws` tags when they clarify the public contract. End tag
  descriptions with punctuation and do not add empty or redundant tags.
- Document non-obvious fields and non-trivial private methods when their purpose or behavior is
  not clear from their names.

## Review checklist

Before finishing a Java change:

1. Inspect changed declarations and nearby code for naming, layout, braces, line length,
   imports, visibility, and initialization violations.
2. Check that new or changed public APIs have useful Javadoc and that comments describe intent,
   not merely an obvious implementation step.
3. Preserve behavior and public APIs when applying style fixes unless the user explicitly asks
   for a design change.
4. Run the repository's relevant tests and, for documentation changes, the Javadoc task as
   well. Use Java 25 as required by the repository instructions.
