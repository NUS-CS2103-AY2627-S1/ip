---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page that visually presents changes in this Git repository. Use when asked to show, review, share, or inspect code changes visually; compare revisions, branches, commits, or the worktree; or create an HTML diff.
---

# Present Changes Visually

Generate one interactive HTML page containing every changed file as a
side-by-side before/after diff. The page folds long unchanged runs, highlights
changed words within modified lines, lets readers filter files, and includes
collapsed panels for unchanged files.

## Generate the page

1. Treat this repository as the target unless the user identifies another one.
2. Use `HEAD` as the before point and `WORKTREE` as the after point unless the
   user specifies comparison points. `WORKTREE` covers staged, unstaged and
   untracked files, but not ignored ones.
3. Write to `_temp/visual-diff.html` unless the user supplies an output path.
   `_temp/` is already gitignored, so generated pages stay out of commits.
4. Run the bundled generator from the repository root:

   ```bash
   python3 .claude/skills/present-changes-visually/scripts/generate-split-view-diff.py \
     . HEAD WORKTREE _temp/visual-diff.html
   ```

   Replace `HEAD`, `WORKTREE` and the output path with the requested values.
   Either comparison point can be any commit-ish — `HEAD~1`, a tag, a branch, a
   commit SHA — or `WORKTREE` for the current files. Both cannot be `WORKTREE`.

5. Confirm the command succeeded and report the absolute path to the generated
   page. Do not open a browser unless the user asks; `--open` does that.

Other options: `--no-unchanged` omits the collapsed panels for files that did
not change, which is worth using when the repository is large and the point is
the diff rather than the surrounding code.

## Verify output

Check that the page exists and that the generator's summary reports the
changed-file count you expect. Cross-check it against `git status --short` and
say so if the two disagree — a surprising count usually means the comparison
points were not the ones intended.

## Reporting

Report the path and the summary line the generator prints. Do not describe the
visual appearance of the page as though you had looked at it, unless you
actually opened and inspected it.

## Commit messages

When proposing a commit message for the reviewed changes, follow the
conventions in `AGENTS.md`: an imperative subject, and a body detailed enough to
explain what changed and why. Do not commit or push unless explicitly asked.

## Notes

- The generator uses only the Python standard library, so there is nothing to
  install beyond `python3`.
- The page is self-contained apart from one `<script>` tag pointing at a
  highlight.js CDN for syntax colouring. It renders correctly offline; the code
  is simply not colourised.
- Generated pages embed the full contents of the compared files. Treat a page as
  carrying the same sensitivity as the source, and do not share one anywhere the
  repository itself could not go.

## Resource

`scripts/generate-split-view-diff.py` — the bundled generator, vendored from
https://github.com/se-edu/skill-present-changes-visually at commit `95c044c`.
Re-copy that file to take upstream fixes.
