---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page for changes in this project's Git repository. Use when asked to show, review, share, or inspect project changes visually; compare revisions, branches, commits, or the worktree; or create an HTML diff.
---

# Present Changes Visually

Generate one interactive HTML page containing each changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, lets readers filter files, and includes collapsed panels for unchanged files.

## Generate the page

1. Treat this project repository as the target unless the user identifies another repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked (but not ignored) files.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path.
4. From the project root, run:

   ```bash
   python3 .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py \
     . HEAD WORKTREE _temp/visual-diff.html
   ```

   Replace the comparison points and output path when requested. Comparison points can be Git commit-ishes such as `HEAD~1`, a tag, a branch, or a commit SHA.
5. Confirm the command succeeds and report the absolute path to the generated page. Do not open a browser unless the user asks.

## Verify output

Check that the page exists and that the generator's summary reports the expected changed-file count. For a visual review, open or inspect the rendered page only when the user asks.

## Resource

`scripts/generate-split-view-diff.py` is the bundled standard-library-only generator. Keep the page self-contained except for optional syntax-highlighting resources loaded by the page.
