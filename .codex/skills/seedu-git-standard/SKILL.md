---
name: seedu-git-standard
description: Apply SE-EDU Git conventions when proposing or writing commit messages and choosing branch names in this project.
---

# SE-EDU Git standard

Use the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit messages

- Start the subject with a capital and an imperative verb. Omit a final period.
  Aim for 50 characters; the hard limit is 72. Optional scopes are allowed.
- Give nontrivial commits a body, separated by a blank line and wrapped at
  72 characters. Use paragraphs or bullets for readability.
- Explain the existing situation in present tense, why it needs changing,
  the requested change in imperative form, and the rationale for that choice.
  Describe what and why rather than narrating implementation details.
- Avoid redundant phrases such as “currently” and repetition of code comments.
  Split unrelated changes into cohesive commits.

## Branches and checks

Use meaningful kebab-case names by default, with an issue number prefix when
applicable. Preserve branch names explicitly required by the user, including
course increment names such as `branch-Level-9`.

Before committing, inspect the staged diff, check message lengths, and exclude
unrelated or generated files. Follow the repository's test and authorization
requirements. This skill does not grant permission to commit, tag, or push.
