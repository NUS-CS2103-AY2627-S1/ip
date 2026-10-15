---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches or proposing, reviewing, or creating commits in this project.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever naming a branch or proposing, reviewing, or creating a commit in this project.

## Commit subjects

- Write a specific, meaningful subject for every commit.
- Use the imperative mood, capitalize the first letter, and do not end with a period.
- Aim for no more than 50 characters and never exceed 72 characters.
- Add a relevant `<scope>:` or `<category>:` prefix only when it improves clarity.

## Commit bodies

- Add a body for non-trivial commits. A subject alone is sufficient for a genuinely simple change.
- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters and use blank lines between paragraphs. Use bullet points when they improve readability.
- Explain what changed and why it was needed or designed that way. Leave implementation mechanics to the diff.
- Describe the pre-change situation in the present tense and describe the action taken in the imperative mood.
- Include enough context for a reviewer to judge the change without first reading the diff, while avoiding repetition of code comments.

## Branch names

- Use a meaningful kebab-case name made from relevant keywords, such as `refactor-ui-tests`.
- For work tied to an issue, start with the issue number, such as `1234-ui-freeze-error`.

## Project safeguards

- Inspect the actual diff or staged changes before proposing a message so the message matches the commit scope.
- Use lightweight tags unless the user requests an annotated tag.
- Never rewrite, amend, or rebase past commits merely to make their messages comply; rewriting history can affect the course's progress tracking.
- Do not commit or push unless the user explicitly asks.
