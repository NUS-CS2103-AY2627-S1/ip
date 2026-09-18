---
name: seedu-java-coding-standard
description: The Java coding standard all code in this project must follow, based on se-education.org's intermediate Java coding conventions. Use whenever writing, reviewing, or refactoring any Java source or test file in this repository (naming, layout, imports, statements, Javadoc).
---

# se-edu Java Coding Standard (intermediate)

Source: https://se-education.org/guides/conventions/java/intermediate.html

Every `.java` file under `src/` (production and test) must follow the rules below.
When a rule below is silent on a topic, fall back to Google's Java Style Guide.
When you touch a file, bring the lines you edit into compliance; do not leave a
file less compliant than you found it.

## Quick checklist (run through this before finishing any Java edit)

- [ ] Package declared; package name all lowercase (`altair`, `altair.task`, ...).
- [ ] Class/enum names are PascalCase nouns; methods are camelCase verbs; variables camelCase; constants `UPPER_SNAKE_CASE`.
- [ ] Acronyms are not all-caps inside a name (`Ui`, `toFileString`, `exportHtml` — not `UI` mid-name, `exportHTML`).
- [ ] Imports listed explicitly (no `*`), grouped and ordered: static, then `java.*`, then `javax.*`, then third-party, then project; blank line between groups; alphabetical within a group.
- [ ] No fully-qualified class names in code where an import would do.
- [ ] Indent 4 spaces, no tabs. Wrapped/continuation lines indent 8 spaces from the parent line.
- [ ] Lines <= 120 chars (aim for <= 110).
- [ ] K&R ("Egyptian") braces: opening brace on the same line.
- [ ] Every `if`/`else`/`for`/`while` body is wrapped in braces, even one-liners; the condition/branch body is on its own line.
- [ ] Spaces around binary operators, after commas, after `;` in `for`, and after keywords (`if (`, `while (`, `catch (`).
- [ ] Logical units within a method separated by a single blank line.
- [ ] Variables declared in the smallest scope possible and initialized at declaration.
- [ ] Array brackets on the type: `int[] values` — not `int values[]`.
- [ ] No `public` fields (constants excepted; data-only classes excepted).
- [ ] Header comment (Javadoc) on every class and every public method — see Javadoc rules.
- [ ] Comments and identifiers in English, American spelling.

## Naming

| Kind | Rule | Example |
|---|---|---|
| Package | all lowercase | `altair.storage` |
| Class / enum | PascalCase noun | `Task`, `CommandType` |
| Method | camelCase verb phrase | `getStatusIcon()`, `parseDate()` |
| Variable | camelCase | `taskNumber`, `fileLines` |
| Constant (`static final`) | UPPER_SNAKE_CASE | `MAX_ITERATIONS`, `INPUT_DATE_FORMAT` |
| Boolean var/method | reads as a yes/no: `is`/`has`/`was`/`can`/`should` prefix | `isDone`, `hasNextCommand()`, `wasDone` |
| Collection | plural noun | `tasks`, `words`, `dates` |

- Acronyms/abbreviations are treated as words: `Ui`, `exportHtmlSource()`, `openDvdPlayer()` — never `UI` inside a longer name, `exportHTMLSource()`.
- Large scope => longer, more descriptive name. Small scope (loop index, scratch) => short is fine: `i`, `j`, `k` for ints, `c` for chars. Use `j`, `k` only for nested loops.
- Related constants share a prefix: `COLOR_RED`, `COLOR_GREEN`, `COLOR_BLUE`.
- Test methods: `featureUnderTest_testScenario_expectedBehavior()`, e.g. `load_unknownTypeMarker_throwsAltairException()`. The scenario and/or expected-behavior part may be dropped when obvious.

## Layout

- **Indentation:** 4 spaces, never tabs.
- **Continuation lines:** indent 8 spaces (twice normal) past the parent line.
- **Line length:** soft limit 110, hard limit 120.
- **Line breaks:** break *after* a comma, *before* an operator (`+`, `&&`, `.`, the `|` in a multi-catch). Keep a method name attached to its `(`. Prefer breaking at the highest syntactic level.

  ```java
  totalSum = a + b + c
          + d + e;
  return new Event(description, parseDate(fromText, "An event start date"),
          parseDate(toText, "An event end date"));
  ```

- **Braces:** K&R / Egyptian style.

  ```java
  while (!done) {
      doSomething();
      done = moreToDo();
  }
  ```

- **`if` / `for` / `while` / `do-while` / `try-catch`:** always braced, body on its own line. No `if (x) doThing();` and no unbraced loop bodies.
- **`switch`:** keep `case` labels aligned with the `switch` keyword (se-edu checkstyle uses `caseIndent = 0`). Add an explicit `// Fallthrough` comment on any `case` that has statements but no `break`.
- **Whitespace within statements:**

  | Good | Bad |
  |---|---|
  | `a = (b + c) * d;` | `a=(b+c)*d;` |
  | `while (true) {` | `while(true){` |
  | `doSomething(a, b, c);` | `doSomething(a,b,c);` |
  | `for (i = 0; i < 10; i++) {` | `for(i=0;i<10;i++){` |

- **Blank lines:** separate logical units inside a block with exactly one blank line.

## Statements

- **Package:** every class is in a package.
- **Imports:** explicit only (no wildcards). Order: static imports, `java.*`, `javax.*`, third-party (`org.*`, `com.*`, ...), then project imports; one blank line between groups; alphabetical within each group. Do not fully-qualify a class inline when you can import it.
- **Types:** `int[] a = new int[20];` — array brackets bind to the type.
- **Variables:** initialize at declaration; declare in the narrowest scope that works (e.g. declare the loop counter in the `for`).
- **Fields:** never `public` unless the class is a pure data holder with no behavior. Constants (`public static final`) are exempt.
- **Loops & conditionals:** body always in braces regardless of length; the controlled statement goes on its own line.

## Comments & Javadoc

- English, American spelling, no slang. Indent comments to match the code they describe.
- **Header comment required** for every class and every public method.
- **May be omitted** for: getters/setters, methods that override a documented supertype method (the inherited Javadoc applies), and test classes/methods.
- **Javadoc form:**

  ```java
  /**
   * Returns the lateral location of the specified position.
   * If the position is unset, NaN is returned.
   *
   * @param x X coordinate of the position.
   * @param y Y coordinate of the position.
   * @param zone Zone of the position.
   * @return The lateral location.
   * @throws IllegalArgumentException If zone is <= 0.
   */
  ```

  - `/**` on its own line; each `*` aligned and followed by a space; no blank line between the Javadoc block and the thing it documents.
  - First sentence is a short summary written in the third person: "Returns ...", "Creates ...", "Sends ..." — not "Return ..." / "To return ...".
  - One blank line between the description and the first `@` tag.
  - End every `@param` / `@return` / `@throws` description with punctuation (a full stop).
  - `@return` may be omitted when the method returns `void` or the return is stated in the summary. Include `@param` for **all** parameters or **none** — not some.
  - For an overridden method that needs only small additions to the inherited text, use `{@inheritDoc}`.
- Single-line member Javadoc is fine: `/** The file that holds the saved task list between runs. */`.

## After editing Java

Follow the `## Code update workflow` in `AGENTS.md` (UI test plan review, `$test-ui`, JUnit updates, `./gradlew test`). A coding-standard change that alters no observable behavior still needs `./gradlew test` to pass.
