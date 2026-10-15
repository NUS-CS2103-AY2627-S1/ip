---
name: seedu-git-standard
description: "Apply the SE-EDU Git conventions when naming branches and creating commit messages for this project."
---

# Seedu Git Standard

Apply these conventions when creating a branch or preparing an authorized commit in this project.
The authoritative guide is the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Branch names

- Use meaningful, relevant keywords in kebab-case, such as `refactor-ui-tests`.
- For issue-related work, use `issueNumber-keywords-from-issue-title`, such as
  `1234-ui-freeze-error`.

## Commit subject

- Every commit must have a clear subject line. Keep it near 50 characters and never exceed 72.
- Use the imperative mood (`Add README.md`, not `Added README.md`).
- Capitalize the first letter and do not end the subject with a period.
- Add a scope or category prefix only when useful, such as `Parser: Handle empty input` or
  `bug fix: Reject invalid dates`.

## Commit body

- Give every non-trivial commit a body separated from the subject by one blank line.
- Wrap body lines at 72 characters and use blank lines between paragraphs. Use bullets when they
  make the explanation easier to scan.
- Explain what changed and why it changed, not how the implementation works; the diff shows how.
- Structure the explanation as: current situation, why it needs to change, what to do, why that
  approach is appropriate, and any other relevant information.
- Avoid repeating information already present in code comments. Split an overly long change into
  smaller commits when the explanation becomes difficult to keep focused.

## Commit checklist

Before an authorized commit, inspect the staged diff and confirm that the subject and body follow
these rules. Keep unrelated files out of the commit and do not create a commit merely to satisfy
this checklist; existing project instructions and explicit user authorization still control whether
a commit may be made.
