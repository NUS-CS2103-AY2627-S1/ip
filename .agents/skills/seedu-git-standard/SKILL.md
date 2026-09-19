---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing or creating commit messages and branch names for this project.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever proposing or creating a commit or branch in this project.

## Commit subjects

- Write an informative subject in imperative mood.
- Capitalize its first letter and do not end it with a period.
- Aim for at most 50 characters; never exceed 72 characters.
- Add a meaningful scope or category prefix only when it improves clarity.

## Commit bodies

- Add a body for every non-trivial commit, separated from the subject by a blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why; let the diff show how.
- Describe the existing situation in present tense and the action in imperative mood.
- Split the work into smaller cohesive commits when a clear body becomes excessively long.

## Branch names

- Use a meaningful kebab-case name.
- For issue-related work, start with the issue number followed by relevant keywords, such as `1234-ui-freeze-error`.

Before committing, check the complete message against these rules. Before creating a branch, check its proposed name. User-specified names take precedence, but mention a standards conflict before using one when possible.
