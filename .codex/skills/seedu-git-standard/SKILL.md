---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches and preparing commits in this Nob project.
---

# SE-EDU Git Standard

Use this skill whenever creating a branch, proposing a commit message, or making a commit in this project. Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Branch names

- Use meaningful, relevant keywords in kebab case, such as `refactor-ui-tests`.
- For an issue-related branch, use `issueNumber-keywords-from-issue-title`, such as `1234-ui-freeze-error`.
- Preserve a user-requested branch name when it conflicts with these conventions.

## Commit subjects

- Write a clear subject for every commit using the imperative mood: `Add task persistence`, not `Added task persistence`.
- Start with a capital letter, aim for 50 characters, and never exceed 72 characters.
- Do not end the subject with a period. A useful scope or category prefix is allowed when it improves clarity.

## Commit bodies

- Add a body for non-trivial commits, separated from the subject by one blank line and wrapped at 72 characters.
- Explain what changed and why; let the diff explain implementation details.
- Describe the current situation in present tense, then the need for change, the intended change in imperative mood, its rationale, and other relevant context. Use paragraphs or bullets where they improve clarity.
- If the explanation becomes too long, consider whether the change should be split into smaller, coherent commits.

## Before committing

- Inspect the staged diff and status. Stage only files relevant to the intended commit.
- Propose a compliant subject and, for non-trivial changes, a body that explains the rationale.
- Do not commit or push without the user's explicit permission.
