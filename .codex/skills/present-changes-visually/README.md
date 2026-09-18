# Present Changes Visually

This project-local package provides the `present-changes-visually` Codex skill.
It generates a self-contained, interactive HTML page that presents changed
files as a GitHub-style side-by-side diff.

The implementation is based on [se-edu/skill-present-changes-visually](https://github.com/se-edu/skill-present-changes-visually).

## Install

The skill is already installed for this project under
`.codex/skills/present-changes-visually`; Codex can discover it from `SKILL.md`.

## Use

Run the bundled generator from the target Git repository's root:

```bash
python .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py \
  . HEAD WORKTREE _temp/visual-diff.html
```

Use `python3` instead of `python` when that is the available command.

The output is a single HTML file. The generator uses only Python's standard
library.

## Repository layout

- `SKILL.md` — instructions for using the Codex skill.
- `agents/openai.yaml` — display metadata and the default prompt.
- `scripts/generate-split-view-diff.py` — the diff-page generator.
