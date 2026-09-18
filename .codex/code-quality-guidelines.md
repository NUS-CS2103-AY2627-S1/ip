# CS2103/T Code Quality Guidelines

Use this checklist before and during each stand-alone refactoring. It summarizes
the course textbook chapter linked below; the source remains authoritative.

Source checked: 2026-09-09  
Source: <https://nus-cs2103-ay2627-s1.github.io/website/se-book-adapted/chapters/codeQuality.html>

## Core Goal

Optimize code primarily for the next programmer who must read, understand, and
change it. Preserve observable behavior during a refactoring, and prefer one
small, independently reviewable improvement at a time.

## Readability

- Keep methods focused. Reconsider a method once it grows beyond roughly 30
  lines, but extract methods only when the resulting responsibilities and names
  are clearer.
- Avoid deep nesting. Prefer guard clauses or early `continue` statements when
  they make the normal, successful path prominent.
- Break complicated expressions into well-named intermediate values, especially
  when negations or nested parentheses obscure the intent.
- Replace unexplained numeric, string, and other literal values with named
  constants when the name conveys domain meaning.
- Make intent explicit with braces, grouping, suitable types, and enums for a
  small fixed set of states.
- Arrange related statements together and in an order that lets the code read
  like a story. Use classes, methods, indentation, and blank lines to show the
  logical structure.
- Keep each method or code fragment at one level of abstraction (SLAP), ideally
  the highest useful level.
- Avoid surprising readers: remove unused parameters, do not put multiple
  statements on one line, and avoid inconsistent visual or data-flow patterns.
- Prefer simple, direct code (KISS). Do not introduce speculative abstractions
  or optimize without evidence of a real bottleneck.

## Consistency

- Follow the project coding standard so the codebase reads as if one person
  wrote it.
- For Java, apply the project-specific SE-EDU basic and intermediate coding
  standard. Use the Google Java style guide only where SE-EDU is silent.
- Use automated formatting and IDE checks where they support, but do not replace,
  deliberate review.

## Naming

- Use nouns for things such as classes and variables, and verbs for actions such
  as methods.
- Distinguish one item from a collection, commonly by singular and plural names.
- Use correctly spelled, standard English words; avoid slang, private jokes,
  texting abbreviations, and temporary cultural references.
- Make every name explain its role precisely. Avoid vague names such as `flag`,
  `temp`, and generic `process...` names when the actual intent is knowable.
- Keep multi-word names in a natural order.
- Do not distinguish names only by case or numbers; describe their roles instead.
- Choose names long enough to convey meaning but no longer than needed. Use
  abbreviations consistently and explain unfamiliar ones.
- Name related things similarly and unrelated things differently. Avoid names
  that are ambiguous, misleading, hard to pronounce, or almost identical.

## Safe Constructs

- Give every `switch` a meaningful `default` branch. Use the final `else` for
  genuinely unclassified cases, not merely as shorthand for the last known case.
- Give each variable one purpose. Do not recycle variables or overwrite method
  parameters for unrelated local calculations.
- Never silently ignore errors with an empty `catch` block. Handle, propagate, or
  deliberately document an exceptional case.
- Delete dead code; version control can recover it if it is ever needed again.
- Declare variables in the narrowest useful scope, near their first use. Minimize
  shared mutable state and implicit coupling.
- Avoid copy-paste-modify duplication. Consolidate repeated knowledge when doing
  so produces a simpler and clearer design.

## Comments

- First improve unclear code; do not use comments to compensate for poor names or
  tangled structure.
- Do not restate what the code already says.
- Write for future maintainers, not as private notes about the author's history.
- Document the contract (what) and non-obvious rationale or constraints (why).
  Let clear code explain the mechanics (how).
- Keep useful class and operation header comments aligned with the project's
  Javadoc requirements.

## Checklist for Each Refactoring

- Identify one concrete quality problem and the guideline it violates.
- Confirm the change is behavior-preserving and narrow enough to stand alone.
- Choose the smallest clear improvement; avoid unrelated cleanup.
- Check naming, abstraction level, nesting, scope, duplication, and comments in
  the touched code.
- Follow the Java coding standard and preserve project conventions.
- Update tests only where necessary to preserve or clarify coverage; update the
  UI test plan if console behavior changes.
- Run the relevant automated and UI tests before considering the refactoring
  complete.
- Review the diff to ensure it contains only the intended refactoring.

