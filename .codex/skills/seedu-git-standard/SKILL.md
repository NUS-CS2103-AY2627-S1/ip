---
name: seedu-git-standard
description: Apply SE-EDU Git conventions and Conventional Commits when naming branches or preparing, reviewing, or creating commits in this project.
metadata:
  short-description: Apply the project's Git conventions
---

# SE-EDU Git Standard

Use this skill whenever naming a branch or preparing, reviewing, suggesting, or creating a commit for this repository. Follow the SE-EDU [Git conventions](https://se-education.org/guides/conventions/git.html) and the [Conventional Commits specification](https://www.conventionalcommits.org/en/v1.0.0/).

## Commit subject

- Every commit subject must use the form `<type>: <description>`.
- Use a meaningful lowercase type, such as `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `build`, `ci`, `perf`, or `chore`.
- Give the description a clear imperative verb with an initial capital letter, and do not end it with a period. For example: `style: Standardize documentation`.
- Aim for 50 characters; never exceed 72.

## Commit body

For non-trivial commits, include a body after one blank line. Wrap body lines at 72 characters and use blank lines or bullets to make it easy to scan.

- Explain what changed and why it was necessary, rather than implementation details already visible in the diff.
- Describe the present situation in present tense; use imperative mood for the planned or completed change.
- Include enough context for a reviewer to judge the change without reading the diff. If that requires an overly long explanation, consider splitting the work into smaller, coherent commits.

## Branch names

- Use a meaningful kebab-case name made from relevant keywords, for example `refactor-ui-tests`.
- For issue-related work, use `issueNumber-relevant-keywords`, for example `1234-ui-freeze-error`.

## Before committing

Confirm that the message and branch name meet the rules above. This skill governs conventions only: continue to respect repository instructions and obtain the user's permission before committing or pushing.
