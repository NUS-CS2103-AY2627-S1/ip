---
name: seedu-java-coding-standard
description: The SE-EDU intermediate Java coding standard (naming, layout, statements, comments/Javadoc) that all Java code in this project must follow. Load before writing, editing, or reviewing any .java file under src/ - both new code and changes to existing code.
---

# seedu-java-coding-standard

Source of truth: https://se-education.org/guides/conventions/java/intermediate.html

This project mandates the rules below for every `.java` file under `src/`.
When writing or editing Java code, apply these rules directly. When
reviewing code (yours or someone else's), check against this list. If a
rule and an explicit user instruction conflict, ask rather than silently
picking one.

## Naming

- **Packages**: all lower case (e.g. `maggigorengayam.command`).
- **Classes/enums**: nouns in `PascalCase` (e.g. `TaskList`).
- **Variables**: `camelCase` (e.g. `taskNumber`).
- **Constants** (`static final`): `ALL_CAPS_WITH_UNDERSCORES` (e.g. `MAX_ITERATIONS`). Constants that belong to one conceptual group should share a common prefix (e.g. `COLOR_RED`, `COLOR_GREEN`).
- **Methods**: verbs in `camelCase` (e.g. `computeTotalWidth()`). Test methods use `featureUnderTest_testScenario_expectedBehavior()` (parts may be omitted if not needed), e.g. `sortList_emptyList_exceptionThrown()`.
- **Abbreviations/acronyms** inside a name are not written in all caps: `exportHtmlSource()` not `exportHTMLSource()`; `Ui` not `UI`.
- **English only** for all identifiers and comments.
- **Scope-appropriate length**: short names (`i`, `j`, `k`, `c`, `d`) only for small-scope scratch variables; wider-scope variables get descriptive names. `j`/`k` are reserved for nested loops.
- **Booleans** read like a yes/no question: prefix `is`/`has`/`was` (`isDone`, `hasLicense()`, `wasOpen`).
- **Collections** are named in the plural (`tasks`, not `taskList` for a `List<Task>` field - though a class named `TaskList` is fine since the class itself isn't the collection instance).

## Layout

- **Indentation**: 4 spaces, never tabs.
- **Line length**: soft limit 110 chars, hard limit 120 chars. Wrap longer lines.
- **Wrapped-line indentation**: 8 spaces relative to the line being continued (not 4). When a wrapped string is still too long, split it into concatenated literals across lines, breaking *before* the `+` operator, each continuation indented another 8 spaces past the previous line - see the existing `/from`/`/to` messages in `Parser.java` for the pattern.
- **Braces**: K&R/Egyptian style - opening brace on the same line, e.g. `while (!done) {`.
- **Braces are never omitted**, even for a single-statement `if`/`for`/`while` body:
  ```java
  if (stream != null) {
      readFile(stream);
  }
  ```
  not `if (stream != null) readFile(stream);`.
- **Conditionals** go on their own line, never inlined with the body on the same line as the `if`.
- **`switch`**: `case` labels are indented one level deeper than `switch`, and case bodies one level deeper again:
  ```java
  switch (condition) {
      case ABC:
          statements;
          // Fallthrough
      case DEF:
          statements;
          break;
      default:
          statements;
          break;
  }
  ```
  Any case that intentionally has no `break` must have a `// Fallthrough` comment.
- **Whitespace**: space after `if`/`while`/`for`/`catch`/etc. before `(`; space around binary operators; space after commas. `a = (b + c) * d;` not `a=(b+c)*d;`.
- **Blank lines** separate logical units within a method body.

## Statements

- **Every class lives in a package** (already true throughout this project).
- **No wildcard imports** - always import classes explicitly (`import java.util.List;`, never `import java.util.*;`).
- **Import order**: static imports, then `java.*`, then `javax.*`, then `org.*`, then `com.*`, then everything else (this project's own `maggigorengayam.*` packages count as "everything else" and come after the `java.*` group, as already done consistently).
- **Array specifiers attach to the type**, not the variable: `int[] a`, never `int a[]`.
- **Declare variables in the smallest scope possible, initialized at the point of declaration** - except when the initial value genuinely depends on a branch taken later (e.g. a `try`/`catch` or `switch` that decides the value); that's a legitimate exception, not a violation.
- **Instance fields are never `public`** unless the class is a pure data-holder with no behavior (e.g. `Storage.LoadResult`, `DateTimeUtil.ParsedDateTime` - both are fine as-is). `protected` fields on a base class meant for inheritance (e.g. `Task.description`) are not covered by this rule.
- **Loop bodies always use braces**, regardless of how many statements they contain.

## Comments / Javadoc

- **Every public class and every public method needs a header Javadoc comment.** Optional (but welcome) for getters/setters, method overrides whose parent Javadoc still applies as-is, and test classes/methods.
- **Format**:
  ```java
  /**
   * Returns lateral location of the specified position.
   *
   * @param x X coordinate of position.
   * @return Lateral location.
   * @throws IllegalArgumentException If zone is <= 0.
   */
  public double computeLocation(double x, int zone) throws IllegalArgumentException {
  ```
  - Opening `/**` on its own line for anything longer than one sentence; a short doc may stay on one line: `/** Number of connections to this database */`.
  - First sentence is a short summary.
  - **Method doc summaries are third-person present tense, not imperative**: `Returns ...`, `Parses ...`, `Adds ...` - not `Return ...`, `Parse ...`, `Add ...`.
  - Blank `*` line between the description and the `@param`/`@return`/`@throws` block, when both are present.
  - No blank line between the Javadoc block and the class/method it documents.
  - `@param`/`@return` are optional when the parameter/return value is self-explanatory from the summary - don't add them just to fill the template.
  - A literal `<...>` placeholder (e.g. documenting the `mark <n>` command) must be written as `{@code mark <n>}`, not backticks or raw angle brackets - raw `<n>` is parsed as an (invalid) HTML tag by the `javadoc` tool and breaks the build.
  - A `{@link Foo}` to a class not imported in the current file must be fully qualified (`{@link maggigorengayam.task.Task}`) or it won't resolve.
- Comments are indented to match the code they describe.

## Verifying

`./gradlew javadoc` must complete with 0 errors (warnings for missing optional `@param`/`@return` are fine and consistent with this project's existing terse doc style - don't chase them). `./gradlew build` must stay green after any change made to satisfy this standard.
