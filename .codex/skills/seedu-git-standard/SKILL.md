---
name: seedu-git-standard
description: Prepare Git branches and commit messages for this project according to the SE-EDU Git conventions.
metadata:
  short-description: Apply this project's Git conventions
---

# SE-EDU Git Standard

Apply this skill whenever preparing a branch or proposing, reviewing, or creating a commit in this project. These rules are mandatory unless the user explicitly gives conflicting instructions. The source is the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Branch names

- Use meaningful, relevant keywords in kebab case, for example `refactor-ui-tests`.
- For issue-related branches, use `issueNumber-keywords-from-issue-title`, for example `1234-ui-freeze-error`.

## Commit subject

- Write a clear subject in imperative mood, beginning with a capital letter and without a trailing period.
- Target 50 characters and never exceed 72 characters.
- Prefix an applicable scope or category followed by a colon when it improves clarity, for example `Parser: Reject blank task names` or `chore: Update release date`.

## Commit body

- Add a body for every non-trivial commit, separated from the subject by one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines. Use bullets when they make several points clearer.
- Explain WHAT changes and WHY they are needed; leave implementation detail (HOW) to the diff.
- Structure longer bodies as: current situation, why it needs to change, imperative description of the change, why that approach was chosen, then other relevant information. Do not use `currently` or `originally` to describe the present state.
- If the explanation becomes too long, consider splitting the work into finer-grained commits.

## Before committing

Review the proposed commit message against these rules. Respect the project's separate authorization requirement: do not create a commit or push it unless the user explicitly asks.
