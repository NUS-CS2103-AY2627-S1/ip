---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when creating, proposing, or reviewing commits and branch names in this project.
metadata:
  short-description: Enforce the project's SE-EDU Git conventions
---

# SE-EDU Git Standard

Apply this skill whenever a commit is requested, proposed, reviewed, or prepared in this repository. The source standard is the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subject

- Write a clear subject for every commit.
- Use imperative mood, capitalize the first letter, and do not end with a period.
- Keep the subject preferably within 50 characters and never over 72 characters.
- Add a concise `<scope>:` or `<category>:` prefix only when it improves clarity.

## Commit body

- Give every non-trivial commit a body separated from the subject by one blank line.
- Wrap body lines at 72 characters and use blank lines between paragraphs.
- Explain what changed and why. Describe the current situation in present tense and the change in imperative mood; leave implementation details to the diff.
- Use bullet points when they improve readability, and avoid repeating information already captured by code comments.

## Branch names

- Use meaningful kebab-case names containing relevant keywords, such as `refactor-ui-tests`.
- For issue-related branches, use `<issue-number>-<keywords-from-issue-title>`.

## Commit workflow

Before creating or proposing a commit, review the staged diff and verify the subject/body rules above. Do not create or push a commit unless the user explicitly authorizes it. When a commit message is requested, make its rationale specific enough for a reviewer to judge the change without reconstructing the reasoning from the diff.
