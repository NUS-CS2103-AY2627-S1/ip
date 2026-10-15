---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page that visually presents changes in this Java project. Use when asked to show, review, share, or inspect code changes visually; compare revisions, branches, commits, tags, or the worktree; or create an HTML diff.
---

# Present Changes Visually

Generate one interactive HTML page containing every changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, lets readers filter files, and includes collapsed panels for unchanged files.

## Generate the page

1. Run from this repository's root unless the user identifies another Git repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked files, but excludes ignored files.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path. Keep generated diff pages temporary; do not commit them unless the user explicitly asks.
4. Run the bundled standard-library-only generator:

   ```bash
   python3 .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py \
     . HEAD WORKTREE _temp/visual-diff.html
   ```

   Replace `HEAD`, `WORKTREE`, and the output path with the requested values. A comparison point can be any Git commit-ish, including `HEAD~1`, a tag, a branch, or a commit SHA. Use `WORKTREE` for the current files.

5. Confirm the command succeeded and report the absolute path to the generated page. Do not open a browser unless the user asks.

## Review this project

When summarizing a visual diff, explain important Java and object-oriented design changes at the level of an intermediate undergraduate student. Pay particular attention to behavior changes, parsing and command handling, persistence compatibility, JavaFX UI changes, tests, and Gradle configuration when those files are present. Do not claim that the project builds or tests pass unless those checks were actually run with Java 25 as required by the repository instructions.

## Verify output

Check that the page exists and that the generator's summary reports the expected changed-file count. For a visual review, open the generated HTML file in a browser or inspect its rendered page only when the user asks.

## Resource

`scripts/generate-split-view-diff.py` is the bundled generator. It keeps the page self-contained except for optional syntax highlighting loaded from a CDN; without network access, the diff remains usable but has no token coloring.
