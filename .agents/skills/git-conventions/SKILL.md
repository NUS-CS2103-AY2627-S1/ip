---
name: git-conventions
description: Apply the SE-EDU Git conventions when proposing or reviewing commit messages and branch names for this project.
---

# Git Conventions

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).
Consult the live source when a relevant detail is not covered below or when the user requests a full conventions audit.

## Commit messages

- Use an imperative, capitalized subject without a trailing period.
- Aim for at most 50 characters and never exceed 72 characters.
- Add a meaningful scope or category prefix only when it improves clarity.
- For non-trivial changes, add a body after a blank line and wrap it at 72 characters.
- Explain what changed and why rather than implementation mechanics visible in the diff.
- Use blank lines and bullet points when they improve readability.
- Recommend smaller commits when a message becomes too broad or long.

## Branch names

- Use meaningful kebab-case keywords.
- For issue-related work, prefer `issueNumber-keywords-from-title`.

## Completion check

Before suggesting Git actions:

1. Inspect the relevant Git status and diff when available.
2. Confirm generated files and unrelated changes are not included in the proposed commit.
3. Suggest a convention-compliant commit message for the actual change.
4. State the exact assignment tag when an increment has been completed.
