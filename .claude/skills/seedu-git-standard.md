---
name: seedu-git-standard
description: SE-EDU Git commit message convention for this project
---

# SE-EDU Git Commit Message Standard

Apply these rules to all Git commits in this project.

## Subject Line

- **50 characters max** (hard limit: 72).
- **Imperative mood**: "Add feature", not "Added feature" or "Adding feature".
- **Capitalize first letter**: "Move file to root", not "move file to root".
- **No period at end**.
- **Optional scope prefix**: `ClassName: Do something` or `category: Do something`.
  Examples: `Storage: Fix file parsing bug`, `bug fix: Handle null dates`.

## Body (for non-trivial commits)

- **Blank line** between subject and body.
- **Wrap at 72 characters**.
- **Explain WHAT and WHY**, not HOW (the diff shows how).
- **Use blank lines** to separate paragraphs.
- **Use bullet points** where helpful.
- **Structure** (when applicable):
  1. Current situation (present tense).
  2. Why the change is needed.
  3. What is being done (imperative mood).
  4. Why this approach was chosen.
- **Minimize redundancy** with code comments in the same commit.

## Branch Names

- **Kebab case with keywords**: `refactor-ui-tests`, `1234-fix-date-parsing`.
