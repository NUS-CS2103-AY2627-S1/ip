---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches or proposing or creating commits in this project.
---

# SE-EDU Git Standard

Apply the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever creating or naming a branch, proposing a commit message, or creating a commit in this project.

## Commit subjects

- Write an informative subject in imperative mood, starting with a capital letter and with no ending period.
- Aim for 50 characters; never exceed 72 characters.
- Add an optional `<scope>:` or `<category>:` prefix only when it makes the change clearer.

## Commit bodies

- Give every non-trivial commit a body, separated from the subject by one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines. Use bullets when they make the explanation clearer.
- Explain WHAT changed and WHY, rather than implementation details that the diff already shows.
- Describe the current situation in present tense, then explain why it should change, what the commit does, why that approach is appropriate, and any relevant context. Use imperative mood for the change being made.
- Split a commit into smaller cohesive commits when its explanation becomes unwieldy.

## Branch names

- Use meaningful, relevant keywords in kebab case, for example `refactor-ui-tests`.
- For issue-related branches, use `issueNumber-keywords-from-issue-title`, for example `1234-ui-freeze-error`.

## Pull requests

- Write the PR title as `type(scope): Imperative description`, with a
  lowercase, relevant `type` and `scope`. For example,
  `refactor(core): Improve code quality and error handling` or
  `chore(java): Add defensive assertions for task management`.
- Choose a type that reflects the primary intent: use `feat` for a new
  user-facing capability, `fix` for a defect correction, `refactor` for
  internal restructuring and `chore` for maintenance work.
- Keep titles informative, capitalized after the prefix, without an ending
  period, and within 72 characters.
- Use this PR description structure:

  ```markdown
  ## Summary

  - Describe the user-visible or design-relevant changes.
  - Where possible, use bullet points and keep descriptions brief.

  ## Rationale

  Explain the problem addressed and why this approach is appropriate.
  ```
- Exclude other non-necessary sections.