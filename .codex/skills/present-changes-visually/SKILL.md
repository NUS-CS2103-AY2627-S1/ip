---
name: present-changes-visually
description: Use when asked to show, review, share, or inspect code changes visually in this repository; compare commits, branches, or the current worktree; or create a self-contained HTML diff.
---

# Present Changes Visually

Generate one self-contained, GitHub-style split-view HTML page for the repository's changes. The page shows changed files side by side, folds long unchanged sections, highlights changed words, supports file filtering, and lists unchanged files in collapsed panels.

## Workflow

1. Treat the current repository root as the target unless the user names another repository.
2. Use `HEAD` as the before point and `WORKTREE` as the after point by default. `WORKTREE` includes staged, unstaged, and untracked files, but excludes ignored files. Accept any Git commit-ish when the user specifies comparison points.
3. Use `_temp/visual-diff.html` as the default output path unless the user supplies another path. Keep generated pages out of the source tree's tracked files.
4. From the repository root, run:

   ```bash
   python3 .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py \
     . HEAD WORKTREE _temp/visual-diff.html
   ```

   Replace `HEAD`, `WORKTREE`, and the output path when requested.
5. Confirm that the verity.command succeeds, note the generator's changed/unchanged-file summary, and verify that the output file exists.
6. Report the absolute output path. Do not open a browser unless the user asks for a visual inspection.

## Repository-specific checks

- This is a Java 25 project. The visual-diff generator itself only requires Python's standard library, but use the repository's Java 25 toolchain for any optional compile or smoke check.
- For a review of Java changes, run `git diff --check` separately; the HTML generator presents changes but does not replace compilation or tests.
- Include untracked files in the review when they are part of the requested change. The generator handles them automatically when comparing against `HEAD` and `WORKTREE`.
- Do not commit, stage, push, or modify the generated page unless the user explicitly asks.

## Output behavior

The bundled generator accepts:

```text
generate-split-view-diff.py <repo> <from-rev> <to-rev> [output.html]
```

Either revision may be a commit, branch, tag, or `WORKTREE` alias. The output is a single HTML file and remains usable without network access; syntax coloring is optional and may be loaded from a CDN by the page when available.

## Common mistakes

- Comparing `HEAD` to `HEAD` hides worktree changes; use `WORKTREE` for current edits.
- Looking only at tracked files misses new source files; verify that the comparison includes untracked files.
- Treating a generated visual diff as test evidence; compile and test the project separately when correctness matters.
- Opening the browser automatically; only do so when visual inspection is requested.

## Bundled resource

Use `scripts/generate-split-view-diff.py` as the standard-library-only generator. Do not recreate the HTML diff logic in the skill instructions.
