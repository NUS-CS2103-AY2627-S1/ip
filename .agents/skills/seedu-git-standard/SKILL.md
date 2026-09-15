---
name: seedu-git-standard
description: Apply the mandatory SE-EDU Git conventions when proposing or creating commits and branches in this project.
---

# SE-EDU Git Standard

Apply the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
to every proposed or created commit and branch in this project. This skill does
not grant permission to commit, push, tag, or create a branch; follow the
project's authorization rules first.

## Commit subject

- Give every commit a well-written subject.
- Aim for at most 50 characters and never exceed 72 characters.
- Use imperative mood, capitalize the first letter, and do not end with a
  period: `Add task persistence`, not `Added task persistence.`
- Add a meaningful `<scope>:` or `<category>:` prefix only when useful.

## Commit body

- Add a body for every non-trivial commit.
- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why; let the diff show how. Give enough context for
  a reviewer to judge the change without reading the implementation first.
- Use bullets when they improve clarity and avoid repeating code comments.
- For detailed bodies, cover the present situation, why it should change, what
  the commit does in imperative mood, why that approach was chosen, and other
  relevant context. Avoid redundant time words such as “currently” and
  “originally”.
- Split a commit when its message becomes too broad or long to explain clearly.

## Branch names

- Use a meaningful kebab-case name, such as `refactor-ui-tests`.
- For issue-related work, use
  `issueNumber-some-keywords-from-issue-title`, such as
  `1234-ui-freeze-error`.

## Verification

Before proposing or creating Git history:

1. Check that the changes form one coherent commit.
2. Check subject mood, capitalization, punctuation, and length.
3. Check whether a non-trivial commit needs a wrapped explanatory body.
4. Check new branch names against the kebab-case and issue-name formats.
