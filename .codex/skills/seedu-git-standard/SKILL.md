---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions for this project when advising on or creating commits, tags, branches, and pushes.
---

# SE-EDU Git Standard

Use this skill for all Git advice and any requested commit, tag, branch, merge, or push work in this repository.

Follow the SE-EDU Git conventions:
https://se-education.org/guides/conventions/git.html

## Commit Messages

- Every commit must have a meaningful subject line.
- Use imperative mood in the subject, such as `Add parser tests`, not `Added parser tests`.
- Capitalize the first letter of the subject.
- Do not end the subject with a period.
- Keep the subject around 50 characters where practical; never exceed 72 characters.
- Use an optional scope or category prefix only when it improves clarity, for example
  `Parser: Add saved task parsing test`.
- For non-trivial commits, include a body separated from the subject by a blank line.
- Wrap commit message body lines at 72 characters.
- Explain what changed and why. Avoid spending the body on implementation details that are clear from the diff.

## Branch Names

- Use meaningful branch names made of relevant keywords.
- Use kebab case for branch names, for example `add-parser-tests`.
- If the branch relates to an issue, prefer `issueNumber-some-keywords-from-issue-title`.

## Project Rules

- Use lightweight tags unless the user explicitly requests annotated tags.
- Do not commit, tag, merge, or push unless the user explicitly asks for that Git operation.
- Before any requested commit, inspect `git status` and avoid staging unrelated user changes.
