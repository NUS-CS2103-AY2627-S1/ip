---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard (intermediate level) to Java code in this project. Use whenever writing, reviewing, or changing Java source or test code here, and when asked whether code follows the coding standard.
---

# SE-EDU Java Coding Standard

The rules below are the intermediate-level standard published at
<https://se-education.org/guides/conventions/java/intermediate.html>. They apply to every Java
file in this project, under `src/main/java` and `src/test/java` alike.

Apply them while writing code, not only when asked to check. When a rule and an existing
neighbouring file disagree, follow the rule and say so.

## Naming

* Put every class in a package, named in all lower case. For a school project, start the name
  with the project or group name and add logical groups, such as `todobuddy.ui`. Do not use
  `edu.nus.comp.*`.
* Name classes and enums with nouns in PascalCase, such as `Line` or `AudioSystem`.
* Name variables in camelCase, such as `line` or `audioSystem`.
* Name constants in SCREAMING_SNAKE_CASE, such as `MAX_ITERATIONS` or `COLOR_RED`.
* Name methods with verbs in camelCase, such as `getName()` or `computeTotalWidth()`.
* Name a test method `featureUnderTest_testScenario_expectedBehavior()`, such as
  `sortList_emptyList_exceptionThrown()`. Drop the second or third part when it adds nothing.
* Write an abbreviation or acronym as a normal word inside a name: `exportHtmlSource()`, not
  `exportHTMLSource()`; `openDvdPlayer()`, not `openDVDPlayer()`.
* Write every name in English.
* Give a variable with a large scope a long name and a variable with a small scope a short one.
  Use `i`, `j`, `k`, `m`, `n` for scratch indices and `c`, `d` for characters.
* Name a boolean variable or method so that it reads as a boolean, using a prefix such as `is`,
  `has`, `was` or `can`: `isSet`, `isVisible`, `hasData`, `hasLicense()`, `canEvaluate()`.
* Write a boolean setter as `void setFound(boolean isFound);`.
* Use a plural name for a collection, such as `Collection<Point> points;`.
* Use `j` and `k` only for nested loops.
* Give associated constants a common prefix, such as `COLOR_RED`, `COLOR_GREEN`, `COLOR_BLUE`.

## Layout

* Indent with 4 spaces. Never use tabs.
* Keep every line at or below 120 characters, and preferably below 110.
* Indent a wrapped line 8 spaces more than its parent line, twice the normal indentation.
* When wrapping, break after a comma, break before an operator (including `.`, `&` and `|`),
  keep a method or constructor name attached to its opening parenthesis, and prefer a break at
  a higher level of the expression to one at a lower level.
* Use K&R braces, with the opening brace at the end of the line that opens the block.
* Follow the standard forms for a method definition, `if`/`else`, `for`, `while`, `do`/`while`,
  `switch`, `try`/`catch` and `try`/`catch`/`finally`.
* Put `// Fallthrough` immediately before any `case` that deliberately runs into the next one.
* Put spaces around an operator, after a reserved word, after a comma, around a colon used as a
  binary or ternary operator, and after each semicolon of a `for` header:
  `a = (b + c) * d;`, `while (true) {`, `doSomething(a, b, c, d);`, `for (i = 0; i < 10; i++) {`.
* Separate logical units within a block with one blank line.

## Statements

* Put every class in a package.
* Keep the ordering of import statements consistent: static imports first, then `java.*`, then
  `javax.*`, then `org.*`, then `com.*`, then the project's own packages.
* List imported classes explicitly. Never write a wildcard import such as `import java.util.*;`.
* Attach an array specifier to the type, not the variable: `int[] a = new int[20];`, not
  `int a[] = new int[20];`.
* Declare a variable in the smallest possible scope and initialize it where it is declared.
* Never declare a class variable public unless the class is a data class with no behaviour.
  Constants are exempt.
* Wrap a loop body in braces however few lines it holds.
* Put a conditional on its own line, and wrap the body of a conditional in braces even when it
  holds one statement.

## Comments and Javadoc

* Write every comment in English, using American spelling and no local slang.
* Write a descriptive header Javadoc for every public class and public method. It may be omitted
  for a getter or setter, for an override whose inherited documentation applies exactly, and for
  a test class or test method.
* Put the opening `/**` on its own line, align the later `*` characters under the first one, and
  leave a space after each `*`.
* Make the first sentence a short summary. For a method, start it with an action verb in the
  third person, such as `Returns`, `Sends` or `Adds`, and end it with punctuation.
* Leave one blank line between the description and the tags, and no blank line between the
  closing `*/` and the declaration it documents.
* End each `@param` description with punctuation. Include `@param` for every parameter or for
  none: omit them only when all parameters are self-explanatory or already explained in the
  description. Omit `@return` when the method returns nothing or the return value is obvious.
  Include a `@throws` tag when it tells the reader something the description does not.
* Use `{@inheritDoc}` when an override needs the inherited documentation with small additions.
* A one-line Javadoc such as `/** Number of items. */` is fine for a member.
* Indent a comment to match the code it describes. A trailing comment is fine where useful.

## Check the code against the standard

These commands find the violations that can be spotted mechanically. Run them from the
repository root. Each should print nothing.

```bash
grep -rn "	" src --include="*.java"
```

```bash
awk 'length > 120 {print FILENAME ":" FNR ": " length " chars"}' $(find src -name "*.java")
```

```bash
grep -rn "^import .*\*;" src --include="*.java"
```

```bash
grep -rnE "\w+ \w+\[\] *[=;]|\w+ \w+\[\]\)" src --include="*.java" | grep -vE "\w+\[\] \w+"
```

The rest needs reading: Javadoc wording, naming, blank lines between logical units, and braces
around single-statement conditionals and loops.
