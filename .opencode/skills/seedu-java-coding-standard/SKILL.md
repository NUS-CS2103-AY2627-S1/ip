---
name: seedu-java-coding-standard
description: Enforces the SE-EDU Java coding standard (basic + intermediate) for this repo. Use when writing, editing, generating, formatting, or reviewing any Java code in src/**/*.java, and when answering Java style questions. Use ONLY for code style, not for git commits or branch names (use seedu-git-standard for those).
---

# SE-EDU Java coding standard (basic + intermediate)

Apply every rule below to ALL Java code in this project, including AI-generated code.
For topics not covered here, follow the Google Java Style Guide:
https://google.github.io/styleguide/javaguide.html

Full standard: https://se-education.org/guides/conventions/java/intermediate.html

## Naming

| Item | Rule | Examples |
|---|---|---|
| Packages | all lowercase; root = project name | `lenzabot.ui`, `lenzabot.storage` |
| Classes/enums | nouns, PascalCase | `Task`, `AudioSystem` |
| Variables/methods | camelCase; methods are verbs | `taskList`, `computeTotal()` |
| Constants | SCREAMING_SNAKE_CASE | `MAX_ITERATIONS`, `COLOR_RED` |
| Booleans | sound boolean; prefer `is/has/was/can/should` prefixes | `isFound`, `hasData` |
| Collections | plural name | `Collection<Task> tasks;` |
| Test methods | `featureUnderTest_testScenario_expectedBehavior()` | `parse_invalidDate_exceptionThrown()` |

Additional rules:
- Do not uppercase abbreviations/acronyms inside names: `exportHtmlSource()`, not `exportHTMLSource()`.
- Write all names in English.
- Use long names for large scopes; short scratch names (`i`, `j`, `k`) only for tiny scopes.
- Boolean setters use the form `void setFound(boolean isFound);`.
- Give associated constants a common prefix (`COLOR_RED`, `COLOR_GREEN`).

## Layout

- Indent with 4 spaces, never tabs.
- Keep lines below 110 characters when practical; 120 characters is the hard limit.
- Indent wrapped lines 8 spaces more than the parent line.
- Use K&R braces: opening brace on the same line.
- Break after commas and before operators, including `.`, `+`, and `|`.
- Keep a method or constructor name attached to its opening `(`.
- Prefer higher-level breaks over lower-level breaks.
- Write a ternary on one line or place each arm on its own indented line.
- Surround operators with spaces and put spaces after reserved words, commas, and `for` semicolons.
- Separate logical units within a block with one blank line.
- Always brace loop and conditional bodies, even when they contain one statement.
- Put conditional bodies on separate lines.

Use these statement forms:

```java
if (condition) {
    statements;
} else if (condition) {
    statements;
} else {
    statements;
}

for (initialization; condition; update) {
    statements;
}

while (condition) {
    statements;
}

switch (condition) {
    case ABC -> method("1");
    case DEF -> method("2");
    default -> method("0");
}

try {
    statements;
} catch (Exception exception) {
    statements;
} finally {
    statements;
}
```

For colon-style switches, include `// Fallthrough` whenever a case intentionally lacks `break`.

## Packages, imports, types, and variables

- Put every class in a package.
- Keep import ordering consistent.
- List imported classes explicitly; wildcard imports are forbidden.
- Attach array specifiers to the type: `int[] values`, never `int values[]`.
- Initialize variables where declared and declare them in the smallest possible scope.
- Do not expose class variables publicly unless the class is a behavior-less data class. Constants are exempt.

## Comments and Javadoc

- Write comments in English using American spelling and no local slang.
- Indent comments relative to their surrounding code.
- Add header comments to all public classes and methods, except getters/setters, tests, and overrides whose inherited Javadoc applies exactly.
- Begin method summaries with third-person verbs such as "Returns", "Adds", or "Sends".
- Put `/**` on its own line and align subsequent `*` characters.
- Put a blank line between the description and Javadoc tags.
- End parameter descriptions with punctuation.
- Include `@param` for all parameters or none; omit tags that add no information.
- A one-line Javadoc is allowed for class members.

## Compliance checklist

Before finishing a Java change, verify naming, 4-space indentation, braced bodies,
explicit imports, small-scope initialized variables, required Javadocs, and the
120-character hard line limit.
