---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page that visually presents changes in this Java project. Use when asked to show, review, share, or inspect code changes visually; compare revisions, branches, commits, or the worktree; or create an HTML diff.
---

# Present Changes Visually

Use this project-scoped skill when the user wants to understand or share repository changes through a visual diff.

Generate one interactive HTML page containing every changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, lets readers filter files, and includes collapsed panels for unchanged files. It covers Java source and test files as well as documentation and configuration files.

## Generate the page

1. Treat the current repository root as the target unless the user identifies another repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked (but not ignored) files.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path. This repository already ignores `_temp/`.
4. Run the bundled generator from the repository root:

   ```powershell
   python .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py \
     . HEAD WORKTREE _temp/visual-diff.html
   ```

   If the `python` command is unavailable on Windows, use `py -3` in its place. Replace `HEAD`, `WORKTREE`, and the output path with the requested values. The generator uses only Python's standard library; no third-party package installation is required.
5. Confirm that the command succeeded and report the absolute path to the generated page. Do not open a browser unless the user asks.

## Verify output

Check that the page exists and that the generator summary reports the expected changed-file count. For a visual review, open the generated HTML file in a browser or inspect its rendered page only when the user asks.

## Resource

`scripts/generate-split-view-diff.py` is the bundled standard-library-only generator. Keep the page self-contained except for optional syntax-highlighting resources loaded by the page.

