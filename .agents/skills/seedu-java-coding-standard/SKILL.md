---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java (Intermediate) coding conventions to any Java code written or reviewed in this project. Use whenever writing new Java code, editing existing Java code, or checking Java code for style compliance.
---

# SE-EDU Java coding standard

Source: https://se-education.org/guides/conventions/java/intermediate.html

Apply these rules to every Java file in this project — new code and edits to existing code alike.

## Naming

- **Packages:** all lowercase, dot-separated by logical grouping. Use the project's own root package (`clearblue`), never a university/organization path.
- **Classes/enums:** PascalCase nouns (`Task`, `CommandType`).
- **Variables:** camelCase (`taskList`, `commandArguments`).
- **Constants:** SCREAMING_SNAKE_CASE (`MAX_TASKS`, `DATA_FILE`).
- **Methods:** camelCase verbs (`getDescription()`, `computeTotal()`).
- **Test methods:** `featureUnderTest_testScenario_expectedBehavior()`, e.g. `parse_todoWithoutDescription_throwsWithHelpfulMessage()`. Omit parts that don't apply.
- **Abbreviations:** never kept uppercase inside a name — `exportHtmlSource()`, not `exportHTMLSource()`.
- **Language:** identifiers in English only.
- **Scope:** longer, descriptive names for large scope; short names (`i`, `j`, `k`) are fine for scratch/loop variables only.
- **Booleans:** prefix variables/methods with `is`/`has`/`was`/`can`/`should` (`isDone`, `hasData()`). A boolean setter takes the form `void setFound(boolean isFound)`.
- **Collections:** plural names (`Collection<Task> tasks`).
- **Associated constants:** share a common prefix to group them (`COLOR_RED`, `COLOR_GREEN`).

## Layout and formatting

- **Indentation:** 4 spaces, never tabs.
- **Line length:** soft limit 110 characters, hard limit 120.
- **Wrapped lines:** indent continuation by 8 spaces (double the normal indent); break after commas, before operators (including `.`, `&`, `|`).
- **Brace style:** K&R/Egyptian — opening brace on the same line (`while (!done) {`).
- **Loop/if bodies:** always braced, even for a single statement.
- **Ternary:** either on one line, or broken at `?` and `:` with 8-space indentation.
- **Switch:** cases indented; arrow syntax (`case X -> ...`) is fine and preferred where it avoids fallthrough ambiguity; include a `// Fallthrough` comment if a classic `case` intentionally omits `break`.
- **Whitespace:** spaces around binary operators, after reserved words before `(`, after commas; blank line between logical units within a block.
- **Arrays:** brackets attach to the type, not the variable — `Task[] tasks`, never `Task tasks[]`.
- **Imports:** no wildcard imports; group and sort (static imports, then `java.*`, `javax.*`, project packages, then third-party). Order alphabetically within a group.
- **Every class belongs in a package** — no default package.
- **Fields:** never `public` unless the class is a pure data class with no behavior (constants excluded from this rule). Initialize at declaration in the smallest possible scope.

## Comments and Javadoc

- English only, American spelling.
- **Javadoc is required** on every public/non-private class and method. It's **optional** for simple getters/setters, overridden methods, and test code — but this project's own `AGENTS.md` asks for Javadoc there too when the increment specifically targets documentation coverage (e.g. an `A-JavaDoc`-style increment).
- Format: `/**` on its own line; first sentence is a short summary starting with a verb (`Returns...`, not `Return...`); blank line between the description and `@param`/`@return`/`@throws`; punctuation after each `@param` description; include `@param` for all parameters or none; `@return` can be omitted if obvious; use `{@inheritDoc}` for an overridden method that adds nothing new.
- A one-line field comment is fine: `/** Description */ private int count;`.

## Applying this in the project

When writing or editing Java code here:
1. Follow every rule above without being asked.
2. If existing code in the file you're touching violates a rule, fix it as part of the change rather than perpetuating it — unless the fix is unrelated in scope to the current task, in which case flag it instead.
3. Compile and run the project's test suites (`javac`/`java` via `test/ui-test-plan.md`, and `./gradlew test`) after any style-driven change to confirm no regressions.
