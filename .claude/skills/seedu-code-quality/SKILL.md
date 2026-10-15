---
name: seedu-code-quality
description: The project's code-quality conventions, derived from the CS2103 "Code Quality" topics. Apply whenever writing, reviewing, or refactoring code so that it remains readable, simple, maintainable, and well named.
---

# se-education code-quality conventions

This is the authoritative code-quality standard for this project. It is derived
from the CS2103 "Code Quality" chapter
(https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/codeQuality.html)
and the SE-EDU conventions, and is graded, so apply it while writing code
rather than only at refactoring increments.

This skill complements the
[seedu-java-coding-standard](../seedu-java-coding-standard/SKILL.md), which
specifies Java-specific mechanical rules such as indentation, imports, and
Javadoc. This skill focuses on design and readability decisions.

This complements the Java coding standard: `seedu-java-coding-standard` fixes the
*mechanical* naming form (PascalCase classes, camelCase methods, UPPER_SNAKE
constants). This skill is about whether a name, once correctly cased, actually
*communicates* — the judgement rules below.

## How to use this skill

1. Before writing or editing code, skim the rules below.
2. After editing, self-check every new or renamed identifier against the
   **Review checklist** at the end. Fix any violation before considering the
   work done.
3. Git and Java-specific mechanical layout conventions are **not** part of this skill; they
   live in the [seedu-git-standard](../seedu-git-standard/SKILL.md) and
   [seedu-java-coding-standard](../seedu-java-coding-standard/SKILL.md) skills.

---

## 1. Naming

### Core principle

**Classes and variables are nouns; methods and functions are verbs.**
As Robert C. Martin puts it: "Functions are the verbs of that language, and
classes are the nouns."

- Class / variable: `Task`, `TaskList`, `Person student` — a thing.
- Method: `parseTask()`, `getDescription()`, `removeWhiteSpace()` — an action.
- A method that answers yes/no reads as a question: `isValidInput()`,
  `hasNextCommand()`, `isExit()`.

### 1.1 Distinguish single- vs. multi-valued names

A name should reveal whether it holds one value or many.

```java
Person student;                 // one
ArrayList<Person> students;     // many — plural
```

### 1.2 Use standard, correct spelling

Avoid texting-style spelling, slang, foreign-language words, and
context-specific in-jokes or dated references. Prefer the plain, correctly
spelled word.

```
DaUser.png      ->  User.png        // "Da" is texting slang for "The"
btnSnd          ->  sendButton
```

### 1.3 Names should explain

A name must describe the entity accurately and with enough detail that a reader
does not need the implementation to understand it. Avoid vague fillers like
`temp`, `flag`, `data`, `process`, `handle`, `value` when a specific name is
possible.

```
processInput()  ->  removeWhiteSpaceFromInput()
flag            ->  isValidInput
temp            ->  originalDescription   // name it for what it holds
```

### 1.4 Use a sensible word order

Order the words the way they read naturally in English.

```
bySizeOrder()   ->  orderBySize()
listTaskAll()   ->  listAllTasks()
```

### 1.5 Avoid number- and case-only distinctions

Do not tell related names apart only by a trailing digit, or by capitalization
alone. Name each for its actual role instead.

```
value1, value2  ->  originalValue, finalValue
value / Value   ->  rawValue / parsedValue
```

### 1.6 Balance name length

Avoid both cryptic abbreviation and needless length. Match the length to the
scope: a loop index may be `i`, but a field visible across a class earns a full,
descriptive name. When you do abbreviate, use one consistent abbreviation with a
clear meaning throughout.

### 1.7 Avoid misleading or hard-to-distinguish names

- Related items should share a naming pattern; unrelated items should look
  clearly different.
- Avoid names that differ only by easily confused characters (`0` vs `O`,
  `l` vs `I` vs `1`).
- Keep a pattern consistent across a set: prefer `hexForBlack`, `hexForWhite`,
  `hexForRed` over mixing `colorBlack` with `hexForRed`.

## 2. Readability and structure

- Avoid long methods (especially methods exceeding roughly 30 lines). Extract
  cohesive pieces into methods with intention-revealing names.
- Avoid deep nesting. Handle unusual or error cases early with guard clauses so
  the normal path remains easy to follow.
- Avoid complicated expressions with many negations or nested parentheses.
  Calculate meaningful intermediate values instead.
- Replace magic numbers and other unexplained literals with named constants.
- Make code explicit and obvious; use enums and clear grouping rather than
  relying on implicit behavior or clever shortcuts.
