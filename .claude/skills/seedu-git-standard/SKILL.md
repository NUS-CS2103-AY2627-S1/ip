---
name: seedu-git-standard
description: The SE-EDU git conventions (https://se-education.org/guides/conventions/git.html) that ALL commits and branches in this project must follow. Use this whenever writing or proposing a commit message, or naming a new branch. Trigger on any request to commit, write a commit message, or create/name a branch, even if the user doesn't name the standard explicitly.
---

# SE-EDU Git Conventions

This is the git standard this project's `AGENTS.md` mandates for every commit and branch. Apply it whenever drafting a commit message or naming a branch — including when just proposing a message for the user to review, not only when actually committing.

## Commit message: subject line

- Imperative mood, as if finishing the sentence "If applied, this commit will ___": `Add README.md`, `Move index.html file to root`, `Update sample data`. Not past tense (`Added README.md`), not continuous (`Adding README.md`).
- Capitalize the first letter.
- No period at the end.
- Aim for 50 characters; 72 is the hard limit — never exceed it.
- An optional scope/category prefix is fine: `Person class: Remove static imports`, `bug fix: Add space after name`, `chore: Update release date`.

## Commit message: body

- Required for any non-trivial commit — a one-line subject alone is only acceptable for genuinely trivial changes.
- Separate the subject from the body with one blank line.
- Wrap body text at 72 characters per line.
- Blank lines separate paragraphs.
- Explain **what** changed and **why** — not **how**; the diff itself already shows how. A reader should be able to understand the reasoning without opening the diff.
- Bullet points are fine for organizing multiple points.
- Don't restate what's already said in inline code comments.
- A natural shape to follow: present situation → why a change is needed → what this commit does about it → why it's done that way → any other relevant context.

## Branch names

- kebab-case, made of a few meaningful keywords: `refactor-ui-tests`.
- If tied to a tracked issue, prefix with the issue number: `1234-ui-freeze-error`.
- (This project also uses a `branch-<Level-N|A-Something>` naming convention for graded increments — that's this project's own layer on top of kebab-case, not a contradiction of it.)

## When proposing a commit message

Base it on what's actually staged/changed (check `git diff`/`git status` — don't guess), follow the subject + body shape above, and give the user the message to review. Per this project's standing rule, never run `git commit` (or any other git command that changes repo state) unless the user explicitly asks you to.
