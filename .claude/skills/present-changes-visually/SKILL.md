---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page that visually presents changes in the current Git repository. Use when asked to show, review, share, or inspect code changes visually; compare revisions, branches, commits, or the worktree; or create an HTML diff.
---

# Present Changes Visually

Generate one interactive HTML page containing every changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, lets readers filter files, and includes collapsed panels for unchanged files.

## Generate the page

1. Treat the current repository as the target unless the user identifies another repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked (but not ignored) files.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path. `_temp/` is already listed in this repository's `.gitignore`, so generated pages stay out of version control.
4. Run the bundled generator from the repository root:

   ```bash
   python .claude/skills/present-changes-visually/scripts/generate-split-view-diff.py \
     . HEAD WORKTREE _temp/visual-diff.html
   ```

   Replace `HEAD`, `WORKTREE`, and the output path with the requested values. The comparison points can be any Git commit-ish such as `HEAD~1`, a tag, a branch, or a commit SHA. Use `WORKTREE` for the current files.

   Use `python`, not `python3`. On this Windows machine `python3` resolves to a Microsoft Store alias stub that is not a working interpreter; `py` also works.

5. Confirm the command succeeded and report the absolute path to the generated page. Do not open a browser unless the user asks.

## Options

- `--open` opens the page in the default browser when generation finishes. Pass it only when the user asks to see the page.
- `--no-unchanged` omits the collapsed panels for files that did not change, giving a shorter page.
- The output path is optional. Omitting it writes to `_temp/compare-<base>-<head>.html`.

## Verify output

Check that the page exists and that the generator's summary reports the expected changed-file count. The generator prints a line such as `3 changed file(s), 12 unchanged` followed by the written path and file size. For a visual review, open the generated HTML file in a browser or inspect its rendered page when the user asks.

## Resource

`scripts/generate-split-view-diff.py` is the bundled generator and uses only Python's standard library. The page is self-contained apart from syntax highlighting, which loads highlight.js from a CDN; without network access the page still renders correctly, just without colored tokens.
