---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page for changes in this Java project. Use when asked to show, review, share, or inspect code changes visually, compare revisions, branches, commits, or the worktree, or create an HTML diff.
---

# Present Changes Visually

Generate one interactive HTML page containing every changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, lets readers filter files, and includes collapsed panels for unchanged files.

## Project conventions

- Treat the current repository as the target unless another repository is identified.
- Use `HEAD` as the before point and `WORKTREE` as the after point unless comparison points are specified. `WORKTREE` includes staged, unstaged, and untracked non-ignored files.
- Keep Java source paths under `src/main/java` and tests under the repository's existing test directories when interpreting the diff.
- When running project commands for context, use Java 25 as required by this repository's `AGENTS.md`.

## Generate the page

1. Write to `_temp/visual-diff.html` unless an output path is supplied.
2. From the repository root, run:

   ```powershell
   python .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py . HEAD WORKTREE _temp/visual-diff.html
   ```

   Replace the comparison points and output path when requested. Comparison points may be any Git commit-ish, such as `HEAD~1`, a tag, branch, or commit SHA.
3. Confirm the command succeeded, check that the reported changed-file count is plausible, and report the absolute path to the generated page. Do not open a browser unless asked.

## Verify output

Check that the generated HTML exists. For a visual review, open or render it only when the user asks. The generator is standard-library-only; syntax highlighting is optional and loaded by the page from a CDN.
