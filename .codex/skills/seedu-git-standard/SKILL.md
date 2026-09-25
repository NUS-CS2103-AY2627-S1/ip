---
name: seedu-git-standard
description: >-
  Apply the SE-EDU Git conventions when proposing or creating commits, commit
  messages, branches, merges, or tags in this project.
---

# SE-EDU Git standard

Use this skill whenever Git history, commit messages, or branch names are part
of a request. Follow the authoritative
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit messages

- Write a meaningful subject in imperative mood.
- Capitalize the subject, omit its final period, prefer at most 50 characters,
  and never exceed 72 characters.
- Use an optional scope or category prefix only when it improves clarity.
- For non-trivial commits, separate the body with a blank line and wrap it at
  72 characters.
- Explain what changed and why; let the diff show how.
- Describe the existing situation in present tense and the intended change in
  imperative mood.
- If one message needs an overly long body, recommend splitting the work into
  smaller logical commits.

Before proposing a message, inspect the actual diff and status so the message
describes only the changes that will be committed.

## Branch names

- Use meaningful kebab-case keywords, for example `document-command-methods`.
- For issue-related work, prefer
  `issueNumber-keywords-from-issue-title`.

## Project safeguards

- Do not commit, push, merge, tag, or rewrite history unless the user explicitly
  authorizes that action.
- Use lightweight tags unless the user requests an annotated tag.
- Preserve unrelated worktree changes and run `git diff --check` before a
  commit.
