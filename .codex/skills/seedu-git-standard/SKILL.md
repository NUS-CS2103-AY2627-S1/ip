---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when creating, reviewing, or preparing commits and branch names in this project.
---

# SE-EDU Git Standard

Apply the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
to every commit and branch created or reviewed in this repository.

## Commit messages

- Write a meaningful subject in imperative mood, capitalise its first letter, and do not end
  it with a period. Keep it within 72 characters, preferably within 50. An optional
  `scope:` or `category:` prefix is allowed when useful.
- Give every non-trivial commit a body separated from the subject by a blank line. Wrap body
  lines at 72 characters and use blank lines between paragraphs.
- Explain what changed and why, not how; the diff communicates implementation details. Make
  the explanation detailed enough to judge the change without reading the diff, avoid merely
  repeating code comments, and split an overly broad change into smaller commits when needed.
- Prefer the guide's structure: current situation, why it needs to change, what to do,
  why that approach is appropriate, and other relevant information. Describe the situation
  in present tense and the change in imperative mood.

Before committing, inspect the staged diff and verify that the message describes the complete
staged change. Do not commit or push unless the user explicitly authorises it.

## Branch names

Use meaningful kebab-case names made from relevant keywords. For issue-related branches, use
`issueNumber-some-keywords-from-issue-title`.

## Reference

For any Git topic not covered here, consult the linked SE-EDU guide and follow its cited
guidance where applicable.
