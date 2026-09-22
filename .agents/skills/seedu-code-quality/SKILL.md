---
name: seedu-code-quality
description: >-
  Enforces code quality principles and best practices based on the SE-EDU / CS2103 textbook.
  Mandates SLAP (Single Level of Abstraction Principle), prominent happy paths with guard clauses,
  clean logical structure, explanatory naming, safe language practices, and minimal sufficient commenting.
---

# SE-EDU Code Quality Guidelines

Reference: [CS2103 / SE-EDU Textbook: Code Quality](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/codeQuality.html)

Production code must be of high quality. Readability and understandability are primary dimensions of software quality: code is written once but read, modified, and debugged many times by others and your future self.

---

## 1. Guideline: Maximize Readability

### Avoid Long Methods
* Keep methods focused and short. Treat **30 lines of code** as a prompt to check whether a method can be
  shortened, not as an automatic failure threshold.
* A long method often contains more cognitive load than a reader can process at once: *"The bigger the haystack, the harder it is to find a needle."*

### Avoid Deep Nesting
* Avoid deep indentation levels. More than **3 levels of indentation** is a strong signal to simplify the
  control flow or extract a method.
* Avoid "arrowhead style" code (`if` inside `if` inside `if`...):
  ```java
  // Bad: Arrowhead indentation
  if (!isSenior) {
      if (!isSubsidized) {
          if (!isPartTime) {
              subsidy = 500;
          }
      }
  }

  // Good: Flattened control flow
  if (isSenior) {
      subsidy = REJECT_SENIOR;
  } else if (isSubsidized) {
      subsidy = SUBSIDIZED_SUBSIDY;
  } else if (isPartTime) {
      subsidy = FULLTIME_SUBSIDY * RATIO;
  } else {
      subsidy = FULLTIME_SUBSIDY;
  }
  ```

### Avoid Complicated Expressions
* Avoid expressions packed with multiple negations and nested parentheses.
* Break complicated expressions down into intermediate boolean variables with clear explanatory names:
  ```java
  // Bad
  return ((length < MAX_LENGTH) || (previousSize != length)) && (typeCode == URGENT);

  // Good
  boolean isWithinSizeLimit = length < MAX_LENGTH;
  boolean hasSizeChanged = previousSize != length;
  boolean isValidCode = isWithinSizeLimit || hasSizeChanged;
  boolean isUrgent = typeCode == URGENT;

  return isValidCode && isUrgent;
  ```

### Avoid Magic Numbers and Literals
* Do not use unexplained literals (numbers, strings, characters) directly in logic.
* Declare named constants (`static final` in `SCREAMING_SNAKE_CASE`) to explain their meaning:
  ```java
  // Bad
  if (statusCode == 4) ...
  return "Error 1432";

  // Good
  private static final int MAX_RETRY_COUNT = 4;
  private static final String STORAGE_CORRUPT_ERROR = "Error 1432: Corrupted task line.";
  ```

### Make the Code Obvious
* Make intent explicit even when syntax allows it to be implicit:
  * Use explicit type casting/conversion rather than relying on implicit coercion.
  * Use parentheses to make expression grouping explicit when operator precedence could confuse the reader.
  * Use `enum` types instead of arbitrary integers or strings to model a finite set of states (e.g., `enum State { STARTING, ENABLED, DISABLED }`).

### Structure Code Logically
* Lay out code so it reads like a coherent story.
* Group related statements together and separate logical steps with blank lines.
* Arrange operations in an order that aids narrative flow and comprehension.

### Do Not "Trip Up" the Reader
* Avoid anything that causes cognitive confusion:
  * Eliminate unused parameters from method signatures.
  * Do not make similar things look different or different things look similar.
  * Do not put multiple statements on a single line.
  * Avoid data flow anomalies (such as assigning a value to a variable, then overwriting it before it is ever used).

### Practice KISSing (Keep It Simple, Stupid)
* Do not write "clever" code or over-engineer for hypothetical future requirements. Prefer a simple brute-force
  solution unless a more complex design has a strong, demonstrated benefit.

### Avoid Premature Optimizations
* Prioritize clarity and correctness first: **"Make it work, make it right, make it fast."**
* Profile before optimizing so effort targets measured bottlenecks. Optimization can reduce readability and make
  compiler optimization harder, but it may take priority when requirements or resource constraints demand it.

### SLAP Hard (Single Level of Abstraction Principle)
* Keep each method or coherent code fragment at one level of abstraction. Statements that appear together should
  describe either the high-level workflow or its lower-level implementation details, not both.
* Do not mix high-level orchestration with low-level details:
  ```java
  // Bad: Mixing high-level steps with low-level arithmetic and conditional logic
  void processPayroll() {
      readData();
      salary = basic * rise + 1000;
      tax = (taxable ? salary * 0.07 : 0);
      displayResult();
  }

  // Good: All statements are at the same high level of abstraction
  void processPayroll() {
      readData();
      calculateSalaryAndTax();
      displayResult();
  }
  ```
* Write at the highest useful level of abstraction, expressing logic in domain concepts rather than low-level
  manipulation where possible.
* If keeping two levels together remains more readable than extraction, make each high-level step explicit with a
  concise comment and separate adjacent steps with blank lines. Treat this as a deliberate exception to SLAP, not
  the default structure.

