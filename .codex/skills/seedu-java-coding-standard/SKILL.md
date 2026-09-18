---
name: seedu-java-coding-standard
description: The SE-EDU Java coding standard (intermediate level) that all Java code in this project must follow. Use when writing or changing any Java code here, when reviewing Java code against the coding standard, when checking naming, layout, imports or Javadoc, and when reviewing someone else's pull request for coding standard violations.
---

# SE-EDU Java coding standard (intermediate)

Source: https://se-education.org/guides/conventions/java/intermediate.html

All Java code in this project follows the rules below. Apply them to new code
as it is written rather than fixing style afterwards.

## Naming

* Package names are all lower case. The root package is the project name,
  followed by logical group names: `kenbot`, `kenbot.task`.
* Class and enum names are nouns in `PascalCase`.
* Method names are verbs in `camelCase`.
* Variable names are `camelCase`.
* Constant names are `ALL_UPPERCASE_WITH_UNDERSCORES`.
* Related constants share a prefix: `COLOR_RED`, `COLOR_GREEN`.
* Abbreviations and acronyms are **not** written in uppercase inside a name:
  `exportHtmlSource()`, not `exportHTMLSource()`.
* All names are in English.
* Name length tracks scope: a widely used variable gets a long, descriptive
  name; a variable living for two lines can be short.
* `i`, `j`, `k`, `m`, `n` are acceptable for loop indices and integers, and
  `c`, `d` for characters. `j` and `k` only for nested loops.
* Booleans read as booleans, using `is`, `has`, `was` and similar prefixes.
  A boolean setter takes the form `void setFound(boolean isFound)`.
* Collections take a plural name.

### Test method names

`featureUnderTest_testScenario_expectedBehavior()`, for example
`of_blank_throws()`. The third part, or both the second and third parts, may be
dropped when they add nothing. Underscores are correct here and nowhere else.

## Layout

* Indent with **4 spaces**, never tabs.
* Line length: 110 characters is the soft limit, **120 is the hard limit**.
* Wrapped lines are indented **8 spaces** (twice the normal indent).
* Break lines *after* a comma and *before* an operator, including `.`, the `&`
  in a type bound, and the `|` in a multi-catch.
* Prefer breaking at the highest syntactic level available.
* A method or constructor name stays attached to its opening `(`.
* Use K&R ("Egyptian") braces: the opening brace ends the same line as the
  construct it belongs to, and `else`, `catch` and `finally` share a line with
  the preceding closing brace.
* Surround operators with spaces. Follow reserved words and commas with a
  space. A colon acting as a binary or ternary operator is surrounded by
  spaces; the colon after a `switch` label and the semicolons in a `for` header
  are only followed by one.
* Separate logical units inside a block with a single blank line.
* Modern `switch` expressions and arrow labels are acceptable. When a
  traditional `case` deliberately falls through, mark it `// Fallthrough`.
* A ternary is either entirely on one line, or split with each part on its own
  line.

## Statements

* Every class lives in a package.
* Import statements are listed explicitly — **no wildcard imports** such as
  `java.util.*`.
* Import order is consistent throughout the project: static imports first, then
  `java.*`, `javax.*`, `org.*`, `com.*`, `javafx.*`, `junit.*`, then the
  project's own packages, with a blank line between groups.
* Array brackets attach to the type, not the variable: `int[] a`, not
  `int a[]`.
* Declare variables in the smallest possible scope and initialise them where
  they are declared.
* Class variables are never `public` unless the class is a pure data class with
  no behaviour. Constants are exempt.
* A loop body is always wrapped in braces, however short it is.
* A conditional's statement goes on its own line, and is always wrapped in
  braces, even when it is a single statement.

## Comments and Javadoc

* Write all comments in English, using American spelling, and avoid slang.
* Write a descriptive header comment for every public class and public method,
  **except**:
  * getters and setters,
  * overridden methods, when the parent's Javadoc still applies,
  * test classes and test methods.
* Javadoc form: `/**` alone on its own line, subsequent `*` aligned under it
  with a space after each, a blank line between the description and the
  `@param`/`@return` block, a full stop after each parameter description, and
  no blank line between the comment and what it documents.
* The first sentence is a short summary, because Javadoc lifts it into the
  summary table. For a method it starts with a verb in the third person:
  "Returns", "Sends", "Adds" — never "Return" or "Returning".
* `@return` may be omitted when the method returns nothing or the value is
  obvious from the name.
* `@param` tags are either given for every parameter or left out entirely.
* `{@inheritDoc}` is available for overridden methods.
* A one-line Javadoc is acceptable for a class member.
* Indent a comment to match the code it describes. Trailing `// comments` on a
  line of code are allowed.

## How to check code against this

The mechanical rules can be scanned quickly from the repository root:

```bash
awk 'length>120 {print FILENAME":"FNR}' $(find src -name "*.java")   # hard limit
grep -rlP "\t" src --include="*.java"                                # tabs
grep -rnP "[ \t]+$" src --include="*.java"                           # trailing space
grep -rn "import .*\*;" src --include="*.java"                       # wildcard imports
grep -rn "\w\+ \w\+\[\]" src --include="*.java"                      # int a[] form
```

The judgement rules — whether a name is meaningful, whether a method does one
thing, whether a Javadoc sentence says anything useful — have to be read by a
person. Do not report a mechanical pass as a full review.

## When reviewing someone else's code

Read the standard before flagging anything, and honour the exemptions above.
Missing Javadoc on an override, on a getter, or on a test method is **not** a
violation. Neither is a single-line Javadoc. Tutorial boilerplate that the
course supplied is not the author's naming choice, so do not flag it.
