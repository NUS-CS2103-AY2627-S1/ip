---
name: seedu-git-standard
description: >-
  Apply the SE-EDU Git conventions from
  https://se-education.org/guides/conventions/git.html when writing commit
  messages, proposing commits, naming branches, or creating commits in this
  project. Use for every future commit and every commit-message suggestion.
---

# SE-EDU Git conventions

**Mandatory** for every commit and commit-message suggestion in this project.
Source of truth: https://se-education.org/guides/conventions/git.html

Still do not commit or push unless the user explicitly asks.

## Commit subject (every commit)

- Limit to 50 characters when possible; hard limit 72
- Imperative mood: `Add README.md`, not `Added` / `Adding`
- Capitalize the first letter
- No trailing period
- Optional prefix: `Person class: Remove static imports`, `bug fix: Add space after name`, `chore: Update release date`

## Commit body (non-trivial commits)

- Blank line after the subject
- Wrap at 72 characters
- Blank lines between paragraphs; bullets when they help
- Explain **what** and **why**, not **how** (the diff shows how)
- Enough detail that a reviewer can judge the change without reading the diff
- Do not restate the same-commit code comments

Body structure:

```
{current situation}          -- present tense; avoid "currently"/"originally"
{why it needs to change}
{what is being done}         -- imperative mood; "Let's" may start this part
{why it is done that way}
{any other relevant info}
```

Example:

```
Find command: make matching case-insensitive

Find command is case-sensitive.

A case-insensitive find is more user-friendly because users cannot be
expected to remember the exact case of the keywords.

Let's,
* update the search algorithm to use case-insensitive matching
* add a script to migrate stress tests to the new format
```

## Branch names

- Kebab-case keywords: `refactor-ui-tests`
- Issue-related: `1234-ui-freeze-error`

## This project's extra Git rules

- Lightweight tags unless the user asks for an annotated tag
- Do not commit or push unless explicitly asked
