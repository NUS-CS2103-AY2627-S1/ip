---
name: seedu-git-standard
description: "Apply SE-EDU Git conventions when naming branches or preparing, reviewing, and splitting commits in this project."
---

# SE-EDU Git Standard

Use this skill when preparing, reviewing, or proposing Git branches and commits in this repository. Do not use it to alter Git history or perform Git operations that the user has not authorized.

The authoritative reference is the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Branch names

- Use a meaningful, kebab-case branch name made from relevant keywords.
- For issue work, use `issueNumber-keywords-from-issue-title`.
- When the course or repository requires an exact branch name, preserve that required name instead of applying the generic convention.

## Commit subjects

- Write an imperative, capitalized subject without a trailing period.
- Aim for 50 characters; never exceed 72 characters.
- Add an optional scope or category only when it clarifies the affected area, for example `Parser: Reject empty keywords`.
- Keep each commit cohesive. Split independent changes when doing so makes the history easier to understand and review.

## Commit bodies

For non-trivial commits, add a body separated from the subject by a blank line.

- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why; let the diff explain how.
- Describe the existing situation in present tense, the reason for the change, and the intended result. Use bullets when they improve clarity.
- If the necessary explanation becomes long, reconsider whether the work should be split into smaller commits.

## Verify

Before proposing a commit message, inspect the staged change and ensure the subject accurately covers it. Preserve any grading-sensitive course branch, merge, tag, and push instructions that apply to the task.
