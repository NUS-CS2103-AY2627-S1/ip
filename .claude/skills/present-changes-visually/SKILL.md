---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page that visually presents changes in the current Git repository. Use when asked to show, review, share, or inspect code changes visually; compare revisions, branches, commits, or the worktree; or create an HTML diff.
---

# Present Changes Visually

Generate one interactive HTML page containing every changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, lets readers filter files, and includes collapsed panels for unchanged files.

The generator uses only Python's standard library, so no packages need to be installed. Syntax colouring is fetched from a CDN by the page itself; without a network the page still renders, just without coloured tokens.

## Generate the page

1. Treat the current repository as the target unless the user identifies another repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked (but not ignored) files.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path. `_temp/` is already git-ignored in this project.
4. Run the bundled generator from the repository root:

   ```bash
   python3 .claude/skills/present-changes-visually/scripts/generate-split-view-diff.py \
     . HEAD WORKTREE _temp/visual-diff.html
   ```

   On Windows, `python` or `py` may be the working launcher instead of `python3`.

   Replace `HEAD`, `WORKTREE`, and the output path with the requested values. The comparison points can be any Git commit-ish such as `HEAD~1`, a tag (e.g. `Level-9`), a branch (e.g. `branch-A-Checkstyle`), or a commit SHA. Use `WORKTREE` for the current files.

5. Confirm the command succeeded and report the path to the generated page. Do not open a browser unless the user asks.

## Verify output

Check that the page exists and that the generator's summary reports the expected changed-file count. For a visual review, open the generated HTML file in a browser or inspect its rendered page when the user asks.

## Commit messages

If the user then asks for a commit covering the reviewed changes, follow this repository's Git conventions in `AGENTS.md`: a lightweight tag unless an annotated one is requested, an imperative capitalised subject with enough detail to explain the rationale, and no commit until the user explicitly asks.

## Resource

`scripts/generate-split-view-diff.py` is the bundled standard-library-only generator. Keep the page self-contained except for the optional syntax-highlighting script the page loads from a CDN.
