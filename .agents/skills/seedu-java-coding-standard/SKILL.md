---
name: seedu-java-coding-standard
description: Apply the mandatory SE-EDU basic and intermediate Java coding standard when creating, editing, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Apply the [SE-EDU basic and intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
to every Java change in this project. Use the Google Java Style Guide only for
topics the SE-EDU standard does not cover. Preserve user behavior unless a
convention-compliant change requires otherwise.

## Naming

- Use lowercase package names grouped logically under the project name.
- Use noun-based PascalCase class and enum names.
- Use camelCase English names for variables and verb-based methods.
- Use SCREAMING_SNAKE_CASE for constants; give associated constants a common
  prefix.
- Keep abbreviations and acronyms in normal title/camel case, such as `Ui` and
  `exportHtmlSource()`.
- Use descriptive names in large scopes and short scratch names only in small
  scopes. Use `i`, then `j` or `k`, for nested iterators.
- Name booleans so they read as booleans, preferably with `is`, `has`, `was`,
  `can`, or `should`. Name boolean setters as `setX(boolean isX)`.
- Use plural names for collections.
- Test methods may use
  `featureUnderTest_testScenario_expectedBehavior()`; omit segments only when
  the remaining name still explains the test.

## Layout

- Indent with four spaces, never tabs.
- Keep lines below 110 characters where practical and never exceed 120.
- Indent wrapped lines eight spaces beyond the parent line. Break after commas
  and before operators; keep a method or constructor name attached to `(`.
- Prefer higher-level line breaks and format ternaries consistently.
- Use K&R braces. Always use braces for loop and conditional bodies, put
  conditional bodies on separate lines, and mark intentional switch
  fall-through with `// Fallthrough`.
- Put spaces around operators and after Java keywords, commas, and semicolons.
  Separate logical units within a block with one blank line.

## Declarations and statements

- Put every class in a package.
- Keep import ordering consistent, list imports explicitly, and remove unused
  imports. Never use wildcard imports.
- Attach array brackets to the type, such as `int[] values`.
- Declare variables in the smallest useful scope and initialize them where
  declared when a valid value is available.
- Do not expose class variables as public unless the class is a behavior-free
  data class; constants are exempt.

## Comments and Javadoc

- Write comments in English using American spelling. Explain intent and
  rationale rather than narrating obvious code.
- Write descriptive Javadoc for every public class and public method, except
  obvious getters/setters, overrides whose inherited contract applies exactly,
  and test code. Project-specific user instructions may require documentation
  even for those exceptions.
- Begin Javadoc with a concise summary sentence such as “Returns …” or
  “Adds …”. Keep `/**` on its own line, align `*`, leave a blank line before
  tags, punctuate tag descriptions, and place no blank line before the
  declaration.
- Document either all parameters or none. Include `@return` and `@throws` when
  they add information. Use `{@inheritDoc}` when reusing and refining an
  overridden contract.
- Indent comments with the code they describe.

## Verification

After editing Java code:

1. Review naming, layout, imports, control-flow braces, and Javadoc against this
   skill.
2. Check for tabs, wildcard imports, trailing whitespace, and lines over 120
   characters.
3. Compile with Java 25 and run relevant Gradle tests when the environment
   permits.
4. Report any remaining violation or verification limitation explicitly.
