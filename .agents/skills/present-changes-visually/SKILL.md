---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page for changes in this Git repository. Use when asked to show, review, share, or inspect code changes visually; compare revisions, branches, commits, or the worktree; or create an HTML diff.
---

# Present Changes Visually

Create one interactive HTML page containing every changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, lets readers filter files, and includes collapsed panels for unchanged files.

## Generate the page

1. Treat the current repository as the target unless the user identifies another repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked (but not ignored) files.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path.
4. Run the bundled generator from the repository root:

   ```text
   python .agents/skills/present-changes-visually/scripts/generate-split-view-diff.py . HEAD WORKTREE _temp/visual-diff.html
   ```

   Use `python3` or the available workspace Python runtime when `python` is not on `PATH`. Replace the comparison points and output path with the requested values. Comparison points can be any Git commit-ish such as `HEAD~1`, a tag, a branch, or a commit SHA. Use `WORKTREE` for the current files.
5. Confirm the command succeeded and report the absolute path to the generated page. Do not open a browser unless the user asks.

## Verify output

Check that the page exists and that the generator summary reports the expected changed-file count. For a visual review, open the generated HTML file in a browser or inspect its rendered page only when the user asks.

## Dependencies

The bundled generator uses Python's standard library and Git; it does not require third-party Python packages. If a future change introduces a missing dependency, install it only when needed and report the installation.

## Resource

`scripts/generate-split-view-diff.py` is the bundled standard-library-only generator. Keep the generated page self-contained except for optional syntax-highlighting resources loaded by the page.
