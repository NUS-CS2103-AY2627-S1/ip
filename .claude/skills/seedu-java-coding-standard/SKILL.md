---
name: seedu-java-coding-standard
description: Java coding standard for this project (SE-EDU intermediate Java conventions). Use whenever writing, reviewing, or editing any .java file in this repository, so new and modified code complies with naming, layout, statement, and comment rules.
version: 1.0.0
user-invocable: true
---

Applies the SE-EDU intermediate Java coding standard
(https://se-education.org/guides/conventions/java/intermediate.html) to all
Java code in this project. Check new/edited code against every rule below
before considering the work done.

## Naming

- Packages: all lowercase (e.g. `kiki.task`). Never use institutional
  prefixes like `edu.nus.comp.*`.
- Classes/enums: nouns in `PascalCase` (e.g. `TaskList`).
- Variables: `camelCase`.
- Constants: `SCREAMING_SNAKE_CASE`, with a common prefix when related
  (e.g. `COLOR_RED`, `COLOR_GREEN`).
- Methods: verbs in `camelCase` (e.g. `getName()`, `computeTotalWidth()`).
- Test methods: `featureUnderTest_testScenario_expectedBehavior()`; the
  second and/or third part may be omitted when the test covers all
  scenarios for that feature (e.g. `sortList_emptyList()`, `sortList()`).
- Abbreviations/acronyms are not upper-cased inside a name:
  `exportHtmlSource()`, not `exportHTMLSource()`.
- All names in English.
- Scope-appropriate length: short names (`i`, `j`, `k`, `c`, `d`) only for
  small-scope scratch variables; longer, descriptive names for larger
  scope. `j`, `k` are reserved for nested loops — the outer loop uses `i`.
- Booleans read like yes/no questions: `isSet`, `hasData`, `wasOpen`,
  `boolean canEvaluate()`.
- Collections use plural names: `Collection<Point> points;`, `int[] values;`.

## Layout

- 4 spaces per indent level, no tabs.
- Line length: soft limit 110 chars, hard limit 120 chars.
- Wrapped lines get 8 extra spaces (twice the normal indent) relative to
  the parent line; break after a comma or before an operator; keep a
  method/constructor name attached to its opening parenthesis; prefer
  higher-level breaks over breaks inside a parenthesized expression.
- K&R ("Egyptian") braces — opening brace on the same line as the
  statement, never on its own line.
- Always wrap loop and conditional bodies in `{ }`, even single-statement
  ones; the condition/statement itself goes on its own line (never
  `if (x) doThing();` on one line).
- Traditional `switch` with intentional fallthrough needs an explicit
  `// Fallthrough` comment on the case that omits `break`; the modern
  arrow form (`case ABC -> ...;`) does not need this.
- One blank line between logical units within a block (each unit ideally
  preceded by a short comment explaining its purpose).
- Whitespace: spaces around binary operators (`a = (b + c) * d;`), after
  keywords (`while (true) {`), after commas (`doSomething(a, b, c);`).

## Statements

- Every class belongs to a package.
- Imports are explicit — never `import java.util.*;`.
- Import order (blank line between groups): static imports, then
  `java.*`, then `javax.*`, then third-party libraries (alphabetically),
  then this project's own packages (`kiki.*`).
- Array brackets attach to the type, not the variable: `int[] a`, never
  `int a[]`.
- Declare variables in the smallest possible scope and initialize them
  at declaration, not before.
- Class fields are `private` unless the class is a plain data holder with
  no behavior; constants are exempt from this rule.

## Comments

- English, American spelling, no local slang.
- Javadoc is mandatory for every public class and public method, except:
  straightforward getters/setters, overridden methods whose parent
  Javadoc already applies as-is, and test code.
- Javadoc's first sentence is a short summary starting with a verb like
  `Returns ...`, `Adds ...`, `Sends ...` (not `Return ...` or
  `Returning ...`) — it is used as the one-line summary. Leave a blank
  line between the summary and the `@param`/`@return`/`@throws` block.
  If any parameter is documented, document all of them; `@param` may be
  skipped entirely when every parameter name is self-explanatory.
  `@return` may be skipped for `void` methods or an obvious return value.
- Inline comments are indented to match the code they describe, never
  outdented or left at the block's own indent when the code inside is
  indented further.

## Applying this in this project

- Run this checklist mentally (or literally re-read it) before finishing
  any edit to `src/main/java/**/*.java` or `src/test/java/**/*.java`.
- When a pre-existing rule violation is spotted in code you are already
  touching, fix it as part of that change. Do not go out of scope to
  fix unrelated files unless asked.
- See `docs/CODINGSTANDARDS.md` for this project's compact summary of the
  same standard.
