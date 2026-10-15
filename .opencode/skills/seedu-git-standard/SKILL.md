---
name: seedu-git-standard
description: Enforces SE-EDU git conventions for this repo. Use when proposing or writing any commit message, or when creating/renaming any branch in this repository. Use ONLY for git conventions, not for Java code style (use seedu-java-coding-standard for that).
---

# SE-EDU git conventions

Apply these rules to every commit message and branch name in this project.

Full standard: https://se-education.org/guides/conventions/git.html

## Commit subject

- Prefer at most 50 characters; 72 characters is the hard limit.
- Use imperative mood: `Add README.md`, not `Added README.md`.
- Capitalize the first letter.
- Do not end with a period.
- An optional `<scope>:` or `<category>:` prefix is allowed when useful.

## Commit body

Use a body for non-trivial commits.

- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Use bullet points when they improve readability.
- Explain WHAT and WHY, not HOW.
- Give enough information to judge the change without reading the diff.
- Describe the existing situation in present tense.
- Explain why it needs to change.
- Describe what is being done using imperative mood; this section may begin with "Let's".
- Explain why the chosen approach is appropriate and add other relevant information.
- Avoid redundant words such as "currently" and "originally".
- Do not repeat information already present in code comments.

## Branch names

- Use meaningful kebab-case keywords, e.g. `refactor-ui-tests`.
- For issue branches, use `<issueNumber>-<keywords-from-title>`, e.g. `1234-ui-freeze-error`.

## Pre-commit checklist

Verify the imperative, capitalized subject is at most 50 characters and has no
period. For a non-trivial commit, verify the body is wrapped at 72 characters
and explains WHAT and WHY. Verify new branch names use kebab-case unless the
course explicitly requires another branch name.
