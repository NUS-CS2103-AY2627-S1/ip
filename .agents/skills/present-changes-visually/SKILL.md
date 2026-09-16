---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page that visually presents changes in the current Git repository. Use when asked to show, review, share, or inspect code changes visually; compare revisions, branches, commits, or the worktree; or create an HTML diff.
---

# Present Changes Visually

Generate one interactive HTML page containing a GitHub-style, side-by-side diff for the requested repository changes.

## Generate the page

1. Target the current Git repository unless the user specifies another repository.
2. Use the revisions requested by the user. If none are given, compare `HEAD` with `WORKTREE` so staged, unstaged, and untracked changes are included.
3. Write the page to `_temp/visual-diff.html` unless the user specifies a destination. The project's `_temp/` directory is ignored by Git.
4. Run the bundled generator from the repository root:

   ```bash
   python3 .agents/skills/present-changes-visually/scripts/generate-split-view-diff.py \
     . HEAD WORKTREE _temp/visual-diff.html
   ```

5. Add `--no-unchanged` when the user wants only changed files.
6. Confirm that generation succeeded and report the absolute output path. Do not open the page unless the user asks you to.

## Verify the output

- Check that the command exits successfully and reports the expected revisions and file counts.
- Confirm that the HTML output file exists and is non-empty.
- If there are no differences, say so clearly; a valid page may contain zero changed files.

## Resource

The generator is `scripts/generate-split-view-diff.py`. It uses only the Python standard library. The generated page remains usable offline; syntax highlighting is enhanced when its optional Highlight.js CDN resources are available.
