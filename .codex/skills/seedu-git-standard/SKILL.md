---
name: seedu-git-standard
description: Apply SE-EDU Git conventions to branch names and commit messages for this project.
---

# SE-EDU Git Standard

Apply these rules whenever creating, reviewing, or proposing commits and
branches. Source: https://se-education.org/guides/conventions/git.html

## Commit subjects

- Keep the subject at 50 characters when practical and never over 72.
- Use imperative mood, capitalize the first letter, and omit the final period.
- An optional scope/category prefix is allowed when useful.

## Commit bodies

- Add a body for non-trivial commits, separated from the subject by a blank
  line and wrapped at 72 characters.
- Explain what changed and why; do not narrate implementation mechanics.
- Describe the current situation, why it needs changing, what to do, and why
  that approach is appropriate. Use paragraphs or bullets as helpful.

## Branch names

- Use meaningful kebab-case keywords, such as `refactor-ui-tests`.
- For issue-related work, use `<issue-number>-<keywords-from-title>`.

Do not commit or push unless the user explicitly requests it. When proposing a
commit, check the subject length, imperative mood, capitalization, punctuation,
body separation, and body wrapping before presenting it.
