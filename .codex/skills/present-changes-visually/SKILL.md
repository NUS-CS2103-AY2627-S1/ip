---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page that visually presents changes in this Java repository. Use when asked to show, review, share, or inspect code changes visually; compare revisions, branches, commits, or the worktree; or create an HTML diff.
metadata:
  short-description: Create a split-view HTML diff for this project
---

# Present Changes Visually

Generate one interactive HTML page containing every changed file as a
side-by-side before/after diff. The page folds long unchanged runs, highlights
changed words within modified lines, lets readers filter files, and includes
collapsed panels for unchanged files.

## Project defaults

- Treat the current repository root as the target repository.
- Compare `HEAD` with `WORKTREE` by default. `WORKTREE` includes staged,
  unstaged, and untracked non-ignored files.
- Write the result to `_temp/visual-diff.html` unless the user supplies an
  output path. `_temp/` is already ignored by this project.
- Preserve the Java source and documentation; only create or replace the
  requested HTML artifact.
- Use the generator's Java and Markdown syntax highlighting when those files
  are present. The generator supports other common file types too.

## Generate the page

Run the bundled standard-library-only generator from the repository root:

```bash
python3 .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py \
  . HEAD WORKTREE _temp/visual-diff.html
```

Replace `HEAD`, `WORKTREE`, and the output path when the user specifies other
comparison points. Comparison points can be Git commit-ish values such as a
commit SHA, tag, branch, or `HEAD~1`.

## Verify and report

- Confirm the command succeeds and the output file exists.
- Check that the generator summary reports the expected changed-file count.
- Report the absolute path to the generated HTML page.
- Do not open a browser unless the user asks for a visual inspection or wants
  the page opened.

The generated page is self-contained except for optional browser-loaded
syntax-highlighting resources. It remains usable without network access, but
syntax colors may be unavailable in that case.
