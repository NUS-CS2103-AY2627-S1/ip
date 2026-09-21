---
name: seedu-git-standard
description: "Plan and review Sheppy's commits, branches, merges, tags, and pushes using the SE-EDU Git conventions."
---

# Sheppy Git standard

Use this skill whenever a commit, branch, merge, tag, or push is being planned or reviewed.

## Commit conventions

- Inspect `git status`, the staged diff, and `git diff --check` before committing.
- Keep each commit focused on one logical change. Do not include generated build output or unrelated files.
- Write an imperative, capitalized subject with no final period. Aim for 50 characters and never exceed 72 characters.
- For a non-trivial change, add a body separated by a blank line and wrapped at
  72 characters. Explain what changed and why, not implementation details.
- Propose the commit message before creating it when coaching the student.

## Branches, merges, and tags

- Use the assignment's exact branch names and keep parallel increments on their
  own branches until they are ready to merge.
- Inspect the target branch and merge with `--no-ff` when the assignment requires a merge commit.
- Resolve conflicts deliberately, review the result, and run the relevant build/tests before committing the merge.
- Use lightweight tags unless the assignment explicitly asks for an annotated
  tag. Tag the required commit, then verify it with `git show`.
- Never rewrite already-pushed history unless the user explicitly authorizes it.

## Pushing

- Push the branch, merged target branch, and required tags explicitly when the assignment asks for them.
- Verify local status and the remote tracking state after pushing.

The full reference is the [SE-EDU Git conventions guide](https://se-education.org/guides/conventions/git.html).
