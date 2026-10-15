# Present Changes Visually

This is a project-specific Claude Code skill, based on
[se-edu/skill-present-changes-visually](https://github.com/se-edu/skill-present-changes-visually).
It generates a self-contained, interactive HTML page that presents changed
files as a GitHub-style side-by-side diff.

## Use

Invoke it from Claude Code:

```
/present-changes-visually
```

Or run the bundled generator directly from the repository root:

```bash
py .claude/skills/present-changes-visually/scripts/generate-split-view-diff.py . HEAD WORKTREE _temp/visual-diff.html
```

The output is a single HTML file. The generator uses only Python's standard
library.

## Repository layout

- `SKILL.md` — instructions for using the Claude Code skill.
- `scripts/generate-split-view-diff.py` — the diff-page generator.
