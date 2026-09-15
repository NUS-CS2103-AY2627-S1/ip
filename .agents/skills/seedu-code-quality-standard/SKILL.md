---
name: seedu-code-quality-standard
description: Apply the mandatory CS2103/T code-quality guidelines when creating, editing, or reviewing code in this project.
---

# SE-EDU Code Quality Standard

Apply the [CS2103/T code-quality guidelines](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/codeQuality.html)
to every code change and review in this project. Preserve behavior during a
refactoring unless the user explicitly requests a behavior change.

## Readability and structure

- Keep methods focused and normally below 30 lines; extract coherent steps
  when a longer method mixes responsibilities or abstraction levels.
- Avoid deep nesting. Use guard clauses to handle exceptional paths early and
  keep the happy path prominent.
- Break complicated expressions into well-named intermediate values when that
  makes the intent easier to follow.
- Keep related statements together and arrange operations in a logical order.
- Prefer simple, obvious solutions over clever or premature optimizations.
- Replace unexplained literals with descriptive constants when the value has
  domain or configuration meaning.

## Names and state

- Use nouns for classes and variables, verbs for methods, and plural names for
  collections.
- Choose names that accurately explain an entity at a useful level of detail.
  Avoid numbered, ambiguous, misleading, excessively short, or unnecessarily
  long names.
- Do not reuse variables or parameters for unrelated purposes.
- Declare variables in the smallest useful scope.
- Avoid storing derived state when it can be obtained reliably from a single
  source of truth.

## Safety and duplication

- Include a meaningful default branch in every switch statement.
- Do not ignore errors with empty catch blocks.
- Remove dead code instead of retaining it for possible future use.
- Minimize duplicated logic and data while avoiding abstractions that make a
  simple operation harder to understand.

## Comments

- Prefer self-explanatory code to comments that narrate the implementation.
- Use comments and Javadoc to explain contracts, intent, and non-obvious
  rationale. Do not repeat information already clear from the code.
- Write comments for future maintainers rather than as private notes.

## Verification

For each change, review the code for method length, nesting, abstraction level,
expression complexity, unexplained literals, naming, variable scope, duplicated
state or logic, unsafe shortcuts, dead code, and redundant comments. Report any
remaining issue that is intentionally deferred to a later refactoring.
