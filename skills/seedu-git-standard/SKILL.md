---
name: seedu-git-standard
description: Apply SE-EDU Git conventions to commit messages and branch names in this project.
---

# SE-EDU Git standard

Apply the SE-EDU Git conventions to every commit and branch operation in this repository. The
authoritative reference is the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit messages

- Always write a meaningful subject in imperative mood, capitalize its first letter, omit the
  final period, and keep it at 50 characters where possible (72 characters maximum).
- For every non-trivial commit, add a blank line followed by a body wrapped at 72 characters.
- Explain what changed and why; the diff already communicates how. Use present tense for the
  current situation and imperative mood when describing the change. Separate paragraphs and use
  bullets when they improve readability.
- Avoid vague filler and words such as “currently” or “originally”. Keep the body detailed enough
  for a reviewer to evaluate the change without reading the diff.

## Branch names

- Use meaningful kebab-case keywords, such as `refactor-ui-tests`.
- For issue work, use `<issue-number>-<kebab-case-keywords>`.

## Commit review checklist

Before creating a commit, verify the subject length, imperative mood, capitalization, punctuation,
body separation and wrapping, and that the message explains what and why. Do not commit or push
unless the user explicitly requests it.
