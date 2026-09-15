---
name: seedu-java-coding-standard
description: The SE-EDU Java coding standard (intermediate level) that ALL Java code in this project must follow — naming, layout, statements, and Javadoc rules. Use this skill whenever writing, editing, reviewing, or refactoring any .java file in this repository, whenever adding or changing a Javadoc comment, and whenever the user asks about code style, conventions, formatting, naming, or whether some code "follows the standard". Apply it proactively without being asked — new code is expected to conform on the first attempt, not after a later cleanup pass.
---

# SE-EDU Java Coding Standard (Intermediate)

Source: https://se-education.org/guides/conventions/java/intermediate.html

This is the standard the course marks against, so conforming as you write is
cheaper than a cleanup pass later. When a rule below conflicts with your
instinct, follow the rule — consistency across the codebase is the point.

## Naming

| Element | Rule | Example |
|---|---|---|
| Package | all lowercase | `nova`, `todobuddy.ui` |
| Class / Enum | noun, PascalCase | `Line`, `AudioSystem` |
| Variable | camelCase | `line`, `audioSystem` |
| Constant | UPPER_SNAKE_CASE | `MAX_ITERATIONS`, `COLOR_RED` |
| Method | verb, camelCase | `getName()`, `computeTotalWidth()` |
| Test method | `feature_scenario_expectedBehaviour` | `sortList_emptyList_exceptionThrown()` |

**Abbreviations and acronyms are not uppercased inside a name.** Write
`exportHtmlSource()` and `openDvdPlayer()`, not `exportHTMLSource()` or
`openDVDPlayer()` — an all-caps run collides visually with the next word.

**All names in English.**

**Name length tracks scope.** A variable visible across a long method or a whole
class earns a descriptive name; a loop index living for three lines does not.
`i, j, k, m, n` are fine for integer scratch values and `c, d` for characters —
but reserve `j, k` for genuinely nested loops. A *parameter* on a method other
classes call has wide scope, so name it `index`, not `i`.

**Booleans should read as a yes/no question.** Prefix with `is`, `has`, `was`,
`can`, `should`: `isSet`, `isVisible`, `hasData`, `wasOpen`, `canEvaluate()`,
`shouldAbort`. A setter takes the same form: `void setFound(boolean isFound)`.

**Collections are plural**: `Collection<Point> points`, `int[] values`.

**Related constants share a prefix**: `COLOR_RED`, `COLOR_GREEN`, `COLOR_BLUE`.

## Layout

- **Indent 4 spaces. Never tabs.**
- **Lines under 110 characters** (hard limit 120).
- **Wrapped lines indent 8 spaces** — double the normal indent, so a
  continuation is visually distinct from a nested block.
- **K&R braces**: opening brace on the same line as the statement.

Break lines after a comma, and *before* an operator (including `.`, the `&` in
`<T extends Foo & Bar>`, and the `|` in a multi-catch). Keep a method name
attached to its `(`. Prefer breaking at the highest syntactic level available.
A ternary is either one line, or broken before both `?` and `:`.

```java
while (!done) {
    doSomething();
}

if (condition) {
    statements;
} else if (condition) {
    statements;
} else {
    statements;
}

try {
    statements;
} catch (Exception exception) {
    statements;
} finally {
    statements;
}
```

**Whitespace**: spaces around operators, after reserved words, after commas,
after semicolons.

```java
a = (b + c) * d;                 // not  a=(b+c)*d;
while (true) {                   // not  while(true){
doSomething(a, b, c, d);         // not  doSomething(a,b,c,d);
for (i = 0; i < 10; i++) {       // not  for(i=0;i<10;i++){
```

**Separate logical units inside a block with one blank line.**

## Statements

**Every class goes in a package.**

**Import each class explicitly** — never `import java.util.*;`. Keep import
order consistent: static imports, then `java`, `javax`, `org`, `com`.

**Array brackets attach to the type**: `int[] a`, never `int a[]`.

**Declare variables in the smallest scope that works, and initialise them at
the point of declaration.** Don't hoist declarations to the top of a method.

**Never make a class variable `public`** unless the class is a pure data class
with no behaviour. Constants are the exception.

**Always brace the body of a loop or conditional**, however short:

```java
if (stream != null) {            // not  if (stream != null) readFile(stream);
    readFile(stream);
}

for (i = 0; i < 100; i++) {      // not  for (...) sum += value[i];
    sum += value[i];
}
```

**Put the conditional on its own line** — `if (isDone) { doCleanup(); }` split
across lines, never `if (isDone) doCleanup();`.

In a traditional `switch`, mark deliberate fallthrough with a
`// Fallthrough` comment so a reader can tell it from a missing `break`.

## Comments and Javadoc

**English, American spelling, no local slang.**

**Write a header comment for every public class and method.** You may omit it
for getters and setters, for overridden methods where the parent's Javadoc
still applies, and for test classes and test methods — in those cases the
signature or the test name already says everything a comment would.

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
public double computeLocation(double x, double y, int zone)
        throws IllegalArgumentException {
```

Rules that are easy to get wrong:

- `/**` sits on its own line; each following `*` aligns under the first, with a
  space after it.
- **The first sentence is a short summary**, written in the third person —
  "Returns …", "Sends …", "Adds …". Not the imperative "Return …".
- **Blank line between the description and the first `@` tag.**
- **Every `@param` description ends with punctuation.**
- **`@param` is all-or-nothing** — document every parameter or none.
- `@return` may be omitted when the method returns nothing, or when the return
  is obvious from the summary.
- No blank line between the Javadoc block and the thing it documents.
- Single-line form for short member comments: `/** Divider between blocks. */`
- Use `{@inheritDoc}` when an override extends the parent's contract.

**Indent comments to match the code they describe.**

## Before you finish

Run through this when you have touched any `.java` file:

1. Lines under 110 chars, 4-space indent, no tabs
2. Names follow the table above — especially booleans and wide-scope parameters
3. No wildcard imports; imports ordered
4. Every loop and conditional body braced
5. Public classes and methods have Javadoc, third person, `@param`s punctuated
6. No public non-constant fields

A quick mechanical check for the numeric rules:

```bash
awk 'length > 110 {printf "%s:%d len=%d\n", FILENAME, FNR, length}' src/main/java/**/*.java
grep -rn "import .*\*;" src/main/java/
grep -rPn "\t" src/main/java/
```
