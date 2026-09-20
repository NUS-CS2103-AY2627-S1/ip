---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to commits and branch names in this project.
---

# SE-EDU Git standard

Use this skill whenever preparing, reviewing, or creating commits or branch
names in this repository. The authoritative source is the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

For every commit:

- Write a subject in imperative mood, capitalize its first letter, omit the
  final period, and keep it preferably within 50 characters (hard limit 72).
- Add a body for non-trivial changes. Separate it from the subject with a
  blank line and wrap body lines at 72 characters.
- Use the body to explain what changed and why; do not describe implementation
  mechanics that are already evident from the diff. Use paragraphs or bullets
  when they improve clarity.

For branches, use meaningful kebab-case keywords. If the work relates to an
issue, prefer `issueNumber-some-keywords-from-issue-title`.

Do not create or amend commits unless the user explicitly asks. When proposing
a commit message, apply these rules and include enough rationale for a
non-trivial change.
