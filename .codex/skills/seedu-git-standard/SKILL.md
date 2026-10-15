---
name: seedu-git-standard
description: Draft and review future Git commit messages for this project using the SE-EDU Git conventions; never rewrite existing history.
---

# SE-EDU Git Standard

Apply the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever proposing, reviewing, or creating a future commit message in this repository.

## Commit subject

- Write an informative imperative subject, capitalize its first letter, and omit the final period.
- Aim for 50 characters or fewer; never exceed 72 characters.
- Add a useful `<scope>:` or `<category>:` prefix only when it improves clarity.

## Commit body

For a non-trivial commit, separate the body from the subject with a blank line and wrap it at 72 characters. Explain what changes and why; let the diff explain how. Describe the existing situation in present tense, then state the intended change in imperative mood. Use separate paragraphs or bullets when they aid readability.

If the message becomes long or covers unrelated rationales, recommend smaller cohesive commits. Do not amend, rebase, filter, or otherwise modify past commit messages solely to enforce this standard.
