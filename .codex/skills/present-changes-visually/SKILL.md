---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page for changes in this project. Use when asked to show, review, share, or inspect code changes visually, compare revisions, branches, commits, or the worktree, or create an HTML diff.
---

# Present Changes Visually

Create one interactive HTML page containing every changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, lets readers filter files, and includes collapsed panels for unchanged files.

## Generate the page

1. Treat this repository as the target unless the user identifies another local Git repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked files, but excludes ignored files.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path. This directory is ignored by this repository.
4. From the repository root, run the bundled generator:

   ```powershell
   python .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py `
     . HEAD WORKTREE _temp/visual-diff.html
   ```

   In another environment, use `python3` if that is the available Python command. Replace `HEAD`, `WORKTREE`, and the output path with the requested values. Comparison points may be any Git commit-ish, such as `HEAD~1`, a tag, a branch, or a commit SHA.

5. Confirm that the command succeeded, the output file exists, and the generator summary reports the expected changed-file count. Report the absolute path to the generated page. Do not open a browser unless the user asks for a rendered visual review.

## Generator

`scripts/generate-split-view-diff.py` is the bundled standard-library-only generator. No third-party Python packages are required. It supports `--no-unchanged` to omit unchanged-file panels and `--open` to open the generated page when explicitly requested.
