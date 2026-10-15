---
name: present-changes-visually
description: Render the current set of code changes (a git diff) as a published HTML Artifact for visual review, instead of (or in addition to) a plain-text summary. Use when the user asks to "show me the changes visually" / "present the changes" for this project.
---

# Present Changes Visually

Turn a set of pending code changes into a published Artifact the user can visually review, rather than (or in addition to) a terminal diff/table.

## Steps

1. **Gather the diff.** Typically `git diff` (unstaged), `git diff --staged`, or `git diff <base>...<head>` for a branch, depending on what the user is asking to see. Use `git diff --stat` first to get the file list and change counts.
2. **Load the `artifact-design` skill** before writing any HTML — it is required before publishing any artifact and calibrates how much design effort this deliverable warrants. This is a working review artifact, not a polished deliverable, so keep it functional: clear, scannable, no more visual weight than the content needs.
3. **Build one self-contained HTML page** that presents, per changed file:
   - The file path as a heading.
   - A unified diff view: added lines highlighted (green-ish, prefixed `+`), removed lines highlighted (red-ish, prefixed `-`), context lines neutral. Use a monospace font and preserve whitespace (`white-space: pre`).
   - Long diffs get their own `overflow-x: auto` scroll container — never let the page scroll horizontally.
   - A short summary at the top: number of files changed, total insertions/deletions (from `git diff --stat`).
   - Respect the artifact theme rules (light/dark via `prefers-color-scheme` and `[data-theme]`, per the Artifact tool's instructions) — diff colors must stay legible in both.
4. **Publish it** with the Artifact tool (`file_path` pointing at the HTML you wrote, a `title` naming the change/increment, a `favicon`, and a one-line `description`).
5. **Report the link** to the user along with a one-line summary of what changed — don't re-paste the whole diff as text once it's in the artifact.

## Notes

- This skill is for *showing* a diff, not editing anything — read-only.
- If the diff is very large (many files or huge files), consider grouping by directory/package or summarizing the least-interesting files (e.g. generated/lockfiles) rather than dumping everything at full length.
- Prefer this skill over a plain-text visual comparison table when the user explicitly asks for something visual, or when a diff has enough files/lines that a table would be hard to scan.
