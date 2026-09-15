---
name: seedu-java-coding-standard
description: >-
  Apply the SE-EDU Java coding standard (basic + intermediate) and the project's
  Checkstyle config to all Java in this CS2103T iP. Use when writing, editing,
  reviewing, or reformatting Java source or tests, or when the user mentions
  coding standard, A-CodingStandard, A-CheckStyle, Checkstyle, Javadoc, or
  SE-EDU Java conventions.
---

# SE-EDU Java coding standard

Follow [Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html) for **all** Java in this project (`src/main/java` and `src/test/java`). For anything not covered there, use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

Style is machine-checked by Checkstyle (`config/checkstyle/checkstyle.xml` and `suppressions.xml`, same files as AddressBook Level 3). Do not edit those configs unless the user asks. Do not invent extra house style.

If Checkstyle disagrees with the IntelliJ formatter or a looser reading of the written standard, **follow Checkstyle**. Prefer the simplest change that makes the file compliant.

Read [reference.md](reference.md) for examples, edge cases, and Checkstyle shapes that the formatter often gets wrong.

## Naming

- Packages: all lowercase. Root is the project name (`ekud`), then a role (`ekud.ui`). Never `edu.nus.*`.
- Classes/enums: nouns, PascalCase (`TaskList`, `CommandType`).
- Methods: verbs, camelCase (`getName()`, `computeTotalWidth()`). Getters use `get`/`is`/`has`.
- Variables: camelCase. Wider scope → longer name. Loop/scratch ints may be `i, j, k, m, n`.
- Constants: `SCREAMING_SNAKE_CASE` (`MAX_ITERATIONS`). Related constants share a prefix (`COLOR_RED`).
- Booleans: sound like booleans; prefer `is`/`has`/`was` (`isDone`, `hasTime`). Setter: `setFound(boolean isFound)`.
- Collections: plural (`tasks`, `values`).
- Acronyms in names are not fully uppercase: `exportHtmlSource()`, not `exportHTMLSource()`.
- English names only.
- Test methods: `featureUnderTest_testScenario_expectedBehavior()`. The last part, or the last two parts, may be omitted.

## Layout

- Indent with **4 spaces**, never tabs.
- Soft line limit 110 characters; hard limit **120**. Wrap with **8 extra spaces** relative to the parent line.
- Break after a comma; break before an operator (including `.`). Keep `methodName(` together: the `(` stays at the end of the line, never as the first token on the next line.
- Prefer a higher-level break (outside parentheses) over a lower-level one.
- K&R / Egyptian braces: `{` on the same line as `if`/`else`/`for`/`while`/`try`/`catch`/`method`.
- One blank line between logical units in a block.
- Spaces: around operators, after reserved words, after commas, after `;` in `for`.

## Statements

- Every class is in a package.
- Import order: static, then `java`/`javax`, then `org`, then `com`, then other packages (`ekud`). No blank-line surprises — match neighboring files.
- No wildcard imports. List each class.
- Arrays: `int[] values`, not `int values[]`.
- Declare variables in the smallest scope and initialize at declaration when a real value exists.
- No public fields unless the type is a data class with no behavior. Constants may be public.
- Always use braces on `if`/`for`/`while`, even for one statement.
- Put the `if` condition and body on separate lines. Never `if (done) doCleanup();`.
- Classic `switch` fall-through must have `// Fallthrough`.
- Member order in a class: static fields, instance fields, constructors, then methods (including static factory methods such as `parse`). Do not place `parse` above the constructor.

## Comments and Javadoc

- Comments are English with **American** spelling (`behavior`, `recognize`). No slang.
- Indent comments with the code they describe.
- **Required** header comments: every class and every public method.
- **May omit** Javadoc for: getters/setters; overrides when the parent Javadoc still applies exactly; test classes/methods.
- Method first sentence starts with a verb: `Returns ...`, `Adds ...`, `Parses ...` (not `Return` / `Returning`).
- Form: `/**` on its own line; space after `*`; blank line before `@param`/`@return`/`@throws`; punctuation at the end of each tag; no blank line between the block and the method.
- `@param` for every parameter or for none. `@return` may be omitted if void or obvious.

## Checkstyle

After every Java edit, run `gradlew checkstyleMain checkstyleTest` and fix every violation. Tests are suppressed for missing type/method Javadoc; main code is not.

Do not treat IntelliJ's "URI is not registered" warning on the Checkstyle DTD URLs as a project error. Leave the `<!DOCTYPE>` lines alone.

Suppress a rule only when no reasonable code shape satisfies it:

```
//CHECKSTYLE.OFF: RuleName
...
//CHECKSTYLE.ON: RuleName
```

## Before finishing a Java change

1. Scan new/edited code against this checklist.
2. Update matching JUnit tests in the same change (see `AGENTS.md` Testing).
3. Keep public error messages in American English if you change them; update tests that assert those strings.
4. Run `gradlew checkstyleMain checkstyleTest` and leave no Checkstyle violations.
