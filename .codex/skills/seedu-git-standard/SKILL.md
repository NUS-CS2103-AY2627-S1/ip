---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when preparing, reviewing, or proposing commits and branches in this project.
---

# SE-EDU Git Standard

Use this skill for every commit and branch operation in this project.
The rules are based on the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subject

- Every commit must have a well-written subject line.
- Prefer 50 characters or fewer. Never exceed 72 characters.
- Use the imperative mood.
- Capitalize the first letter.
- Do not end the subject with a period.
- Add a scope or category prefix when it improves clarity, such as `Parser:` or `bug fix:`.

## Commit body

For a non-trivial commit:

- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters.
- Use blank lines between paragraphs and bullets where useful.
- Explain what changed and why. Do not explain implementation details that the diff already shows.
- Describe the existing situation in the present tense.
- Explain why the change is needed.
- Describe the action in the imperative mood and explain the rationale.
- Include other relevant information when it helps a reviewer judge the change.
- Split the work into smaller commits if the message becomes too long or covers unrelated changes.

## Branch names

- Use meaningful kebab-case names with relevant keywords, such as `refactor-ui-tests`.
- For issue-related branches, use `issueNumber-keywords`, such as `1234-ui-freeze-error`.

## Project workflow

Before proposing or creating a commit, inspect the staged diff and check the
subject, body, and branch name against these rules. Do not create or push a
commit unless the user explicitly asks for it.
