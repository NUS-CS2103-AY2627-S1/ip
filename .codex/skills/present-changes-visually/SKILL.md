---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page that visually presents changes in this Git repository. Use when asked to show, review, share, or inspect code changes visually; compare revisions, branches, commits, or the worktree; or create an HTML diff.
---

# Present Changes Visually

Generate one interactive HTML page containing every changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, lets readers filter files, and includes collapsed panels for unchanged files.

When creating or proposing a commit message for reviewed changes, follow this project's `AGENTS.md`: use an imperative subject and include enough detail to explain the rationale. Do not commit or push unless the user explicitly asks.

## Generate the page

1. Treat this repository as the target unless the user identifies another repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked (but not ignored) files.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path. This project already ignores `_temp/`.
4. Run the bundled generator from the repository root:

   ```powershell
   python3 .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py `
     . HEAD WORKTREE _temp/visual-diff.html
   ```

   Replace `HEAD`, `WORKTREE`, and the output path with the requested values. The comparison points can be any Git commit-ish such as `HEAD~1`, a tag, a branch, or a commit SHA. Use `WORKTREE` for the current files. Resolve an available Python 3 executable before running the command. In Codex Desktop, use the Python path returned by the workspace-dependencies tool when `python3` is unavailable; do not assume that the Windows `py` launcher has a runtime installed.
5. Confirm the command succeeded and report the absolute path to the generated page. Do not open a browser unless the user asks.

## Verify output

Check that the page exists and that the generator's summary reports the expected changed-file count. For a visual review, open the generated HTML file in a browser or inspect its rendered page when the user asks.

## Resource

`scripts/generate-split-view-diff.py` is the bundled standard-library-only generator. Keep the page self-contained except for optional syntax-highlighting resources loaded by the page.

This project-specific skill is based on `se-edu/skill-present-changes-visually` at upstream commit `95c044cdcc57d49acaba704d0a5d9205f09ed58c`.
