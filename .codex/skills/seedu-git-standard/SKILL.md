---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to commits and branches in this project.
metadata:
  short-description: Enforce the project Git standard
---

# SE-EDU Git standard

Use this skill whenever creating, proposing, or reviewing a commit or branch
in this project. The canonical reference is the [SE-EDU Git
conventions](https://se-education.org/guides/conventions/git.html).

## Commit subjects

- Write every subject in the imperative mood, with the first letter
  capitalized and no period at the end.
- Target 50 characters and never exceed 72 characters.
- Add a meaningful scope or category prefix when useful, such as
  `Parser: Handle empty input` or `chore: Update dependencies`.

## Commit bodies

- Give every non-trivial commit a body separated from the subject by one blank
  line.
- Wrap body lines at 72 characters and use blank lines between paragraphs.
- Explain what changed and why it changed; do not use the body to narrate how
  the diff was implemented.
- Describe the situation, the reason for change, the proposed change, and its
  rationale in present tense and imperative language where appropriate. Use
  bullets when they improve clarity and avoid repeating code comments.

## Branch names

- Use meaningful kebab-case names containing relevant keywords, such as
  `refactor-ui-tests`.
- For issue-related branches, use `<issue-number>-<issue-keywords>`.
- Preserve any repository or host-required branch prefix while keeping the
  descriptive portion in kebab case.

## Applying the standard

Before committing, review the subject length, mood, capitalization, ending,
body separation, body wrapping, and what/why explanation. Do not create,
amend, or push commits unless the user explicitly authorizes that action.
