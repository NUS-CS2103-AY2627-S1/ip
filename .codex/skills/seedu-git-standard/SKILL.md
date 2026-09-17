---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to commit subjects, commit bodies, and branch names in this project. Use when proposing, reviewing, editing, or preparing a future Git commit or branch.
---

# SEEDU Git Standard

Use these rules for every future commit message and branch name in this repository. Treat the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) as the source of truth.

## Commit subject

- Give every commit a clear subject. Aim for 50 characters or fewer; never exceed 72.
- Write the subject in the imperative mood, capitalize its first letter, and do not end it with a period.
- Add a meaningful `<scope>:` or `<category>:` prefix only when it helps identify the affected area.

Good subject: `Java style: Add package declarations`

## Commit body

- Add a body for every non-trivial commit. Separate it from the subject with one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines. Use bullets when they make several changes easier to scan.
- Explain WHAT changed and WHY it changed, not HOW the diff implements it.
- Structure the explanation as the present situation, why it needs to change, the imperative change, why that approach is appropriate, and any other relevant information.
- Avoid words such as `currently` and `originally`; the present situation is implied. Do not repeat information already clear from code comments.
- Use present tense for the situation and imperative mood for the requested change; use `Let's` only when it makes the change section easier to read.
- Split an overlong description into smaller commits when the changes are not one coherent unit.

## Branch names

- Use meaningful kebab-case keywords, such as `refactor-ui-tests`.
- For issue-related work, use `issueNumber-keywords-from-issue-title`.
- Keep branch names specific enough to identify the increment or area being developed.

## Review checklist

1. Check subject length, imperative mood, capitalization, and final punctuation.
2. Add and wrap a WHAT/WHY body when the commit is non-trivial.
3. Check that the branch name is meaningful and kebab-case.
4. Do not create a commit or push it unless the user explicitly requests that action.
