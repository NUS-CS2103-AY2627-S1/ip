---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to commits and branch names in this project, including imperative commit subjects, clear bodies, and meaningful kebab-case branches.
---

# SE-EDU Git standard

Use this skill whenever you create, amend, review, or propose a commit, or
when you create or rename a branch in this project.

The source of these conventions is the SE-EDU Git conventions guide:
<https://se-education.org/guides/conventions/git.html>

## Commit requirements

- Write a well-formed subject line. Aim for 50 characters and never exceed 72
  characters.
- Start the subject with a capital letter, use the imperative mood, and do not
  end it with a period. A concise scope or category prefix is allowed when it
  improves clarity.
- For non-trivial commits, separate the subject and body with a blank line.
- Wrap the body at 72 characters and use blank lines or bullet points to keep
  multiple ideas readable.
- Explain what changed and why it changed. Do not spend the body explaining
  implementation details that are already clear from the diff.
- Structure a non-trivial body around the current situation, why it needs to
  change, what to do, and why that solution is appropriate.

## Branch requirements

- Use meaningful branch names in kebab case, such as `refactor-ui-tests`.
- For issue-related work, use `<issue-number>-<keywords-from-issue-title>`.

## Workflow

1. Inspect the staged diff and confirm that the commit contains only the
   intended changes.
2. Choose a subject that follows the requirements above; add a body when the
   change is non-trivial.
3. Check the final message for imperative mood, capitalization, punctuation,
   and line length before committing.
4. Keep branch names descriptive and consistent with the work.

