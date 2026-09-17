---
name: present-changes-visually
description: Generate a self-contained GitHub-style split-view HTML page for changes in this Java project. Use when asked to show, review, share, or inspect code changes visually, compare revisions or branches, or create an HTML diff.
---

# Present Changes Visually

Create one interactive HTML page containing the repository's changed files as side-by-side before/after diffs. The page folds long unchanged sections, highlights changed words, supports file filtering, and keeps unchanged files collapsed.

## Generate the page

1. Treat this repository root as the target unless the user identifies another repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless comparison points are specified. `WORKTREE` includes staged, unstaged, and untracked non-ignored files.
3. Write to `_temp/visual-diff.html` unless the user gives another output path.
4. From the repository root, run the bundled standard-library-only generator. On Windows, use `py` if `python` is unavailable:

   ```powershell
   py .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py . HEAD WORKTREE _temp/visual-diff.html
   ```

   Replace the revisions and output path when requested. Valid revisions include commits, tags, branches, and commit SHAs.
5. Confirm the command succeeds, the output file exists, and the reported changed-file count is plausible. Report the absolute output path. Do not open a browser unless asked.

## Java-project considerations

Include source, test, configuration, and documentation changes reported by Git; do not filter out Java files. Do not run the application or build solely to generate the page. If the user requests a visual review of the rendered page, open or inspect the generated HTML and report any layout issue.

## Resource

`scripts/generate-split-view-diff.py` is the adapted SE-EDU generator and requires only Python's standard library. Syntax highlighting is loaded by the generated page from highlight.js's CDN when network access is available; the page remains usable without it.
