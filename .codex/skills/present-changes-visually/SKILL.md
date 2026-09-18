/st---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page for visually reviewing changes in this Java project. Use when asked to show, review, share, or inspect code changes visually, compare revisions, or create an HTML diff.
---

# Present Changes Visually

Generate one interactive HTML page containing the selected repository changes as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, lets readers filter files, and includes collapsed panels for unchanged files.

This skill only presents changes. It does not commit, push, or modify Git history.
If a commit message is also requested, follow the repository's
`seedu-git-standard` skill.

## Generate the page

1. Treat `D:\ip` as the target repository unless the user identifies another repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked (but not ignored) files.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path. The project ignores `_temp/`, so generated pages do not become Git changes.
4. Run the bundled generator from the repository root in PowerShell:

   ```powershell
   python .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py `
     . HEAD WORKTREE _temp/visual-diff.html
   ```

   If `python` is not available, use `py` with the same arguments. Replace `HEAD`, `WORKTREE`, and the output path with the requested values. Comparison points can be any Git commit-ish such as `HEAD~1`, a tag, a branch, or a commit SHA. Use `WORKTREE` for the current files.

5. Confirm the command succeeded and report the absolute path to the generated page. Do not open a browser unless the user asks for visual inspection.

## Verify output

Check that the page exists and that the generator's summary reports the expected changed-file count. For a visual review, use the browser skill to open the generated HTML file only when the user asks.

## Resource

`scripts/generate-split-view-diff.py` is the bundled standard-library-only generator. Keep the page self-contained except for optional syntax-highlighting resources loaded by the page.
