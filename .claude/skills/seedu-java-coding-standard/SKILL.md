---
name: seedu-java-coding-standard
description: The SE-EDU Java coding standard (basic + intermediate rules) that all Java code in this project must follow. Use when writing or changing any Java code here, when reviewing code for style compliance, when naming a class, method, variable or test, when laying out a switch, a wrapped line or a comment, or when asked whether code follows the coding standard.
---

# SE-EDU Java coding standard

All Java in this project follows the basic and intermediate rules of the SE-EDU
Java coding standard, from
<https://se-education.org/guides/conventions/java/intermediate.html>.

Apply this to every Java file you write or change, in `src/main/java` and
`src/test/java` alike. When changing an existing file, bring the lines you touch
into line with these rules; do not reformat lines you had no other reason to
touch, because that buries the real change in noise.

## Naming

| Thing | Rule | Example |
|---|---|---|
| Package | all lowercase | `billy.task` |
| Class, enum | noun, PascalCase | `TaskList`, `CommandWord` |
| Method | verb, camelCase | `getKeyword()`, `computeTotalWidth()` |
| Variable | camelCase | `taskNumber` |
| Constant | UPPERCASE_WITH_UNDERSCORES | `MAX_ITERATIONS`, `FIELD_SEPARATOR` |

* **All names in English**, and comments in **American spelling** — `behavior`
  not `behaviour`, `recognized` not `recognised`, `capitalization` not
  `capitalisation`, `color` not `colour`. Avoid local slang.
* **Abbreviations and acronyms are not uppercased inside a name.** Write
  `exportHtmlSource()` and `Ui`, not `exportHTMLSource()` or `UI`.
* **Booleans read like booleans**: `isDone`, `hasData`, `wasOpen`,
  `canEvaluate()`, `hasLicense()`. Setter form: `void setFound(boolean isFound)`.
* **Collections take plural names**: `Collection<Point> points`, `int[] values`.
* **Scope decides length.** A variable with a wide scope gets a long name; `i`,
  `j`, `k` are fine as loop counters, `j` and `k` only for nested loops.
* **Associated constants share a prefix**: `COLOR_RED`, `COLOR_GREEN`.
* **Test methods** are named `featureUnderTest_testScenario_expectedBehavior()`,
  e.g. `get_taskInEmptyList_exceptionThrown()`.

## Layout

* **4 spaces** for indentation, never tabs.
* **Line length**: 110 characters soft limit, **120 hard limit**.
* **Wrapped lines are indented 8 spaces** — twice the normal indent, so a
  continuation can never be mistaken for a nested block.

```java
// Good
assertThrows(BillyException.class,
        () -> Parser.parse("event meeting /to 2019-12-02 1600"));

// Bad — 4 spaces reads like a block
assertThrows(BillyException.class,
    () -> Parser.parse("event meeting /to 2019-12-02 1600"));
```

* **Break lines to help the reader**: after a comma, before an operator
  (including `.`), keeping the method name attached to its `(`. Prefer breaking
  at the highest level.
* **K&R braces** — the opening brace ends the line that opens the block.

```java
// Good                          // Bad
while (!done) {                  while (!done)
    doSomething();               {
}                                    doSomething();
                                 }
```

* **`case` labels are indented one level inside `switch`.**

```java
// Good
switch (type) {
    case "T" -> {
        task = new Todo(fields[2]);
    }
    default -> throw new BillyException("unknown task type: " + type);
}

// Bad — case at the same indent as switch
switch (type) {
case "T" -> {
    task = new Todo(fields[2]);
}
}
```

* Include an explicit `// Fallthrough` comment on any `case` without a `break`.
* **Spaces around operators and after keywords, commas and semicolons.**

```java
a = (b + c) * d;              // not  a=(b+c)*d;
while (true) {                // not  while(true){
doSomething(a, b, c);         // not  doSomething(a,b,c);
for (i = 0; i < 10; i++) {    // not  for(i=0;i<10;i++){
```

* **One blank line between logical units** inside a block.

## Statements

* **Every class lives in a package.**
* **Imports are listed explicitly** — never `import java.util.*;`
* **Import order is consistent**: static imports, then `java.*`, then `javax.*`,
  then other organizations, then third-party, then this project's own.
* **Array brackets attach to the type**: `int[] a`, not `int a[]`.
* **Initialize variables where they are declared**, in the smallest scope that
  works.
* **Class variables are never `public`** unless the class is a data class with
  no behavior. Constants are exempt.
* **Always brace the body** of a loop or conditional, however short, and put the
  body on its own line.

```java
// Good                          // Bad
if (isDone) {                    if (isDone) doCleanup();
    doCleanup();
}
```

## Comments

* **English, American spelling.**
* **Write a header comment for every class and every public method.** It may be
  omitted for getters and setters, for an override where the parent's
  documentation applies exactly, and in test classes.
* Javadoc form: `/**` on its own line, aligned `*` with a space after each, a
  short first sentence used as the summary, a blank line before the tag block,
  and no blank line between the comment and what it documents.
* Method descriptions are **third person, not imperative**: "Returns the day
  this falls on", not "Return the day this falls on".
* `@param` is given for all parameters or none; `@return` may be omitted when
  the method returns nothing or the answer is obvious from the summary; end each
  tag description with punctuation.
* A field's Javadoc may be a single line: `/** Number of connections. */`
* **Indent comments to match the code they describe.**

## Checking compliance

Checkstyle enforces the mechanically checkable half of this standard, using the
SE-EDU ruleset in `config/checkstyle/`. Run it first:

```bash
./gradlew checkstyleMain checkstyleTest
```

Silence means no violations; anything found is printed with its file, line and
rule name, and also written to `build/reports/checkstyle/`. Add `--rerun-tasks`
to force a fresh run, as Gradle otherwise reports `UP-TO-DATE` and re-uses the
previous answer.

The rest is still read for, since no ruleset can judge whether a name is
meaningful or a comment says anything worth saying. These two help with the
parts Checkstyle does not cover — the 110-character soft limit, which it does
not enforce, and British spelling, which it cannot see:

```bash
awk 'length > 110 {print FILENAME":"FNR" ("length")"}' $(find src -name '*.java')
```

```bash
grep -rnE "[A-Za-z]+is(ed|ing|ation)\b|\b(behaviour|colour|whilst|spelt)" src --include="*.java"
```

Then run both test suites, since a formatting change that alters behavior is a
bug this standard cannot catch:

```bash
./gradlew test --rerun-tasks && python3 .claude/skills/test-ui/scripts/run-ui-tests.py
```
