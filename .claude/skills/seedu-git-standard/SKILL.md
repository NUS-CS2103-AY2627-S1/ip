---
name: seedu-git-standard
description: The SE-EDU Git commit message and branch naming convention for this project. Use whenever drafting a commit message or naming a branch in this repo, condensed from https://se-education.org/guides/conventions/git.html.
---

# seedu-git-standard

All commits and branches in this project should follow this convention.

## Commit message: subject line

- Aim for ~50 characters; 72 characters is the hard maximum.
- Imperative mood: "Add", "Fix", "Extract" — not "Added"/"Adding"/"Fixed".
- Capitalize the first letter.
- No period at the end.
- An optional `<scope>:`/`<category>:` prefix is fine (e.g. `Parser:`,
  `bug fix:`) when it adds useful context.

## Commit message: body

- One blank line between the subject and the body.
- Wrap body lines at 72 characters.
- Blank line between paragraphs; bullet points where they help.
- Explain **what** changed and **why** — not **how** (the diff already
  shows how; don't restate it as prose).
- Useful structure to reach for (not every commit needs every part):
  1. The situation as it stands (present tense).
  2. Why a change is needed.
  3. What is being done about it (imperative mood).
  4. Why it's being done that way, if non-obvious.
  5. Anything else worth flagging (follow-ups, caveats).
- Avoid "currently"/"originally" when describing prior state; it's
  usually clear from context that the body is describing the past.

## Branch names

- Kebab-case with meaningful keywords (e.g. `refactor-ui-tests`).
- For an issue-linked branch: `issueNumber-keywords-from-title` (e.g.
  `1234-ui-freeze-error`).
- This project's course-increment branches follow their own established
  pattern instead (`branch-Level-N`, `branch-A-<Name>`) — keep using
  that pattern for increment branches; use kebab-case for anything else.

## When something isn't covered

Match the style already used in this repo's recent commit history
rather than introducing a new convention.
