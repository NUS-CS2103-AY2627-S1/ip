---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing or creating commit messages and branch names in this project.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever proposing or creating commits or branch names in this repository.

## Commit subjects

- Write a meaningful subject for every commit.
- Use imperative mood, as if completing the phrase “If applied, this commit will ...”.
- Capitalize the first letter and do not end with a period.
- Target at most 50 characters and never exceed 72 characters.
- Add a useful `<scope>:` or `<category>:` prefix only when it improves clarity.

## Commit bodies

- Add a body for every non-trivial commit, separated from the subject by a blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why it was necessary or appropriate. Leave implementation details that are obvious from the diff out of the message.
- Describe the existing situation in present tense and the change in imperative mood. Avoid redundant qualifiers such as “currently” and “originally”.
- Use bullet points when they make several related changes easier to scan.
- Split the work into finer-grained commits when a clear message becomes excessively long.

## Branch names

- Use meaningful relevant keywords in kebab-case.
- For issue-related work, use `issueNumber-keywords-from-title`, such as `1234-ui-freeze-error`.

## Before creating a commit

Inspect the staged diff and confirm the proposed message accurately describes its complete scope. Do not commit or push unless the user has explicitly authorized that action.
