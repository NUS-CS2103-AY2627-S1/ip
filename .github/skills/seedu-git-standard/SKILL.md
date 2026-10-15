---
name: seedu-git-standard
description: "Use when proposing, preparing, creating, reviewing, or amending Git commits and branches in this project. Applies the SE-EDU Git conventions for commit messages and branch names."
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever working with commits or branches in this repository.

## Commit Subjects

- Write every commit subject in imperative mood, starting with a capital letter and without a trailing period.
- Aim for 50 characters; enforce a 72-character hard limit.
- Include an optional scope or category when it clarifies the change, for example `Parser: Reject blank input` or `bug fix: Preserve task order`.

## Commit Bodies

- Add a body for every non-trivial commit, separated from its subject by one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines. Use bullets where they improve clarity.
- Explain what changed and why; leave implementation mechanics to the diff.
- Structure substantial bodies as: current situation, why it needs to change, what the commit does, why that approach was selected, then other relevant details.
- Write the current situation in present tense and use imperative mood when describing the proposed change. Avoid redundant terms such as `currently` and `originally`.
- Split a commit when the explanation becomes too broad to understand without a long body.

## Branch Names

- Name branches with meaningful, relevant keywords in kebab case, for example `refactor-ui-tests`.
- For issue-related branches, use `issueNumber-keywords-from-issue-title`, for example `1234-ui-freeze-error`.

## Review Checklist

Before proposing or creating a commit, verify that the staged change is focused, the subject meets its length, capitalization, imperative, and punctuation rules, and a non-trivial change has a wrapped body explaining what and why. Before creating a branch, verify its name is meaningful kebab case and uses the issue-number prefix when applicable.
