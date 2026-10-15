---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when preparing commits and branch names in this project.
---

# Seedu Git Standard

Use this skill whenever you create, review, amend, or propose a commit or branch in this repository.
It is based on the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subject

- Write a clear subject for every commit.
- Aim for 50 characters or fewer; never exceed 72 characters.
- Use imperative mood, capitalize the first letter, and do not end with a period.
- Add a concise scope or category prefix when it improves clarity, such as `Parser:` or `bug fix:`.
- Keep the subject focused on the main change. If a change is too broad for one clear subject and
  body, split it into smaller commits when the user has authorized committing.

## Commit body

- Add a body for every non-trivial commit, separated from the subject by one blank line.
- Wrap body lines at 72 characters and use blank lines between paragraphs. Use bullet points when
  they make several related changes easier to scan.
- Explain what changed and why it was needed; leave implementation mechanics to the diff.
- Structure the explanation in this order when useful: current situation in present tense, why it
  needs to change, what to do about it in imperative mood, why that approach was chosen, and any
  relevant additional information.
- Avoid redundant wording such as `currently` or `originally`, and avoid repeating code comments.

## Branch names

- Use meaningful kebab-case names containing relevant keywords, such as `refactor-ui-tests`.
- For issue-related branches, use `<issue-number>-<keywords-from-issue-title>`, such as
  `1234-ui-freeze-error`.

Before committing, check subject length, imperative mood, capitalization, terminal punctuation,
body wrapping, and whether the message explains what and why. Do not commit or push unless the user
explicitly authorizes it.
