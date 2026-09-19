---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when preparing or reviewing commits and branch names in this project.
---

# SE-EDU Git standard

Use this skill for every commit or branch-name review in this repository. The authoritative source is the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html). Do not create or push a commit unless the user explicitly authorizes it.

## Commit subjects

- Every commit must have a clear subject. Use imperative mood, capitalize the first letter, and do not end with a period.
- Prefer a subject of 50 characters or fewer; never exceed 72 characters.
- Add a relevant scope or category prefix when useful, such as `Storage: ...` or `fix: ...`.

## Commit bodies

- Give every non-trivial commit a body separated from the subject by one blank line.
- Wrap body lines at 72 characters and use blank lines between paragraphs when needed.
- Explain what changed and why it was necessary, so the reader can evaluate the change without reading the diff. Avoid explaining implementation mechanics that the diff already shows.
- Structure the body around the present situation, why it needs to change, what to do, why that approach is appropriate, and any relevant additional information.

## Branch names

- Use meaningful kebab-case keywords, such as `refactor-ui-tests`.
- For issue-related branches, use `<issue-number>-<keywords-from-issue-title>`.

## Review workflow

Before creating a commit, inspect the staged diff, check the subject length and tone, add or revise the body for non-trivial changes, and confirm the branch name is meaningful and kebab-case. Keep commits focused; an overly long explanation may indicate that the work should be split into smaller commits.
