---
name: seedu-git-standard
description: Reference checklist for the SE-EDU Git conventions (commit message subject/body format, branch naming). Use when writing a commit message, proposing a commit message, or naming a branch in this project.
---

# SE-EDU Git Conventions

Source: https://se-education.org/guides/conventions/git.html

This project's commits must follow this standard. Apply it whenever proposing or writing a
commit message, and when naming a branch.

## Commit message: subject line

- Limit to 50 characters; 72 is a hard limit.
- Imperative mood: "Add README.md", not "Added README.md" or "Adds README.md".
- Capitalize the first letter.
- No period at the end.
- May optionally be prefixed with a scope/category, e.g. `Person class: ...` or `bug fix: ...`.
  (Conventional Commits-style prefixes are also acceptable if the project later adopts that
  format more formally.)

## Commit message: body

- Separate the subject from the body with one blank line.
- Wrap body text at 72 characters.
- Separate paragraphs with blank lines; use bullet points where they read more clearly than
  prose.
- Explain **what** changed and **why** -- not **how**; the diff already shows how. Give the
  reader enough context to judge the change's merit without reading the code.
- Don't restate what a code comment already says.
- Avoid relative time words like "currently" or "originally" (they go stale).
- A useful structure to follow (skip parts that don't apply):
  1. The situation before this change (present tense).
  2. Why a change is needed.
  3. What this commit does (imperative mood; "Let's ..." is a fine opener).
  4. Why this approach, if it's not obvious.
  5. Any other context worth recording (e.g. what was verified).

## Branch names

- kebab-case, meaningful keywords: `refactor-ui-tests`.
- For an issue-linked branch: `issueNumber-keywords-from-title`, e.g. `1234-ui-freeze-error`.
