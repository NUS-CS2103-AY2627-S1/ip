---
name: seedu-git-standard
description: Apply the SE-EDUCATION Git conventions when creating commits, writing commit messages, or naming branches in this project.
---

# Seedu Git Standard

Apply these rules to all commits and branches in this project. The authoritative source is the
[SE-EDUCATION Git conventions guide](https://se-education.org/guides/conventions/git.html).

## Commit subjects

- Write a meaningful subject in imperative mood, capitalizing its first letter.
- Prefer 50 characters or fewer; never exceed 72 characters.
- Do not end the subject with a period.
- Add a concise scope or category prefix when it improves clarity (for example, `Parser:` or
  `chore:`).

## Commit bodies

- Add a body for non-trivial commits, separated from the subject by one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why; do not narrate implementation steps that the diff already shows.
- Describe the current situation and reason in present tense, then state the change in imperative
  mood. Split unrelated changes into separate commits when the message would otherwise become too
  broad.
- Use bullet points when they make a group of related changes clearer.

## Branch names

- Use meaningful kebab-case names built from relevant keywords.
- For issue branches, use `<issue-number>-<keywords-from-issue-title>`.
