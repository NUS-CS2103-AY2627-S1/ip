# Code Quality Guidelines

Use this checklist when changing production Java code. It is a project-local,
actionable summary of the [CS2103 Code Quality guidance](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/codeQuality.html), which remains the source of truth.

## Readability

* Keep methods short enough to understand at once; extract a named helper when
  it makes one responsibility or level of abstraction clearer.
* Keep nesting shallow. Use guard clauses when they make the normal path more
  prominent.
* Break complicated expressions into well-named intermediate values.
* Replace unexplained numeric or string literals with named constants, except
  where a literal is conventional and clearer in context.
* Make intent explicit: use braces, domain enums, and descriptive names rather
  than relying on clever or implicit constructs.
* Lay out related work together in an order that reads like the behavior's
  story. Do not mix high-level orchestration with low-level detail.

## Simplicity and Names

* Prefer the simplest correct design. Do not introduce abstractions or
  optimizations without a demonstrated need.
* Avoid unused parameters, misleadingly similar names, multiple statements on
  one line, and assignments that are overwritten before use.
* Name values and types as nouns; name operations as verbs. Make boolean names
  read as predicates.
* Keep terminology consistent with `.beryl/agent/ubiquitous-language.md` and
  use the project Java style policy and Checkstyle for mechanical rules.

## Review Requirement

Review every production Java file during a code-quality increment. Make only
behavior-preserving changes that improve this checklist or correct a proven
defect; do not use a review as a reason for unrelated redesign.

## 2026-09-08 Audit Record

| File | Review outcome |
| --- | --- |
| `Deadline.java` | Clear single-purpose value object; no change needed. |
| `DialogBox.java` | FXML adapter remains cohesive; no change needed. |
| `Event.java` | Clear single-purpose value object; no change needed. |
| `HelloWorld.java` | Startup flow is short and readable; no change needed. |
| `Launcher.java` | Minimal launcher; no change needed. |
| `MainWindow.java` | Named route-line geometry values to remove unexplained literals. |
| `Odysseus.java` | Named the repeated task count for a clearer response expression. |
| `OdysseusException.java` | Focused recoverable-error type; no change needed. |
| `Parser.java` | Existing command branches remain direct and readable; no change needed. |
| `Storage.java` | Read/write helpers already keep serialization logic at one level. |
| `Task.java` | Cohesive shared task behavior; no change needed. |
| `TaskAction.java` | Closed command vocabulary is explicit; no change needed. |
| `TaskList.java` | Named one-based numbering and isolated index conversion. |
| `TaskStatus.java` | Closed status vocabulary is explicit; no change needed. |
| `Todo.java` | Minimal specialization; no change needed. |
| `Ui.java` | Focused presentation adapter; no change needed. |
