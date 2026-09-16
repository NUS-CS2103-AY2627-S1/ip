---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing branch names or creating commit messages in this project.
---

# Seedu Git Standard

Apply the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
whenever proposing a branch name or creating a commit in this repository.

- Name branches with meaningful, kebab-case keywords. If a branch relates to an
  issue, use `issueNumber-keywords-from-title`.
- Write a capitalized, imperative subject line. Aim for 50 characters and never
  exceed 72. Do not end it with a period. A scope or category prefix is allowed
  when it makes the change clearer.
- Give non-trivial commits a body separated by a blank line. Wrap body lines at
  72 characters; explain what changed and why, not implementation mechanics.
  Split unrelated changes into separate commits instead of using an overly long
  message.

Before committing, check the message against these rules and confirm that the
staged files represent one logical change. Follow the project’s existing rule
that commits and pushes require explicit user authorization.
