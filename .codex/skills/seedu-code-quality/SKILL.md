---
name: seedu-code-quality
description: Apply CS2103/T code-quality practices when creating, modifying, refactoring, or reviewing Java code in this project. Complements the project Java coding standard; excludes mechanical formatting rules.
---

# CS2103/T Code Quality

Use this skill for Java source and test-code work, including code reviews and refactoring. It complements the project-specific Java coding-standard skill: follow that skill for mechanical Java conventions and use this one for readability and design decisions. Preserve behavior unless the task explicitly requires a behavior change.

These practices are adapted from the [CS2103/T code-quality guide](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/codeQuality.html).

## Maximize readability

- Prefer short, single-purpose methods. When a method grows beyond roughly 30 lines, consider extracting a cohesive, well-named operation; do not split code merely to satisfy a line count.
- Avoid more than three levels of nesting and arrowhead-shaped control flow. Make the normal path easy to scan; handle unusual or error cases early with guard clauses, `continue`, or an equivalent clear structure.
- Replace complicated expressions, especially ones with negations or nested grouping, with clearly named intermediate values. Avoid magic literals of any type; use named constants that explain their meaning.
- Make intent explicit: show meaningful grouping, use explicit conversions where a conversion matters, and represent small finite states with enums rather than encoded numbers or strings.
- Arrange classes and methods as a logical story. Group related statements, separate distinct phases with whitespace, and choose the ordering that makes the story clearest.
- Keep each code fragment at one level of abstraction and write at the highest practical level. If mixing levels is unavoidable, mark and separate the higher-level steps clearly.
- Avoid reader surprises: unused parameters, misleadingly similar forms, multiple statements on a line, and assignments overwritten before their value is used.
- Use the simplest solution that meets the requirement. Do not introduce optimisation-driven complexity until a profiler or measurement identifies a real bottleneck.
- Make the happy path prominent and less nested than exceptional paths. Do not let error handling distract from the main flow.

## Follow a standard

- Follow the project Java coding standard consistently so the codebase reads as though it were written by one developer. Use the configured IDE and checkstyle support to enforce mechanical conventions.

## Name well

- Use correctly spelled, standard English names; avoid texting-style spelling, foreign-language words, slang, private jokes, and time-specific references. Classes and variables name things; methods name actions. Make collection names plural and boolean names state a condition.
- Make names explain the entity's purpose and level of detail, with words in a sensible order. Avoid vague placeholders and names distinguished only by number or case.
- Prefer names that are not too long and never too short. If an abbreviation or acronym is necessary, use it consistently and explain its full meaning in an obvious location.
- Name related concepts consistently and different concepts differently. Do not use ambiguous, misleading, similarly sounding, or hard-to-pronounce names.

## Avoid unsafe shortcuts

- Include a `default` branch in every `switch`. Use it for the intended default action or to detect an unexpected value, never as a shortcut for the final known case. Similarly, a final `else` means every remaining case; state an explicit condition with `else if` whenever possible.
- Give each variable one purpose; do not repurpose variables or reassign parameters as local storage. Avoid global variables and declare values in the smallest practical scope, close to their first use.
- Do not silently swallow exceptions. An empty `catch` block needs a specific, reader-focused reason when it is genuinely unavoidable.
- Remove dead code when it is within the requested or authorised change. Version control preserves recoverable history.
- Think twice before copy-paste-modify duplication: it usually has a clearer alternative. Extract shared behaviour when it clarifies the code, but do not create an abstraction for coincidental similarity.

## Comment minimally, but sufficiently

- First improve code so that it explains how it works. Do not add comments that restate obvious code.
- Write comments for the next programmer, not as private notes. A header comment explaining a class or operation's purpose is often useful.
- Explain the intended **what** or non-obvious **why**, not the implementation **how**.
- Follow `seedu-java-coding-standard` for Javadoc requirements. Keep Javadoc accurate whenever the documented public contract changes.

## Before finishing

Check that the modified code remains easy to read top-to-bottom, names reflect intent, the happy path is prominent, and every exceptional path has a clear purpose. Run relevant tests or build checks when the task authorises implementation work.
