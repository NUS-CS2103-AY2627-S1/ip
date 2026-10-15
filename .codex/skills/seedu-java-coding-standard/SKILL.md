---
name: seedu-java-coding-standard
description: >-
  Apply the SE-EDU Java coding standard (basic + intermediate) from
  https://se-education.org/guides/conventions/java/intermediate.html when
  writing, reviewing, refactoring, or formatting any Java code in this project.
  Use for all .java files, Javadoc, naming, layout, imports, braces, and comments.
---

# SE-EDU Java coding standard (basic + intermediate)

**Mandatory** for every Java file in this project. Source of truth:
https://se-education.org/guides/conventions/java/intermediate.html

For anything not covered there, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

Read [reference.md](reference.md) when applying a specific rule in detail.

## When to apply

- Creating or editing `.java` files
- Adding Javadoc
- Reviewing or refactoring Java
- Formatting, renaming, or organizing imports

Do not leave style violations in code you touch. When asked to update existing code, fix violations that are already present.

## Checklist

### Naming

- Packages: all lowercase; root is the project name (e.g. `thomas.command`), never `edu.nus.*`
- Classes/enums: nouns, PascalCase
- Methods: verbs, camelCase
- Variables: camelCase; collections use plural names
- Constants: `SCREAMING_SNAKE_CASE`; related constants share a prefix
- Acronyms in names: not fully uppercase (`exportHtmlSource`, not `exportHTMLSource`)
- English names only
- Long names for large scope; short names (`i`, `j`, `c`) only for small-scope scratch/index variables
- Booleans sound like booleans: prefix `is`/`has`/`was`/`can`/`should`; setter `void setFound(boolean isFound)`
- Test methods: `featureUnderTest_testScenario_expectedBehavior()` (later parts optional)

### Layout

- Indent 4 spaces, never tabs
- Soft line limit 110, hard limit 120; wrap with **8 extra spaces**
- Break after commas, before operators (including `.`); keep method name attached to `(`
- Prefer higher-level breaks; K&R (Egyptian) braces
- Space around operators; space after reserved words and commas; space after `;` in `for`
- Separate logical units in a block with one blank line
- `// Fallthrough` on intentional switch fall-through

### Statements

- Every class is in a package
- No wildcard imports; list classes explicitly
- Keep import order consistent; blank line between groups (static, `java`, `javax`, third-party, project)
- Arrays: `int[] values`, not `int values[]`
- Initialize at declaration; smallest possible scope
- No public non-constant fields unless the class is a data class with no behavior
- Always brace loop and `if`/`else` bodies; put the condition on its own line (never `if (x) doY();`)

### Comments (American English)

- Header comments **required** for all classes and public methods, except: getters/setters, overrides whose parent Javadoc still applies exactly, and test classes/methods
- Method first sentence: `Returns ...`, `Adds ...`, `Creates ...` (not `Return` / `Returning`)
- Javadoc form: `/**` on its own line; `*` aligned; space after `*`; blank line before `@param`/`@return`/`@throws`; period after each tag description; no blank line between the block and the member
- `@param` for all parameters or none; `@return` omitted if void or obvious
- Indent comments with surrounding code

## Do not

- Reformat unrelated files just to churn style
- Invent extra house style that contradicts SE-EDU or Google Java Style
