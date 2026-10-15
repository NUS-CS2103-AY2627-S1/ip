---
name: seedu-java-coding-standard
description: "Apply the SE-EDU basic and intermediate Java coding conventions to all Java code in this project."
---

# Seedu Java Coding Standard

Apply these conventions whenever you create, edit, review, or refactor Java source or test code in
this project. The authoritative guide is the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html);
use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) for topics
not covered here.

## Naming

- Keep package names lowercase and use the project name as the package root.
- Name classes and enums as PascalCase nouns; name methods as camelCase verbs.
- Name variables and parameters in camelCase. Use SCREAMING_SNAKE_CASE for constants.
- Use English and avoid uppercase abbreviations inside names (`exportHtmlSource`, not
  `exportHTMLSource`).
- Give large-scope variables descriptive names; short names such as `i`, `j`, and `k` are only
  appropriate for small-scope scratch or nested-loop indices.
- Make boolean names read as predicates, preferably with `is`, `has`, `was`, or `can`. Boolean
  setters take a parameter such as `isFound` and use the form `setFound(boolean isFound)`.
- Use plural names for collections. Related constants share a common prefix.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior()`.

## Layout and whitespace

- Use four spaces for indentation, never tabs. Use K&R braces for classes, methods, and control
  statements.
- Keep lines at or below 120 characters; treat 110 characters as a soft target. Wrap long lines
  for readability, indenting continuation lines by eight spaces beyond the parent indentation.
- Prefer breaks after commas and before operators; keep a method or constructor name attached to
  its opening parenthesis.
- Put spaces around operators, after Java keywords and commas, and after semicolons in `for`
  statements. Separate logical units in a block with one blank line.

## Statements, imports, and variables

- Put every class in a package. Keep import ordering consistent, list imported classes explicitly,
  and remove unused imports; never use wildcard imports.
- Attach array brackets to the type (`int[] values`).
- Initialize variables at declaration when possible and declare them in the smallest useful scope.
- Keep class variables private unless the type is a behavior-free data class; constants are exempt.
- Always use braces for loop and conditional bodies, including single-statement bodies. Put the
  conditional body on its own line.
- For intentional switch fall-through, include an explicit `// Fallthrough` comment.

## Documentation

- Write comments in English using American spelling and without local slang. Indent comments with
  the code they describe.
- Add descriptive Javadoc to every public class and public method, except getters/setters,
  overriding methods whose inherited documentation applies exactly, and test code.
- Javadoc starts with `/**` on its own line, opens with a concise third-person summary such as
  `Returns ...` or `Adds ...`, and uses correctly indented `*` lines. Include a blank line before
  tags, punctuation in tag descriptions, and `@param`, `@return`, and `@throws` tags when they add
  useful information.
- Add member Javadoc for non-obvious fields and nontrivial private methods when it improves
  understanding.

## Review checklist

Before finishing a Java change, inspect the diff for naming, braces, indentation, line length,
imports, variable scope, encapsulation, and required Javadocs. Run the project's tests after code
changes and update relevant tests when behavior changes.
