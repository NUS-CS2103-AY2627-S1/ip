---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page that visually presents changes in this Git repository. Use when asked to show, review, share, or inspect code changes visually; compare revisions, branches, commits, or the worktree; or create an HTML diff.
---

<!-- Used Codex to generate skill -->

# Present Changes Visually

Generate one interactive HTML page containing every changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, lets readers filter files, and includes collapsed panels for unchanged files.

## Generate the page

1. Treat this project repository as the target unless the user identifies another repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked (but not ignored) files.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path.
4. Run the bundled generator from the repository root:

   ```powershell
   python .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py `
     . HEAD WORKTREE _temp/visual-diff.html
   ```

   Replace `HEAD`, `WORKTREE`, and the output path with the requested values. The comparison points can be any Git commit-ish such as `HEAD~1`, a tag, a branch, or a commit SHA. Use `WORKTREE` for the current files.

5. Confirm the command succeeded and provide a clickable Markdown link to the generated page. On Windows, convert backslashes to forward slashes and prefix the drive path with `/`, for example:

   ```markdown
   [Open the interactive visual diff](</C:/path/to/repository/_temp/visual-diff.html>)
   ```

   Keep angle brackets around the link target so paths containing spaces remain valid. Do not open a browser unless the user asks.

## Verify output

Check that the page exists and that the generator's summary reports the expected changed-file count. For a visual review, open the generated HTML file in a browser or inspect its rendered page when the user asks.

## Resource

`scripts/generate-split-view-diff.py` is adapted from [se-edu/skill-present-changes-visually](https://github.com/se-edu/skill-present-changes-visually) and uses only Python's standard library. Keep the page self-contained except for optional syntax-highlighting resources loaded by the page.
