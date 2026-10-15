# Present Changes Visually

Project-scoped skill that generates a self-contained, interactive HTML page
presenting changed files as a GitHub-style side-by-side diff.

Adapted for Claude Code from the SE-EDU skill
<https://github.com/se-edu/skill-present-changes-visually> (originally packaged
for Codex). The generator script is unmodified; only the `SKILL.md` paths and
the commit-message note were adjusted for this repository. The upstream
`agents/openai.yaml` (Codex display metadata) is not included because Claude
Code does not use it.

## Use

Run the bundled generator from the repository root:

```bash
python3 .claude/skills/present-changes-visually/scripts/generate-split-view-diff.py \
  . HEAD WORKTREE _temp/visual-diff.html
```

The output is a single HTML file written under `_temp/` (git-ignored). Only
Python's standard library is required.

Comparison points can be any Git commit-ish (`HEAD~1`, a tag, a branch, a SHA)
or the literal word `WORKTREE` for the current working files.

## Layout

- `SKILL.md` — instructions Claude follows when the skill is invoked.
- `scripts/generate-split-view-diff.py` — the diff-page generator.
