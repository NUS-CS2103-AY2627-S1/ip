---
name: seedu-git-standard
description: Propose, review, and create commit messages for the Friday repository using the SE-EDU Git conventions, including merge commits.
---

# SE-EDU Git standard

Source: [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).
Read the source when an edge case is not covered here.

## Message rules

- Use an imperative subject with an initial capital and no final period.
  Aim for 50 characters; never exceed 72. A meaningful scope prefix is optional.
- Give nontrivial changes a body. Separate it from the subject and separate paragraphs with blank lines.
  Wrap body lines at 72 characters. Bullets are allowed.
- Explain the change and its reason, not an implementation walkthrough or duplicated code comments.
  Describe the prior situation in present tense, the need, the action in imperative mood, the reason
  for that approach, and relevant supporting information. Avoid unnecessary temporal qualifiers.
- Split unrelated work if its explanation becomes unwieldy.

## Friday workflow

Inspect the actual staged diff before committing. Include the rationale and verified test results;
never claim a check ran when it did not. Follow the requested file boundaries for standalone commits.
Apply the same checks to merge messages. This skill grants no permission to commit, merge, tag, push,
or rewrite history: use the user's scope and `AGENTS.md` workflow.

Before proposing or creating a message, check subject length, imperative wording, capitalization,
punctuation, the blank separator, body widths, and correspondence to the staged files.

Example shape (replace all content with facts about the actual change):

```text
Clarify task selection errors

Invalid selections share an ambiguous error message.

Explain which task numbers are accepted so users can correct their input.
Keep the saved task format unchanged to preserve existing data.

Verify the affected parser and UI cases with the required Java SDK.
```
