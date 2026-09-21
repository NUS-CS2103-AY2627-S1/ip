---
name: seedu-git-standard
description: The SE-EDU Git conventions that all commits and branch names in this project must follow. Use when writing a commit message, proposing one, or naming a branch.
---

# SE-EDU Git conventions

Source: https://se-education.org/guides/conventions/git.html

## Subject line

- Limit to 50 characters; 72 is the hard limit.
- Use the imperative mood: "Add README.md", not "Added README.md".
- Capitalize the first letter.
- Do not end with a period.
- A `<scope>:` prefix is optional: `Person class: Remove static imports`.

## Body

- Separate the subject from the body with a blank line.
- Wrap at 72 characters.
- Separate paragraphs with blank lines; use bullet points where they help.
- Explain **what** and **why**, not how. The diff already shows how.
- Do not repeat what code comments already say.
- Structure it as: the present situation (present tense), why it needs to
  change, what this commit does (imperative), why this approach, then
  anything else worth knowing.
- Do not write "currently" or "originally" when describing the present
  situation - present tense already says it.
- "Let's" is the idiomatic way to open the description of the change.

## Branch names

- Meaningful, with the relevant keywords.
- kebab-case: `refactor-ui-tests`.
- For an issue: `issueNumber-some-keywords-from-issue-title`, e.g.
  `1234-ui-freeze-error`.

## In this repository

Increment branches keep the course's own naming (`branch-A-JavaDoc`,
`branch-Level-9`), because the course materials refer to them by those names.
Tags are lightweight and named for the increment: `A-JavaDoc`, `Level-9`.
