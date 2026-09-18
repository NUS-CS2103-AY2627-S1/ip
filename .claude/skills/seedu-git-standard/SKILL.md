---
name: seedu-git-standard
description: SE-EDU git conventions (commit message format, branch naming). Use whenever drafting a commit message or a branch name in this project.
---

# SE-EDU Git Conventions

Source: https://se-education.org/guides/conventions/git.html

## Commit message: subject line

- Imperative mood: "Add find command", not "Added find command".
- Capitalize the first letter; no period at the end.
- Aim for 50 characters, hard limit 72.
- An optional scope/category prefix is fine, e.g. `Parser:` or `bug fix:`.

## Commit message: body

- Give non-trivial commits a body; separate it from the subject with one
  blank line.
- Wrap body text at 72 characters; use blank lines between paragraphs and
  bullet points where they help.
- Explain WHAT changed and WHY, not HOW (the diff already shows how) — enough
  detail that a reader can judge the change's merit without opening the diff.
- Structure: current situation → why a change is needed → what is being
  done → why done that way → anything else relevant.
- Prefer phrasing like "Let's ..." for the change being made, and avoid
  "currently"/"originally" when describing the present state.
- Don't just restate what's already in code comments.

## Branch names

- kebab-case, with meaningful keywords, e.g. `refactor-ui-tests`.
- For an issue-linked branch: `issueNumber-keywords-from-title`, e.g.
  `1234-ui-freeze-error`.

## Commit granularity

- One logical change per commit. When several unrelated things changed at
  once (e.g. application code plus separate documentation/process files),
  split them into separate commits rather than bundling them.
