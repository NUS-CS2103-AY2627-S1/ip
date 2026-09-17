---
name: seedu-java-coding-standard
description: Enforces the SE-EDU Java Coding Standard (Basic + Intermediate rules) for all Java source and test files in the project. Use whenever writing, editing, refactoring, reviewing, or formatting Java code.
---

# SE-EDU Java Coding Standard (Basic + Intermediate)

This skill mandates and explains the **SE-EDU Java Coding Standard** (combining Basic and Intermediate conventions) for all code written or modified in this repository.

For any topics not covered in this guide, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

---

## 1. Naming Conventions

### Packages
* **All lowercase**: Package names must consist entirely of lowercase letters (no uppercase, no underscores).
  ```java
  com.company.application.ui
  panda.command
  panda.parser
  ```
* **Project root package**: Root package name should be the project name or group name followed by logical package names (e.g., `panda.ui`, `panda.storage`). Do not use `edu.nus.comp.*`.

### Classes and Enums
* **PascalCase & Nouns**: Must be nouns or noun phrases written in `PascalCase`.
  ```java
  Line, AudioSystem, TaskList, AddDeadlineCommand
  ```

### Variables
* **camelCase**: Variables (instance fields, local variables, parameters) must be written in `camelCase`.
  ```java
  line, audioSystem, taskCount
  ```
* **Scope-based length**: Variables with a large scope should have longer, descriptive names; variables with a small scope (e.g. within a few lines) can have short names (`i`, `j`, `k` for integer loops; `c`, `d` for chars).
* **Booleans**: Names must sound like booleans with prefixes such as `is`, `has`, `can`, `should`, `was`.
  ```java
  boolean isSet, isVisible, isFinished, isFound, isOpen, hasData, wasOpen;
  boolean hasLicense();
  boolean canEvaluate();
  ```
* **Boolean Setters**: Setter methods for boolean variables must take the form:
  ```java
  void setFound(boolean isFound);
  ```
* **Collections & Arrays**: Use plural nouns to represent collections or arrays.
  ```java
  Collection<Point> points;
  int[] values;
  List<Task> tasks;
  ```
* **Iterator variables**: Can be called `i`, `j`, `k`. Use `j`, `k` only for nested loops.

### Constants
* **SCREAMING_SNAKE_CASE**: All uppercase letters with words separated by underscores for static final constants and enum constants.
  ```java
  public static final int MAX_ITERATIONS = 10;
  public static final String COLOR_RED = "#FF0000";
  ```
* **Common prefix for associated constants**: Related constants must share a prefix to indicate grouping and keep them clustered when sorted.
  ```java
  static final int COLOR_RED   = 1;
  static final int COLOR_GREEN = 2;
  static final int COLOR_BLUE  = 3;
  ```

### Methods
* **camelCase & Verbs**: Method names must be verbs or verb phrases in `camelCase`.
  ```java
  getName(), computeTotalWidth(), execute()
  ```
* **Test Method Names**: May use underscores following the three-part pattern:
  `featureUnderTest_testScenario_expectedBehavior()`
  ```java
  sortList_emptyList_exceptionThrown()
  getMember_memberNotFound_nullReturned()
  parse_validDeadlineCommand_returnsAddDeadlineCommand()
  ```
  The third part or both second and third parts can be omitted if the test covers broader scenarios (e.g. `sortList_emptyList()`, `sortList()`).

### Acronyms and Abbreviations
* **Avoid uppercase acronyms**: Treat abbreviations and acronyms as regular words rather than capitalizing every letter.
  * **Good**: `exportHtmlSource()`, `openDvdPlayer()`, `parseXml()`, `class Ui`
  * **Bad**: `exportHTMLSource()`, `openDVDPlayer()`, `parseXML()`, `class UI`

### Language
* **English only**: All identifiers, comments, and strings must be in English with American spelling.

---

## 2. Layout & Formatting

### Indentation & Line Length
* **4-space indentation**: Basic indentation is strictly 4 spaces (never tabs).
* **Line length limit**: Soft limit is 110 characters; hard limit is 120 characters.
* **Wrapped line indentation**: Continuation lines must be indented by **8 spaces** (two levels) relative to the parent line.
  ```java
  setText("Long line split"
          + "into two parts.");
  if (isReady) {
      setText("Long line split"
              + "into two parts.");
  }
  ```

### Line Breaks
* **Break after a comma**.
* **Break before an operator**: Including binary operators (`+`, `-`, `*`), dot separators (`.`), type bounds (`&`), and multi-catch pipes (`|`).
  ```java
  totalSum = a + b + c
          + d + e;
  method(param1,
          object.method()
                  .method2(),
          param3);
  ```
* **Keep method/constructor name attached to opening parenthesis `(`**:
  * **Good**:
    ```java
    someMethodWithVeryVeryVeryVeryLongName(
            int anArg, Object anotherArg);
    ```
  * **Bad**:
    ```java
    someMethodWithVeryVeryVeryVeryLongName
            (int anArg, Object anotherArg);
    ```
