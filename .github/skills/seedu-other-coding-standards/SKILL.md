---
name: seedu-other-coding-standards
description: Apply the project's SE-EDU code-quality rules for readable, safe, and well-commented Java production code.
---

# SE-EDU other coding standards

Follow the CS2103/T [code-quality guidelines](https://nus-cs2103-ay2627-s1.github.io/website/schedule/week5/topics.html#w5-4-code-quality-readability-unsafe-practices-code-comments)
for all Java production code in this repository. Apply these rules together with the project's
Java coding standard.

## Readability

- Keep methods focused and consider extracting well-named methods when one grows beyond about
  30 lines.
- Avoid deep nesting and complicated expressions. Use guard clauses and meaningful intermediate
  variables when they make the main path easier to follow.
- Replace unexplained literal values with named constants, including magic strings and other
  non-numeric values.
- Prefer explicit, unsurprising constructs: use explicit conversions, braces or parentheses that
  clarify grouping, and enums for values drawn from a small fixed set.
- Organize classes, methods, statements, and whitespace so related code is grouped and the code
  reads in a logical order.
- Avoid unused parameters, multiple statements on one line, confusingly similar names, and data
  flow that overwrites a value before it is used.
- Choose the simplest sufficient implementation. Optimize only when there is a demonstrated need,
  preferably supported by profiling or another measurement.
- Keep each method or code fragment at one clear level of abstraction and express operations at
  the highest useful level.
- Make the normal execution path prominent; handle exceptional or unusual cases early when doing
  so reduces nesting.

## Safe practices

- Cover unexpected values explicitly. A `switch` should have a meaningful `default` branch, and a
  final `else` should represent all remaining cases rather than an unstated last option.
- Give each variable one purpose and do not reuse method parameters as local working variables.
- Do not leave `catch` blocks empty. If ignoring an exception is genuinely necessary, document why.
- Delete dead or unused code; rely on version control if it is needed again.
- Declare variables in the smallest practical scope and minimize mutable global state.
- Minimize duplication, especially copy-paste-modify code, while avoiding abstractions that make
  the solution harder to understand.

## Comments

- First improve unclear code instead of using comments to compensate for it.
- Do not repeat information that is already obvious from the code.
- Write for future readers, not as private notes about how or when the code was written.
- Use comments to explain what code is required to accomplish or why a non-obvious decision exists;
  let self-explanatory code show how it works.
- Keep comments accurate when the corresponding code changes.
