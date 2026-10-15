---
name: seedu-git-standard
description: Enforce SE-EDU git conventions for commit messages, branch names, and tags in this Java project.
---

# SeEDU Git Standard

Enforce the git conventions from https://se-education.org/guides/conventions/git.html for all git operations in this project.

## Commit message: Subject

- Limit the subject line to 50 characters (hard limit: 72 chars).
- Use the **imperative mood**.
- Capitalize the first letter.
- Do not end with a period.
- Optionally add a scope: prefix, e.g. Person class:, Main.java:, bug fix:, chore:.

## Commit message: Body

- Separate subject from body with a **blank line**.
- Wrap the body at 72 characters.
- Use blank lines to separate paragraphs.
- Explain **WHAT** and **WHY**, not **HOW** (the diff shows HOW).
- Structure:
  - {current situation} — use present tense
  - {why it needs to change}
  - {what is being done} — use imperative mood
  - {why it is done that way}
  - {any other relevant info}

## Branch names

- Use kebab-case with meaningful keywords: refactor-ui-tests
- For issues: issueNumber-keywords e.g. 1234-ui-freeze-error

## Tags

- Use lightweight tags unless annotated tags are requested.

## Enforcement

Before any commit, ensure the commit message follows these rules. When creating branches or tags, follow the naming conventions above.
