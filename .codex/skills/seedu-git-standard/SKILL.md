---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when creating commits or naming branches in a repository.
---

# SEEDU Git Standard

Use this skill whenever a commit is being prepared in the target repository. Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html):

- Write a clear commit subject in the imperative mood. Capitalize its first letter, do not end it with a period, and keep it under 50 characters where practical (72 characters maximum). Add a meaningful scope or category prefix only when useful.
- Give every non-trivial commit a body. Separate it from the subject with a blank line, wrap it at 72 characters, and use blank lines between paragraphs when needed.
- Structure the body around the current situation, why it needs to change, what is being changed, why that approach is used, and any relevant additional information. Explain what and why rather than describing implementation steps; use present tense for the situation and imperative mood for the change.
- Use meaningful branch names in kebab case, such as `refactor-ui-tests`. If work addresses an issue, use `<issue-number>-<keywords-from-issue-title>`.

Before committing, inspect the staged diff and confirm that the subject and body describe the actual change. Follow the repository's existing instruction not to commit unless the user explicitly requests it.
