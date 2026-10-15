---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing or creating commit messages and branch names in this project.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
whenever proposing or creating commits or branches in this project.

## Commit subjects

- Write a meaningful summary in the imperative mood.
- Capitalize the first letter and do not end with a period.
- Aim for at most 50 characters and never exceed 72 characters.
- Add a relevant `<scope>:` or `<category>:` prefix only when it improves clarity.

## Commit bodies

- Add a body for every non-trivial commit.
- Separate the subject and body with one blank line.
- Wrap body text at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why; let the diff show how it was implemented.
- Describe the existing situation in the present tense and the action taken in the imperative mood.
- Split the work into finer-grained commits if a clear body becomes excessively long.
- Use bullet points when they make multiple related changes easier to understand.

## Branch names

- Use meaningful keywords in kebab case, such as `refactor-ui-tests`.
- For issue-related branches, use `issueNumber-keywords`, such as `1234-ui-freeze-error`.
- Preserve branch names explicitly required by the course, even when they do not follow ordinary kebab case.

## Before committing

1. Confirm the user has explicitly authorized the commit.
2. Inspect the staged diff and ensure it represents one coherent change.
3. Check the proposed subject and body against this standard.
4. Do not amend, force-push, or otherwise rewrite history unless the user explicitly requests it.
