---
name: seedu-java-coding-standard
description: Apply and audit the SE-EDU intermediate Java coding standard whenever creating, editing, reviewing, or refactoring Java production or test code in this project.
---

# SE-EDU Java Coding Standard

Use the [official SE-EDU intermediate Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
as the authority. For topics it does not cover, follow the Google Java Style Guide as
directed by the official standard. Follow stricter user or project instructions when they
apply.

## Workflow

1. Inspect every Java file affected by the change, including related tests.
2. Apply the naming, layout, organization, statement, and documentation rules below.
3. Run `bash .codex/skills/seedu-java-coding-standard/scripts/check-java-style.sh` from
   the project root.
4. Review the code manually for semantic rules that a mechanical check cannot verify.
5. Update high-value JUnit tests as required by `AGENTS.md`, then run `./gradlew test`.

## Naming

- Use lowercase package names and group classes by purpose.
- Name classes and enums with nouns in PascalCase.
- Name methods with verbs in camelCase.
- Name variables in camelCase and constants in `SCREAMING_SNAKE_CASE`.
- Keep acronyms readable inside names; do not write an entire acronym in uppercase.
- Use English names. Prefer descriptive names for wide scopes and short scratch names only
  in tiny scopes.
- Prefix boolean names with words such as `is`, `has`, or `was`, and make collections
  plural.
- Name JUnit test methods as
  `featureUnderTest_testScenario_expectedBehavior` when a descriptive name is long.

## Layout and organization

- Indent with four spaces and never with tabs.
- Keep lines within 110 characters where practical and never exceed 120 characters.
- Indent wrapped lines by eight spaces and break after commas or before operators.
- Use K&R braces and one statement per line.
- Separate logical sections with blank lines, without adding excessive vertical space.
- Put every class in a package and keep imports explicit, grouped, and consistent. Do not
  use wildcard imports.
- Attach array brackets to the type, such as `String[] values`.
- Declare variables in the smallest useful scope and initialize them near their first use.

## Statements

- Always use braces for conditionals and loops, including one-line bodies.
- Put conditional expressions and bodies on separate lines.
- Add an explicit `// Fallthrough` comment to a switch case that intentionally continues
  into the next case.
- Use spaces around binary and ternary operators and after commas, semicolons, and control
  keywords.
- Avoid public fields except constants or fields in intentional data-only classes.

## Comments and JavaDoc

- Write comments in clear English using American spelling; avoid slang and unnecessary
  comments that merely repeat the code.
- Give public classes and non-trivial public methods descriptive JavaDoc unless an allowed
  exception applies, such as a simple getter, setter, test method, or an override whose
  inherited documentation remains exact.
- Start JavaDoc with a summary sentence, keep tag descriptions parallel, and end parameter
  descriptions with punctuation when tags are present.
- Document non-trivial private methods when doing so clarifies contracts, validation, or
  algorithmic intent, as required by this project's agent guidance.

## Scope discipline

- Preserve behavior when the task is a coding-standard cleanup.
- Do not perform unrelated redesigns or add dependencies merely to satisfy formatting.
- If a rule requires judgment, choose the simplest readable option and note the decision
  in the change summary.
