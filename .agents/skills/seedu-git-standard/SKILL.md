---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to commit messages and branch names in this project.
---

# SE-EDU Git Standard

Use this skill whenever you create, review, or propose a commit, or create or
rename a branch in this repository. It is based on the [SE-EDU Git
conventions](https://se-education.org/guides/conventions/git.html).

## Commit subjects

- Write a clear subject for every commit. Aim for 50 characters and never
  exceed 72 characters.
- Use imperative mood, capitalize the first letter, and do not end the subject
  with a period. For example, use `Add README.md`, not `Added README.md`.
- Add a meaningful `<scope>:` or `<category>:` prefix when it improves clarity,
  such as `Parser: Handle empty input` or `chore: Update dependencies`.

## Commit bodies

- Non-trivial commits must include a body separated from the subject by one
  blank line. Wrap body lines at 72 characters and use blank lines between
  paragraphs.
- Explain what changed and why. The body should let a reviewer judge the
  change without reading the diff; the diff already shows how it was done.
- Structure the explanation as: current situation in present tense, why it
  needs to change, what to do in imperative mood, why that approach is used,
  and any relevant additional context. Avoid filler terms such as `currently`
  and `originally`; use bullet points when they make details clearer.

## Branch names

- Use meaningful kebab-case names made from relevant keywords, such as
  `refactor-ui-tests`.
- For issue-related work, use `<issue-number>-<keywords-from-issue-title>`,
  such as `1234-ui-freeze-error`.

## Before committing

Review the subject length, imperative mood, capitalization, punctuation, body
presence for non-trivial changes, 72-character body wrapping, and branch name.
Do not create a commit unless the user explicitly asks for one.
