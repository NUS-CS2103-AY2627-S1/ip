---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to commits, commit messages, branches, and lightweight tags in this project.
---

# SE-EDU Git standard

Apply these conventions whenever preparing or creating a commit in this project. The canonical source is the [SE-EDU Git conventions guide](https://se-education.org/guides/conventions/git.html).

## Before committing

- Review the staged diff and keep each commit focused on one coherent change.
- Use a meaningful branch name in kebab case, such as `refactor-ui-tests`. For issue-related branches, use `<issue-number>-<keywords-from-issue-title>`.
- Do not commit or push unless the user explicitly asks for it. When the user asks for a commit, apply every rule in this skill and show the proposed message before creating it when practical.

## Commit subject

- Write a concise subject line; aim for 50 characters and never exceed 72 characters.
- Use the imperative mood, capitalize the first letter, and do not end with a period.
- Add a relevant scope or category before the subject when it improves clarity, such as `Person class: Remove static imports` or `chore: Update release date`.

## Commit body

Non-trivial commits must include a body separated from the subject by one blank line. Wrap body lines at 72 characters and use blank lines between paragraphs. Use bullets where they make several changes easier to scan.

Explain what changed and why it was necessary; the diff already explains how. A useful body generally presents:

1. The current situation, in the present tense.
2. Why it needs to change.
3. What is being done, in the imperative mood.
4. Why that approach was chosen.
5. Other relevant information, if any.

Avoid unnecessary repetition of information already present in code comments. If the explanation becomes too long, consider splitting the work into smaller commits.

## Tags

Use lightweight tags unless the user explicitly requests an annotated tag.
