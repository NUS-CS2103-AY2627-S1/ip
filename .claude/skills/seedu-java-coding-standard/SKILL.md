---
name: seedu-java-coding-standard
description: The SE-EDU Java coding standard (intermediate level) that all Java code in this project must follow - naming, layout, statements, and comments. Use when writing or editing any .java file, when reviewing Java code for style, or when asked whether code follows the coding standard.
---

# SE-EDU Java coding standard (intermediate)

Every `.java` file in this project, in `src/main/java` and `src/test/java`
alike, follows this standard. It is the standard at
<https://se-education.org/guides/conventions/java/intermediate.html>, recorded
here so it can be applied without fetching the page.

Apply it while writing code, not as a clean-up pass afterwards.

## Naming

| Thing | Rule | Example |
|---|---|---|
| Package | all lower case | `elsa.task` |
| Class, enum | noun, PascalCase | `TaskList`, `CommandType` |
| Method | **verb**, camelCase | `getName()`, `computeTotalWidth()` |
| Variable | camelCase | `audioSystem` |
| Constant | UPPER_CASE with underscores | `MAX_ITERATIONS`, `COLOR_RED` |

- All names are in English.
- **Abbreviations and acronyms are not uppercased inside a name.** Write
  `exportHtmlSource()` and `openDvdPlayer()`, not `exportHTMLSource()` or
  `openDVDPlayer()`.
- **Booleans sound like booleans.** Prefix with `is`, `has`, `was`, `can`,
  `should`: `isSet`, `isVisible`, `hasData`, `wasOpen`, `boolean canEvaluate()`.
  A setter is `void setFound(boolean isFound)`.
- **A collection gets a plural name**: `Collection<Point> points`, `int[] values`.
- **Associated constants share a prefix**: `COLOR_RED`, `COLOR_GREEN`.
- **Scope decides length.** A variable with a large scope gets a long name; a
  small scope may use a short one. Scratch integers may be `i`, `j`, `k`, `m`,
  `n`; characters `c`, `d`. Use `j` and `k` only in nested loops.
- **Test methods** may use underscores, in the form
  `featureUnderTest_testScenario_expectedBehavior()`, for example
  `sortList_emptyList_exceptionThrown()`. The third part, or the second and
  third, may be dropped when they add nothing.

## Layout

- **4 spaces** of indentation. Never tabs.
- **Line length**: aim under 110 characters, never exceed 120.
- **Wrapped lines are indented 8 spaces** past the line they continue.
- **Break after a comma; break before an operator**, including `.`, `&` and `|`:

```java
throw new ElsaException("There are no tasks yet, so there is nothing to "
        + keyword + ". Add one first.");
```

- A method or constructor name **stays attached** to the `(` that follows it:
  `someMethod(int anArg)`, never `someMethod (int anArg)`.
- Prefer breaking at a higher syntactic level than a lower one.
- **K&R brackets**: the opening brace ends the line that opens the block.

```java
public void someMethod() throws SomeException {
    if (condition) {
        statements;
    } else {
        statements;
    }
}
```

- Whitespace inside statements:

| Rule | Write | Not |
|---|---|---|
| Operators are surrounded by spaces | `a = (b + c) * d;` | `a=(b+c)*d;` |
| A reserved word is followed by a space | `while (true) {` | `while(true){` |
| A comma is followed by a space | `doSomething(a, b, c);` | `doSomething(a,b,c);` |

- **Separate logical units within a block with one blank line.**
- In a `switch`, `case` labels are **indented 4 spaces past the `switch`**, and
  the closing brace returns to the `switch`'s own indentation. Arrow form
  (`case ABC -> ...`) and switch expressions are both fine. A `case` that falls
  through to the next carries an explicit `// Fallthrough` comment.

```java
switch (fields[1]) {
    case DONE -> task.markAsDone();
    case NOT_DONE -> task.markAsNotDone();
    default -> throw new ElsaException("unknown marker");
}
```

## Statements

- **Every class is in a package.**
- **Import classes explicitly**; never `import java.util.*;`. Equally, never
  write a fully qualified name inline (`new elsa.task.Event(...)`) in place of an
  import.
