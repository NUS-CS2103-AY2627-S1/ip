---
name: seedu-git-standard
description: Review and write this project's Git commits using SE-EDU conventions.
---

# SE-EDU Git Standard

Use this skill when reviewing Git history, proposing a commit, or creating a
commit in this repository. Apply the official [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
as the source of truth.

## Commit subject

- Give every commit a clear, meaningful subject.
- Use the imperative mood, capitalize the first letter, and do not end with a
  period.
- Aim for at most 50 characters; never exceed 72 characters.
- Add a concise scope or category prefix when it improves clarity, for
  example, `Parser: Add date aliases` or `chore: Update documentation`.

## Commit body

For a non-trivial commit, separate the subject and body with a blank line and
wrap body lines at 72 characters. Explain what changed and why it changed,
using present tense for the situation and imperative mood for the proposed
change. Use paragraphs or bullet points where useful, and avoid explaining
implementation details that the diff already makes clear. If the explanation
becomes too long, consider splitting the work into smaller commits.

Recommended body structure:

1. State the current situation.
2. Explain why it needs to change.
3. State what the commit does and why that approach is suitable.
4. Add other relevant context only when needed.

## Branch names

Use meaningful kebab-case branch names containing relevant keywords. If the
branch is tied to an issue, use the issue number followed by relevant keywords,
such as `1234-ui-freeze-error`.

## Review workflow

1. Inspect `git status`, the diff, and nearby commit history before drafting a
   message.
2. Check that the commit is focused and that unrelated changes are not mixed
   in.
3. Review the subject against the subject rules and the body against the
   what/why structure.
4. Run `git diff --check` and confirm the final staged contents before a
   commit.
5. Do not commit or push unless the user explicitly authorizes that action.
