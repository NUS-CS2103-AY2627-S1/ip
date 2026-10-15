---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page showing changes in this Java repository. Use when asked to show, review, share, or inspect code changes visually, compare revisions, or create an HTML diff.
---

# Present Changes Visually

Generate one interactive HTML page containing every changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, lets readers filter files, and includes collapsed panels for unchanged files.

## Workflow

1. Treat `/Users/kaishean/ip` as the target repository unless the user specifies another path.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked non-ignored files.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path.
4. From the repository root, run:

   ```bash
   python3 .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py \
     . HEAD WORKTREE _temp/visual-diff.html
   ```

   Replace the comparison points and output path when requested.
5. Confirm that the output exists and report its absolute path. Do not open a browser unless the user asks.

The bundled generator uses only Python's standard library. Syntax highlighting is provided in the generated page through an optional browser CDN resource; the page remains usable without network access.
