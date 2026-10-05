---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to commits and branch names in this project.
---

# SE-EDU Git standard

Apply this skill whenever proposing, preparing, or creating a commit or branch in
this repository. Use the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
as the source of truth if a rule is not summarized here. This skill does not
authorize committing or pushing; do those only when the user explicitly asks.

## Commit subject

- Every commit needs a meaningful subject line. Aim for 50 characters and never
  exceed 72 characters.
- Use the imperative mood, capitalize the first letter, and do not end the
  subject with a period.
- Add a concise scope or category prefix when it improves clarity, such as
  `Parser: ...` or `chore: ...`.

## Commit body

- Give every non-trivial commit a body separated from the subject by one blank
  line. Wrap body lines at 72 characters and separate paragraphs with blank
  lines; use bullets when they improve readability.
- Explain what changed and why it changed. The diff already shows how it was
  implemented, so avoid using the body as a step-by-step implementation log.
- Structure the explanation around the situation, why it needs to change, what
  is being done, why that approach was chosen, and any relevant extra context.
  Use present tense for the situation and imperative mood for the change.
- Avoid filler terms such as `currently` and `originally`, and avoid repeating
  information that is already clear from code comments.

## Branch names

- Use meaningful kebab-case names containing relevant keywords, for example
  `refactor-ui-tests`.
- For issue-related branches, use `<issue-number>-<keywords-from-issue-title>`.

## Review checklist

Before finishing a Git change, inspect the staged diff and verify the subject,
body, line lengths, and branch name against these rules. Keep the commit focused
enough that its explanation can describe the change and rationale clearly.
