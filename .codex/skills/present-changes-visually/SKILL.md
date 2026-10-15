---
name: present-changes-visually
description: Generate a self-contained, GitHub-style split-view HTML page for reviewing changes in this Java project.
---

# Present Changes Visually

Use this skill when the user asks to show, review, compare, or share the project’s code changes visually.

## Generate the page

1. Treat this repository as the target unless the user identifies another repository.
2. Compare \`HEAD\` with \`WORKTREE\` by default. \`WORKTREE\` includes staged, unstaged, and untracked files, but excludes ignored files.
3. Write the result to \`_temp/visual-diff.html\` unless the user supplies another output path.
4. From the repository root, run:

   \`\`\`bash
   python3 .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py \
     . HEAD WORKTREE _temp/visual-diff.html
   \`\`\`

   Replace the revisions or output path when the user requests a different comparison.
5. Confirm that the output file exists and report its absolute path. Do not open a browser unless the user asks.

The generated page is self-contained apart from optional syntax-highlighting resources loaded by the browser. The bundled generator uses only Python's standard library.

## Visual review

When the user explicitly asks to inspect the rendered result, open the generated HTML in a browser or use an available page-inspection tool. Otherwise, report the generated file without opening it.

## Resource

The generator is in [scripts/generate-split-view-diff.py](scripts/generate-split-view-diff.py).
