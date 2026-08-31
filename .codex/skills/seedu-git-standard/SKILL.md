---
name: seedu-git-standard
description: The SE-EDU Git conventions this project follows for commit messages and branch names. Use before writing any commit message or creating any branch in this repository.
---

# SE-EDU Git conventions

Source: <https://se-education.org/guides/conventions/git.html>

## Subject line

* Imperative mood, as if completing "If applied, this commit will ...".
  Good: `Add README.md`, `Move index.html file to root`.
  Bad: `Added README.md`, `Adding README.md`.
* Capitalise the first letter.
* No full stop at the end.
* Aim for 50 characters. Hard limit 72.
* An optional category prefix is allowed when it helps:
  `Person class: Remove static imports`, `bug fix: Add space after name`.

## Body

* Separate the subject from the body with one blank line.
* Wrap the body at 72 characters.
* Separate paragraphs with blank lines; use bullet points where they read better.
* Explain **what** and **why**, not **how**. The diff already shows how.
* A useful order: current situation, why it needs to change, what this commit
  does, why it was done this way, anything else worth knowing.

## Branch names

* Kebab case, made of meaningful keywords: `refactor-ui-tests`.
* For a branch addressing an issue: `issueNumber-keywords-from-title`,
  e.g. `1234-ui-freeze-error`.
* Course increments are the exception and keep their prescribed names, such as
  `branch-Level-9` and `branch-A-CodingStandard`, because the grading scripts
  look for those exact names.

## Checklist before committing

1. Is the subject imperative, capitalised, under 72 characters, with no full stop?
2. Is there a blank line before the body, and is the body wrapped at 72?
3. Does the body say why the change was needed, not just what changed?
