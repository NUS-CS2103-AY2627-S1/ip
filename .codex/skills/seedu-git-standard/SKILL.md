---
name: seedu-git-standard
description: >-
  Apply the SE-EDU Git conventions used in CS2103/T. Use when proposing commit
  messages, creating commits, naming branches, reviewing Git history, or
  checking whether project Git usage follows the course standard.
---

# SE-EDU Git Standard

## Overview

Follow the SE-EDU Git conventions at https://se-education.org/guides/conventions/git.html for
future commits and branch names in this project.

## Commit Messages

- Keep the subject line ideally within 50 characters and always within 72 characters.
- Use imperative mood in the subject, such as `Add parser tests`, not `Added parser tests`.
- Capitalize the first letter of the subject.
- Do not end the subject with a period.
- Add a scope or category prefix when it improves clarity, such as `Parser: Handle blank input`.
- For non-trivial commits, include a body separated from the subject by one blank line.
- Wrap body lines at 72 characters.
- Use the body to explain what changed and why, not how the code implements it.
- Split unrelated changes into separate commits.

## Branch Names

- Use meaningful kebab-case branch names, such as `fix-storage-load-error`.
- For course-required iP branches, use the exact branch names required by the course, such as
  `branch-A-CodingStandard` or `branch-Level-9`.

## Workflow

1. Before proposing or creating a commit, check the subject against the rules above.
2. If a change mixes independent concerns, split it into separate commits.
3. Use a commit body for non-trivial changes, especially refactoring or behavior changes.
4. Do not rewrite past commits unless the user explicitly asks for history rewriting.
