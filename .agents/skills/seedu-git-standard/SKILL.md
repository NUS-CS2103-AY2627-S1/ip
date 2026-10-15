---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing or creating commits and when suggesting or creating branches in this project.
---

# SE-EDU Git Standard

Use this skill for commit and branch work in this repository. Treat the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
as authoritative.

## Commit Messages

- Give every commit a clear subject line.
- Write the subject in the imperative mood, capitalize its first letter, and do not end it with a period.
- Aim for no more than 50 characters in the subject and never exceed 72 characters.
- Add an optional scope or category prefix only when it improves clarity, for example,
  `Parser: Handle empty find keywords`.
- For a non-trivial commit, separate the subject from the body with a blank line.
- Do not hard-wrap commit-message body text at 72 characters. Keep each paragraph on one line and separate paragraphs with blank lines. This project preference overrides the linked SE-EDU body-wrapping convention.
- Explain what changed and why it was needed. Leave implementation details that are evident from the diff out of
  the message.
- Use bullet points when they make multiple changes easier to understand.
- If a commit message needs a long or unfocused explanation, split the work into smaller logical commits where
  doing so does not conflict with the user's requested history.

## Branch Names

- Use meaningful keywords in kebab case, for example, `refactor-ui-tests`.
- For work tied to an issue, use `issueNumber-keywords-from-issue-title`, for example,
  `1234-ui-freeze-error`.
- Preserve a branch name explicitly supplied by the user. When Codex creates a branch without an explicit name,
  retain any environment-required prefix and apply these conventions to the remainder of the name.

## Workflow

1. Before proposing or creating a commit, inspect the actual changes so the message describes the commit's scope
   and rationale accurately.
2. Check the subject against the required mood, capitalization, punctuation, and length rules.
3. Add a body when the commit is non-trivial, following the formatting and content rules above.
4. Do not commit or push unless the user has explicitly authorized that action.
