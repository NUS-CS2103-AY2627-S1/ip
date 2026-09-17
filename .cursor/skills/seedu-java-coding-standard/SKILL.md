---
name: seedu-java-coding-standard
description: >-
  Applies SE-EDU Java coding standard (basic + intermediate) to Mouse/ip Java
  code. Use when writing, editing, reviewing, or refactoring any Java source or
  test in this project, or when the user mentions coding standard, Checkstyle,
  or SE-EDU Java conventions.
---

# SE-EDU Java coding standard (basic + intermediate)

Follow https://se-education.org/guides/conventions/java/intermediate.html for all Java in this project.
Use Google Java Style for anything not covered there.

## When to apply

Read this skill before writing or changing Java under `src/main/java` or `src/test/java`.
Fix violations in code you touch. Prefer matching nearby style when the guide allows options.

## Naming

- Packages: all lower case; root is project name (`mouse`, `mouse.task`, …). Never `edu.nus.*`.
- Classes/enums: nouns, PascalCase.
- Methods: verbs, camelCase.
- Variables: camelCase. Large scope → longer names. Scratch vars (`i`, `j`) OK in small scope.
- Constants: `SCREAMING_SNAKE_CASE`. Related constants share a prefix (`COLOR_RED`).
- Booleans: `is`/`has`/`was` prefixes (`isDone`, `hasData`).
- Collections: plural names (`tasks`, `values`).
- Acronyms in names are not all-caps: `exportHtmlSource`, not `exportHTMLSource`.
- Test methods: `featureUnderTest_testScenario_expectedBehavior()` when names are long.
- English only.

## Layout

- Indent 4 spaces (no tabs).
- Soft line length 110; hard limit 120. Wrap with +8 spaces relative to parent.
- Break after commas; break before operators (including `.`).
- Keep method name attached to `(`.
- K&R braces: `{` on same line as `if`/`while`/`else`/`try`.
- Space around operators; space after keywords (`if (`); space after commas.
- Blank line between logical units in a block.
- Switch/`if`/`for`/`while`/`try` forms match the SE-EDU examples (including `// Fallthrough` when intentional).

## Statements

- Every class is in a package.
- Imports: explicit (no `*`); keep a consistent order (static, then java, then others).
- Arrays: `int[] a`, not `int a[]`.
- Declare and initialize in the smallest scope.
- No public instance fields (constants OK). Prefer encapsulation.
- Always brace loop and conditional bodies, even one-liners.
- Put the condition on its own line: never `if (x) doThing();`.

## Comments

- English, American spelling.
- Header comments for all classes and public methods.
  Omit only for: getters/setters; overrides when parent Javadoc still applies exactly; test code.
- Method Javadoc first sentence starts with a verb form: `Returns …`, `Creates …`, `Adds …`.
- Form: `/**` alone on first line; blank line before `@param`/`@return`/`@throws`; no blank line before the method.
- Indent comments with the code they describe.

## Checklist before finishing a Java change

- [ ] Names and booleans follow the rules above
- [ ] Layout/braces/whitespace match
- [ ] No wildcard imports; packages set
- [ ] Public API has Javadoc where required
- [ ] Lines ≤ 120 characters

## Source

Full rule text and rationale: https://se-education.org/guides/conventions/java/intermediate.html
