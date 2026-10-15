---
name: seedu-git-standard
description: Propose, review, and create Git commits that follow the SE-EDU Git commit-message conventions for this project.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever proposing,
reviewing, or creating a commit in this project.

## Commit subject

- Summarize one cohesive change.
- Use imperative mood and capitalize the first letter.
- Do not end with a period.
- Aim for 50 characters and never exceed 72 characters.
- Add a meaningful `<scope>:` or `<category>:` prefix only when it improves clarity.

## Commit body

Add a body for non-trivial commits, separated from the subject by one blank line and wrapped at 72 characters.
Explain what situation motivates the change, why it matters, what the commit changes, and why that approach was
chosen. Focus on what and why; leave implementation details to the diff. Split the commit if its message needs to
describe unrelated changes.

Before committing, inspect the staged diff, propose the complete message, verify the subject and body against these
rules, and commit only the reviewed cohesive change.
