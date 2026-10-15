---
name: seedu-java-coding-standard
description: The SE-EDU Java coding standard (basic + intermediate levels) that ALL Java code in this project must follow. Load before writing, editing, or reviewing any .java file here, and when asked to check or fix coding-standard/style violations.
---

# SE-EDU Java Coding Standard

Source: https://se-education.org/guides/conventions/java/intermediate.html
Anything not covered here falls back to the
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

Apply these rules to **every** `.java` file in `src/main/java` and `src/test/java`.
When editing an existing file, fix violations you touch; do not reformat unrelated code
in the same change unless explicitly asked to.

## 1. Naming

| Element | Rule | Example |
|---|---|---|
| Package | all lower case, project name then logical group | `food.task`, `todobuddy.ui` |
| Class / enum | **noun**, PascalCase | `Line`, `AudioSystem`, `TaskList` |
| Method | **verb**, camelCase | `getName()`, `computeTotalWidth()` |
| Variable | camelCase | `line`, `audioSystem` |
| Constant (`static final`) | ALL_CAPS with underscores | `MAX_ITERATIONS`, `COLOR_RED` |

- **All names in English.**
- **Abbreviations and acronyms are not uppercased inside a name.**
  Good: `exportHtmlSource()`, `openDvdPlayer()`. Bad: `exportHTMLSource()`, `openDVDPlayer()`.
- **Boolean** variables and methods read like a yes/no question: prefix with `is`, `has`, `was`,
  `can`, `should`. Good: `isSet`, `hasData`, `wasOpen`, `boolean canEvaluate()`.
  Setter form: `void setFound(boolean isFound)`.
- **Collections take a plural name.** `Collection<Point> points;`, `int[] values;`
- **Scope drives length**: wide-scope names are long and descriptive; short-lived scratch names may
  be brief. Loop counters may be `i`, `j`, `k` — use `j`/`k` only for nested loops.
- **Associated constants share a prefix**: `COLOR_RED`, `COLOR_GREEN`, `COLOR_BLUE`.
- **Test methods**: `featureUnderTest_testScenario_expectedBehavior()`, e.g.
  `sortList_emptyList_exceptionThrown()`. The third part, or both the second and third, may be
  dropped when the coverage makes them redundant.

## 2. Layout

- **4 spaces** per indent level. **Never tabs.**
- **Line length ≤ 120 chars** (hard limit); prefer to stay under 110.
- **Wrapped lines indent 8 spaces** (twice normal):
  ```java
  setText("Long line split"
          + "into two parts.");
  ```
- **Where to break**: after commas; *before* operators (including `.`, the `&` of a type bound and
  the `|` of a multi-catch). Keep a method name attached to its opening `(`. Prefer breaking at a
  higher syntactic level over a lower one. Do not blindly accept the IDE's auto-format.
- **K&R ("Egyptian") braces** — opening brace on the same line, never on its own line:
  ```java
  while (!done) {
      doSomething();
      done = moreToDo();
  }
  ```
  This includes class declarations: `public class Todo extends Task {` — note the space before `{`.
- **Whitespace inside statements**:
  | Rule | Good | Bad |
  |---|---|---|
  | Spaces around binary operators | `a = (b + c) * d;` | `a=(b+c)*d;` |
  | Space after a reserved word | `while (true) {` | `while(true){` |
  | Space after commas | `doSomething(a, b, c);` | `doSomething(a,b,c);` |
  | Space after `;` in a `for`; no space before `++` | `for (i = 0; i < 10; i++) {` | `for(i=0;i<10;i++){`, `i ++` |
- **Separate logical units inside a block with one blank line.** Do not open a block with a blank
  line — the first statement follows `{` directly.
- **Switch**: prefer the arrow form; it cannot fall through.
  ```java
  switch (condition) {
      case ABC -> method("1");
      default -> method("0");
  }
  ```
  In the old colon form, an intentional missing `break` needs an explicit `// Fallthrough` comment.

## 3. Statements

