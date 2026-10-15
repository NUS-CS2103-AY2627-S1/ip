---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard (basic + intermediate) to all Java in this project. Use when writing, editing, reviewing, or refactoring Java, Javadoc, tests, or formatting; when the user mentions coding standard, SE-EDU, Checkstyle, A-CodingStandard, naming, layout, or comments.
---

# SE-EDU Java coding standard

Follow **[SE-EDU Java coding standard (basic + intermediate)](https://se-education.org/guides/conventions/java/intermediate.html)** for every Java file in this project (`src/main/java` and `src/test/java`).

For topics that page does not cover, use the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

Read [reference.md](reference.md) when you need the full rule text, examples, or rationales.

## When to apply

Read this skill **before** writing or editing Java. Re-check the list below **after** the edit. Do not leave style-only drift for a later pass.

## Checklist

Copy and complete:

```
SE-EDU Java:
- [ ] Naming (packages, types, methods, booleans, tests, constants)
- [ ] Layout (4 spaces, 120-char hard limit, K&R braces, wrap +8)
- [ ] Statements (packages, explicit imports, braces, no public fields)
- [ ] Comments (American English, Javadoc form, public classes/methods)
- [ ] Google fallback (modifier order, diamond operator)
```

## Naming (must)

- Packages: all lowercase; root is the project name (`wodan`), then logical groups (`wodan.ui`, `wodan.task`). Do not use `edu.nus.*`.
- Types: nouns in PascalCase. Methods: verbs in camelCase. Variables: camelCase. Constants: `SCREAMING_SNAKE_CASE`.
- Names in English. Do not uppercase acronyms inside a name (`exportHtmlSource`, not `exportHTMLSource`; `stripBom`, not `stripBOM`).
- Booleans: `is` / `has` / `was` / `can` / `should` prefix (`isDone`, `hasTime()`, `isEmpty()`). Boolean setters: `void setFound(boolean isFound)`.
- Collections: plural names (`tasks`, `lines`).
- Long names for wide scope (fields); short names only for tiny scope (`i` in a loop). Nested-loop indices `j`, `k` only when nested.
- Test methods: `featureUnderTest_testScenario_expectedBehavior()` (later parts may be omitted).
- Related constants share a prefix (`DATE_FORMATTERS`, `DATE_TIME_FORMATTERS`).

## Layout (must)

- Indent with **4 spaces**, never tabs.
- Line length: prefer ≤ 110 characters; **hard limit 120**. Wrap at a readable place: break **after** a comma, **before** an operator (including `.`). Keep the method name attached to `(`. Wrapped lines indent **+8** from the parent line. Do not blindly accept IDE wrapping.
- K&R braces (`while (!done) {` on the same line). Same form for `if` / `else` / `for` / `while` / `do` / `switch` / `try` / `catch` / `finally` / methods.
- Spaces around operators; space after reserved words (`if (`); space after commas; space after `;` in `for`.
- Separate logical units inside a block with **one** blank line.

## Statements (must)

- Every class is in a package. No `import *`. List imported classes explicitly.
- Import order (blank line between groups, ASCII sort within a group):
  1. static imports
  2. `java.*`
  3. `javax.*`
  4. third-party (`org.*`, `com.*`, …)
  5. project (`wodan.*`)
- Array brackets on the type: `int[] values`, not `int values[]`.
- Declare in the smallest scope; initialize at declaration when a real value exists. Do not invent a dummy value just to initialize.
- No `public` instance fields unless the type is a behaviorless data class. Constants may be public.
- Always brace `if` / `else` / `for` / `while` bodies, even for one statement. Put the condition on its own line (`if (isDone) {` then the body).
- `switch`: include `default`. If a `case` does not `break`/`return`/`throw`, write `// Fallthrough`.

## Comments (must)

- English, **American spelling** (`recognize`, `behavior`). No slang. Indent comments with the code they describe.
- Header comments (`/** ... */`) on **all classes** and **all public methods**, except:
  1. getters/setters
  2. overrides whose parent Javadoc still applies exactly (`{@inheritDoc}` is allowed when adding a difference)
  3. test classes and test methods
- Javadoc form:

```java
/**
 * Returns lateral location of the specified position.
 * If the position is unset, NaN is returned.
 *
 * @param x X coordinate of position.
 * @param y Y coordinate of position.
 * @param zone Zone of position.
 * @return Lateral location.
 * @throws IllegalArgumentException If zone is <= 0.
 */
```

- `/**` on its own line; space after each `*`; first sentence is the summary and starts with a verb (`Returns`, `Shows`, `Creates`, `Adds` — not `Return`).
- Blank line before the tag block; period at the end of each `@param` / `@return` / `@throws`; no blank line between the comment and the declaration.
- `@return` may be omitted for `void` or when the description already states the result. `@param` tags are all-or-nothing: every parameter, or none.
- One-line member comments are allowed: `/** Whether this task is done. */`

User-facing chatbot strings may keep the project's flavor text. Comments and Javadoc must still be plain American English.

## Google Style fallback (must, when SE-EDU is silent)

- Modifier order: `public`/`protected`/`private`, then `abstract`, `static`, `final`, … (`public abstract void`, never `abstract public void`).
- Use the diamond operator: `new ArrayList<>()`, not `new ArrayList<Task>()`.
- One statement per line. No unused imports.

## Do not

- Do not reformat unrelated files.
- Do not drop existing Javadoc just because getters *may* omit it.
- Do not change chatbot output to satisfy style unless the user asks; if output does change, update `test/ui-test-plan.md` and run the `test-ui` skill.
