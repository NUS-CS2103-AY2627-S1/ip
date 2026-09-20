---
name: seedu-git-standard
description: Follow the SE-EDU Git conventions for commit messages and branches in this iP project.
---

# SE-EDU Git Standard

Use this skill whenever suggesting commit messages, creating commits, naming branches, or explaining Git workflow in this project.

## Commit Messages

- Write the subject in imperative mood, e.g. `Add storage loading tests`.
- Capitalize the first letter of the subject.
- Do not end the subject with a period.
- Keep the subject at 50 characters or fewer when practical, and never above 72 characters.
- Use an optional scope or category prefix only when it improves clarity.
- For non-trivial commits, add a body separated from the subject by one blank line.
- Wrap body lines at 72 characters.
- Use the body to explain what changed and why. Avoid narrating implementation details that are already obvious from the diff.

## Branch Names

- Use meaningful branch names.
- Prefer lowercase kebab-case, such as `add-storage-tests`, unless the assignment or user asks for an exact branch name.
- When a course increment requires a specific branch name, use that required name exactly.

## Project Practice

- Do not commit or push unless the user explicitly asks.
- After every code change, tell the user the corresponding suggested Git commit message.
- If creating a commit for the user, follow this standard and include a useful body for non-trivial changes.

Source standard: https://se-education.org/guides/conventions/git.html
