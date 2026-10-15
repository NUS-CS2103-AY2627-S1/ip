---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page for changes in the current Git repository. Use when asked to show, review, share, compare, or inspect code changes visually; compare revisions, branches, commits, or the worktree; or create an HTML diff.
---

# Present Changes Visually

Generate one interactive HTML page that presents changed files as side-by-side
before/after diffs. The page supports file filtering, folded unchanged runs,
word-level highlighting within changed lines, and collapsed panels for unchanged
files.

## Generate a visual diff

1. Treat the current repository as the target unless another repository is
   specified.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the
   user specifies other comparison points.
   - `WORKTREE` includes staged, unstaged, and untracked non-ignored files.
   - Either comparison point may be a commit, tag, branch, or commit-ish such
     as `HEAD~2`.
   - Do not compare `WORKTREE` with itself.
3. Write the page to `_temp/visual-diff.html` unless the user supplies an
   output path. This location is ignored by the project.
4. Run the bundled generator from the repository root:

   ```bash
   python3 .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py . HEAD WORKTREE _temp/visual-diff.html
   ```

   Replace the comparison points and output path when requested.
5. Confirm that the command succeeds and report the absolute output path and
   changed-file summary.
6. Do not open the HTML page automatically. Open it in a browser only when the
   user asks for visual inspection.

## Useful variations

Compare two commits:

```bash
python3 .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py . HEAD~1 HEAD _temp/visual-diff.html
```

Omit unchanged-file panels:

```bash
python3 .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py . HEAD WORKTREE _temp/visual-diff.html --no-unchanged
```

Open the result only when explicitly requested:

```bash
python3 .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py . HEAD WORKTREE _temp/visual-diff.html --open
```

## Validate the result

After generation:

- Verify that the output file exists.
- Check that the generator reports the expected changed-file count.
- If the user requested a visual review, open the generated HTML and inspect
  the rendered page.
- Keep the generated HTML self-contained except for the optional browser-loaded
  syntax-highlighting resource.

## Resource

Use `scripts/generate-split-view-diff.py` as the standard-library-only
generator. Do not rewrite the generator for ordinary visual diff requests.
