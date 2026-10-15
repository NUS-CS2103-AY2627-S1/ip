---
name: seedu-code-quality
description: Review this project's Java code for readability, maintainability, and design quality using SE-EDU code-quality principles.
---

# SE-EDU Code Quality

Use this skill after every production or test-code modification, and when the
user asks for a code-quality review. Apply the official
[CS2103/T code-quality guidelines](https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/codeQuality.html).

## Review focus

Inspect the changed code first, then nearby callers and tests. Prioritize one
standalone, highest-impact issue at a time rather than proposing a broad
rewrite. Consider:

- long methods and classes;
- deep nesting and obscured happy paths;
- complicated boolean expressions and excessive branching;
- magic numbers, strings, and other unexplained literals;
- vague or misleading names;
- mixed abstraction levels within one method (SLAP);
- duplicated logic that can be simplified without a clever abstraction;
- premature optimisation or streams that reduce readability;
- finite states that would be clearer as enums;
- responsibilities that are mixed across classes.

## Review procedure

1. Inspect the current diff and relevant surrounding code.
2. Identify the single highest-priority quality issue and explain why it has
   the greatest readability or maintainability impact.
3. Propose a focused refactoring with before-and-after snippets when useful.
4. Preserve existing behavior and public interfaces unless the user approves
   a deliberate change.
5. Check that relevant Javadocs and high-value tests remain accurate.
6. Report lower-priority observations separately so they do not get mixed into
   the approved increment.

This skill reviews design and maintainability. Use
`seedu-java-coding-standard` for Java naming, layout, imports, Javadocs, and
the project's Checkstyle execution.
