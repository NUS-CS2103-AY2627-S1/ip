---
name: seedu-git-standard
description: Review, propose, or write Git commit and merge messages that follow the SE-EDU Git conventions for this project.
---

# SE-EDU Git Standard

Follow https://se-education.org/guides/conventions/git.html whenever proposing,
creating, amending, or reviewing a commit or merge message.

## Inspect before writing

Review the actual diff and recent commit history. Describe the commit's real
scope and match the repository's established terminology without copying an
incorrect earlier style.

## Subject

- Write a meaningful summary in imperative mood.
- Capitalize the first letter and do not end with a period.
- Aim for 50 characters or fewer; never exceed 72 characters.
- Add a useful scope or category prefix only when it improves clarity.

## Body

Include a body for every non-trivial commit and merge. Separate it from the
subject with a blank line and wrap each line at 72 characters.

Explain what the change accomplishes and why it is needed, not implementation
details visible in the diff. A useful order is:

1. State the existing situation in present tense.
2. Explain why it needs to change.
3. Describe the intended change in imperative mood and why that approach fits.
4. Add other relevant context without repeating code comments.

Avoid redundant words such as `currently` and `originally`. Use paragraphs or
bullets when they improve readability. If the body becomes unwieldy, recommend
splitting the work into smaller commits.

## Verify

Before returning or applying a message, check subject length, mood,
capitalization, punctuation, the blank separator, 72-character body wrapping,
and consistency with the actual diff. Do not commit, amend, or push unless the
user explicitly authorizes that Git operation.
