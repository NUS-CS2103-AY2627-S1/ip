---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to commits and branch names in this project.
---

# SE-EDU Git standard

Use this skill whenever creating or proposing a commit or branch in this repository. Follow the SE-EDU Git conventions at https://se-education.org/guides/conventions/git.html.

## Commit messages

- Write a clear subject in imperative mood, capitalized, and without a final period.
- Keep the subject ideally within 50 characters and never over 72 characters.
- For a non-trivial commit, separate the subject from a body with a blank line, wrap the body at 72 characters, and explain what changed and why. Avoid explaining implementation steps that the diff already shows.
- Structure the body around the current situation, why it needs to change, what to do, and why that approach is appropriate. Use bullets when they improve clarity.

## Branch names

- Use meaningful kebab-case names, such as `refactor-ui-tests`.
- For issue-related branches, use `<issue-number>-<keywords-from-issue-title>`.

Do not create or push commits unless the user explicitly authorizes it. When suggesting a commit message, ensure it satisfies the rules above.
