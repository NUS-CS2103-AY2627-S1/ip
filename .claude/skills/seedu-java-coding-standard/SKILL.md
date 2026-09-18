---
name: seedu-java-coding-standard
description: Use when writing or reviewing any Java under src/ in this project — the mandatory SE-EDU basic+intermediate coding standard (naming, layout, imports, statements, JavaDoc comments) that the CS2103T course grades against. Invoke before adding or editing a class, method, field, or JavaDoc block, and when checking a diff for style compliance.
---

# SE-EDU Java coding standard (basic + intermediate)

This project is graded against the SE-EDU Java coding standard. The course
mandates the **basic and intermediate** rules
(<https://se-education.org/guides/conventions/java/intermediate.html>); the
advanced rules are optional and are **not** enforced here, so do not spend
effort on them.

Apply every rule below to any Java you write or edit under `src/`. When
reviewing a change, walk these four groups in order and flag each violation.

## Naming

- Packages are all lowercase, no underscores (`meowmeow.task`, not
  `meowmeow.Task_List`).
- Classes and enums are nouns in `PascalCase` (`TaskList`, `CommandType`).
- Methods are verbs in `camelCase` (`getName()`, `computeTotalWidth()`).
- Variables and parameters are `camelCase` (`taskCount`, `dateLabel`).
- Constants (`static final`) are `UPPER_SNAKE_CASE` (`MAX_ITERATIONS`,
  `FORMAT_HINT`).
- Do not upper-case whole abbreviations inside a name: `exportHtmlSource()`,
  not `exportHTMLSource()`; `parseId`, not `parseID`.
- Booleans and boolean-returning methods read as a yes/no question: prefix
  with `is`, `has`, `was`, `can`, `should` (`isDone`, `hasNextCommand()`,
  `shouldAbort`).
- Collections take a plural name (`List<Task> tasks`, `int[] values`).
- Name length tracks scope: a wide-scope field earns a descriptive name; a
  one-line loop counter may be `i`, `j`, `k` (use `j`/`k` only when nested).
- All names in English.

## Layout

- Indent with **4 spaces**, never tabs. Continuation (wrapped) lines indent
  **8 spaces**.
- Keep lines within **120 characters** (aim for 110).
- K&R / "Egyptian" braces: opening brace on the same line as its statement,
  closing brace on its own line.

  ```java
  // good
  if (isDone) {
      doCleanup();
  }

  // bad
  if (isDone)
  {
      doCleanup();
  }
  ```

- Put spaces around binary operators and after commas, and after keywords
  like `if`, `for`, `while`, `catch`:

  ```java
  // good
  doSomething(a, b, c);
  for (int i = 0; i < 10; i++) {

  // bad
  doSomething(a,b,c);
  for(int i=0;i<10;i++){
  ```

- Break a long line **after** a comma or **before** an operator; keep a
  method name attached to its `(`.
- Separate logical units inside a method body with one blank line.
- No blank line between a JavaDoc block and the class/method it documents.

## Statements

- Every class goes in a package (`package meowmeow....;` on line 1).
- Import each class explicitly — **never** a wildcard:

  ```java
  // good
  import java.util.ArrayList;
  import java.util.List;

  // bad
  import java.util.*;
  ```

- Keep import order consistent: static imports first, then `java`, `javax`,
  `org`, `com`, `javafx`, `junit` groups. Let the IDE auto-order.
- Array brackets attach to the type: `int[] values`, not `int values[]`.
- Declare a variable in the smallest scope that works, and initialise it at
  the point of declaration.
- Never make a class field `public` (constants excepted). Expose state
  through methods.
- Always brace a loop or conditional body, even a one-liner, and put the
  controlled statement on its own line:

  ```java
  // good
  if (isDone) {
      doCleanup();
  }

  // bad
  if (isDone) doCleanup();
  ```

- In a fall-through `switch` case, mark it with a `// Fallthrough` comment.

## Comments

- All comments in English, American spelling, no slang.
- Every non-private class and non-private method needs a JavaDoc header
  comment. You may omit it for: getters/setters, an `@Override` whose
  parent JavaDoc already applies exactly, and test classes/methods.
- JavaDoc form:
  - `/**` sits on its own line; a space follows every leading `*`.
  - The **first sentence is a short summary** and, for a method, **starts
    with a verb in the third-person present tense** — `Returns...`,
    `Prints...`, `Adds...`, `Constructs...` — **not** `Return`, `Returning`,
    nor a noun phrase like `The next line of input.`

    ```java
    // good
    /** Returns the next line of input, trimmed. */
    String readCommand() { ... }

    // bad
    /** The next line of input, trimmed. */
    String readCommand() { ... }
    ```

  - Leave a blank line between the description and the first `@` tag.
  - End each `@param` / `@return` / `@throws` description with a period.
  - `@param` is **all-or-none**: tag every parameter or none.
  - `@return` may be omitted when the method returns nothing or the summary
    already states what it returns.
  - A single-line member comment is fine:
    `/** Number of connections to this database. */`
- Class and field JavaDoc summaries **may** be noun phrases; only method
  (and constructor) summaries must start with a verb.
- Indent a comment to match the code it describes. Trailing comments are
  allowed: `process(dummy); // warm the cache first`.
