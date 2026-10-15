---
name: seedu-git-standard
description: The SE-EDU Git conventions this project follows for commit messages and branch names. Use whenever proposing or creating a commit message, or naming a new branch, in this repo.
---

# SE-EDU Git Conventions

Full reference: https://se-education.org/guides/conventions/git.html

## Commit message subject line

- Target 50 characters, hard limit 72.
- Imperative mood: "Add save/load support", not "Added save/load support".
- Capitalize the first letter.
- No trailing period.
- An optional scope/category prefix is fine, e.g. `Storage: Fix null check`.

## Commit message body

- Blank line between subject and body.
- Wrap body text at 72 characters.
- Blank line between paragraphs; bullet points where useful.
- Explain **what** and **why**, not **how** — the diff already shows how;
  don't just restate the code in prose.
- A good body works through, roughly: what the situation was, why a change
  was needed, what this commit does about it, why it's done this way, and
  anything else a reviewer needs to evaluate the change without reading the
  diff first.

## Branch names

- kebab-case, meaningful keywords: `refactor-ui-tests`.
- For an issue-linked branch: `issueNumber-keywords-from-title`, e.g.
  `1234-ui-freeze-error`.
- This project's increment branches instead follow the course's own
  `branch-<Increment>` convention (e.g. `branch-Level-9`,
  `branch-A-JavaDoc`) — keep using that pattern for increment branches
  specifically; apply the kebab-case convention above for any other
  branch (bugfixes, experiments, etc.).

## Applying this

Follow the subject/body rules above for every commit in this repo, not
just ones done through an AI session.
