---
name: seedu-java-coding-standard
description: Apply and review the SE-EDU basic and intermediate Java coding standard for all Java code created or modified in this project.
---

# SE-EDU Java Coding Standard

Apply the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) to every Java change in this repository. Use the Google Java Style Guide only for topics that SE-EDU does not cover.

## Naming

- Use lowercase package names; PascalCase nouns for classes and enums; camelCase verbs for methods; camelCase variable names;
  and SCREAMING_SNAKE_CASE for constants. Give related constants a common prefix.
- Use English names and lowercase acronyms within identifiers, such as `exportHtmlSource`. Use longer names for variables with
  wider scope; short scratch names are suitable only when their use is local. Reserve `j`, `k`, etc. for nested loops.
- Make boolean variables and methods read as booleans, preferably with prefixes such as `is`, `has`, or `was`. Name boolean
  setters like `setFound(boolean isFound)`. Use plural names for collections and arrays.
- Test method names may use `featureUnderTest_testScenario_expectedBehavior`; omit the final part, or both later parts,
  when the test covers all variations of those parts.

## Layout

- Use four spaces, never tabs. Indent wrapped lines eight spaces beyond their parent line. Prefer lines under 110 characters
  and never exceed 120 characters.
- Wrap for readability: break after commas and before operators, including `.`, `&` in type bounds, and `|` in multi-catch.
  Keep a method or constructor name with its opening `(` and prefer higher-level breaks. Ternaries may stay on one line or
  place `?` and `:` on separate continued lines.
- Use K&R braces. Put `} else`, `} catch`, `} finally`, and `} while` on the forms shown in the guide. Always brace loop
  and conditional bodies, including single statements, and put conditional bodies on separate lines.
- Indent `case` and `default` one level inside `switch`, with their statements one level further. Mark intentional
  fallthrough with `// Fallthrough`.
- Put spaces around operators and ternary colons, and after keywords, commas, and `for` semicolons. Separate logical units
  within a block with one blank line.

## Statements

- Put every class in a package. In school projects, use the group or project name as the root package, not `edu.nus.comp`.
  Keep imports consistently ordered, explicit, minimal, and free of wildcards.
- Attach array brackets to the type. Declare variables in the smallest practical scope and initialize them where declared
  when a valid value exists; otherwise leave them uninitialized rather than using a phony value.
- Never make a class field public unless it is a constant or belongs to a behavior-free data class. This also applies to
  nonconstant `final` fields.

## Comments and Javadoc

- Write comments in English with American spelling, avoid local slang, and align comments with the code they describe.
- Add descriptive Javadocs to every class and public method, except classes or methods used for testing, getters/setters,
  and overrides whose inherited documentation applies exactly. Use `{@inheritDoc}` when extending inherited documentation.
- Start method Javadocs with a short summary whose first sentence begins with a third-person verb such as `Returns`,
  `Adds`, or `Sends`. Put `/**` on its own line, align the `*` characters with a following space, leave a blank line
  before tags, punctuate tag descriptions, and put no blank line between the Javadoc and declaration.
- Include `@param` tags for all parameters or none; omit them only when all parameters are self-explanatory or covered
  in the description. Omit `@return` for `void` methods or when the return value is already clear. Document non-obvious
  exceptions with `@throws`.

## Workflow

When creating or editing Java, inspect the surrounding file for affected violations, apply these rules without changing unrelated behavior, and run the relevant JUnit tests. Before finishing, check changed Java files for tabs, wildcard imports, lines over 120 characters, missing braces, inconsistent switch indentation, and missing required Javadocs.
