---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when preparing commit messages, reviewing commits, or naming branches in this project.
---

# Seedu Git Standard

Use this skill whenever a commit is being prepared, reviewed, or proposed in this repository.
It is based on the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subject

- Every commit must have a clear subject line.
- Use the imperative mood, capitalize the first letter, and do not end the subject with a
  period.
- Prefer no more than 50 characters; never exceed the 72-character hard limit.
- Add a short scope or category prefix such as `Parser:` or `chore:` only when it improves
  clarity.

Good examples:

```text
Add deadline date validation
Parser: Reject empty task numbers
```

Avoid subjects such as `Added ...`, `Adding ...`, lowercase openings, trailing periods, or
vague summaries such as `Fix stuff`.

## Commit body

- Non-trivial commits must include a body separated from the subject by one blank line.
- Wrap body lines at 72 characters and use blank lines between paragraphs.
- Explain what changed and why it changed; let the diff explain how it changed.
- Structure the explanation around the present situation, why it needs to change, what is being
  done, and why that approach is appropriate.
- Use present tense for the current situation and imperative mood when describing the change.
- Avoid unnecessary repetition of code comments or the diff. Use bullets when they make several
  related changes easier to scan.

Example:

```text
Parser: Reject empty task numbers

Task mutation commands accept an empty number and produce a confusing
parser error.

Users need a clear validation message when a task number is missing.

Add explicit validation for the missing argument so the parser reports the
expected command format.
```

## Branch names

- Use meaningful kebab-case names containing relevant keywords, such as
  `refactor-ui-tests`.
- For issue-related branches, use `<issueNumber>-<keywords>`, such as
  `1234-ui-freeze-error`.

## Before committing

1. Review `git status` and the staged diff to confirm that only intended changes are included.
2. Check the subject against the imperative, capitalization, punctuation, and length rules.
3. Add and review a body when the commit is non-trivial; keep it focused on what and why.
4. Run the relevant project checks before creating the commit.
5. Do not create or push a commit unless the user explicitly requests it. When proposing a
   commit message without creating a commit, provide the subject and body separately.
