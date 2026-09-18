---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to this repository's future Git work. Use when proposing or creating branch names, commit messages, or commit plans so commits follow https://se-education.org/guides/conventions/git.html and the repo's local Git instructions.
---

# SE-EDU Git Standard

Follow this skill for all future Git work in this repository.

## Workflow

1. Read the local Git instructions in `AGENTS.md` first.
2. Apply the commit and branch rules in `references/git-rules.md`.
3. For non-trivial changes, prepare a commit message subject and body together.
4. Keep commits narrowly scoped enough that the WHAT and WHY are easy to explain.

## Required Rules

- Write commit subjects in the imperative mood.
- Capitalize the first letter of the subject.
- Do not end the subject with a period.
- Keep the subject concise, preferably within 50 characters and never above 72.
- Add a blank line before the body for non-trivial commits.
- Wrap commit-message body lines at 72 characters.
- Explain WHAT changed and WHY, not the implementation details of HOW.
- Use kebab-case branch names with meaningful keywords.
- When a branch is tied to an issue, prefer `issueNumber-keywords-from-title`.

## Project-Specific Additions

- When proposing commit messages in this repo, include enough detail for a beginner to understand the rationale for the change.
- Use lightweight tags unless the user explicitly asks for annotated tags.
- Do not create commits, branches, or pushes unless the user explicitly asks.

## Reference

Read `references/git-rules.md` when drafting commit messages, naming branches, or checking whether a commit should be split.
