---
name: seedu-git-standard
description: Git commit message and branch naming standard for this project (SE-EDU conventions). Use whenever drafting or proposing a commit message, or naming a new branch, in this repository.
version: 1.0.0
user-invocable: true
---

Applies the SE-EDU Git conventions
(https://se-education.org/guides/conventions/git.html) to every commit and
branch in this project.

## Commit subject line

- Imperative mood: "Add README.md", not "Added README.md" or "Adding
  README.md".
- Capitalize the first letter: "Move index.html file to root", not
  "move index.html file to root".
- No period at the end: "Update sample data", not "Update sample data.".
- Aim for 50 characters; 72 characters is the hard limit.
- An optional scope/category prefix is allowed, e.g. "Person class:
  Remove static imports", "bug fix: Add space after name", "chore:
  Update release date".

## Commit body

- Required for any non-trivial commit; optional for a trivial one-liner
  where the subject already says everything.
- Separate the subject from the body with one blank line.
- Wrap body text at 72 characters; use blank lines between paragraphs;
  bullet points are fine when they aid clarity.
- Explain WHAT changed and WHY — never HOW; the diff already shows HOW.
- Useful structure: the situation before the change, why a change was
  needed, what this commit does (imperative mood, like the subject), and
  why it's done this way, if not obvious.
- Avoid "currently"/"originally" when describing the present state; use
  "Let's ..." to introduce the change itself if natural.

## Branch names

- kebab-case, made of meaningful keywords: `refactor-ui-tests`.
- For an issue-linked branch: `issueNumber-some-keywords-from-issue-title`,
  e.g. `1234-ui-freeze-error`.
- This project layers its own convention on top for course increments:
  `branch-<Increment>` (e.g. `branch-Level-9`, `branch-A-JavaDoc`) — keep
  using that pattern for level/extension branches specifically.

## Applying this in this project

- Before creating any commit, draft the subject and (if non-trivial) body
  against this checklist, and self-correct before running `git commit`.
- When asked to "propose a commit message," present it in a fenced code
  block so it's easy to copy, and wait for confirmation before actually
  committing.
- Tags in this project follow the existing pattern of lightweight tags
  named after the increment (e.g. `Level-9`, `A-JavaDoc`) unless the user
  asks for an annotated tag — this is already stated in AGENTS.md and
  still applies.
