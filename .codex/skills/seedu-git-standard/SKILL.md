---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions for commit messages and branch names in this project.
---

# SE-EDU Git Standard

Follow the SE-EDU Git conventions:
https://se-education.org/guides/conventions/git.html

## Commit Message Subjects

- Every commit must have a clear subject line.
- Keep the subject line near 50 characters where practical, and never exceed 72 characters.
- Use imperative mood, such as `Add parser tests` instead of `Added parser tests`.
- Capitalize the first letter of the subject.
- Do not end the subject with a period.
- Add a useful scope or category prefix when it improves clarity, such as `Parser: Add find parsing`.

## Commit Message Bodies

For non-trivial commits, include a body.

- Separate the subject from the body with a blank line.
- Wrap body lines at 72 characters.
- Explain what changed and why it changed. Leave low-level implementation details to the diff.
- Use present tense when describing the current situation.
- Use blank lines or bullets when they make the commit easier to read.
- Split large unrelated changes into separate commits instead of writing an oversized body.

## Branch Names

- Use meaningful branch names made from relevant keywords.
- Prefer kebab-case for ordinary branch names, such as `refactor-ui-tests`.
- If a branch is tied to an issue, use `issueNumber-some-keywords-from-issue-title`.
- Follow explicit course-required branch names when the assignment gives an exact name.
