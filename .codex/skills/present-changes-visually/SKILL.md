---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page that visually presents changes in this Java project repository. Use when asked to show, review, share, or inspect code changes visually; compare revisions, branches, commits, or the worktree; or create an HTML diff for the ip project.
---

# Present Changes Visually

Generate one interactive HTML page containing every changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, lets readers filter files, and includes collapsed panels for unchanged files.

## Generate the page

1. Treat `ip/` (the directory containing this skill) as the target Git repository. Run the generator from the `ip` repository root unless the user identifies another repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked (but not ignored) files.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path.
4. Run the bundled generator:

   ```powershell
   python .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py `
     . HEAD WORKTREE _temp/visual-diff.html
   ```

   If `python` is unavailable, use the installed Python 3 runtime or ask for permission to install it. Replace `HEAD`, `WORKTREE`, and the output path with requested comparison points. Comparison points may be commits, tags, branches, or commit SHAs.
5. Confirm the command succeeded, check the reported changed-file count, and report the absolute path to the generated page. Do not open a browser unless the user asks.

## Verify output

Check that the HTML file exists and that the generator reports the expected changed-file count. For a visual review, open the generated HTML file in a browser or inspect its rendered page only when the user asks.

## Commit messages

When creating or proposing a commit message for reviewed changes, follow the repository's Git guidance in `AGENTS.md`: use an imperative subject and explain what changed and why. Do not commit or push unless explicitly asked.

## Resource

`scripts/generate-split-view-diff.py` is the bundled standard-library-only generator adapted from `se-edu/skill-present-changes-visually`. The generated page uses optional highlight.js resources loaded from a CDN; it remains usable without network access, but syntax colors may be absent.
