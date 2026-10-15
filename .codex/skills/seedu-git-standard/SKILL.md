---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to commits and branches in this project.
---

# SE-EDU Git standard

Apply the SE-EDU Git conventions to all repository commits and branches. Use the authoritative
guide for details:
https://se-education.org/guides/conventions/git.html

## Commits

- Write a meaningful subject in imperative mood, capitalize its first letter, omit a final period,
  and keep it at or below 72 characters (aim for 50).
- For non-trivial changes, include a body separated by a blank line and wrap it at 72 characters.
- Explain what changed and why. Use present tense for the current situation and imperative mood
  for the change; do not merely describe implementation mechanics.
- Split unrelated changes into separate commits when practical.

## Branches

- Use meaningful kebab-case branch names.
- When a branch corresponds to an issue, use `issueNumber-keywords-from-issue-title`.

Before creating a commit, review the subject, body, scope, and branch name against these rules.
