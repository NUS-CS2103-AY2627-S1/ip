---
name: seedu-git-standard
description: Apply SE-EDU Git commit-message conventions when preparing commits for this repository.
---

# SE-EDU Git Standard

Use the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
for every future commit in this repository.

## Commit messages

- Write an imperative, capitalized subject without a final period. Aim for 50 characters and never exceed 72 characters. Add a useful scope or category when it improves clarity.
- Give each non-trivial commit a body, separated from the subject by a blank line and wrapped at 72 characters. Explain what changed and why; leave implementation mechanics to the diff.
- Structure the body around the current situation, why it needs to change, what the commit does, and why that approach is appropriate. Use present tense for the situation and imperative mood for the change.
- Split changes into focused commits when a clear rationale cannot fit comfortably in one commit. Keep directly related implementation, tests, and documentation together.

Before committing, review the staged diff and confirm the message follows these rules. Do not commit or push unless the user explicitly requests it.
