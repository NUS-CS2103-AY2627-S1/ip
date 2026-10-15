---
name: seedu-git-standard
description: Draft, review, and validate Git commit messages and branch names according to SE-EDU conventions in this project. Use whenever proposing or creating commits or branches.
---

# SE-EDU Git Standard

Use this skill whenever proposing, reviewing, or creating a commit message or branch name in this repository.

Before doing so, read [references/git-standard.md](references/git-standard.md) completely.

## Workflow

1. Inspect the relevant status and diff. For an actual commit, review the staged diff and confirm it contains one
   coherent change.
2. If the change is too broad for a clear message, recommend splitting it before committing.
3. Draft a capitalized, imperative subject with no trailing period. Prefer at most 50 characters and never exceed
   72 characters.
4. For a non-trivial commit, add a body after one blank line. Wrap body text at 72 characters and explain what
   changed and why; leave implementation details to the diff.
5. Verify the final subject and body against the reference before presenting or using them.

When naming a branch, use meaningful kebab-case keywords. Prefix an issue-related branch with its issue number.

This skill does not authorize committing, amending, rebasing, tagging, or pushing. Obtain the authorization required
by `AGENTS.md` immediately before performing any such action.

Use the canonical [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) when a rule needs
clarification.
