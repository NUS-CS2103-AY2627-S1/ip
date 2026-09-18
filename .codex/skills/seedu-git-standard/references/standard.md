# Project Git standard

This checklist paraphrases the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subject

- Write a meaningful subject for every commit.
- Aim for 50 characters and never exceed 72.
- Use imperative mood, capitalize the first letter, and omit the final period.
- Add an optional `scope:` or `category:` prefix only when it improves clarity.

## Commit body

- Add a body for every non-trivial commit. Separate it from the subject with one blank line.
- Wrap body text at 72 characters and separate paragraphs with blank lines. Use bullets when they communicate the change more clearly.
- Explain what changed and why it was needed or chosen. Leave implementation mechanics to the diff and avoid repeating code comments.
- Describe the existing situation in present tense, the reason for change, the action in imperative mood, the rationale for that approach, and other relevant context.
- Avoid redundant time words such as “currently” and “originally”. If the explanation becomes unwieldy, split the work into smaller cohesive commits.

## Branch names

- Use meaningful kebab-case keywords, such as `refactor-ui-tests`.
- For issue work, begin with the issue number, such as `1234-ui-freeze-error`.
