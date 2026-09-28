---
name: seedu-git-standard
description: Apply SE-EDU Git conventions when creating commits or naming branches in this project.
---

# SE-EDU Git standard

Use this skill for every commit or branch created in this repository.

## Commit subjects

- Write a clear subject for every commit.
- Use imperative mood, capitalize the first letter, and do not end with a period.
- Prefer 50 characters or fewer; never exceed 72 characters.
- Add a relevant scope or category prefix when it improves clarity.

## Commit bodies

Non-trivial commits must have a body separated from the subject by one
blank line. Wrap body lines at 72 characters and use blank lines between
paragraphs as needed.

Explain WHAT changed and WHY. Describe the current situation in present
tense, why it needs to change, what is being done in imperative mood, and
why that approach was chosen. Do not spend the body explaining HOW the
diff works, and avoid repeating code comments. Split an overly long change
into smaller commits where appropriate.

## Branch names

Use meaningful kebab-case names containing relevant keywords. For
issue-related branches, use `issueNumber-some-keywords-from-issue-title`.

Source: https://se-education.org/guides/conventions/git.html
