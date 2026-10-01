---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page for reviewing changes in this Java project. Use when asked to show, review, share, or inspect code changes visually, compare revisions, or create an HTML diff.
---

# Present Changes Visually

Create one interactive HTML page containing every changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, lets readers filter files, and includes collapsed panels for unchanged files.

## Workflow

1. Treat this repository as the target unless the user identifies another repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked (but not ignored) files.
3. Write to `_temp/visual-diff.html` unless the user supplies another output path.
4. Run the bundled generator from this repository's root. In PowerShell:

   ```powershell
   python .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py . HEAD WORKTREE _temp/visual-diff.html
   ```

   Replace the revisions and output path when requested. Comparison points may be any Git commit-ish, such as `HEAD~1`, a tag, branch, or commit SHA.
5. Confirm the output exists and report its absolute path. Do not open a browser unless requested.

## Project context

- Java source files use Java syntax highlighting automatically.
- Keep generated HTML under `_temp/`; do not add it to the project source tree or commit it unless the user explicitly asks.
- The generator is standard-library-only Python. Do not install packages for it unless execution reveals an environment-specific need.
- This skill presents changes; it does not commit or push them.

## Verification

Check that the page exists and that the generator reports the expected changed-file count. For a visual review, open or render the generated HTML only when the user asks.

The bundled `scripts/generate-split-view-diff.py` is adapted from `https://github.com/se-edu/skill-present-changes-visually` and should remain self-contained.
