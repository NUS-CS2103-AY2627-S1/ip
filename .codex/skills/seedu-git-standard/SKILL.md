---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing or creating commits and when naming branches in this project.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
for commits and branch names in this project.

## Commit subject

- Write a clear subject for every commit.
- Use imperative mood, as if completing “This commit will ...”.
- Capitalize the first letter and do not end with a period.
- Aim for at most 50 characters; never exceed 72 characters.
- Add an optional `<scope>:` or `<category>:` prefix only when it improves
  clarity.

## Commit body

Add a body for every non-trivial commit. Separate it from the subject with one
blank line, wrap it at 72 characters, and use blank lines between paragraphs.
Use bullet points where they make the explanation clearer.

Explain what changed and why it was needed or designed that way. Leave
implementation mechanics to the diff. Give enough context for a reviewer to
judge the change without reading the diff first, while avoiding repetition of
code comments. Prefer this progression when it is useful:

1. Describe the existing situation in present tense.
2. Explain why it needs to change.
3. State the change in imperative mood.
4. Explain why this approach was chosen.
5. Add other relevant context.

Avoid “currently” and “originally” when the time frame is already implied. If a
body becomes excessively long or covers unrelated reasons, split the work into
smaller cohesive commits rather than compressing the explanation.

## Branch names

Use meaningful kebab-case keywords, such as `refactor-ui-tests`. For work tied
to an issue, use `issueNumber-keywords-from-issue-title`, such as
`1234-ui-freeze-error`.

Before presenting or executing a commit, check the final subject and every body
line against the length limits. Do not commit, create a branch, or push unless
the user has separately authorized that action.
