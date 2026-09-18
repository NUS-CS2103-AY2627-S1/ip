---
name: seedu-git-standard
description: The SE-EDU Git conventions (commit messages, branch names) this project's commits must follow. Load before proposing or creating any commit, or naming a branch, in this repository.
---

# SE-EDU Git Conventions

Source: https://se-education.org/guides/conventions/git.html

This is a **project-specific standard**: every commit made in this
repository from now on must follow it. When proposing a commit message
or naming a branch, check it against every rule below.

## Commit message subject

- Try to limit the subject to 50 characters; hard limit 72.
- **Imperative mood**: `Add README.md`, not `Added README.md` or
  `Adding README.md`.
- Capitalize the first letter: `Move index.html to root`, not
  `move index.html to root`.
- No period at the end: `Update sample data`, not `Update sample data.`.
- An optional `<scope>:`/`<category>:` prefix is fine when it helps, e.g.
  `Parser: Add find-command support`, `bug fix: Add space after name`.

## Commit message body

Non-trivial commits need a body:

- Blank line between subject and body.
- Wrap the body at 72 characters.
- Blank lines between paragraphs; bullet points where a list reads
  better than prose.
- **Explain WHAT and WHY, not HOW** — the diff already shows how; a body
  that's getting long is often a sign the commit should be split up.
  Avoid restating what's already clear from code comments.
- A useful shape to aim for (skip parts that don't apply):
  ```
  {the current situation, in present tense}

  {why it needs to change}

  {what this commit does about it, in imperative mood - "Let's ..."}

  {why it's done that way, if not obvious}

  {any other relevant info, e.g. a reference link}
  ```
  Avoid words like "currently"/"originally" when describing the current
  situation — present tense already implies that.

Example:
```
Find command: make matching case-insensitive

Find command is case-sensitive.

A case-insensitive find is more user-friendly because users cannot be
expected to remember the exact case of the keywords.

Let's,
* update the search algorithm to use case-insensitive matching
```

## Branch names

- kebab-case, with meaningful keywords (e.g. `refactor-ui-tests`).
- For an issue-linked branch: `issueNumber-some-keywords-from-title`
  (e.g. `1234-ui-freeze-error`).
- This project's own increment branches additionally follow
  `branch-Level-N` / `branch-A-Name` per its established convention —
  keep using that pattern for increment branches specifically; the
  kebab-case guidance above governs everything else.
