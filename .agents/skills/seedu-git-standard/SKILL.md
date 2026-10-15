---
name: seedu-git-standard
description: Propose or review Git commit messages and branch names using the SE-EDU Git conventions for this project.
---

# Follow the SE-EDU Git conventions

Use the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever
proposing or reviewing a commit message or branch name. Follow the repository's authorization
rules separately; proposing a message does not authorize creating a commit.

## Commit subjects

- Summarize one cohesive change in the imperative mood.
- Capitalize the first letter and do not end with a period.
- Aim for at most 50 characters and never exceed 72 characters.
- Add a meaningful `<scope>:` or `<category>:` prefix only when it improves clarity.

## Commit bodies

Use a body for non-trivial changes. Separate it from the subject with a blank line, wrap it at
72 characters, and separate paragraphs with blank lines. Explain what changed and why; leave
implementation mechanics to the diff. Describe the existing situation in present tense and the
change in imperative mood. If the body becomes long or covers unrelated concerns, propose
multiple commits.

## Branch names

Use meaningful kebab-case keywords. For issue-related work, use
`issueNumber-keywords-from-issue-title`.

Before presenting a proposed message, verify its subject length, mood, capitalization,
punctuation, cohesion, and whether a body is warranted.
