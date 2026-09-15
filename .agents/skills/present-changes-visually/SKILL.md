---
name: present-changes-visually
description: Use when asked to show, review, share, or inspect repository changes visually; compare revisions, branches, commits, or the worktree; or create an HTML diff.
---

# Present Changes Visually

Generate one interactive HTML page containing every changed file as a side-by-side before/after diff. The page folds long unchanged runs, highlights changed words within modified lines, supports file filtering, and lists unchanged files in collapsed panels.

## Generate the page

1. Use the current repository unless the user identifies another repository.
2. Compare `HEAD` with `WORKTREE` unless the user specifies comparison points. `WORKTREE` includes staged, unstaged, and untracked files, but excludes ignored files.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path.
4. Run the generator from the repository root:

   ```powershell
   python .agents/skills/present-changes-visually/scripts/generate-split-view-diff.py `
       . HEAD WORKTREE _temp/visual-diff.html
   ```

   Comparison points can also be commits, branches, tags, or expressions such as `HEAD~1`.

5. Confirm that generation succeeded and report the absolute output path. Do not open the page unless the user asks.

## Verify the result

- Confirm the HTML file exists.
- Compare the generator's changed-file count with `git status --short` or the requested revision range.
- When the user requests visual inspection, open or render the page and check that the changed files and before/after content appear correctly.

## Quick reference

| Request | Base | Compare |
|---|---|---|
| Current uncommitted changes | `HEAD` | `WORKTREE` |
| Latest commit | `HEAD~1` | `HEAD` |
| Two branches | first branch | second branch |
| Since a tag | tag | `HEAD` |

## Common mistakes

- A Markdown summary or Mermaid diagram is not the requested visual diff.
- Do not omit staged or untracked files when comparing with `WORKTREE`.
- Do not add `_temp/visual-diff.html` to Git; `_temp/` is ignored.
- Do not open a browser without the user's request.

## Resource

`scripts/generate-split-view-diff.py` is the bundled standard-library-only generator. Its HTML output remains usable without network access; only optional syntax highlighting depends on a CDN.
