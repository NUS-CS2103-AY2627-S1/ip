---
name: seedu-git-standard
description: Follow the SE-EDU Git conventions for this CS2103/IP project whenever proposing commit messages, creating commits, creating branches, or reviewing Git history.
---

# SE-EDU Git Standard

Use this skill whenever proposing or creating Git commits or branch names in this repository.

## Source

Based on the SE-EDU Git conventions:
https://se-education.org/guides/conventions/git.html

## Commit Subjects

- Write a clear subject for every commit.
- Prefer subjects of 50 characters or fewer; never exceed 72 characters.
- Use imperative mood, as if completing "If applied, this commit will ...".
- Capitalize the first letter of the subject text.
- Do not end the subject with a period.
- A category or scope prefix may be used when helpful, including Conventional Commit-style prefixes such as `docs:`, `test:`, `fix:`, `refactor:`, `build:`, or `chore:`.

## Commit Bodies

- Add a body for non-trivial commits.
- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters.
- Explain what changed and why it changed; leave low-level how details to the diff.
- Use present tense for the situation being changed.
- Use imperative mood for what the commit does.
- Split commits when the body becomes too broad or mixes unrelated concerns.
- Avoid repeating information already clear from nearby code comments.

## Branch Names

- Use meaningful branch names made from relevant keywords.
- Use kebab case, such as `add-junit-tests`.
- If tied to an issue, prefix with the issue number, such as `1234-fix-ui-freeze`.
