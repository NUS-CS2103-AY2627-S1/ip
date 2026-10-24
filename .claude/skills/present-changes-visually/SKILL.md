---
name: present-changes-visually
description: Turn the current git diff in this repo into a published HTML Artifact showing a clean, colored before/after (or unified) view of every changed file, plus a short rationale/pros-cons writeup. Use this whenever asked to "present changes visually", "run /present-changes-visually", show a visual diff, or visualize what a commit/refactor changed, instead of just pasting a raw `git diff` into the chat.
---

# Present Changes Visually

Publishes the current (or a specified) git diff as a readable HTML page —
colored additions/removals per file, with a short explanation of *why* the
change was made — rather than dumping a raw unified diff into the
conversation.

## Why this exists

A raw terminal diff is fine for a quick glance but bad for actually
reviewing a refactor: no color, no file grouping, no room for the
rationale. This project's course workflow (CS2103 iP) explicitly asks for
a "visual diff" step after each refactor increment so changes can be
reviewed like a small code review, not just skimmed as text.

## Steps

1. **Get the diff.** Default to the most recent commit unless told
   otherwise:
   ```bash
   git show --stat HEAD          # file list + insertion/deletion counts
   git show HEAD                 # the actual diff
   ```
   If the change is still uncommitted, use `git diff` (unstaged) /
   `git diff --cached` (staged) instead. If the user names a specific
   commit, range, or branch, diff that instead.

2. **Load the `artifact-design` skill before writing any HTML** — this is
   required by the Artifact tool itself, not optional. It calibrates how
   much visual design the page actually warrants (this is a code diff
   viewer, not a marketing page — keep it functional: monospace, clear
   +/- coloring, minimal chrome).

3. **Build the page.** One HTML file, one `<title>`, containing:
   - A short heading naming the change (e.g. the commit subject).
   - A **rationale section**: 2-4 sentences on *why* this change was made,
     then a brief pros/cons or trade-offs note if relevant — pull this
     from the commit message body and/or the conversation context, don't
     just repeat the diff mechanically.
   - **Per file**, a clearly-separated block showing the diff: removed
     lines with a red-tinted background and a leading `-`, added lines
     with a green-tinted background and a leading `+`, context lines
     plain. Use a monospace font and `overflow-x: auto` per-file (never
     let the page scroll horizontally as a whole — see the Artifact
     tool's responsive-design rules).
   - Respect the Artifact tool's theme rules (light/dark tokens on
     `:root`, no color defined only inside a media query) — diff red/green
     tints need light *and* dark variants that stay legible in both.

4. **Publish it** with the `Artifact` tool (`favicon` required, e.g. 🔀;
   give it a short distinctive title, not a generic one like "Diff").
   Redeploy to the same URL on later calls in the same session by reusing
   the same `file_path`.

5. **Send the user the link and a one-paragraph summary** of what changed
   and why — don't just say "done," name the actual files and the core
   change.

## Notes

- Keep the generated HTML self-contained (inline CSS, no external
  requests) — the Artifact sandbox blocks external resources anyway.
- This is meant to run *after* a change is implemented (and ideally
  committed), as a review aid — not as a substitute for the actual code
  review or for testing.
