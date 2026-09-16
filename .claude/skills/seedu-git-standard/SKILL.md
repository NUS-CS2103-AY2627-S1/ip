---
name: seedu-git-standard
description: Git commit message and branch naming standard for this project (SE-EDU conventions -- https://se-education.org/guides/conventions/git.html). Use whenever writing or proposing a commit message, or naming a new branch, in this repository.
---

# SE-EDU Git Convention

Reference checklist for commit messages and branch names in this project,
based on https://se-education.org/guides/conventions/git.html. Apply this
whenever drafting a commit message or creating a branch.

## Commit subject line

- Every commit needs a well-written subject line: soft limit 50 characters,
  hard limit 72. Many tools truncate beyond that.
- Imperative mood: "Add README.md", not "Added README.md" or "Adding
  README.md".
- Capitalize the first word: "Move index.html to root", not "move
  index.html to root".
- No trailing period: "Update sample data", not "Update sample data.".
- An optional scope/category prefix is fine when it adds clarity, e.g.
  `Person class: Remove static imports`, `bug fix: Add space after name`.

## Commit body

- For any non-trivial commit, include a body.
- Blank line between subject and body.
- Wrap body lines at 72 characters.
- Blank line between paragraphs; bullet points are fine and often clearer
  than prose for a list of changes.
- Explain **what** changed and **why** -- not a line-by-line narration of
  **how** (the diff already shows that). A reader should be able to judge
  whether the change is reasonable without opening the diff.
- If the body is getting long enough that it's hard to summarize, that's a
  sign the commit should be split into smaller commits instead.
- Don't repeat what's already said in code comments.
- A useful shape for the body, when applicable:
  1. The situation before this change (present tense, don't say
     "currently" or "originally").
  2. Why a change is needed.
  3. What this commit does about it (imperative mood, matching the
     subject).
  4. Why it's done that way, if a design choice needs justifying.
  5. Anything else worth knowing (follow-ups, things deliberately left
     out, etc.).

## Branch names

- kebab-case, meaningful keywords: `refactor-ui-tests`.
- If tied to an issue: `issueNumber-some-keywords-from-issue-title`, e.g.
  `1234-ui-freeze-error`.
- This project's own convention (used throughout its history) is
  `branch-<Increment-Id>` for increment work, e.g. `branch-Level-9`,
  `branch-A-MoreOOP` -- keep using that pattern for increment branches;
  apply the kebab-case/issue-number pattern above for anything else (bug
  fixes, unplanned refactors, etc.).

## One change, one commit

- Keep unrelated changes in separate commits: e.g. a source-code change
  and an unrelated documentation/config change belong in two commits, not
  one, even if made in the same sitting.
