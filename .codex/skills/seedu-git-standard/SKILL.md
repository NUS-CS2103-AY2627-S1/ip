---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches and creating or reviewing commits in this project.
---

# Seedu Git Standard

Use this skill whenever a Git commit is being prepared, proposed, reviewed, or
created in this repository. It is based on the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subjects

- Write a meaningful subject for every commit.
- Use the imperative mood, capitalize the first letter, and do not end with a period.
- Prefer no more than 50 characters; never exceed the 72-character hard limit.
- Add a concise scope or category prefix only when it improves clarity, such as
  `Parser: Add command validation` or `chore: Update dependencies`.

## Commit bodies

- Add a body for every non-trivial commit, separated from the subject by one blank line.
- Wrap body lines at 72 characters and use blank lines between paragraphs.
- Explain what changed and why it changed; do not merely describe the implementation
  steps. The diff provides the implementation details.
- Structure substantial bodies around the present situation, why it needs to change,
  what is being changed, why that approach was chosen, and any relevant context.
- Use bullet points when they improve clarity, and avoid repeating information already
  captured in code comments.

## Branch names

- Use meaningful lowercase kebab-case names containing relevant keywords, such as
  `refactor-ui-tests`.
- For issue-driven work, use `<issue-number>-<keywords-from-title>`.

## Commit checklist

Before creating a commit:

1. Inspect `git status` and the staged diff to ensure only intended changes are included.
2. Check the subject and body against the rules above.
3. Ensure the commit explains the rationale sufficiently for a reviewer to judge the
   change without reading the diff.
