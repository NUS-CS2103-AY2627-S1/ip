---
name: seedu-git-standard
description: Apply the SE-EDU Git commit message conventions whenever proposing or creating commits in this project.
---

# SE-EDU Git Commit Message Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
for every proposed or created commit message.

## Subject

- Use an imperative verb, as if completing the phrase “This commit will…”.
- Capitalize the first letter and do not end with a period.
- Aim for at most 50 characters; never exceed 72 characters.
- Add a concise scope or category prefix only when it improves clarity.

## Body

Include a body for non-trivial changes.

- Separate the body from the subject with one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why it is needed; leave implementation details to
  the diff.
- Describe the existing situation in present tense, then explain the reason
  for change and the chosen outcome. Use imperative mood for the change.
- Avoid repeating code comments or using time-relative words such as
  “currently” and “originally”.

Before proposing or creating a commit, inspect the staged diff and ensure the
commit contains one cohesive change. Do not commit, tag, or push unless the
user has authorized that action.
