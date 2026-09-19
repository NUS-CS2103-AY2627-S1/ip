---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches or creating, reviewing, or amending commits in this project.
---

# Seedu Git Standard

Use this skill for every branch, commit, merge, tag, push, or commit-message review in this repository. Follow the SE-EDU Git conventions alongside the project-specific Git rules in `AGENTS.md`.

## Branches

- Use a meaningful kebab-case branch name made from relevant keywords, unless the user explicitly specifies the branch name.
- For issue work, use `issueNumber-relevant-keywords` when an issue number is available.
- Preserve required increment branches after merging when the assignment requires their history to remain detectable.

## Commit messages

- Write an imperative, capitalized subject with no final period. Aim for 50 characters; never exceed 72.
- For non-trivial commits, add a blank-line-separated body wrapped at 72 characters. Explain what changed and why; leave implementation detail to the diff.
- Split unrelated changes into focused commits. Confirm staged content and run appropriate validation before committing.

## Safety

- Do not commit, tag, push, or otherwise mutate shared Git history unless the user has authorized it.
- Use lightweight tags unless the user requests annotated tags. Respect the project’s required no-fast-forward increment merges.

Before creating a commit, check that the worktree and index contain only intended changes. Source: https://se-education.org/guides/conventions/git.html
