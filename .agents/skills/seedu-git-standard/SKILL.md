---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing or creating commit messages and branch names in this project.
---

# Follow the SE-EDU Git Conventions

Use the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
as the source of truth. This skill governs message quality; it does not grant
permission to commit, amend, tag, merge, push, or rewrite history.

## Commit subject

- Summarize one coherent outcome.
- Use the imperative mood, for example `Add task deletion`.
- Capitalize the first letter and do not end with a period.
- Aim for at most 50 characters and never exceed 72.
- Add a meaningful `<scope>:` or `<category>:` only when it improves clarity.

## Commit body

- Add a body for every non-trivial commit, separated from the subject by one
  blank line.
- Wrap every body line at 72 characters and separate paragraphs with blank
  lines.
- Explain what changed and why it is valuable or necessary; let the diff show
  implementation details.
- Describe the prior situation in the present tense and the chosen change in
  the imperative mood. Use bullets when they make multiple changes clearer.
- Split the commit when its rationale becomes too broad to explain concisely.

## Branch names

- Use meaningful keywords in `kebab-case`.
- For issue branches, prefer `issueNumber-keywords-from-issue-title`.
- Preserve exact branch names prescribed by the course even when they are an
  explicit exception to the general convention.

## Before and after committing

1. Confirm the intended branch and a clean understanding of `git status`.
2. Stage only explicit files or hunks belonging to the coherent change.
3. Inspect `git diff --cached --check`, `git diff --cached`, and the staged
   file list before committing.
4. Validate the proposed subject and every body line against the 72-character
   hard limit.
5. After committing, inspect `git show --stat --oneline HEAD`, the full commit
   message, and `git status`.
