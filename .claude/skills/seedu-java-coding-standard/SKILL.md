---
name: seedu-java-coding-standard
description: The SE-EDU intermediate Java coding standard this project follows. Use whenever writing, reviewing, or refactoring Java in this repository, and before committing any Java change.
---

# SE-EDU Java coding standard (intermediate level)

> This file is mirrored at `.codex/skills/seedu-java-coding-standard/SKILL.md`.
> Update both copies together.

Source: <https://se-education.org/guides/conventions/java/intermediate.html>

Most of this is enforced mechanically by `config/checkstyle/checkstyle.xml`. Always finish with:

~~~
./gradlew checkstyleMain checkstyleTest
~~~

A clean run is necessary but not sufficient: naming quality and comment usefulness are not checkable by a tool.

## Naming

* Packages: all lower case, `projectname.logicalgroup` (this project uses `bibi`, `bibi.task`, `bibi.command`).
* Classes and enums: nouns in `PascalCase`.
* Methods: verbs in `camelCase`.
* Variables: nouns in `camelCase`. Constants: `UPPER_SNAKE_CASE`.
* Booleans read as booleans: prefix with `is`, `has`, `was`, `can`, `should` (`isComplete`, `hasMatch`, `canEvaluate()`).
* Collections take a plural name (`tasks`, `warnings`).
* Long names for wide scope, short for narrow. `i`, `j`, `k` are fine as loop counters only.
* Acronyms are not all-caps inside a name: `exportHtmlSource()`, not `exportHTMLSource()`.
* Related constants share a prefix (`COLOR_RED`, `COLOR_GREEN`).
* Test methods: `featureUnderTest_testScenario_expectedBehavior()`, e.g. `parse_emptyInput_exceptionThrown()`.
* All names in English.

## Layout

* Indent 4 spaces. Never tabs.
* Wrapped continuation lines indent 8 spaces.
* Maximum line length 120 characters; aim for 110.
* Break *after* a comma, *before* an operator. Never start a line with `(`.
* K&R braces: the opening brace ends the line that opens the block.
* Always use braces, even for a single-statement `if` or loop body.
* Put the condition of an `if` on its own line.
* One statement per line; one variable per declaration.
* Separate logical units inside a block with a single blank line.
* Space around binary operators, after commas, and after keywords: `while (true) {`, `doSomething(a, b)`.
* Case labels inside a `switch` are indented one level in from the `switch`.

## Statements

* Every class lives in a package.
* Import classes explicitly. No wildcard imports, no unused imports.
* Import order, one blank line between groups:
  1. static imports
  2. `java.` and `javax.`
  3. `org.`
  4. `com.`
  5. this project's own classes (`bibi.…`)
* Array brackets attach to the type: `int[] values`, not `int values[]`.
* Declare a variable in the smallest scope that works, and initialise it there.
* Never make a field public unless the class is a pure data class. Constants are exempt.

## Comments

* English, American spelling.
* Write a header comment for every public class and method. It may be omitted for getters and setters, for an override whose parent comment applies exactly, and in test classes.
* Javadoc form: `/**` on its own line, aligned `*` with a space after each, a one-sentence summary starting with a verb (`Returns…`, `Adds…`), then a blank line before any block tags.
* Supply `@param` for every parameter or for none; do not document only some.
* `@return` may be omitted when the method is void or the answer is obvious.
* Prefer `{@inheritDoc}` or a short specific summary on an override rather than copying the parent's tags.
* A `{@link}` to a class in another package needs a qualified name, e.g. `{@link bibi.Parser Parser}`. Run `./gradlew javadoc` to catch broken links, which do not fail compilation.
* Explain *why*, not *what*, in inline comments. `TODO` is preferred over `FIXME`.
