---
name: seedu-java-coding-standard
description: The SE-EDU Java coding standard (basic + intermediate levels) that ALL Java code in this project must follow. Use whenever writing, editing, reviewing, or generating Java source in this repository - including new classes, new methods, bug fixes, refactors, tests, and Javadoc - and whenever asked to check or fix code style.
---

# SE-EDU Java Coding Standard (Basic + Intermediate)

Source: https://se-education.org/guides/conventions/java/intermediate.html

Every Java file in this project must satisfy the rules below. When editing an
existing file, bring the lines you touch into compliance; do not reformat
unrelated code unless asked.

## 1. Naming

| Element | Rule | Example |
|---|---|---|
| Package | all lowercase, no underscores | `davidgoggins.task` |
| Class / enum | noun, `PascalCase` | `Task`, `AudioSystem` |
| Interface | noun/adjective, `PascalCase` | `Comparable` |
| Method | **verb**, `camelCase` | `getName()`, `computeTotalWidth()` |
| Variable | `camelCase` | `line`, `audioSystem` |
| Constant (`static final`) | `SCREAMING_SNAKE_CASE` | `MAX_ITERATIONS`, `COLOR_RED` |

Additional naming rules:

- **English only**, for names and comments alike. Use American spelling.
- **Abbreviations and acronyms are not uppercased** inside a name:
  `exportHtmlSource()` not `exportHTMLSource()`; `openDvdPlayer()` not `openDVDPlayer()`.
- **Booleans read like booleans**: prefix with `is`, `has`, `was`, `can`, `should`.
  `isSet`, `hasData`, `wasOpen`, `boolean canEvaluate()`, `void setFound(boolean isFound)`.
- **Collections take plural names**: `Collection<Point> points`, `int[] values`.
  Conversely, a name for a *single* object should be singular.
- **Associated constants share a prefix**: `COLOR_RED`, `COLOR_GREEN`, `COLOR_BLUE`.
- **Name length tracks scope**: wide scope gets a long descriptive name; a tiny
  scope may use a short one. `i`, `j`, `k` are for loop counters (`j`, `k` only
  when nested); `c`, `d` for characters.
- **Test methods** use `featureUnderTest_testScenario_expectedBehavior()`:
  `sortList_emptyList_exceptionThrown()`, `getMember_memberNotFound_nullReturned()`.

## 2. Layout

- **Indent 4 spaces. Never tabs.**
- **Line length: 120 characters hard limit** (aim to wrap by 110).
- **Wrapped lines indent 8 spaces** (double the normal indent), so the
  continuation is visibly distinct from a nested block:
  ```java
  setText("Long line split"
          + "into two parts.");
  ```
- **Break lines to aid reading**: break *after* a comma, *before* an operator
  (including `.`, `&&`, `+`). Prefer breaking at the highest syntactic level.
  Keep a method name attached to its opening parenthesis.
- **K&R (Egyptian) braces** - opening brace on the same line:
  ```java
  while (!done) {
      doSomething();
  }
  ```
- **`case` labels are indented one level inside the `switch`**:
  ```java
  switch (condition) {
      case ABC -> method("1");
      case DEF -> method("2");
      default -> method("0");
  }
  ```
  In the classic form, add `// Fallthrough` wherever a `break` is deliberately omitted.
- **Whitespace inside statements** - space around operators, after keywords, and
  after commas and semicolons:

  | Good | Bad |
  |---|---|
  | `a = (b + c) * d;` | `a=(b+c)*d;` |
  | `while (true) {` | `while(true){` |
  | `doSomething(a, b, c, d);` | `doSomething(a,b,c,d);` |
  | `for (i = 0; i < 10; i++)` | `for(i=0;i<10;i++)` |
  | `class Event extends Task {` | `class Event extends Task{` |

- **Separate logical units with one blank line.** Do not open a block with a
  blank line, and leave no trailing whitespace at end of line.

## 3. Statements

- **Every class lives in a package.**
- **Import each class explicitly** - `import java.util.List;`, never `import java.util.*;`.
- **Import order must be consistent**: static imports, then `java`, `javax`,
  `org`, `com`, `javafx`, then this project's own packages.
- **Array brackets attach to the type**: `int[] a = new int[20];`, not `int a[]`.
- **Declare a variable in the smallest possible scope and initialise it there.**
  Declare loop variables inside the loop.
- **Class variables are never `public`** (except constants, or a pure data class
  with no behaviour). Keep fields `private`/`protected` and expose behaviour.
- **Always brace the body** of a loop or conditional, even a single statement:
  ```java
  if (stream != null) {
      readFile(stream);
  }
  ```
  Never `if (stream != null) readFile(stream);`.
- **Put the conditional on its own line**, separate from the statement it
  guards, so a debugger can break on the body.

## 4. Comments and Javadoc

- **Comments are written in English**, in American spelling, without slang.
- **Write a header Javadoc for every class and every public method.** It may be
  omitted for trivial getters/setters, for overrides where the parent's doc
  applies exactly (or use `{@inheritDoc}`), and for test classes/methods.
- **Javadoc format**:
  - `/**` on its own line; each following line starts with an aligned `*` plus a space.
  - First sentence is a short summary in the **third person indicative**:
    `Returns ...`, `Adds ...`, `Sends ...` - not the imperative `Return ...`.
  - Blank line between the description and the block tags.
  - `@param` for **all** parameters or for none; `@return` may be omitted when
    the method returns nothing or the value is self-evident; `@throws` for
    checked exceptions.
  - End each tag description with punctuation. No blank line between the
    Javadoc and the element it documents.
  - Single-line form is fine for fields: `/** Number of retries. */ private int retries;`
- **Indent comments to match the code they describe.** Trailing comments are
  allowed: `process("ABC"); // process a dummy String first`.
- **Put a space after `//`**: `// like this`, not `//like this`.

## 5. Project conventions layered on top

- Keep the Javadoc *description* to at most 4 lines of prose (block tags are
  exempt) - see the `javadoc` skill.
- Prefer the simplest design that meets the requirement; note a more advanced
  alternative in a comment or in your reply rather than building it.

## Self-check before finishing any Java edit

```
grep -rnP '\t' --include='*.java' src                 # tabs
awk 'length > 120 {print FILENAME":"FNR}' $(find src -name '*.java')   # long lines
grep -rn ' $' --include='*.java' src                  # trailing whitespace
grep -rnE '//[^ /]' --include='*.java' src            # missing space after //
grep -rn 'import .*\*;' --include='*.java' src        # wildcard imports
```
