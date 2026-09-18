# Present Changes Visually

This project-local skill generates a self-contained, interactive HTML page that
presents changed files as a GitHub-style side-by-side diff.

## Use

From `D:\ip`, run the bundled generator:

```powershell
python .codex/skills/present-changes-visually/scripts/generate-split-view-diff.py `
  . HEAD WORKTREE _temp/visual-diff.html
```

Use `py` instead of `python` if needed. The output is a single HTML file, and
the generator uses only Python's standard library.

## Repository layout

- `SKILL.md` — instructions for using the Codex skill.
- `agents/openai.yaml` — display metadata and the default prompt.
- `scripts/generate-split-view-diff.py` — the diff-page generator.
