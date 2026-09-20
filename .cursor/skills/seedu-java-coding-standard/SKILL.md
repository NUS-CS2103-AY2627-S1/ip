---
name: seedu-java-coding-standard
description: >-
  Apply SE-EDU Java coding standards (basic + intermediate) to all Java code in
  this project. Use when writing, reviewing, or refactoring Java source or test
  files, adding Javadoc, or when the user mentions coding style or conventions.
---

# SE-EDU Java coding standard

**Source:** https://se-education.org/guides/conventions/java/intermediate.html

For topics not covered here, follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Mandatory rules (apply to every change)

### Packages and imports

- Every class must be in a package (root name = project name, e.g. `axiom`, `axiom.task`).
- No wildcard imports (`import java.util.*`).
- Import order: static imports → `java.*` → `javax.*` → third-party → project packages. Separate groups with a blank line.

### Naming

| Element | Convention | Example |
|---------|------------|---------|
| Package | all lowercase | `axiom.parser` |
| Class/enum | PascalCase noun | `TaskList` |
| Method | camelCase verb | `parseTodo()` |
| Variable | camelCase | `taskCount` |
| Constant | `SCREAMING_SNAKE_CASE` | `MAX_SIZE` |
| Boolean | `is`/`has`/`can` prefix | `isDone`, `hasData` |
| Test method | `featureUnderTest_testScenario_expectedBehavior` | `parse_invalidFormat_exceptionThrown` |

- Do not uppercase abbreviations in names (`exportHtmlSource`, not `exportHTMLSource`).
- Use plural names for collections (`tasks`, not `task`).

### Layout

- 4-space indentation (no tabs).
- Max line length 120 chars (aim for 110).
- K&R braces (opening `{` on same line).
- Always use braces for `if`, `for`, `while`, `do` bodies — even single statements.
- Put `if` condition on its own line; never `if (x) doSomething();` on one line.
- One blank line between logical units within a method.
- Wrap long lines with +8 spaces indent from the parent line.

### Types and variables

- Array type on the left: `int[] a`, not `int a[]`.
- Declare variables in the smallest scope; initialize where declared when possible.
- No `public` instance fields (constants excepted).

### Javadoc (required)

- **All classes** and **all public methods** must have header Javadoc.
- May omit for: getters/setters, `@Override` methods when parent Javadoc applies unchanged, test code.
- First sentence of method Javadoc: start with a verb phrase — `Returns ...`, `Creates ...`, `Parses ...`, `Displays ...` (not `Return` or `Returning`).
- Format:

```java
/**
 * Returns lateral location of the specified position.
 *
 * @param x X coordinate of position.
 * @return Lateral location.
 * @throws IllegalArgumentException If zone is <= 0.
 */
```

- Opening `/**` on its own line; space after each `*`; blank line before `@param` block.
- No blank line between Javadoc block and the declaration.
- Use `@inheritDoc` when overriding with the same contract.
- Comments in English; American spelling.

### Switch

- Add `// Fallthrough` comment when a `case` intentionally omits `break`.

## Checklist before finishing Java work

- [ ] All new/changed classes and public methods have Javadoc
- [ ] No wildcard imports; import groups ordered correctly
- [ ] Line length ≤ 120 chars
- [ ] Braces on all control-flow bodies
- [ ] Names follow camelCase / PascalCase / boolean-prefix rules
- [ ] `./gradlew test` passes
