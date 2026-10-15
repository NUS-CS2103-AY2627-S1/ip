---
name: code-quality-checker
description: Review a codebase against the NUS CS2103/T Code Quality chapter and report each issue with its suggested fix in one code block. Use for code-quality audits, readability reviews, and requests to check compliance with the linked Code Quality guidelines.
---

# NUS CS2103/T Code Quality Checker

Review the repository against the authoritative [NUS CS2103/T Code Quality chapter](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/codeQuality.html).

## Workflow

1. Inspect the repository structure and identify source files, tests, configuration, and any project-specific coding standards. Review production code by default; include tests when the user asks for the whole codebase or when test code is clearly part of the requested scope.
2. Determine the languages and use language-appropriate parsing, search, or static-analysis tools where available. Do not report a rule that cannot be supported by the code or its surrounding context.
3. Review the applicable guidelines below. Treat thresholds such as 30 lines for a method and three indentation levels as warning signals, not automatic violations; explain why the specific code harms readability.
4. Report concrete findings with file path and line number. Group repeated instances only when the same issue and fix genuinely apply; otherwise report each issue separately.
5. Do not modify the code unless the user explicitly requests fixes.

## Review checklist

### Maximize readability

- Avoid long methods and deep or arrowhead nesting.
- Split complicated expressions, especially those with many negations or nested parentheses, into named intermediate values.
- Replace unexplained magic literals, including numbers and strings, with named constants or suitably named values. Ignore conventional values such as `0`, `1`, or empty strings when their meaning is obvious in context.
- Make code explicit and obvious: use clear grouping, explicit conversions where appropriate, and enums for finite states.
- Structure related statements logically, with meaningful ordering, spacing, and grouping.
- Look for unused parameters, confusingly similar or inconsistent constructs, multiple statements on one line, and data-flow anomalies.
- Prefer simple, understandable solutions over clever complexity.
- Flag hand optimization only when it adds complexity without evidence of a measured bottleneck; do not assume every optimization is premature.
- Apply the Single Level of Abstraction Principle: avoid mixing high-level operations with low-level implementation details in the same code fragment.
- Keep the happy path prominent; use guard clauses, early returns, or `continue` where they clearly reduce unnecessary nesting.

### Follow a standard

- Check whether the project has a declared language/style standard and whether the code follows it consistently.
- If the project is Java and no more specific standard is supplied, use the SE-EDU Java coding standard as the practical reference, while keeping this skill focused on the Code Quality chapter.
- Do not invent style rules that are not supported by the project standard or the linked chapter.

### Name well

- Use nouns for classes, fields, and values; use verbs for methods/functions.
- Use standard spelling and avoid texting-style abbreviations, slang, private jokes, or context-specific wording.
- Make names explain the entity, use a sensible word order, and distinguish singular from plural values.
- Avoid names distinguished only by numbers or case, names that are too vague or too short, misleading/ambiguous names, and hard-to-pronounce abbreviations.
- Keep names reasonably concise; if an abbreviation is necessary, use it consistently and document its meaning at an obvious location.

### Avoid unsafe shortcuts

- Include a meaningful default branch in `switch`/`case` statements. For `if`/`else`, ensure the final `else` means “everything else” rather than merely implementing the last known option.
- Do not recycle variables or formal parameters for a different purpose.
- Do not leave catch/exception handlers empty unless a clear explanatory comment justifies it.
- Remove dead or unused code rather than leaving it commented out or otherwise inactive.
- Minimize global state and declare variables in the narrowest practical scope.
- Identify meaningful copy-paste duplication and suggest extraction or reuse only when it improves clarity and preserves appropriate cohesion.

### Comment minimally, but sufficiently

- Do not report comments that merely restate obvious code.
- Comments should address the reader and explain the purpose of a class or operation where the code alone is insufficient.
- Prefer comments that explain WHAT the code must do or WHY a non-obvious decision exists; do not use comments to narrate HOW self-explanatory code works.
- When a comment disagrees with the implementation, report the mismatch as a correctness/readability issue.

## Output format

Start with a short summary of the reviewed scope and the number of findings. Then report findings in severity order: high-impact readability or correctness risks first, followed by lower-impact clarity issues.

Suggested code fixes must also follow the style guide at https://se-education.org/guides/conventions/java/index.html
Use this format:

```text
Issue 1 — [rule/category]
Location: path/to/File.ext:line
Problem: [specific evidence and why it violates or weakens the guideline]
Suggested fix: [specific refactoring or code change]

Before:
[minimal relevant snippet]

After:
[illustrative corrected snippet]
```

If no issues are found, state that clearly and list any checks that were necessarily inconclusive.

Only after suggesting the fixes, then prompt the user to ask for a code rewrite if they want the fixes applied automatically. It should be done per fix and not in bulk, to avoid unintended changes.

## Scope and evidence rules

- Cite the relevant chapter guideline by name in each finding.
- Prefer precise, actionable findings over exhaustive speculative criticism.
- Distinguish definite violations from review warnings or judgment calls.
- Do not claim that code violates a language-specific standard unless that standard is available or explicitly adopted by the project.
- Preserve behavior in suggested fixes unless the user asks for redesign.
