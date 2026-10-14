---
name: seedu-git-standard
description: The project's mandatory Git conventions, based on the SE-EDU Git conventions (https://se-education.org/guides/conventions/git.html). Load this before drafting or creating any commit message, and before naming any new branch.
---

# seedu-git-standard

This project's commits and branch names must follow the SE-EDU Git conventions
(https://se-education.org/guides/conventions/git.html). Check every commit message — and every new branch
name — against the rules below before proposing or creating it.

## Commit message: subject line

- Every commit has a well-written subject line: aim for 50 characters, hard limit 72.
- Imperative mood: `Add README.md`, not `Added README.md` or `Adding README.md`.
- Capitalize the first letter: `Move index.html file to root`, not `move index.html file to root`.
- No trailing period: `Update sample data`, not `Update sample data.`.
- An optional `<scope>:`/`<category>:` prefix is fine when it helps: `Person class: Remove static
  imports`, `bug fix: Add space after name`, `chore: Update release date`.

## Commit message: body

Non-trivial commits get a body:

- A blank line separates subject from body.
- Wrap body text at 72 characters.
- Blank lines separate paragraphs; bullet points are fine where they help.
- Explain **WHAT** and **WHY**, not **HOW** — the diff already shows how; the body should let a reader
  judge whether the change is a good idea without reading the diff first. If the explanation is getting
  long, that's a sign the commit should be split into smaller ones.
- Don't repeat what's already said in code comments added by the same commit.
- Structure (present tense throughout, except step 3):
  1. Current situation.
  2. Why it needs to change.
  3. What is being done about it — imperative mood; "Let's" can mark the start of this part.
  4. Why it's being done that way.
  5. Any other relevant info.
- Avoid "currently"/"originally" when describing the existing situation — it's implied by tense.

## Commit granularity

- One commit per logically-independent, standalone change. Don't bundle unrelated changes (e.g. a
  feature change and an unrelated docs/config change) into a single commit just because they happened in
  the same session.

## Branch names

- Meaningful, kebab-case, with relevant keywords: `refactor-ui-tests`.
- For an issue-related branch: `issueNumber-some-keywords-from-issue-title`, e.g. `1234-ui-freeze-error`.

## References

- Source: https://se-education.org/guides/conventions/git.html
