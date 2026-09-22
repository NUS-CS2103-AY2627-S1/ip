---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing, creating, or amending commits and when naming branches in this repository.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) for commit messages and branch names. This skill defines formatting; it does not authorize creating, amending, or otherwise modifying commits.

## Commit subject

- Write a meaningful summary in the imperative mood.
- Capitalize the first letter and do not end with a period.
- Aim for at most 50 characters and never exceed 72 characters.
- Add a relevant `<scope>:` or `<category>:` prefix only when it improves clarity.

## Commit body

Include a body for every non-trivial commit.

- Separate the body from the subject with one blank line.
- Wrap body text at 72 characters and use blank lines between paragraphs.
- Use paragraphs or bullet points according to which communicates the change best.
- Explain what changed and why it was needed; leave implementation mechanics to the diff.
- Describe the existing situation in the present tense and the action taken in the imperative mood.
- Avoid redundant words such as “currently” and “originally”, and do not repeat details already clear from code comments.
- Split the work into finer-grained commits when a coherent explanation becomes excessively long.

For a substantial change, organize the body around the existing situation, why it needs to change, what the commit does, why that approach was chosen, and any other context a reviewer needs.

## Branch names

- Use meaningful keywords in kebab-case, for example `refactor-ui-tests`.
- For issue-related work, use `issueNumber-keywords-from-title`, for example `1234-ui-freeze-error`.

Before creating or amending a commit, review the complete proposed message against these rules and obtain any authorization required by `AGENTS.md`.
