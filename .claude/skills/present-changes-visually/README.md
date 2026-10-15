# Present Changes Visually

This directory packages the `present-changes-visually` skill for this project.
Adapted from the upstream [se-edu/skill-present-changes-visually](https://github.com/se-edu/skill-present-changes-visually)
repository (originally a Codex skill) for use as a Claude Code project skill.
The skill generates a self-contained, interactive HTML page that presents
changed files as a GitHub-style side-by-side diff.

## Install

This skill lives at `.claude/skills/present-changes-visually` in this
project. Claude Code discovers it automatically from `SKILL.md`.

## Use

Run the bundled generator from the repository root:

```bash
python3 .claude/skills/present-changes-visually/scripts/generate-split-view-diff.py \
  . HEAD WORKTREE _temp/visual-diff.html
```

The output is a single HTML file. The generator uses only Python's standard
library.

## Repository layout

- `SKILL.md` — instructions for using the skill.
- `agents/openai.yaml` — display metadata and default prompt, kept for
  compatibility with the upstream Codex skill.
- `scripts/generate-split-view-diff.py` — the diff-page generator.