- Structure related statements into logical blocks separated by blank lines.
  Keep each block at one level of abstraction (SLAP).
- Keep the simple solution (KISS) and avoid premature optimization until a real
  bottleneck has been measured.
- Remove dead code, duplicated logic, and variables whose scope can be smaller.
- Do not reuse a variable or parameter for a different purpose.
- Include a `default` branch in switches to handle unexpected values or report
  an error.
- Never leave a `catch` block empty. Either handle the exception, or at minimum
  log/comment why it is safe to ignore — a silently swallowed exception hides
  failures from both the user and future maintainers.

  ```java
  } catch (IOException e) {
      // Save file is optional; app still works with in-memory data only.
      logger.warning("Could not load save file: " + e.getMessage());
  }
  ```

- Treat a final `else` (or a `default` case, per above) as meaning "everything
  else I haven't anticipated," not just "the last option I happened to think
  of." Do not write a final `else` whose body only makes sense for one specific
  remaining case — if the branches are meant to be exhaustive alternatives
  rather than a genuine catch-all, use explicit `else if` conditions instead
  and let the true final `else` handle the truly-unexpected case (e.g. by
  throwing or reporting an error).
- Use explicit type conversions rather than relying on implicit/automatic
  ones. Casts and conversions should be visible in the code, not hidden by
  language coercion rules.

  ```java
  int total = (int) Math.round(average);   // explicit
  int total = average;                      // avoid: silent narrowing
  ```

- Watch for reader "trip hazards" that make code easy to misread:
  - **Unused parameters or variables** — remove them, or if a parameter must
    stay (e.g. to satisfy an interface), make clear it is intentionally unused.
  - **Confusingly similar code** — two blocks that look almost identical but
    differ in one subtle spot invite copy-paste bugs; either unify them or
    make the difference obvious.
  - **Multiple statements per line** — one statement per line (see
    [seedu-java-coding-standard](../seedu-java-coding-standard/SKILL.md)) so
    nothing is hidden on a shared line.
  - **Data-flow anomalies** — e.g. defining a variable and reassigning it
    before it is ever read, or reading a variable that no branch has
    initialized on some path. These usually indicate a bug or dead code.

## 3. Comments

- Do not write comments that merely repeat obvious code.
- Write comments for the reader, explaining behavior or rationale that the code
  cannot communicate by itself.
- Explain WHAT the code is intended to do and WHY the approach is needed, not
  HOW the statements operate line by line.
- A comment must not be used to compensate for unclear code. If a reader needs
  a comment to understand what a piece of code does, first try to make the
  code itself clearer (better names, extracted method, simpler expression);
  add a comment only for information the code still cannot express on its own
  (e.g. WHY a workaround is needed).

---

## Review checklist

Before considering a change done, confirm every new or renamed identifier:

- [ ] Classes/variables are nouns; methods are verbs; boolean methods read as
      yes/no questions (`is`/`has`/`can`/`should`).
- [ ] Collections are plural; single values are singular (§1.1).
- [ ] Spelling is standard — no slang, texting shorthand, or in-jokes (§1.2).
- [ ] No vague fillers (`temp`, `flag`, `data`, `process`) where a specific name
      fits; the name explains the entity (§1.3).
- [ ] Words are in natural reading order (§1.4).
- [ ] Related names are not distinguished only by a digit or by case (§1.5).
- [ ] Length suits the scope; abbreviations are consistent (§1.6).
- [ ] Names are not misleading and avoid easily confused characters (§1.7).

---

- [ ] Methods are focused and not unnecessarily long; nesting is shallow (§2).
- [ ] Expressions are readable; magic literals, dead code, and duplication are
      avoided (§2).
- [ ] Related statements are logically grouped and kept at one abstraction
      level (§2).
- [ ] The implementation favors simple, obvious code over cleverness or
      premature optimization (§2).
- [ ] Variables have one purpose and the smallest practical scope (§2).
- [ ] Switches include a meaningful `default` branch (§2).
- [ ] No empty `catch` blocks; each either handles or explains why it's safe
      to ignore (§2).
- [ ] A final `else`/`default` means "everything else," not just "the last
      case I thought of" (§2).
- [ ] Type conversions are explicit, not left to implicit coercion (§2).
- [ ] No unused parameters/variables, no confusingly similar duplicated code,
      no multiple statements per line, no data-flow anomalies (§2).
- [ ] Comments add non-obvious WHAT/WHY information and do not explain HOW or
      restate the code (§3).
- [ ] Comments are not a substitute for clearer code — unclear code was
      simplified first (§3).
