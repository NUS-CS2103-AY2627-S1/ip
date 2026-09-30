---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches or preparing commits in this project.
---

# SE-EDU Git Standard

Use the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever creating a branch or preparing a commit in this repository.

## Branches

- Use a meaningful kebab-case branch name made from relevant keywords, such as `refactor-ui-tests`.
- For issue-related branches, use `issueNumber-keywords-from-issue-title`, such as `1234-ui-freeze-error`.

## Commit subjects

- Write a well-formed subject line in imperative mood: `Add task persistence`, not `Added task persistence`.
- Start with a capital letter, do not end with a period, and aim for 50 characters (never exceed 72).
- Add a useful scope or category when appropriate, such as `MemoryHandler: Close file resources` or `bug fix: Reject empty task names`.

## Commit bodies

- Give non-trivial commits a body separated from the subject by a blank line. Wrap it at 72 characters and use blank lines between paragraphs.
- Explain what changed and why; let the diff show how it was implemented. Include enough context for a reviewer to judge the change without opening the diff.
- If the body becomes long because the change covers unrelated work, split it into smaller, focused commits.
- Describe the current situation in present tense, then explain the reason and the intended change in imperative mood. Bullets are appropriate when they improve clarity.

## Before committing

Review the staged diff and confirm the subject/body follow these rules. Do not commit, amend, push, rebase, or otherwise change Git history without the user’s explicit authorization.
