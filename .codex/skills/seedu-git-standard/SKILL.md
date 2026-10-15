---
name: seedu-git-standard
description: Apply SE-EDU Git conventions when creating branches or proposing, creating, or reviewing commits in this project.
---

# Seedu Git Standard

Apply this skill whenever creating or renaming a branch, or proposing, creating, or reviewing a commit in this repository.

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Branch names

- Unless the user specifies an exact name, use a meaningful kebab-case name made from relevant keywords.
- For issue-related branches, use `issueNumber-relevant-keywords`.
- Preserve an explicit user-requested branch name even when it differs from these conventions.

## Commit subjects

- Use the imperative mood, start with a capital letter, and do not end with a period.
- Aim for 50 characters and never exceed 72 characters.
- Add an optional scope or category prefix only when it improves clarity.

## Commit bodies

- Include a body for non-trivial commits, separated from the subject by one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why it matters; leave implementation details to the diff.
- Describe the existing situation in present tense, state the reason for change, then describe the change in imperative mood.
- If a message becomes too long, consider whether the work should be split into smaller commits.

## Workflow

Before committing, review the staged diff and ensure the commit groups one coherent change. Propose a compliant subject and, when needed, body. Do not commit, push, merge, tag, or otherwise change Git state without the user's authorization.
