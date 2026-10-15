---
name: seedu-java-coding-standard
description: Comprehensive Java coding standard based on SE-EDU (Basic + Intermediate rules) and the Google Java Style Guide. Use when writing, refactoring, reviewing, or analyzing Java source code in this project.
---

# SE-EDU Java Coding Standard (Basic + Intermediate)

Follow these coding conventions when writing, modifying, or reviewing Java code in this project. For any topics not explicitly covered in this document, fall back on the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

---

## 1. Naming Conventions

### General
* All identifier names must be in clear **English** (using consistent American spelling). Avoid slang or informal terms.

### Packages
* Package names must be in **all lowercase** without underscores (e.g., `james.ui`, `james.task`).
* In student projects, root package names must reflect the project or group name, not institutional domains (do **not** use `edu.nus.comp.*`).

### Classes and Enums
* Class, interface, and enum names must be **nouns or noun phrases** written in **PascalCase** (UpperCamelCase) (e.g., `TaskList`, `UserInputException`, `Command`).

### Variables and Fields
* Variable, field, and parameter names must be written in **camelCase** (lowerCamelCase) (e.g., `taskList`, `filePath`, `delIdx`).
* **Scope-dependent length**: Variables with a large scope should have long, descriptive names; variables with a small, localized scope (e.g., loop indices `i`, `j`, `k`) may have short names.
* **Collections**: Names representing a collection or array of items must use **plural forms** (e.g., `List<Task> tasks`, `int[] counts`).
* **Boolean variables**: Must sound like booleans and use appropriate prefixes such as `is`, `has`, `was`, `can`, `should` (e.g., `isRunning`, `isDone`, `hasMatch`).
* **Boolean setters**: Setters for boolean properties must follow the format `void setFound(boolean isFound);`.

### Constants
* Constant names (static final fields whose values are immutable) must be in **all uppercase with words separated by underscores** (`SCREAMING_SNAKE_CASE`) (e.g., `DISPLAY_FORMAT`, `DEFAULT_CAPACITY`).
* **Associated constants**: Constants belonging to a logical group should share a common prefix (e.g., `COLOR_RED`, `COLOR_GREEN`).

### Methods
* Method names must be **verbs or verb phrases** written in **camelCase** (e.g., `parseCommand`, `deleteTask`, `isDone`).
* **Boolean methods**: Must sound like predicates returning a boolean value (e.g., `isDone()`, `hasNextCommand()`, `isTaskMatchingDate(...)`).
* **JUnit test methods**: Must follow the 3-part structured convention:
  $$\text{featureUnderTest\_testScenario\_expectedBehavior}$$
  (e.g., `parseDate_validDateString_returnsParsedDate()`, `getTask_negativeIndex_throwsIndexOutOfBoundsException()`).

---

## 2. Layout & Formatting

### Indentation
* Use **4 spaces** for basic indentation level. **Never use tabs**.

### Line Length
* **Soft limit**: Aim to keep line lengths under **110 characters**.
* **Hard limit**: Lines must never exceed **120 characters**.
* **Continuation indent**: Indent wrapped continuation lines by **8 spaces** (twice the standard indentation).
* **Line breaks**: Place breaks after commas or binary operators when breaking long lines.

### Class Element Ordering
Organize class contents in the following top-to-bottom sequence:
1. Class/Interface Javadoc comment
2. Class/Interface declaration
3. Class (static) variables (`public`, `protected`, package-private, `private`)
4. Instance variables (`public`, `protected`, package-private, `private`)
5. Constructors
6. Methods (grouped logically or by functionality)

### Modifier Ordering
* Modifiers must follow the Java Language Specification order:
  `public` / `protected` / `private` $\rightarrow$ `abstract` $\rightarrow$ `static` $\rightarrow$ `final` $\rightarrow$ `transient` $\rightarrow$ `volatile` $\rightarrow$ `synchronized` $\rightarrow$ `native` $\rightarrow$ `strictfp`.
  Access modifiers must always appear first (e.g., `public static final`, never `static public final`).

### Whitespace & Spacing
* Place a single blank line between logical units within a method body to improve readability.

---

## 3. Statements & Types

### Package and Imports
* Every class must belong to an explicit named package.
* **No wildcard imports**: Never use `import java.util.*;`. Explicitly import each required class (e.g., `import java.util.ArrayList;`).
* Maintain a consistent import ordering across all files.

### Types and Declarations
* Array specifiers must be attached to the **type**, not the variable name:
  * Good: `String[] args`, `int[] taskCounts`
  * Bad: `String args[]`, `int taskCounts[]`

### Encapsulation
* All instance variables must be non-public (`private` or `protected`). Use getter and setter accessors rather than exposing public mutable fields.

### Keyword `this`
* Avoid redundant use of `this.`.
* Use `this` **only** when disambiguating a field that is shadowed by a constructor parameter or method parameter (e.g., `this.description = description;`).

### Control Flow (Loops & Conditionals)
* **Braces are mandatory**: Loop bodies (`for`, `while`, `do-while`) and conditional branches (`if`, `else`, `else if`) must **always** be enclosed in curly braces `{}`.
* Never place conditional statements and their single-statement body on the same line.

---

## 4. Comments & Javadoc

### General Language
* All comments and Javadoc must be in English with consistent American spelling and grammar.

### Header / Javadoc Requirement
* Mandatory Javadoc headers for:
  1. All classes and interfaces.
  2. All `public` and `protected` methods.
  3. All non-trivial `private` or package-private methods.
* Exceptions: Getters/setters with obvious behavior, overridden methods whose parent Javadoc applies verbatim, and test methods/classes.

### Javadoc Formatting Rules
* The opening `/**` must be placed on its own line.
* The first sentence must be a concise summary starting with a **3rd-person singular present-tense verb** (e.g., `Returns...`, `Parses...`, `Initializes...`, `Deletes...` — never `Return...` or `Returning...`) and ending with a period (`.`).
* Subsequent lines must align `*` characters vertically with a single space following each asterisk.
* An **empty line** must precede the tag section (`@param`, `@return`, `@throws`).
* Each `@param`, `@return`, and `@throws` tag description must end with punctuation (a period `.`).

```java
/**
 * Computes lateral offset for the specified location coordinate.
 * If the coordinate is unset, a default offset is calculated.
 *
 * @param x X coordinate of the position.
 * @param y Y coordinate of the position.
 * @param zone Region zone index.
 * @return Calculated lateral offset.
 * @throws IllegalArgumentException If zone is less than or equal to zero.
 */
public double computeOffset(double x, double y, int zone) throws IllegalArgumentException {
    // ...
}
```