### Make the Happy Path Prominent
* The "happy path" (the execution path when everything succeeds) should be prominent, unindented, and clear.
* Use **guard clauses** (early `return` or early `throw`) to handle error checks and unusual cases immediately, keeping the main path at the top indentation level:
  ```java
  // Bad: Happy path nested deeply inside condition checks
  if (isValidInput) {
      if (hasPermission) {
          executeCommand();
          saveState();
      } else {
          showPermissionError();
      }
  } else {
      showInvalidInputError();
  }

  // Good: Guard clauses handle errors early; happy path is linear and prominent
  if (!isValidInput) {
      showInvalidInputError();
      return;
  }

  if (!hasPermission) {
      showPermissionError();
      return;
  }

  executeCommand();
  saveState();
  ```
* Inside loops, use `continue` to filter out non-applicable iterations early rather than nesting the loop body in `if` blocks.

---

## 2. Guideline: Follow a Standard

* Strictly adhere to project style and coding standards (e.g. [SE-EDU Java Coding Standard](https://se-education.org/guides/conventions/java/intermediate.html)).
* The entire codebase should look as if written by a single disciplined developer.

---

## 3. Guideline: Name Well

### Nouns for Things, Verbs for Actions
* Use nouns/noun phrases for classes and variables (`TaskList`, `activeSession`, `studentCount`).
* Use verbs/verb phrases for methods (`calculateTotal()`, `findTasksOn()`, `printSummary()`).
* Clearly differentiate single-valued variables from multi-valued collections:
  * Single: `Student student;`
  * Multi-valued: `List<Student> students;`

### Use Standard Words
* Use correct English spelling.
* Avoid texting-style abbreviations (`u`, `plz`, `idx`, `desc`).
* Avoid slang, colloquialisms, and insider references.

### Use Name to Explain
* Names must convey purpose and behavior accurately:
  * Bad: `processInput()`, `data`, `flag`, `temp`
  * Good: `removeWhitespaceFromInput()`, `activeUserSession`, `isValidDate`
* Keep word order sensible (e.g., `orderBySize()` rather than `bySizeOrder()`).
* Never distinguish names solely by numbers or casing (avoid `value1` vs `value2`, or `value` vs `Value`; prefer `initialValue` vs `adjustedValue`).

### Not Too Long, Not Too Short
* Avoid cryptic 1- or 2-letter names (except conventional loop indices `i`, `j`).
* If abbreviations or acronyms are used, keep them consistent and well-known in the domain.

### Avoid Misleading Names
* Related concepts should be named similarly; unrelated concepts must not share naming patterns.
* Avoid ambiguous names, homophones (e.g., `redBooks` vs `readBooks`), and easily confused characters (`0` vs `O`, `1` vs `l` vs `I`).

---

## 4. Guideline: Avoid Unsafe Shortcuts

### Use the Default Branch
* In `switch` statements, always provide a `default` branch to handle or flag unexpected cases.
* In `if-else` chains, the final `else` must mean *"everything else"* (often error detection or fallback), not simply the final expected option without validation:
  ```java
  // Bad: Assumes anything other than red is blue
  if (color.equals("red")) {
      handleRed();
  } else {
      handleBlue();
  }

  // Good: Explicit checks for all expected options with fallback error handling
  if (color.equals("red")) {
      handleRed();
  } else if (color.equals("blue")) {
      handleBlue();
  } else {
      throw new IllegalArgumentException("Unsupported color: " + color);
  }
  ```

### Don't Recycle Variables or Parameters
* Dedicate one variable to one single purpose. Never reuse a variable for a different purpose just because it shares the same type.
* Never reassign method parameters as local work variables:
  ```java
  // Bad: Parameter reassigned as accumulator
  double calculateArea(double length, double width) {
      length = length * width;
      return length;
  }

  // Good: Explicit local variable
  double calculateArea(double length, double width) {
      double area = length * width;
      return area;
  }
  ```

### Avoid Empty Catch Blocks
* Never silently swallow exceptions with empty `catch` blocks.
* At minimum, log the error, propagate an appropriate domain exception, or document why ignoring it is intentionally safe.

### Delete Dead Code
* Immediately delete obsolete, unreachable, or commented-out code.
* Do not keep dead code "just in case" — revision control (Git) retains all history.

### Minimize Scope of Variables
* Avoid global or broadly shared mutable state because it creates implicit links between otherwise separate code.
* Declare variables in the smallest possible scope.
* Declare local variables at the point where they are first used, rather than grouping declarations at the top of a method.

### Minimize Code Duplication (DRY Principle)
* Think twice before copy-paste-modifying logic. Zero duplication is not always practical; extract shared logic
  when doing so makes the code clearer and does not introduce a forced abstraction.

---

## 5. Guideline: Comment Minimally, But Sufficiently

### Do Not Repeat the Obvious
* Good code is self-explanatory. Avoid comments that merely translate syntax into words:
  ```java
  // Bad: Pointless comment
  x++; // increment x
  trimInput(); // trim the input
  ```

### Write to the Reader
* Write comments for fellow engineers maintaining the code, not private personal reminders.
* Use header comments to explain the purpose of classes and operations when the purpose is not already clear, and
  follow the project's documentation standard for required Javadoc.

### Explain WHAT and WHY, Not HOW
* **WHAT**: High-level specification of what the code achieves, enabling the reader to verify if the implementation matches intent.
* **WHY**: Rationale, non-obvious design choices, or external constraints explaining why the code was written this way:
  ```java
  // Good (WHY): Explains external constraint/rationale
  // Storage delimiter '|' is escaped to avoid corrupting CSV-like save format
  validateNoStorageDelimiter(userInput);
  ```
* **HOW**: How the code executes should be clear from clean, self-explanatory code structure itself rather than redundant inline comments.
