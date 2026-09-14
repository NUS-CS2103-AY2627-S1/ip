---
name: seedu-git-standard
description: Review and compose Git commit messages and branch names for this project using the SE-EDU Git conventions. Use whenever preparing, reviewing, or suggesting a commit or branch name.
---

# SE-EDU Git Standard

Apply the SE-EDU Git conventions at https://se-education.org/guides/conventions/git.html to all future commits and branches in this project.

## Commit subject

- Write a well-formed subject for every commit.
- Use imperative mood, such as `Add README.md`, not past or gerund tense.
- Capitalize the first letter.
- Do not end the subject with a period.
- Prefer 50 characters or fewer; never exceed 72 characters.
- Add a relevant scope or category prefix only when it improves clarity.

## Commit body

- Add a body for every non-trivial commit.
- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters.
- Explain WHAT changed and WHY it changed, not HOW the diff implements it.
- Use present tense for the current situation and imperative mood for the proposed change.
- Use paragraphs or bullet points when they improve readability.
- Structure the body around the current situation, reason for change, requested change, and rationale.

## Branch names

- Use meaningful kebab-case names containing relevant keywords, such as `refactor-ui-tests`.
- For issue-related work, use `issueNumber-keywords-from-issue-title`.

## Workflow

1. Inspect the staged or intended scope before drafting a message.
2. Summarize the WHAT and WHY without duplicating implementation comments.
3. Check subject length, imperative mood, capitalization, punctuation, and body wrapping.
4. Do not commit, amend, tag, or push unless explicitly requested.
