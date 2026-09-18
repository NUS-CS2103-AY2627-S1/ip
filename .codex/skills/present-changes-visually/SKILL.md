---
name: present-changes-visually
description: Generate a self-contained GitHub-style split-view HTML page for changes in a local Git repository. Use when asked to show, review, share, or inspect code changes visually, compare revisions, or create an HTML diff.
---

# Present Changes Visually

Generate one interactive HTML page containing every changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, lets readers filter files, and includes collapsed panels for unchanged files.

## Workflow

1. Treat the current repository as the target unless the user identifies another repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked (but not ignored) files.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path.
4. From the target repository root, run the bundled `scripts/generate-split-view-diff.py` helper. When the skill is installed globally, use the helper from the directory containing this `SKILL.md`:

   ```powershell
   py <path-to-this-skill>/scripts/generate-split-view-diff.py . HEAD WORKTREE _temp/visual-diff.html
   ```

   Replace the comparison points and output path when requested. The points may be any Git commit-ish such as `HEAD~1`, a tag, branch, or commit SHA.
5. Confirm that the command succeeded, that the summary reports the expected changed-file count, and report the absolute output path. Do not open a browser unless asked.

The bundled generator uses only Python's standard library. It supports Java and common project files, and the generated page remains usable without network access (syntax coloring is optional and loaded from a CDN).

## Project conventions

- This repository is a Java 25 project. If you need to run the application or build while preparing or checking a diff, use Java 25 as required by the repository instructions.
- Do not commit or push changes as part of this skill.
- Do not include ignored files in the visual diff.