* **Prefer higher-level breaks** over lower-level breaks.
* **Ternary operator layout**:
  ```java
  alpha = (aLongBooleanExpression) ? beta : gamma;
  // or
  alpha = (aLongBooleanExpression)
          ? beta
          : gamma;
  ```

### Braces Style (K&R / Egyptian Style)
* Opening braces `{` appear at the end of the line that begins the statement; closing braces `}` appear on a new line aligned with the statement start.
  ```java
  while (!done) {
      doSomething();
      done = moreToDo();
  }
  ```

### Control Statements Layout
* **if-else**:
  ```java
  if (condition) {
      statements;
  } else if (condition) {
      statements;
  } else {
      statements;
  }
  ```
* **for**:
  ```java
  for (initialization; condition; update) {
      statements;
  }
  ```
* **while & do-while**:
  ```java
  while (condition) {
      statements;
  }
  
  do {
      statements;
  } while (condition);
  ```
* **switch**:
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
  * Always include an explicit `// Fallthrough` comment whenever a `case` deliberately falls through without a `break`.
* **try-catch-finally**:
  ```java
  try {
      statements;
  } catch (Exception exception) {
      statements;
  } finally {
      statements;
  }
  ```

### Whitespace Within Statements
* Space around operators (`a = (b + c) * d;`).
* Space after Java keywords (`if (condition)`, `while (true)`, `for (...)`).
* Space after commas (`doSomething(a, b, c, d);`).
* Space around binary/ternary colons (`condition ? a : b`, `for (Task task : tasks)`).
* Space after semicolons in `for` statements (`for (int i = 0; i < 10; i++)`).

### Blank Lines
* Logical units within a method/block should be separated by **one blank line**.

---

## 3. Statements & Types

### Package and Imports
* **Put every class in a package**: No default/unnamed package.
* **Import ordering**:
  1. `import static ...` (alphabetically sorted)
  2. Blank line
  3. Standard Java imports (`java.*`, `javax.*`) (alphabetically sorted)
  4. Blank line
  5. Third-party imports (e.g., `org.junit...`) (alphabetically sorted)
  6. Blank line
  7. Application/project imports (`panda.*`) (alphabetically sorted)
* **Explicit imports only**: Never use wildcard imports (`import java.util.*;` is forbidden; use `import java.util.List;`).

### Types & Variables
* **Array brackets on type**: `int[] a = new int[20];` (never `int a[];`).
* **Variable initialization & scope**: Declare variables in the smallest possible scope and initialize where declared.
* **Encapsulation**: Class fields must never be declared `public` unless in a behavior-less data class. Use private/protected fields with accessors. (Constants `public static final` are permitted).

### Control Flow Braces
* **Always use curly braces `{}`** for all loop bodies and conditional statements, even if single-line:
  * **Good**:
    ```java
    if (isDone) {
        doCleanup();
    }
    ```
  * **Bad**:
    ```java
    if (isDone) doCleanup();
    ```

---

## 4. Comments & Javadoc

### Header Comments (Javadoc)
* **Mandatory for all public classes, interfaces, enums, and public methods/constructors**.
* Exceptions where Javadoc may be omitted:
  1. Getters and setters with obvious behavior.
  2. Overridden methods where parent Javadoc applies as-is.
  3. Test classes and test methods.

### Javadoc Structure & Formatting
* Opening `/**` on a separate line; subsequent lines prefixed with ` * `; no blank line between Javadoc block and declaration.
* **First sentence**: Short summary in third-person present tense starting with a verb (`Returns ...`, `Executes ...`, `Adds ...`, `Creates ...`, `Checks ...`, `Parses ...`, `Signals ...`).
* **Tag sections (`@param`, `@return`, `@throws`)**:
  * Separated from summary description by an empty comment line (` * `).
  * Capitalize the first letter of each tag description and **end each description with a period `.`**.
  * Either document all `@param` tags or omit all if fully self-explanatory.
* **Indentation**: Comments must be indented relative to their surrounding code.

```java
/**
 * Computes lateral location of the specified position.
 * If the position is unset, NaN is returned.
 *
 * @param x X coordinate of position.
 * @param y Y coordinate of position.
 * @param zone Zone of position.
 * @return Lateral location.
 * @throws IllegalArgumentException If zone is <= 0.
 */
public double computeLocation(double x, double y, int zone)
        throws IllegalArgumentException {
    // ...
}
```

---

## 5. Verification Checklist

When reviewing or writing Java code, verify:
- [ ] No wildcard imports (`.*`).
- [ ] Static imports grouped at top, followed by sorted groups with blank lines.
- [ ] Class, method, variable, and constant names adhere to casing and acronym rules (`Ui` not `UI`).
- [ ] Boolean fields/methods use boolean sound (`isMarked`, `hasData`).
- [ ] All `if`, `else`, `for`, `while`, `do` blocks use `{}`.
- [ ] Indentation is 4 spaces (8 for continuation lines).
- [ ] Line lengths $\le 120$ chars.
- [ ] All public classes and public non-trivial methods have complete Javadoc with trailing periods on tags.
