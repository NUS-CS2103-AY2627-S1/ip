---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when preparing commits or branch names in this project.
---

# SE-EDU Git Standard

Apply these rules to every commit and branch created for this project. The
authoritative source is the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subjects

- Write a subject for every commit in the imperative mood.
- Capitalize the first letter and do not end the subject with a period.
- Prefer 50 characters or fewer; never exceed the 72-character hard limit.
- A scope or category prefix such as `ui:` is optional when it improves
  clarity.

## Commit bodies

- Add a body for every non-trivial commit, separated from the subject by a
  blank line.
- Wrap body lines at 72 characters and use blank lines or bullets to separate
  distinct points.
- Explain what changed and why. Do not spend the body explaining how the diff
  implements the change.
- When useful, describe the situation, why it needs to change, the intended
  change, and why that approach was chosen. Use present tense for the
  situation and imperative mood for the change.

## Branch names

- Use meaningful kebab-case names made from relevant keywords.
- For issue-related branches, use `issueNumber-keywords-from-title`.

## Review before committing

Review the staged diff and commit message for scope, clarity, spelling, subject
length, imperative mood, punctuation, and 72-character body wrapping. Keep
changes uncommitted unless the user explicitly asks for a commit.
