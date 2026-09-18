---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page for changes in this Java project. Use when asked to show, review, share, compare, or inspect code changes visually, including differences between commits, branches, tags, or the current worktree.
---

# Present Changes Visually

Generate one interactive HTML page containing every changed file as a side-by-side
before/after diff. The page folds long unchanged runs, highlights changed words
within modified lines, supports file filtering, and includes collapsed panels for
unchanged files.

## Generate a visual diff

1. Treat the repository root as the target unless the user identifies another
   repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the
   user specifies comparison points. `WORKTREE` includes staged, unstaged, and
   untracked files, but excludes ignored files.
3. Use `_temp/visual-diff.html` as the output path unless the user supplies one.
4. Run the bundled generator from the repository root. On this Windows project,
   use `python`:

   ```powershell
   python .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py `
     . HEAD WORKTREE _temp/visual-diff.html
   ```

   Replace `HEAD`, `WORKTREE`, and the output path with the requested values.
   Valid comparison points include commit SHAs, tags, branches, and expressions
   such as `HEAD~2`. Use `WORKTREE` for the current files.
5. Confirm that the command succeeds, check that the output file exists, and
   report its absolute path together with the changed-file count printed by the
   generator.

Do not open a browser automatically. Open or inspect the generated page only when
the user asks for a visual review. Pass `--no-unchanged` when the user wants only
changed files; pass `--open` only when the user explicitly asks to open the page.

## Resource

Use `scripts/generate-split-view-diff.py` for the generation step. It uses only
Python's standard library; syntax highlighting is loaded by the generated page
from a CDN when network access is available, and the page remains usable without
it.
