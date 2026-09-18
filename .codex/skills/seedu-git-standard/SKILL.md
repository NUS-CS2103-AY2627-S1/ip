---
name: seedu-git-standard
description: Apply and review the SE-EDU Git conventions for commit messages and branch names in this project. Use whenever proposing, creating, or reviewing a commit or branch.
---

# SE-EDU Git Standard

Apply the rules from the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).
These rules govern message quality and branch naming; they do not authorize committing, pushing,
rewriting history, or changing tags without the user's permission.

## Before a commit

Inspect the exact staged diff and repository status before proposing or creating a commit. Ensure the
staged changes form one coherent unit. If they mix unrelated concerns, recommend splitting them rather
than hiding the mixture behind a broad message.

## Commit subject

Every commit must have a clear subject that follows all of these rules:

- Aim for at most 50 characters; never exceed 72 characters.
- Use the imperative mood, describing the change as a command: `Add`, `Fix`, `Move`, or `Update`.
- Capitalize the first letter of the subject.
- Do not end the subject with a period.
- Describe the actual intent of the staged change, not merely the files touched.
- Add an optional `<scope>:` or `<category>:` prefix only when it improves clarity. The text after the
  prefix should still begin with a capitalized imperative verb.

Examples:

- `Add task persistence tests`
- `Meka class: Extract command parsing`
- `chore: Update release date`

## Commit body

A body is optional for a trivial, self-explanatory commit and expected for a non-trivial commit.
When a body is used:

- Separate it from the subject with one blank line.
- Wrap each line at 72 characters.
- Use blank lines between logical paragraphs and bullets when they improve readability.
- Explain WHAT changed and WHY it was needed; leave implementation details to the diff.
- Describe the pre-change situation in the present tense, explain the reason for changing it, then
  describe the intended change in the imperative mood and explain the chosen direction.
- Avoid filler such as `currently` and `originally`, and avoid repeating code comments or the subject.
- If the body becomes long or covers unrelated reasons, split the work into smaller commits.

## Branch names

- Use a meaningful, concise set of relevant keywords in kebab case, such as `refactor-ui-tests`.
- For an issue-specific branch, use `issueNumber-relevant-keywords`, such as
  `1234-ui-freeze-error`.
- Follow an externally mandated exact branch name, such as a course increment branch, even when it
  differs from the general kebab-case convention. Do not silently rename required branches.

## Completion check

Before presenting or executing a Git operation, verify the subject length and grammar, body wrapping,
staged scope, and branch-name format. Report any deliberate exception caused by an external naming
requirement.
