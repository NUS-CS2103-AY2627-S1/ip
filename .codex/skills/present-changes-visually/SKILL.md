---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page that visually presents changes in this Java project. Use when asked to show, review, share, or inspect code changes visually; compare revisions, branches, commits, or the worktree; or create an HTML diff.
---

# Present Changes Visually

Generate one interactive HTML page containing every changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, lets readers filter files, and includes collapsed panels for unchanged files.

## Project conventions

- Treat the current repository as the target unless the user identifies another repository.
- Use `HEAD` as the before point and `WORKTREE` as the after point unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked (but not ignored) files.
- Write to `_temp/visual-diff.html` unless the user supplies an output path. `_temp/` is already ignored by this project.
- The generator recognizes Java files and common project formats; do not modify source code merely to create a visual diff.

## Generate the page

Run the bundled generator from the repository root. On this Windows project, use the bundled Python runtime when a system `python` command is unavailable:

```powershell
$python = 'C:\Users\aagya\.cache\codex-runtimes\codex-primary-runtime\dependencies\python\python.exe'
& $python .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py . HEAD WORKTREE _temp/visual-diff.html
```

Replace `HEAD`, `WORKTREE`, and the output path with the requested values. Comparison points may be any Git commit-ish such as `HEAD~1`, a tag, branch, or commit SHA. Use `WORKTREE` for current files.

Confirm that the command succeeds, that the output exists, and that the summary reports the expected changed-file count. Report the absolute output path. Do not open a browser unless the user asks.

## Resource

`scripts/generate-split-view-diff.py` is the upstream standard-library-only generator. It creates a self-contained HTML file, with optional browser-side syntax highlighting loaded from a CDN.
