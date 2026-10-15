---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page that visually presents changes in this Java project. Use when asked to show, review, share, or inspect code changes visually; compare revisions, branches, commits, or the worktree; or create an HTML diff.
---

# Present Changes Visually

Generate one interactive HTML page containing every changed file as a
side-by-side before/after diff. The page folds long unchanged runs, highlights
changed words within modified lines, lets readers filter files, and includes
collapsed panels for unchanged files.

## Generate the page

1. Treat the current project repository as the target unless the user
   identifies another repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the
   user specifies comparison points. `WORKTREE` includes staged, unstaged, and
   untracked (but not ignored) files.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path.
4. Run the bundled generator from the repository root. On Windows, use
   `python` or the configured Python 3 executable:

   ```powershell
   python .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py . HEAD WORKTREE _temp/visual-diff.html
   ```

   Replace `HEAD`, `WORKTREE`, and the output path with the requested values.
   Comparison points can be any Git commit-ish such as `HEAD~1`, a tag, a
   branch, or a commit SHA. Use `WORKTREE` for the current files.
5. Confirm that the command succeeded and that the reported changed-file count
   matches the intended comparison. Report the absolute path to the generated
   page. Do not open a browser unless the user asks.

## Verify output

Check that the generated HTML file exists and that the generator summary
reports the expected changed-file count. For a visual review, open the HTML in
a browser or inspect its rendered page only when the user asks.

## Resource

`scripts/generate-split-view-diff.py` is the bundled standard-library-only
generator. It needs Python 3 and Git; no third-party packages are required.
