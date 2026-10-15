---
name: seedu-git-standard
description: Apply the SE-EDU Git commit-message convention to every future commit in this project.
---

# SE-EDU Git standard

Use this skill whenever preparing or proposing a Git commit in this repository. Project rules still require explicit user authorization before committing or pushing.

## Commit subject

- Use a clear, imperative, English subject that begins with a capital letter and has no final period.
- Aim for 50 characters; never exceed 72 characters.
- Add a useful scope or category prefix when it clarifies the affected area, for example `Parser: Reject empty descriptions`.

## Commit body

- Give non-trivial commits a body separated from the subject by one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why it is useful; let the diff explain how it was implemented.
- If the explanation becomes too long or covers unrelated changes, split the work into smaller, focused commits.

Before committing, inspect the staged changes to ensure the message accurately describes one coherent change.