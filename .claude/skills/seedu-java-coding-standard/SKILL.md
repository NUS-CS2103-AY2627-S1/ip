---
name: seedu-java-coding-standard
description: The SE-EDU intermediate Java coding standard (https://se-education.org/guides/conventions/java/intermediate.html) that ALL Java code in this project must follow, mechanically enforced by Checkstyle (config/checkstyle/checkstyle.xml, run via ./gradlew checkstyleMain checkstyleTest). Use this whenever writing new Java code, editing existing Java code, or reviewing/auditing code in this repository for style — naming, brace style, import ordering, declaration order, line length, comments, and general layout. Trigger on any request to write, refactor, format, lint, or review Java source in this project, even if the user doesn't name the standard explicitly.
---

# SE-EDU Intermediate Java Coding Standard

This is the coding standard this project's `AGENTS.md` mandates for all Java code. Apply it whenever writing new code; when reviewing or auditing existing code, check against every rule below.

**This standard is also mechanically enforced by Checkstyle**, configured in `config/checkstyle/checkstyle.xml` (copied from AddressBook Level 3, which targets this exact standard) and run via `./gradlew checkstyleMain checkstyleTest` — both are wired into `check`, so `./gradlew build` fails on any violation. Checkstyle is the authoritative, automatically-checked source of truth: if this document and what Checkstyle actually flags ever disagree, trust Checkstyle and update this document to match, not the other way around. (This has already happened once — the import-ordering rule below was corrected after Checkstyle's `CustomImportOrder` module turned out stricter than this document originally described.)

## Naming

- **Packages**: all lower case (e.g. `ace.task`).
- **Classes/enums**: nouns, PascalCase (e.g. `Deadline`, `TaskManager`).
- **Variables**: camelCase. Scope drives length — a large-scope variable deserves a long, descriptive name; a small-scope one (a loop counter, say) can be short (`i`, `j`, `k`, `n` for integers; `c`, `d` for characters).
- **Booleans**: prefix with `is`/`has`/`was`/`can` (variables: `isVisible`, `hasData`; methods: `boolean isCompleted()`, `boolean hasLicense()` — **not** `boolean getCompleted()`). Boolean setters still use `set`: `void setFound(boolean isFound)`.
- **Collections**: plural names (`Collection<Point> points`).
- **Constants**: `ALL_CAPS_WITH_UNDERSCORES` (e.g. `MAX_ITERATIONS`). Related constants share a prefix (`COLOR_RED`, `COLOR_GREEN`). Only applies to `static final` fields — a `private` field of any mutability doesn't need to be all-caps even if it's never reassigned.
- **Methods**: verbs, camelCase (e.g. `getName()`, `computeTotalWidth()`).
- **Test methods**: `featureUnderTest_testScenario_expectedBehavior()` (e.g. `sortList_emptyList_exceptionThrown()`).
- **Abbreviations**: not all-caps when part of a name — `exportHtmlSource()`, not `exportHTMLSource()`.
- All names in English.

## Layout & Formatting

- **Indentation**: 4 spaces, never tabs. A wrapped line indents 8 spaces (twice normal) from its parent line.
- **Line length**: soft limit 110 chars, hard limit 120 — never exceed 120.
- **Every file must end with a newline** (a trailing blank line after the final `}`). Easy to lose when hand-editing the last line of a file — Checkstyle's `NewlineAtEndOfFile` catches it immediately.
- **Braces: K&R ("Egyptian") style, no exceptions.** The opening brace stays on the same line as the statement, and `else`/`catch`/`finally` stay on the same line as the closing brace before them:
  ```java
  if (condition) {
      statements;
  } else if (other) {
      statements;
  } else {
      statements;
  }

  try {
      statements;
  } catch (SomeException e) {
      statements;
  } finally {
      statements;
  }
  ```
  Never `}\nelse {` or `}\ncatch (...) {` on separate lines — that's Allman style, not K&R, and is a hard violation.
- **Loop and conditional bodies are always braced**, even a single statement — no bare `if (x) doThing();`.
- **Whitespace**: spaces around binary operators (`a = (b + c) * d;`), space after reserved words before `(` (`while (true) {`), space after commas (`doSomething(a, b, c);`), space after `;` in a `for` header (`for (i = 0; i < 10; i++)`).
- **Wrapping a long call/lambda**: when a line must wrap, don't start the new line with an opening `(` — keep it attached to the end of the previous line. Wrong:
  ```java
  assertThrows(WrongCommandException.class,
          () -> Parser.parseCommand(input, taskManager, ui));
  ```
  Right — either fits on one line, or the wrap point moves so `(` stays at end-of-line:
  ```java
  assertThrows(WrongCommandException.class, () -> Parser.parseCommand(
          input, taskManager, ui));
  ```
- **Blank lines**: separate logical units within a block with one blank line.
- **Array declarations**: bracket attaches to the type, not the variable — `int[] a = new int[20];`, never `int a[] = ...`.

## Statements

- **Imports**: always explicit, never a wildcard (`import java.util.*;` is forbidden). Group and order: static imports first, then `java.*`/`javax.*` (one merged group), then `org.*`, then `com.*` — alphabetical within each group. **This project's own `ace.*` packages don't belong to any of those groups, so they sort last, after every other group** (verified directly against Checkstyle's `CustomImportOrder` output — it's stricter than "reasonable convention" here, it's an exact, enforced rule). Example:
  ```java
  import java.time.LocalDate;
  import java.time.format.DateTimeFormatter;

  import org.junit.jupiter.api.Test;

  import ace.exception.WrongDateFormatException;
  import ace.task.TaskManager;
  ```
- **Variables**: initialize where declared, and declare in the smallest scope that works.
- **Class fields should never be `public`**, unless the class is a pure data holder with no behavior (no non-trivial methods). A class with real methods (state changes, computed behavior) keeps its fields `private` and exposes them through accessors if needed.
- **Modifier order** follows the JLS-prescribed sequence: `public`/`protected`/`private`, then `abstract`, then `static`, then `final`, then `transient`, `volatile`, `synchronized`, `native`, `strictfp`. So `public abstract class Task` (not `abstract public`), `private static final int MAX` (not `private final static`).
- **Declaration order within a class**: static fields, then instance fields, then constructors, then methods — with each of the two field groups themselves ordered `public` → `protected` → package-private → `private`. A `private` instance field declared above a `public static final` constant is out of order, even though both are individually named/scoped correctly.

## Comments

- Written in English, American spelling.
- **Every public class and public method needs a header comment** — *except* getters/setters, overridden methods (when the parent's documentation already applies — use `{@inheritDoc}` instead of repeating it), and test classes/methods (their descriptive names already say what they do).
- Javadoc shape:
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
  ```
  - Opening `/**` on its own line; subsequent `*` aligned with it, one space after each `*`.
  - First sentence is a short summary, written in third person ("Returns...", "Adds...", "Marks..."), not imperative ("Return...", "Add...", "Mark...").
  - A blank `*` line separates the description from the `@param`/`@return`/`@throws` block.
  - No blank line between the Javadoc block and the class/method it documents.
  - Document either **all** parameters or none (unless a parameter is genuinely self-explanatory).
  - `@return` can be omitted for `void` methods or when the return value is obvious.

## When reviewing existing code against this standard

Check, in order: brace placement (`}` immediately followed by `else`/`catch`/`finally` on the *same* line, never the next), public fields on behavior-bearing classes, boolean-returning methods named with `get` instead of `is`/`has`, import grouping/ordering (own-package imports last), modifier order, declaration order (static-before-instance, then constructors, then methods), missing trailing newline, line length, wrapped lines starting with `(`, and whether public methods (other than the documented exceptions above) have header comments. Report findings with file and line, and don't silently fix anything unless asked — this project's convention is to report first and apply changes only after explicit approval. For a definitive check rather than a manual read-through, `./gradlew checkstyleMain checkstyleTest` catches most of this automatically — worth running alongside a manual review, not instead of one, since a few things (like the getter/setter Javadoc-omission judgment call) still need human reasoning.
