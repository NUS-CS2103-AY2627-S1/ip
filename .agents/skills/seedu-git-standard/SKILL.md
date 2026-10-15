---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to every commit made in this project — commit message format and branch naming. Use whenever proposing or creating a commit message, or naming a new branch.
---

# SE-EDU Git standard

Source: https://se-education.org/guides/conventions/git.html

Apply these rules to every commit in this repository.

## Commit message: subject line

- Imperative mood: "Add README.md", not "Added README.md".
- Capitalize the first letter.
- No trailing period.
- Aim for ≤50 characters; hard limit 72.
- An optional scope/category prefix is fine (`Person class:`, `bug fix:`).

## Commit message: body

- Separate the subject from the body with one blank line.
- Wrap body text at 72 characters.
- Blank line between paragraphs; bullet points where they help.
- Explain **what** changed and **why** — not **how**, since the diff already shows that.
- Present tense for the current situation, imperative mood for the change being made ("Let's add X" rather than "Adding X" or "This adds X").
- Avoid "currently"/"originally" — describe state as of this commit, not as a narrative of the project's history.
- Keep it proportionate: this project's own convention (see `AGENTS.md`) is to keep bodies short — 1–3 sentences is usually enough. Only expand into the full current-situation/why/what/rationale structure if the user asks for more detail on a specific commit.

## Branch names

- kebab-case keywords describing the change, e.g. `refactor-ui-tests`.
- For an issue-linked branch: `issueNumber-keywords-from-title`, e.g. `1234-ui-freeze-error`.

## Applying this in the project

1. Follow this format for every commit message you propose or create.
2. Never commit or push without being explicitly asked to, per `AGENTS.md`.
3. Prefer several small, atomic commits over one large commit when the changes are logically separable (e.g. source code vs. test-plan updates vs. documentation).
