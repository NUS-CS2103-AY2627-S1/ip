# Present Changes Visually

This repository packages the `present-changes-visually` Codex skill. The skill
generates a self-contained, interactive HTML page that presents changed files
as a GitHub-style side-by-side diff.

## Install

Clone this repository into a project's skill directory:

```bash
git clone <repository-url> .codex/skills/present-changes-visually
```

Codex can then discover the skill from `SKILL.md`.

## Use

Run the bundled generator from the target Git repository's root:

```bash
python3 .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py \
  . HEAD WORKTREE _temp/visual-diff.html
```

The output is a single HTML file. The generator uses only Python's standard
library.

## Repository layout

- `SKILL.md` — instructions for using the Codex skill.
- `agents/openai.yaml` — display metadata and the default prompt.
- `scripts/generate-split-view-diff.py` — the diff-page generator.
