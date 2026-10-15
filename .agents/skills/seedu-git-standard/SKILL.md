---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing or creating commits and branches in the Zikiai project.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever proposing or performing Git commits or creating branches. This skill does not grant permission to commit, push, tag, merge, rewrite history, or otherwise mutate Git state; obtain the authorization required by `AGENTS.md` and the user.

## Commit subjects

- Write an informative subject for every commit.
- Aim for at most 50 characters and never exceed 72 characters.
- Use imperative mood, capitalize the first word, and do not end with a period.
- Add a meaningful scope or category prefix only when it improves clarity.

## Commit bodies

- Add a body for non-trivial commits, separated from the subject by a blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what changes and why it is worthwhile; leave implementation details to the diff.
- Describe the existing situation in present tense and the chosen change in imperative mood.
- Split unrelated or overly broad work into focused commits.

## Branch names

- Use meaningful kebab-case keywords, such as `refactor-ui-tests`.
- When tied to an issue, start with its number, such as `1234-fix-ui-freeze`.
- Preserve an assignment-mandated branch name even when it differs from this convention.

## Before creating a commit

Review the staged diff, confirm it contains only the intended increment, and verify the applicable tests. Never amend, force-push, or rewrite shared history unless the user explicitly requests it and the exact target has been checked.
