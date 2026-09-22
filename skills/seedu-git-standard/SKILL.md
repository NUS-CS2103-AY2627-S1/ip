---
name: seedu-git-standard
description: Apply the SE-EDU Git commit and branch conventions in this project.
---

# SE-EDU Git Standard

Use this skill whenever preparing, reviewing, or proposing a commit or branch name in this repository.

Source: https://se-education.org/guides/conventions/git.html

## Commit subjects

- Write a clear subject for every commit.
- Use imperative mood, capitalize the first letter, and do not end with a period.
- Prefer 50 characters or fewer; never exceed 72 characters.
- Add a relevant scope or category prefix when it improves clarity.

Examples:

```text
Add task storage support
Date: Reject impossible calendar dates
```

## Commit bodies

For non-trivial commits, separate the subject and body with a blank line. Wrap body lines at 72 characters and use blank lines between paragraphs when useful.

Explain what the change is and why it is needed, rather than narrating implementation details. A useful body describes the current situation, why it needs to change, what the change does, and the rationale for that approach. Use present tense for the current situation and imperative mood when describing the change.

## Branch names

- Use meaningful keywords in kebab-case, such as `refactor-ui-tests`.
- For issue-related branches, use `<issue-number>-<keywords>`, such as `1234-ui-freeze-error`.

## Review checklist

Before proposing or creating a commit, inspect the subject and body against these rules, check that the commit is focused, and do not commit or push unless the user explicitly requests it.
