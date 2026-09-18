---
name: seedu-git-standard
description: Compose and review Git commit messages using the SE-EDU Git conventions for this project.
---

# SE-EDU Git standard

Apply this skill whenever a commit message is proposed, reviewed, or created in
this project. Use the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
as the authoritative reference.

## Subject

- Write a clear subject for every commit.
- Prefer 50 characters or fewer; never exceed 72 characters.
- Use the imperative mood, capitalize the first letter, and do not end with a
  period.
- Add a concise scope or category prefix when it helps identify the affected
  area, such as `Parser:` or `chore:`.

## Body

- Add a body for non-trivial commits, separated from the subject by one blank
  line.
- Wrap body lines at 72 characters and use blank lines between paragraphs.
- Explain what changed and why it changed, not how the diff implements it.
- Describe the situation in present tense, state why it needs to change, then
  use imperative mood for the change and its rationale. Avoid redundant details
  already clear from code comments or the diff.
- Use bullet points when they make multiple changes easier to scan. If the
  explanation becomes too long, consider splitting the work into smaller
  commits.

## Branch names

- Use meaningful kebab-case keywords, such as `refactor-ui-tests`.
- For issue-related branches, use `issueNumber-some-keywords-from-issue-title`.

This skill governs commit-message quality only. Follow the repository's separate
Git-control policy before performing any Git write operation.
