---
name: git-conventions
description: Compatibility alias for the project-specific seedu-git-standard skill.
---

# Git conventions

Use `.codex/skills/seedu-git-standard/SKILL.md` as the canonical project standard for branches and commits.

Use these rules when proposing, reviewing, or creating a branch name or commit message.

## Commit subject

- Use imperative mood: `Add README.md`, not `Added README.md`.
- Capitalize the first letter.
- Do not end with a period.
- Aim for 50 characters; never exceed 72 characters.
- Add a concise scope or category prefix when useful, such as `Task:`, `bug fix:`, or `chore:`.

## Commit body

Non-trivial commits should include a body separated from the subject by a blank line. Wrap lines at 72 characters and use blank lines between paragraphs or bullets.

Explain **what** changed and **why** it changed. Do not spend the body explaining implementation details that are apparent from the diff. A useful order is:

1. Current situation.
2. Why it needs to change.
3. What the commit does.
4. Why that approach was chosen.
5. Other relevant context.

Keep commits focused; if the explanation becomes too long, consider splitting the change.

## Branch names

Use meaningful kebab-case names containing relevant keywords, for example `refactor-ui-tests`. For issue-related branches, use `<issue-number>-<keywords>`, for example `1234-ui-freeze-error`.

## Safety

Follow the user's authorization for commit and push operations. Do not commit or push merely because this skill is active.
