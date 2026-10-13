---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to future commits and branch names in this project. Use when proposing, reviewing, creating, or editing commit messages; when naming branches; or when asked to follow SE-EDU, CS2103T, or project Git conventions.
---

# SE-EDU Git Standard

## Overview

Use this skill whenever preparing commit messages or branch names for this repository. Prefer the rules summarized in `references/rules.md`.

## Workflow

1. Read `references/rules.md` before proposing or creating a commit message or branch name.
2. Check that the commit subject is imperative, capitalized, concise, and has no trailing period.
3. Add a commit body for nontrivial commits, separated from the subject by one blank line.
4. Explain what changed and why in the body; leave implementation details to the diff unless they are essential context.
5. Wrap commit body lines at 72 characters.
6. Suggest splitting a commit when the body becomes too long or covers unrelated changes.
7. Use meaningful kebab-case branch names; prefix with the issue number when the branch is issue-related.

## Project Notes

- Continue to follow `AGENTS.md`: do not commit, push, or create branches unless explicitly asked.
- Use lightweight tags unless the user requests an annotated tag.
- When proposing a commit message, include enough rationale for the change to be reviewed without reading the diff first.
