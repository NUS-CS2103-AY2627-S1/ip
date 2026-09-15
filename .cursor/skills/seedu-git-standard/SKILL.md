---
name: seedu-git-standard
description: >-
  Apply the SE-EDU Git conventions to commit messages and branch names in this
  project. Use when proposing or writing commits and branches.
---

# SE-EDU Git Standard

Follow https://se-education.org/guides/conventions/git.html for all future
commits in this repository.

## Commit subject

- Imperative mood: `Add`, `Fix`, `Update`, not `Added` or `Adding`.
- Capitalize the first letter.
- No period at the end.
- Aim for 50 characters; hard limit 72.
- Optional scope prefix is allowed, e.g. `Parser: Handle empty find keyword`.

## Commit body

- Separate subject and body with a blank line.
- Wrap body lines at 72 characters.
- Explain what changed and why, not how.
- Use bullet points when helpful.
- Use present tense for the current situation; use imperative mood for the
  change being made.

## Branch names

- Use kebab-case with meaningful keywords.
- iP increment branches follow course names, e.g. `branch-Level-9`.

## Example

```
Find command: Make matching case-insensitive

Find command is case-sensitive.

A case-insensitive find is more user-friendly because users cannot be
expected to remember the exact case of keywords.

Let's update the search to ignore case when matching descriptions.
```

## When editing this project

- Propose commit messages in this format before committing.
- Do not rewrite old commit messages unless explicitly asked.
