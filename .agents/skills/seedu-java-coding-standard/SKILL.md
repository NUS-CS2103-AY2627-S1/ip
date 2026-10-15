---
name: seedu-java-coding-standard
description: Apply the required SE-EDU basic and intermediate Java coding rules when writing, reviewing, refactoring, or auditing Java code in this project.
---

# Follow the SE-EDU Java Coding Standard

Use the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
as the source of truth. Apply every basic and intermediate rule. For topics it
does not cover, follow the Google Java Style Guide without overriding SE-EDU or
repository-specific requirements.

## Naming

- Use lowercase package names and put every class in a package.
- Name classes and enums as English nouns in `PascalCase`.
- Name variables in `camelCase` and methods as verbs in `camelCase`.
- Name constants in `SCREAMING_SNAKE_CASE`; give associated constants a common
  prefix.
- Keep acronyms lowercase inside names, for example `exportHtml` and `Ui`.
- Make large-scope names descriptive; reserve short scratch names for very
  small scopes and conventional loop indices.
- Make boolean names read as booleans, preferably with `is`, `has`, `was`,
  `can`, or `should`.
- Use plural names for collections.
- Test methods may use
  `featureUnderTest_testScenario_expectedBehavior()`.

## Layout and statements

- Indent with four spaces, never tabs.
- Aim for at most 110 characters per line and never exceed 120.
- Indent wrapped lines eight spaces beyond the parent line. Break after commas
  and before operators, keeping a method name attached to its opening `(`.
- Use K&R braces and braces around every loop and conditional body, including
  single statements. Put conditional bodies on their own lines.
- Surround operators with spaces; add spaces after Java keywords, commas, and
  semicolons in `for` headers.
- Separate logical units inside a block with one blank line.
- List imports explicitly and use one consistent grouping and ordering.
- Attach array brackets to the type, such as `String[] arguments`.
- Initialize variables at declaration when a valid value is available, and
  declare them in the smallest useful scope.
- Do not expose mutable class variables as `public`.
- Mark intentional switch fall-through with `// Fallthrough`.

## Comments and Javadocs

- Write comments in English using American spelling.
- Add descriptive Javadocs to every public class and public method, except
  straightforward getters/setters, tests, and exact overrides whose inherited
  contract applies unchanged.
- Start a method's first sentence with a third-person verb such as `Returns`,
  `Creates`, or `Displays`.
- Put `/**` on its own line, align each `*`, and leave one blank Javadoc line
  before tags.
- Either document every parameter or omit all `@param` tags when the names are
  entirely self-explanatory. End tag descriptions with punctuation.
- Keep comments aligned with the code they describe and explain contracts or
  rationale rather than narrating obvious statements.

## Verify each change

1. Inspect all changed Java files for every rule above, not only line length.
2. Run `git diff --check` and scan for tabs, trailing spaces, wildcard imports,
   and lines longer than 120 characters.
3. Run `./gradlew test` with the repository's required JDK.
4. If application behavior changed, update the UI test plan and run the
   project-local `test-ui` skill.