- **Every class goes in a package.**
- **Import every class explicitly** — never `import java.util.*;`.
- **Import order must be consistent.** This project uses the order given by the standard:
  1. static imports 2. `java.*` 3. `javax.*` 4. `org.*` 5. `com.*` 6. `javafx.*` 7. everything else
  (including this project's own `food.*`), with a blank line only after the static group.
- **Array brackets attach to the type**: `int[] a = new int[20];`, not `int a[]`.
- **Declare variables in the smallest possible scope and initialize them at declaration.**
- **Class variables are never `public`** unless the class is a pure data class with no behavior.
  Constants are exempt. Use accessors instead.
- **Always brace the body** of a loop or conditional, even a single statement:
  ```java
  if (stream != null) {
      readFile(stream);
  }
  ```
- **The conditional goes on its own line** — never `if (isDone) doCleanup();`.

## 4. Comments and Javadoc

- **All comments in English**, American spelling, no local slang.
- **Every class and every public method needs a descriptive header comment.** May be omitted for:
  getters/setters, overriding methods whose parent Javadoc applies unchanged, and test
  classes/methods.
- **Javadoc format:**
  ```java
  /**
   * Returns lateral location of the specified position.
   * If the position is unset, NaN is returned.
   *
   * @param x X coordinate of position.
   * @param y Y coordinate of position.
   * @return Lateral location.
   * @throws IllegalArgumentException If zone is <= 0.
   */
  public double computeLocation(double x, double y, int zone)
          throws IllegalArgumentException {
  ```
  - `/**` sits on its own line; later `*`s align under the first, each followed by a space.
  - The **first sentence is a short summary** — Javadoc uses it in the index.
  - Method summaries are **third person, not imperative**: "Returns …", "Adds …", "Sends …",
    *not* "Return …" or "Add …".
  - A blank `*` line separates the description from the tag block.
  - **Punctuate every `@param` / `@return` / `@throws` description** (end with a period).
  - No blank line between the Javadoc block and the thing it documents.
  - `@return` may be omitted when the method returns nothing or the return is obvious.
  - `@param` is all-or-nothing: include every parameter, or none (only when all are
    self-explanatory or already covered in the description).
  - Use `{@inheritDoc}` to reuse a parent comment on an override.
- Single-line member comments are fine: `/** Number of connections to this database */`
- **Indent comments to match the code they describe.** Trailing comments are allowed:
  `process("ABC"); // process a dummy String first`

## 5. Checking compliance

Run from the repo root. Every command should print nothing:

```bash
# tabs, trailing whitespace, over-long lines
grep -rnP '\t' src --include='*.java'
grep -rn ' $' src --include='*.java'
awk 'length > 120 {printf "%s:%d\n", FILENAME, FNR}' $(find src -name '*.java')

# brace and operator spacing
grep -rnE '(extends|implements) [A-Za-z<>]+\{|\)\{|(if|for|while|switch|catch)\(' src --include='*.java'
grep -rnE 'i \+\+|\+\+ i' src --include='*.java'

# wildcard imports
grep -rn 'import .*\*;' src --include='*.java'

# unpunctuated javadoc tags (block-aware: a wrapped description carries its period
# on its LAST line, so a plain line-by-line grep gives false positives)
python3 - <<'EOF'
import re, pathlib
tag = re.compile(r'^\s*\*\s+@(param|return|throws)\b')
cont = re.compile(r'^\s*\*\s+\S')
for f in sorted(pathlib.Path('src').rglob('*.java')):
    lines = open(f).read().split('\n')
    i = 0
    while i < len(lines):
        if tag.match(lines[i]):
            j = i
            while (j + 1 < len(lines) and cont.match(lines[j + 1])
                   and not tag.match(lines[j + 1])):
                j += 1
            if not lines[j].rstrip().endswith(('.', ':', '!', '?')):
                print(f'{f}:{j + 1}: {lines[j].strip()}')
            i = j + 1
        else:
            i += 1
EOF
```

Then confirm the code still builds and is documented cleanly:

```bash
sdk use java 25.0.3.fx-zulu
./gradlew test javadoc
```
