---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing or creating commits, branches, or commit-history changes in this project.
---

# SE-EDU Git Standard

Use the authoritative [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever a task involves commit messages, commits, branches, or history edits.

This skill does not grant permission to commit, amend, rebase, tag, push, or otherwise change Git state. Obtain that authority from the user or existing repository instructions.

## Before committing

- Inspect `git status --short --branch` and the relevant diff.
- Keep each commit focused on one logical change. Split unrelated changes before staging.
- Preserve unrelated user changes and generated files that do not belong in the commit.
- Run the verification required by `AGENTS.md` and stop if it fails.
- Review the staged diff with `git diff --cached --check` and an appropriate staged-diff summary or full diff.

## Commit messages

- Write a meaningful subject in imperative mood.
- Capitalize its first letter and do not end it with a period.
- Aim for at most 50 characters; never exceed 72 characters.
- Optional scopes or categories may precede the imperative description when they improve clarity.
- Add a body for every non-trivial commit. Separate it from the subject with a blank line and wrap body lines at 72 characters.
- Explain what needs to change and why. Leave implementation mechanics to the diff.
- Describe the existing situation in present tense and the change in imperative mood.
- Avoid redundant detail and words such as “currently” or “originally”. Use short paragraphs or bullets when that improves readability.

## Branch names

- Use a meaningful kebab-case name made from relevant keywords.
- For issue-specific branches, prefer `issueNumber-short-description`, such as `1234-fix-ui-freeze`.

## After committing

Confirm the resulting commit subject, commit scope, and working-tree state. Do not push unless the user explicitly requests it.
