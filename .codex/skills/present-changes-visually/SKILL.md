---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page showing changes in this project. Use when asked to show, review, share, or inspect code changes visually, or to compare revisions, branches, commits, or the worktree.
---

# Present Changes Visually

Generate one interactive HTML page containing every changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, lets readers filter files, and includes collapsed panels for unchanged files.

## Generate the page

1. Treat this repository as the target unless the user identifies another repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked files, but not ignored files.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path. Create the output directory when needed.
4. From the repository root, run:

   ```bash
   python3 .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py \
     . HEAD WORKTREE _temp/visual-diff.html
   ```

   Replace the comparison points and output path when requested. The comparison points can be Git commit-ish values such as `HEAD~1`, a tag, a branch, or a commit SHA.

## Verify output

Confirm the page exists and the generator summary reports the expected changed-file count. For a visual review, open or render the generated page only when the user asks.

## Resource

`scripts/generate-split-view-diff.py` is a standard-library-only generator. Keep the generated page self-contained except for optional syntax-highlighting resources loaded by the page.
