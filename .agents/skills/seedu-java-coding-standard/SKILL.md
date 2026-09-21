---
name: seedu-java-coding-standard
description: Apply the SE-EDU basic and intermediate Java conventions when writing, changing, or reviewing production Java and JUnit tests in Friday.
---

# SE-EDU Java coding standard

Read the [authoritative basic and intermediate rules](https://se-education.org/guides/conventions/java/intermediate.html)
when applying this skill; this checklist is not an exhaustive replacement.
Use [Google Java style](https://google.github.io/styleguide/javaguide.html) for uncovered topics, not to override SE-EDU.

## Review checklist

- English names: lowercase packages; PascalCase class/enum nouns; camelCase variables and method verbs;
  uppercase underscore constants with shared prefixes for related constants. Treat acronyms as words.
- Prefer boolean predicates (`is`, `has`, `can`); boolean setters use `setX`. Name collections plurally.
  Scope determines name detail; short loop indices are fine. Tests may use `method_scenario_result`.
- Indent four spaces, continuations eight extra. Aim below 110 columns; never exceed 120.
  Break after commas, before operators; keep names beside `(`. Use K&R braces even for single statements.
  Indent switch labels inside switches; mark intentional colon-case fallthrough with `// Fallthrough`.
- Space operators/commas; separate logical sections. Package every class; order explicit imports consistently.
  Attach array brackets to types. Initialize narrowly scoped variables meaningfully; avoid public mutable fields.
- Use American English comments. Document classes and public methods; trivial accessors, exact inherited
  contracts, and tests have exceptions. Start Javadoc on its own line with a third-person summary.
  Align stars, punctuate descriptions, separate tags, and avoid a gap before the declaration.
  Document all parameters or none when redundant; omit obvious returns if appropriate.

## Friday application

Preserve console text, text-block contents, date formats, and persistence behavior during style edits.
Keep action methods such as `mark` descriptive of their side effects even when returning a change flag.
Use static imports, Java imports, third-party imports, then Friday imports, sorted within each group.
Check both `src/main/java` and `src/test/java`; review names and documentation manually as well as formatting.
Follow `AGENTS.md` for Java 25.0.3.fx-zulu, JUnit priorities, UI regression tests, and Git authorization.
