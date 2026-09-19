---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page that visually presents changes in this Java project. Use when asked to show, review, share, or inspect code changes visually; compare revisions, branches, commits, or the worktree; or create an HTML diff.
---

# Present Changes Visually

Generate one interactive HTML page containing every changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, lets readers filter files, and includes collapsed panels for unchanged files.

## Generate the page

1. Treat this repository as the target unless the user identifies another repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked (but not ignored) files.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path. Create the parent directory if needed.
4. Run the bundled generator from the repository root:

   ```bash
   python3 .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py \
     . HEAD WORKTREE _temp/visual-diff.html
   ```

   Replace `HEAD`, `WORKTREE`, and the output path with the requested values. Comparison points can be any Git commit-ish such as `HEAD~1`, a tag, a branch, or a commit SHA.
5. Confirm the command succeeded, the page exists, and the generator summary reports the expected changed-file count. Report the absolute path to the generated page. Do not open a browser unless the user asks.

## Project context

- Keep the visual diff focused on the requested Java project changes; do not modify source files merely to produce the page.
- If the user asks to compare implementation behavior as well as source text, run the project’s relevant checks using Java 25 according to `AGENTS.md`, then mention those results separately from the visual diff.
- The generator itself requires only Python’s standard library. Its generated page may load optional syntax highlighting from a CDN; it remains usable without network access.

## Resource

`scripts/generate-split-view-diff.py` is the bundled generator adapted from the se-edu reference repository. Keep the generated page self-contained except for optional syntax-highlighting resources loaded by the page.
