---
name: seedu-git-standard
description: >-
  Apply the SE-EDU Git conventions when proposing or creating commits and branch
  names in this project.
metadata:
  short-description: Follow SE-EDU Git style
---

# SE-EDU Git Standard

Use this skill whenever proposing, drafting, reviewing, or creating commit
messages or branch names in this project. Follow the SE-EDU Git conventions
from:

https://se-education.org/guides/conventions/git.html

## Commit Subject

- Write a well-written subject line for every commit.
- Try to keep the subject at or below 50 characters.
- Never exceed 72 characters in the subject.
- Use imperative mood, for example `Add README.md`.
- Capitalize the first letter of the subject.
- Do not end the subject with a period.
- Add a `<scope>:` or `<category>:` prefix when it makes the subject clearer.

## Commit Body

- Add a body for non-trivial commits.
- Separate the subject from the body with one blank line.
- Wrap body lines at 72 characters.
- Use blank lines to separate body paragraphs.
- Explain what changed and why it changed; leave implementation details to the
  diff unless they affect the rationale.
- Include enough detail for a reviewer to judge the commit without reading the
  full diff.
- Avoid repeating information already obvious from code comments in the same
  commit.
- Split the work into smaller commits if the body needs to become too long.
- Use bullets when they communicate the rationale more clearly than paragraphs.

For larger bodies, prefer this structure:

1. Describe the existing situation in present tense.
2. Explain why it needs to change.
3. Describe what is being changed, using imperative mood where appropriate.
4. Explain why this approach is used.
5. Add any other relevant information.

## Branch Names

- Use meaningful branch names made from relevant keywords.
- Use kebab case, for example `refactor-ui-tests`.
- For issue-related branches, use `issueNumber-some-keywords-from-issue-title`,
  for example `1234-ui-freeze-error`.
