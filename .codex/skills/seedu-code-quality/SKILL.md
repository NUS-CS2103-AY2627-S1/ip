---
name: seedu-code-quality
description: "Review and write project code using the CS2103/T code-quality rules for readability, naming, safe constructs, and maintainability."
---

# SE-EDU Code Quality

Apply this skill to every new or modified code file in this project, including
tests and support code. Treat the rules below as mandatory review gates, not as
optional suggestions. Preserve behavior and public APIs unless the user
explicitly requests a behavioral change.

These rules are adapted from the course's
[Code Quality](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/codeQuality.html#code-quality)
chapter. Use `seedu-java-coding-standard` as the companion skill for Java
syntax, layout, Javadoc, and the project's boolean-prefix naming convention.

## Readability gates

- Avoid methods longer than 30 lines. When a method exceeds that threshold,
  split it by responsibility. If a safe split is genuinely not possible,
  document the reason in the review instead of silently accepting it.
- Keep nesting to three levels or fewer. Use guard clauses, early returns, and
  `continue` statements to keep the normal path prominent.
- Break complicated expressions into named intermediate values. In particular,
  avoid long chains of negations, nested parentheses, and conditions that make
  the reader mentally simulate several rules at once.
- Replace unexplained numeric or other literal values with named constants.
  Keep literals that are self-explanatory in context, such as loop boundaries,
  collection indices, and boolean values passed to a clearly named constructor.
- Make intent explicit: use braces, explicit conversions, enums for small finite
  states, and a default branch in every `switch`.
- Structure each class and method like a readable story: related statements
  stay together, blank lines separate logical groups, and each method stays at
  one level of abstraction (SLAP).
- Prefer the simplest correct design (KISS). Do not add abstractions or
  hand-optimizations without a concrete requirement or measured bottleneck.

## Naming gates

- Use nouns for classes, fields, variables, and data; use verbs for actions.
- Use standard, correctly-spelled English words. Names must explain the role of
  the entity at the level where it is used; avoid `temp`, `flag`, `data`,
  `value`, and similar vague names unless the surrounding context gives the
  word a precise meaning.
- Do not distinguish related values only with numbers or capitalization. Use
  meaningful qualifiers such as `originalValue` and `finalValue`.
- Keep related things named similarly and unrelated things distinct. Avoid
  abbreviations, slang, hard-to-pronounce names, and words with likely
  ambiguities.
- Boolean fields, locals, parameters, and record components must use a
  predicate prefix such as `is`, `has`, `can`, `should`, or `was`. Boolean
  methods must read as predicates; action methods may return a status only when
  their documentation explains that status.
- Use plural names for collections and avoid reusing the same name for values
  with different purposes.

## Safe-construct gates

- Use the `default` branch for the actual fallback or to report an impossible
  state; do not use it merely as the final enumerated option.
- Do not recycle formal parameters as local variables, and do not reuse a
  variable for a different purpose later in the same method.
- Do not leave `catch` blocks empty. If ignoring an exception is unavoidable,
  include a concise comment explaining why.
- Delete dead, unused, and commented-out code; version control preserves old
  implementations when they are needed again.
- Keep variables in the smallest scope that contains all their uses and avoid
  global mutable state.
- Remove meaningful duplication when a small, clear helper or domain method
  makes the shared rule easier to understand. Do not create abstractions merely
  to eliminate coincidental similarity.

## Comments and review gates

- Comments should explain what the reader cannot infer from the code, especially
  why a non-obvious decision or workaround is required. Do not write comments
  that merely narrate how obvious statements execute.
- Keep the happy path visible and handle unusual or error cases close to where
  they are detected.
- Review the full affected class, not only changed lines, for nearby naming,
  nesting, duplication, scope, and abstraction-level problems.
- A passing Checkstyle run is necessary but not sufficient: it does not detect
  all rules in this skill. Do not declare a code change complete while an
  applicable violation remains or is unreviewed.

## Required workflow

1. Load this skill and `seedu-java-coding-standard` before editing Java.
2. Inspect the affected classes and identify applicable violations before
   editing. Prefer small, behavior-preserving refactorings.
3. Update or add focused tests for changed behavior and keep test code under the
   same naming and readability rules.
4. Run Checkstyle and the relevant automated tests with Java 25. For user-visible
   changes, also follow the project's mandatory `test-ui` workflow.
5. Re-review the diff and the affected classes against every gate above. Report
   any rule that cannot be satisfied instead of treating it as silently waived.
