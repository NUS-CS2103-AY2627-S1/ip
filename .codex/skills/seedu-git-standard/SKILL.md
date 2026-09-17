---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing or creating commits, commit messages, or branches in this project.
---

# SE-EDU Git Standard

Follow these rules whenever proposing, reviewing, or creating a commit or branch in this project. Preserve the user's
authority over Git operations: applying this standard does not grant permission to commit, push, rewrite history, or
create a branch.

The authoritative source is the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html). If this summary and the authoritative
source differ, follow the authoritative source.

## Commit subject

- Write a meaningful subject for every commit.
- Aim for at most 50 characters and never exceed 72 characters.
- Use the imperative mood, as if completing the sentence "If applied, this commit will ...".
- Capitalize the first letter.
- Do not end with a period.
- Optionally prefix the subject with a useful `<scope>:` or `<category>:`, such as `Parser: Handle empty input` or
  `chore: Update release date`.

## Commit body

- Add a body for every non-trivial commit. Separate it from the subject with one blank line.
- Wrap body text at 72 characters and use blank lines between paragraphs. Use bullets when they improve clarity.
- Explain what the change achieves and why it is needed or designed that way. Leave implementation mechanics to the
  diff and avoid repeating code comments.
- Describe the existing situation in present tense, explain why it needs to change, describe the change in imperative
  mood, and include the rationale and other relevant context.
- Avoid redundant time qualifiers such as `currently` and `originally` when describing the existing situation.
- If the body becomes excessively long or covers unrelated rationales, propose splitting the work into smaller,
  cohesive commits.

Before presenting or using a commit message, verify its subject and each body line against the length limits.

## Branch names

- Use a meaningful kebab-case name made from relevant keywords, such as `refactor-ui-tests`.
- For an issue-related branch, use `issueNumber-keywords-from-title`, such as `1234-ui-freeze-error`.
- When the environment requires a branch prefix, preserve that prefix and apply these rules to the descriptive part.
