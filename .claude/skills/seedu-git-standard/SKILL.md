---
name: seedu-git-standard
description: The se-edu Git conventions for this project - commit message and branch naming rules. Use whenever writing or proposing a commit message, or naming a new branch, for this repository.
---

# se-edu Git Conventions

Source: https://se-education.org/guides/conventions/git.html

## Commit message: subject line

- Imperative mood, not past tense: `Add README.md`, not `Added README.md`
  or `Adding README.md`.
- Capitalize the first letter: `Move index.html file to root`, not
  `move index.html file to root`.
- No terminal period: `Update sample data`, not `Update sample data.`
- Soft limit 50 characters, hard limit 72.
- An optional scope prefix is fine when it adds context, e.g.
  `Person class: Remove static imports`, `bug fix: Add space after name`.

## Commit message: body

Only for substantive changes - a trivial commit can be subject-only.

- Blank line between subject and body.
- Wrap body text at 72 characters; blank line between paragraphs; bullet
  points where they help.
- Explain WHAT and WHY, not HOW - don't restate the diff, and don't
  duplicate what's already said in inline code comments.
- Suggested shape:
  ```
  {current situation} -- present tense

  {why it needs to change}

  {what is being done about it} -- imperative mood

  {why it is done that way}

  {any other relevant info}
  ```
- If the explanation is getting long, that's a sign to split into
  smaller, more focused commits instead.

## Branch names

- kebab-case, meaningful keywords: `refactor-ui-tests`, not `fix` or
  `update`.
- Issue-related branches: `issueNumber-keywords-from-title`, e.g.
  `1234-ui-freeze-error`.
