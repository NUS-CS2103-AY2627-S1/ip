---
name: git-standard
description: Apply the SE-EDU Git conventions when creating commits or branches in this project, including commit subject, body, and branch naming rules.
---

# Git Standard

Apply this skill whenever creating or proposing a commit or branch in this repository. The authoritative source is the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit messages

- Write a meaningful subject in the imperative mood, capitalize its first letter, and do not end it with a period.
- Keep the subject near 50 characters and never exceed 72 characters. A relevant `Scope:` or `category:` prefix is allowed when useful.
- For every non-trivial commit, separate the subject from a body with one blank line. Wrap body lines at 72 characters.
- Use the body to explain what changed and why, not how. Describe the current situation, why it needs to change, what the commit does, and the rationale. Use blank lines or bullets to separate ideas when helpful.
- Split changes into finer-grained commits when a message would otherwise become too long or cover unrelated concerns.

Before committing, inspect the staged diff so the message accurately describes the complete commit. Do not commit or push unless the user has authorized it.

## Branch names

Use meaningful kebab-case branch names made from relevant keywords, such as `refactor-ui-tests`. When tied to an issue, use `<issueNumber>-<keywords-from-issue-title>`.
