# Present Changes Visually

This directory packages the `present-changes-visually` skill for Claude Code,
adapted from the [se-edu/skill-present-changes-visually](https://github.com/se-edu/skill-present-changes-visually)
Codex skill. The skill generates a self-contained, interactive HTML page that
presents changed files as a GitHub-style side-by-side diff.

This copy is scoped to this project: it lives at `.claude/skills/present-changes-visually`
and is discovered by Claude Code from its `SKILL.md`.

## Use

Run the bundled generator from the repository root:

```bash
python3 .claude/skills/present-changes-visually/scripts/generate-split-view-diff.py \
  . HEAD WORKTREE _temp/visual-diff.html
```

The output is a single HTML file. The generator uses only Python's standard
library — nothing to install.

## Repository layout

- `SKILL.md` — instructions for using the skill.
- `scripts/generate-split-view-diff.py` — the diff-page generator.

## Differences from the upstream Codex skill

- Paths adapted from `.codex/skills/...` to `.claude/skills/...`.
- Dropped `agents/openai.yaml`, which held Codex/OpenAI-specific display
  metadata not used by Claude Code.
- Dropped the `craft-commit-message` skill reference in `SKILL.md`, since
  that companion skill isn't part of this installation.
