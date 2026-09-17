---
name: seedu-git-standard
description: Review and write this project's Git commit messages and branch names using the SE-EDU Git conventions.
---

# SE-EDU Git Standard

Apply this skill whenever a commit message or branch name is created, proposed,
reviewed, or amended in this repository. Use the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
as the source of truth.

## Commit subject

- Every commit must have a clear, well-written subject.
- Aim for 50 characters or fewer; never exceed 72 characters.
- Use the imperative mood, capitalize the first letter, and do not end with a period. For example, use `Add README.md`, not `Added README.md`.
- Add a meaningful `<scope>:` or `<category>:` prefix only when it helps identify the affected area. Do not impose Conventional Commits syntax unless the user asks for it.

## Commit body

Non-trivial commits must have a body separated from the subject by one blank
line. Wrap body lines at 72 characters, use blank lines between paragraphs, and
use bullet points when they improve clarity.

Explain what changed and why it changed, not how the diff implements it. A
useful body generally covers the current situation in present tense, why it
needs to change, what is being done in imperative mood, why that approach was
chosen, and any other relevant context. Avoid redundant explanations already
obvious from the code or comments, and avoid words such as `currently` and
`originally` when they add no information.

## Branch names

- Use meaningful kebab-case names containing relevant keywords, such as `refactor-ui-tests`.
- For issue-related branches, use `<issue-number>-<keywords-from-issue-title>`, such as `1234-ui-freeze-error`.

## Review checklist

Before proposing or creating a commit, inspect the staged or intended change
well enough to describe its what and why accurately. Check the subject length,
imperative mood, capitalization, period rule, and body formatting. Do not create
a commit or push it unless the user explicitly authorizes that action.
