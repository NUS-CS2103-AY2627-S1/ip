---
name: submit-increment
description: Implement one or more assignment increments on branch-X, add and run feature tests, and prepare (but do not perform) the Git submission commands.
---

# Prepare assignment increment

Use this skill when the user provides an assignment task or one or more tasks and wants the
implementation prepared for later submission.

The task's tag is the value `X` in `branch-X` and `Level-X`. For example, a task named `Level-7`
uses `branch-Level-7` and the tag `Level-7`. Preserve the exact capitalization supplied by the
user. If the task does not provide a usable tag, ask the user for it before creating a branch.

## Required workflow

1. Inspect the repository state and current branch:

   ```powershell
   git status --short --branch
   git branch --list
   ```

   Do not discard, reset, stash, or overwrite existing user changes. If uncommitted changes are
   present and their ownership or intended branch is unclear, stop and ask the user.

2. Create or switch to the feature branch named `branch-X`:

   ```powershell
   git switch -c branch-X
   ```

   If the branch already exists, use:

   ```powershell
   git switch branch-X
   ```

   Do not delete the branch. Do not create a commit.

3. Implement every requested task on `branch-X`. Add or update automated tests for each feature,
   including relevant edge cases. For Java changes, use the `seedu-java-coding-standard` skill.

   Run the feature tests and directly relevant broader tests. Use Java 25. If the CLI changes,
   update `test/ui-test-plan.md` and run the project-local `test-ui` skill; stop at its first
   failure. Report every test command and result.

4. Confirm the final state:

   ```powershell
   git status --short --branch
   git diff --stat
   git diff --check
   ```

## Submission commands to output, but never run

After implementation and verification, output:

```powershell
git switch master
git merge --no-ff branch-X -m "Merge branch-X into master"
git tag X
git push origin master
git push origin branch-X
git push origin X
```

Explain that these create the no-fast-forward merge commit, tag it, and push `master`, the still-
existing feature branch, and the tag. Adapt the remote/base branch if inspection shows they differ.

## Hard constraints

- Never run `git commit`, `git merge`, `git tag`, or `git push` with this skill.
- Never delete `branch-X` or hide pre-existing changes.
- Never claim tests passed without running them.
- If blocked, clearly mark the submission commands as pending.
