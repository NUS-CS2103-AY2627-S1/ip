---
name: seedu-java-coding-standard
description: The SE-EDU Java coding standard (intermediate level) that all Java code in this project must follow. Use when writing, reviewing, or reformatting any Java code here, and when asked to check or fix coding standard compliance, naming, import order, line length, or brace style.
---

# SE-EDU Java Coding Standard

Every `.java` file in this project, under `src/main/java` and `src/test/java`
alike, follows the intermediate-level SE-EDU Java coding standard:
https://se-education.org/guides/conventions/java/intermediate.html

Apply these rules to code you write as you write it, not as a clean-up pass
afterwards.

## Check compliance

Checkstyle is the authority. From the repository root:

```bash
./gradlew checkstyleMain checkstyleTest
```

It runs the official SE-EDU configuration in `config/checkstyle/`, and is wired
into `check`, so `./gradlew build` fails on any violation. Reports land in
`build/reports/checkstyle/`. Where this document and that configuration
disagree, the configuration wins.

For a quicker scan that needs no Gradle:

```bash
py .claude/skills/seedu-java-coding-standard/scripts/check-style.py
```

Pass one or more source roots as arguments to narrow the scan; it defaults to
`src/main/java` and `src/test/java`. The script reports file, line, and rule
for every violation it can detect mechanically: tabs, trailing whitespace, line
length, wildcard imports, import grouping and ordering, missing blank line
after the import block, missing space after a keyword, array specifiers on the
variable, unbraced conditionals and loops, and public non-constant fields.

It exits non-zero when it finds something. A clean run does not prove full
compliance -- naming and comment rules still need a human read -- but a dirty
run always means something must be fixed.

## Naming

* Packages are all lower case: `ff15.task`, `ff15.command`.
* Classes and enums are nouns in PascalCase: `TaskList`, `CommandWord`.
* Methods are verbs in camelCase: `getLabel()`, `computeTotalWidth()`.
* Variables are camelCase: `taskCount`, `fieldSeparator`.
* Constants are SCREAMING_SNAKE_CASE: `FIELD_SEPARATOR`, `DATE_DISPLAY`.
* Acronyms are not left uppercase inside a name: `exportHtmlSource()`, not
  `exportHTMLSource()`.
* Booleans read like booleans, using an `is`, `has`, `was`, `can`, or `should`
  prefix: `isDone`, `hasTime`, `acceptsArguments`.
* Collections take a plural name: `tasks`, `matches`, `lines`.
* Scratch and loop variables may be short (`i`, `j`, `k`); anything with a wide
  scope gets a long, descriptive name.
* Test methods use `featureUnderTest_testScenario_expectedBehavior()`, e.g.
  `parse_malformedDate_throwsException()`.
* All names are in English.

## Layout

* Indent with 4 spaces. Never tabs.
* Indent wrapped lines by 8 spaces -- twice the normal indent -- relative to
  the line being continued.
* Line length: 110 characters soft limit, 120 hard limit.
* Break after commas, and before operators (including `.`, and `|` in a
  multi-catch).
* K&R (Egyptian) braces: the opening brace ends the line that opens the block.
* Separate logical units inside a block with one blank line.

## Statements

* Every class belongs to a package.
* Import each class explicitly. Never `import java.util.*;`.
* Group imports in this order, separated by blank lines, and sort
  alphabetically inside each group:
  1. static imports
  2. `java.*` and `javax.*`
  3. `org.*`, which here means `org.junit.*`
  4. `com.*`
  5. everything else, which here means this project's own `ff15.*` packages
     and `javafx.*`
* Leave a blank line between the import block and the type declaration.
* Attach array brackets to the type: `int[] values`, not `int values[]`.
* Declare variables in the smallest scope that works, and initialise them where
  they are declared.
* Class variables are never `public` unless the class is a pure data holder.
  Constants are exempt.
* Brace every conditional and loop body, however short. Never
  `if (stream != null) readFile(stream);`.
* Put the conditional on its own line, separate from the body.

## Comments

* Write comments in English, using American spelling, and avoid slang.
* Give every public class and method a descriptive header comment. It may be
  omitted for plain getters and setters, for an override whose parent Javadoc
  applies unchanged, and for test classes and methods.
* Javadoc format: `/**` alone on its own line, following `*` aligned under the
  first one with a space after each, no blank line between the comment and what
  it documents.
* Start a method summary with a verb in the third person: "Returns ...",
  "Adds ...", "Sends ...". Not "Return ...".
* Leave a blank line between the description and the first `@param`.
* End every parameter description with punctuation.
* Give `@param` for every parameter or for none. Do not tag only some of them.
* `@return` may be omitted when the method returns nothing, or when the summary
  sentence already makes the return value obvious.
* A single-line member comment is fine: `/** When the task is due. */`.
* Indent comments to match the code they describe.

## Related

See [[seedu-git-standard]] for the commit message conventions used alongside
this standard.
