---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing or creating commit messages or branch names in this project.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever proposing, reviewing,
or creating commit messages or branch names for this project. Do not commit, push, tag, or create a branch without the
authorization required by `AGENTS.md` and the user's request.

## Commit subject

- Write a meaningful subject in the imperative mood.
- Capitalize the first word when the subject has no optional scope or category prefix.
- Do not end the subject with a period.
- Aim for 50 characters or fewer; 72 characters is the hard limit.
- An optional `<scope>:` or `<category>:` prefix may clarify the affected area. Keep the action after the prefix imperative,
  for example `Parser: Reject missing task numbers`.

## Commit body

Add a body for every non-trivial commit.

- Separate the body from the subject with one blank line.
- Wrap body lines at 72 characters and use blank lines between paragraphs.
- Explain what the change accomplishes and why it is needed; leave implementation details to the diff.
- Describe the existing situation in the present tense and the change in the imperative mood.
- Avoid redundant qualifiers such as `currently` and `originally`.
- Use bullets when they make multiple points clearer.
- Split the commit when a clear what-and-why explanation becomes too long or covers unrelated reasons.

Before committing, inspect the exact staged diff and ensure the message describes only that diff. After committing, inspect
the resulting commit subject and body to confirm the limits and separation were preserved.

## Branch names

- Use a meaningful kebab-case name, such as `refactor-ui-tests`.
- For work tied to an issue, use `issueNumber-keywords-from-title`, such as `1234-ui-freeze-error`.
