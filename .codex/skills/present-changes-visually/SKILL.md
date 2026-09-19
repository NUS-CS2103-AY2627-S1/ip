---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page that visually presents changes in this Git repository. Use when asked to show, review, share, or inspect code changes visually; compare revisions, branches, commits, or the worktree; or create an HTML diff.
---

# Present Changes Visually

Generate one interactive HTML page containing every changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, filters files, and collapses unchanged files.

## Generate the page

1. Treat the current repository as the target unless the user identifies another repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked files but excludes ignored files.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path.
4. Run the bundled generator from the repository root:

   ```bash
   python3 .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py \
     . HEAD WORKTREE _temp/visual-diff.html
   ```

   Replace the comparison points and output path when requested. The comparison points can be a Git commit-ish, such as `HEAD~1`, a tag, a branch, or a commit SHA; use `WORKTREE` for the current files.

## Verify output

Confirm that the generator succeeds and that its summary reports the expected changed-file count. Report the absolute path to the generated page. Open or render the page only when the user asks for visual inspection.

## Resource

`scripts/generate-split-view-diff.py` is a standard-library-only generator. Keep the generated page self-contained except for optional syntax-highlighting resources loaded by the page.