- **Import order must be consistent** across the project. Checkstyle's
  `CustomImportOrder` fixes it as five groups, each separated from the next by a
  blank line and each sorted alphabetically within itself:

  1. `import static ...`
  2. `java.` and `javax.`
  3. `org.`
  4. `com.`
  5. everything else, which here means `elsa.` and `javafx.`

  The project's own `elsa.` imports therefore come **last**, not first.
- **Array brackets attach to the type**: `int[] a`, never `int a[]`.
- **Declare a variable in the smallest scope that will do, and initialise it
  where it is declared.**
- **A class variable is never `public`** unless the class is a data class with no
  behaviour. Constants are exempt.
- **Braces always**, however short the body. Both of these are wrong:

```java
if (isReady) doSomething();       // no braces
for (int i = 0; i < 10; i++) sum += i;
```

- **The condition goes on its own line**, not sharing a line with the body.

## Comments

- All comments are in English, using American spelling, and avoid slang.
- **Every public class and method carries a descriptive header comment.**
  Non-trivial private methods should carry one too.
- A header comment may be omitted for a getter or setter, for an overriding
  method whose parent Javadoc applies unchanged, and in test code.
- Javadoc form:

```java
/**
 * Returns the date that text describes.
 *
 * @param text the text to read as a date
 * @return the date that text describes
 * @throws ElsaException if the text is not a date in any accepted form
 */
```

- `/**` sits on its own line; each following `*` is aligned under the first and
  followed by a space; there is a blank line between the description and the
  tag section; there is **no** blank line between the comment and what it
  documents.
- **The first sentence is a short summary**, because Javadoc reuses it in the
  summary table. For a method it begins with a verb in the third person —
  `Returns ...`, `Sends ...`, `Adds ...` — never a gerund (`Returning ...`).
- Each parameter description ends with punctuation.
- `@return` may be omitted when the method returns nothing or the value is
  obvious. `@param` is given for **all** parameters or for none.
- An overriding method may use `{@inheritDoc}`.
- A field's Javadoc may be one line: `/** Description */`.
- **Indent a comment to match the code it describes.** A trailing comment is
  allowed: `process("ABC"); // process a dummy String first`.

## Project-specific notes

These are decisions this project has already made under the standard. Follow
them rather than reopening them.

- **A `{@link X}` needs an import**, so a Javadoc link can create a package
  dependency. The packages here are deliberately one-way between siblings.
  Before adding a link across packages, check the import already exists;
  otherwise write the reference as prose.
- **`Dates.today()`, `TaskList.size()`, `TaskList.get()` and `TaskList.isEmpty()`
  are nouns or mirror the JDK collection API on purpose.** They read better than
  `getToday()` or `getSize()` would, and `LocalDate.now()` sets the precedent.
  Leave them.
- **`Task.occursOn(date)` is a boolean method without an `is`/`has` prefix.** It
  reads as a predicate in third person, which satisfies the intent of the rule.
  Leave it.
- **A class with only static members declares a private constructor**, so that
  Java does not supply a public one. `Dates`, `TaskFormat` and `Parser` do this.

## Checking

Checkstyle enforces the mechanical part of this standard. Its rules are in
`config/checkstyle/checkstyle.xml`, taken from the SE-EDU AddressBook Level 3
project, with `config/checkstyle/suppressions.xml` relaxing the Javadoc rules for
test classes. Run it over both source sets:

```bash
./gradlew checkstyleMain checkstyleTest
```

A violation fails the build, and the reports under `build/reports/checkstyle/`
list every one. Fix the code rather than loosening a rule. Where a rule genuinely
cannot apply, suppress it for that region only:

```java
//CHECKSTYLE.OFF: RuleName
//CHECKSTYLE.ON: RuleName
```

Checkstyle does not check Javadoc content, so this still has to stay at **zero
warnings**:

```bash
./gradlew javadoc
```

The judgement-level rules — whether a name reads well, whether a comment explains
why rather than what, whether a method does one thing — are not expressible as
Checkstyle rules and are still checked by reading.
