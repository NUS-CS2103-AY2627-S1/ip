---
name: seedu-git-standard
description: Propose and review Git commit messages against the SE-EDU Git conventions for this CS2103 project. Use whenever drafting, checking, or creating a commit.
---

# SE-EDU Git Standard

Use the current [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
as the source of truth.

## Commit checklist

- Keep each commit focused and review the exact staged diff before proposing
  its message. Never absorb unrelated working-tree changes.
- Write the subject in imperative mood, capitalize its first letter, and do
  not end it with a period.
- Aim for at most 50 characters; never exceed 72 characters. An optional
  scope or category prefix is allowed when it improves clarity.
- For a non-trivial commit, separate the body from the subject with one blank
  line and wrap every body line at 72 characters.
- Explain what the change addresses and why it is appropriate. Leave
  low-level implementation details to the diff. Describe the existing
  situation in present tense and proposed action in imperative mood.
- Split the change further if its message needs unrelated explanations.

When asked to commit, first show or state the proposed message and validate it
against this checklist. Do not stage, commit, tag, or push without the user's
authorization for that action.
