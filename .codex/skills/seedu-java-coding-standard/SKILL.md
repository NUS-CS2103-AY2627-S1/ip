---
name: seedu-java-coding-standard
description: Enforce the SE-EDU Java coding standard (basic and intermediate rules) whenever creating, editing, refactoring, or reviewing Java code in this project.
---

# SE-EDU Java Coding Standard

Apply this skill to all Java production code, test code, examples, and code-review
feedback in this repository.

## Source and scope

The canonical source is the SE-EDU Java coding standard (basic and intermediate
rules):
<https://se-education.org/guides/conventions/java/intermediate.html>

For Java style topics that this document does not cover, follow the Google Java
Style Guide: <https://google.github.io/styleguide/javaguide.html>.

When a rule below conflicts with a fallback style rule, follow the SE-EDU rule.

## Required workflow

1. Inspect every Java file that will be changed and its surrounding code.
2. Apply every relevant rule in the checklist below.
3. Check all changed Java files for naming, layout, package/import, declaration,
   control-flow, and comment violations before finishing.
4. Preserve program behavior unless the requested task also requires a behavior
   change.
5. Follow the repository's testing requirements after changing Java code.

## Naming

- Write package names in lowercase. For a school project, use the project or group
  name as the root package, followed by logical package names.
- Use PascalCase nouns for classes and enums.
- Use camelCase for variables.
- Use SCREAMING_SNAKE_CASE for constants. Give related constants a common prefix.
- Use camelCase verbs for method names.
- Test methods may use
  `featureUnderTest_testScenario_expectedBehavior()`. The scenario, expected
  behavior, or both may be omitted when unnecessary.
- Treat abbreviations and acronyms as words inside names, such as `exportHtmlSource`
  and `openDvdPlayer`, not `exportHTMLSource` or `openDVDPlayer`.
- Write all names in English.
- Give large-scope variables descriptive names. Short scratch names such as `i`,
  `j`, `k`, `m`, `n`, `c`, and `d` are acceptable only in a small scope. Reserve
  `j`, `k`, and later iterator names for nested loops.
- Name boolean variables and methods so they read as booleans. Prefer prefixes such
  as `is`, `has`, `was`, `can`, and `should`. A boolean setter uses a form such as
  `setFound(boolean isFound)`.
- Use plural names for collections and arrays.

## Layout

- Indent with 4 spaces and never tabs.
- Aim for lines shorter than 110 characters. Never exceed 120 characters.
- Indent wrapped lines 8 spaces beyond their parent line.
- Wrap for readability: break after commas and before operators, including `.`,
  `&` in type bounds, and `|` in multi-catch clauses.
- Keep a method or constructor name attached to its opening parenthesis.
- Prefer a higher-level line break over a lower-level break.
- Keep a ternary expression on one line when readable; otherwise put `?` and `:`
  on separately indented continuation lines.
- Use K&R braces: put an opening brace at the end of its declaration or control
  statement line, and put the matching closing brace on its own line.
- Put each conditional body on separate lines and always use braces, even for a
  single statement. Always use braces for loop bodies too.
- Format `else`, `catch`, and `finally` on the same line as the preceding closing
  brace.
- Indent `case` and `default` labels one level inside their `switch`, and indent
  their statements one further level.
- Add an explicit `// Fallthrough` comment whenever a colon-style switch case
  intentionally continues into the next case without `break`.
- Surround operators with spaces, put a space after Java keywords and commas, and
  put a space after each semicolon in a `for` header. Surround a ternary colon with
  spaces.
- Separate logical units within a block with one blank line.

## Packages, imports, types, and variables

- Put every class in a package that matches its logical location.
- Keep import ordering consistent across the project. Group static imports first,
  then `java`/`javax`, third-party, and project imports, with one blank line between
  groups.
- List every imported class explicitly. Never use wildcard imports. Keep imports
  minimal and remove unused imports.
- Attach array brackets to the type, as in `int[] values`, not `int values[]`.
- Initialize variables where they are declared and declare them in the smallest
  practical scope. If no valid initial value exists yet, leave the variable
  uninitialized instead of assigning a fake value.
- Do not expose class variables as `public` unless they are constants or the class
  is a behavior-free data class. Prefer encapsulation and access methods.

## Comments and Javadoc

- Write comments in English using American spelling and no local slang.
- Add descriptive Javadoc header comments to every class and public method, except
  getters/setters, test code, and overrides whose inherited documentation applies
  exactly as written.
- Start a Javadoc block with `/**` on its own line. Align subsequent `*` characters,
  put one space after `*`, and place no blank line between the block and the
  declaration.
- Start the first sentence with a concise third-person verb such as `Returns`,
  `Sends`, or `Adds`, and end sentences and tag descriptions with punctuation.
- Put one blank Javadoc line between the description and tag section.
- Include either all useful `@param` tags or none. Omit them only when every
  parameter is already self-explanatory or fully explained in the description.
- Include `@return` when it adds information and omit it for `void` methods.
- Document thrown exceptions with `@throws` when applicable.
- Use `{@inheritDoc}` when an override needs inherited documentation plus a
  behavior-specific clarification.
- A short member comment may use a single-line Javadoc block.
- Indent comments to match the code they describe. Trailing comments are allowed
  when they remain readable.

## Final audit checklist

Before completing Java work, verify:

- package and import rules;
- noun/verb, case, boolean, acronym, and collection naming;
- 4-space indentation, K&R braces, wrapping, and the 120-character hard limit;
- braces around every loop and conditional body;
- declarations in the smallest useful scope and no inappropriate public fields;
- English comments and required, correctly punctuated Javadoc; and
- no behavior changes introduced solely by formatting or documentation cleanup.
