---
name: seedu-java-coding-standard
description: SE-EDU intermediate Java coding standard for this project
---

# SE-EDU Java Coding Standard (Intermediate)

Apply these rules to all Java code in this project.

## Naming

- **Packages**: all lowercase (e.g. `amigobot.task`).
- **Classes/Enums**: PascalCase nouns (e.g. `TaskList`, `Command`).
- **Methods**: camelCase verbs (e.g. `getTask()`, `computeTotal()`).
- **Variables**: camelCase (e.g. `taskCount`, `isDone`).
- **Constants**: SCREAMING_SNAKE_CASE (e.g. `MAX_ITERATIONS`).
- **Test methods**: `featureUnderTest_testScenario_expectedBehavior()`.
- **Booleans**: prefix with `is`, `has`, `was`, `can`, `should`.
- **Collections**: use plural names (e.g. `tasks`, `values`).
- **Abbreviations**: no all-caps in names (`exportHtmlSource`, not `exportHTMLSource`).
- **All names in English.**
- **Scope-based length**: large scope = long descriptive names; small scope = short names.

## Layout

- **Indentation**: 4 spaces, no tabs.
- **Line length**: 120 characters hard limit, 110 soft limit.
- **Wrapped lines**: indent 8 spaces from the original line.
- **Line breaks**: break after commas, before operators. Keep method name attached to `(`.
- **Braces**: K&R / Egyptian style (opening brace on same line).
- **Blank lines**: separate logical sections within methods.
- **Whitespace**: spaces around operators, after commas, after reserved words (`if (`, `while (`), after semicolons in `for`.

## Statements

- **No wildcard imports** (`import java.util.*` is wrong; use explicit imports).
- **Import ordering**: static imports first, then `java.*`, `javax.*`, third-party, project. Blank line between groups.
- **Array specifier on type**: `int[] a`, not `int a[]`.
- **Initialize variables at declaration**, in the smallest possible scope.
- **No public class variables** (use getters/setters). Exception: constants.
- **Always use curly braces** for `if`, `for`, `while`, even for single statements.
- **Conditionals on separate lines**: no `if (x) doThing();` on one line.

## Switch Statements

- Traditional `switch` with `case`, `break`, and `default`.
- Include `// Fallthrough` comment when a case intentionally falls through.
- Modern arrow syntax (`->`) is also acceptable.

## Comments

- **All comments in English**, American spelling.
- **Javadoc on all public classes and public methods.**
- **Can omit Javadoc for**: trivial getters/setters, overridden methods where parent Javadoc applies identically, test classes/methods.
- **Javadoc format**: `/**` on its own line, first sentence is the summary, `@param`/`@return`/`@throws` after a blank line, no blank line between Javadoc and the method.
- **Method Javadoc starts with verb**: "Returns...", "Adds...", "Sends..." (not "Return" or "Returning").
- **Comment indentation**: align with the code they describe.
