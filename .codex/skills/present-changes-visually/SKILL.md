---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page that visually presents changes in this Java project. Use when asked to show, review, share, or inspect code changes visually; compare revisions, branches, commits, or the worktree; or create an HTML diff.
---

# Present Changes Visually

Generate one interactive HTML page with every changed file shown as a side-by-side
before-and-after diff. The page folds long unchanged runs, highlights changed words,
filters files, and keeps unchanged files in collapsed panels.

## Generate the page

1. Treat the current repository as the target unless the user names another one.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the user
   gives other comparison points. `WORKTREE` includes staged, unstaged, and untracked
   files, but excludes ignored files.
3. Write to `_temp/visual-diff.html` unless the user gives another output path.
4. Run this command from the repository root, using Python 3:

   ```text
   python .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py . HEAD WORKTREE _temp/visual-diff.html
   ```

   Comparison points can be any Git commit-ish, such as `HEAD~1`, a tag, a branch,
   or a commit SHA.
5. Confirm that the command succeeds and report the absolute path to the HTML page.
   Do not open a browser unless the user asks.

## Verify output

Check that the page exists and that the generator reports the expected number of
changed files. Open the page in a browser only for a requested visual review.

## Resource

`scripts/generate-split-view-diff.py` is a standard-library-only generator. The
 generated page is self-contained except for optional syntax highlighting loaded from
 a CDN. Do not modify project source files while generating a visual diff.
