---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to commits and branches in this project.
---

# SE-EDU Git standard

Follow the SE-EDU [Git conventions](https://se-education.org/guides/conventions/git.html)
for all commits and branches in this repository.

## Commit subjects

- Every commit must have a well-written subject.
- Keep the subject to 50 characters when practical, with a hard limit of 72 characters.
- Use the imperative mood, capitalize the first letter, and do not end with a period.
- Add a concise scope or category when useful, such as `TaskStorage: ...`.

## Commit bodies

- Give non-trivial commits a body separated from the subject by a blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why; do not spend the body describing implementation details that
  are apparent from the diff.
- Use present tense for the current situation and imperative mood for the change.
- Structure substantial explanations around the situation, why it needs to change, what to do,
  why that approach was chosen, and other relevant information.
- Use bullet points when they improve readability.

## Branch names

- Use meaningful kebab-case names, such as `refactor-ui-tests`.
- For issue branches, use `issueNumber-relevant-keywords`, such as
  `1234-ui-freeze-error`.

Before creating a commit, review the staged diff and verify that its message follows these rules.
