---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to this project. Use whenever Codex proposes, reviews, directly writes, amends, or explains commit messages, merge commit messages, branch names, tags, or Git workflows in this repository.
---

# SE-EDU Git Standard

## Overview

Follow the SE-EDU Git conventions for commit messages and branch names in this
project. When a Git situation is not covered by the convention, choose a simple,
consistent style that fits the existing repository history.

## Source

Primary reference:
https://se-education.org/guides/conventions/git.html

## Commit Subject

- Write a clear subject line for every commit.
- Prefer subjects up to 50 characters; never exceed 72 characters.
- Use the imperative mood.
  Good: `Add README.md`
  Bad: `Added README.md`
- Capitalize the first letter of the subject.
- Do not end the subject with a period.
- Add a scope or category prefix when it improves clarity, such as
  `Parser: Reject invalid dates` or `test: Add parser cases`.

## Commit Body

- Add a body for non-trivial commits.
- Separate the subject from the body with one blank line.
- Wrap body lines at 72 characters.
- Use blank lines to separate paragraphs.
- Use bullet points when they improve readability.
- Explain what and why, not low-level how.
- Give enough detail for a reader to judge the change without reading the diff.
- Minimize repeating information already stated in code comments.
- Use present tense when describing the current situation.
- Avoid words such as `currently` and `originally` when describing the current
  situation.
- Use `Let's` to introduce the change section when helpful.

For substantial commits, prefer this body shape:

```text
{current situation}

{why it needs to change}

Let's {what is being done about it}.

{why it is done that way}

{any other relevant information}
```

## Merge Commits

- Use a short subject that describes the branch being integrated, such as
  `Merge branch-Level-7 branch`.
- Add a body when the merge resolves conflicts, combines several meaningful
  commits, or documents a milestone workflow.
- For simple no-conflict merges, a subject-only merge commit is acceptable unless
  the user or assignment asks for a fuller message.

## Branch Names

- Use meaningful names made from relevant keywords.
- Use kebab-case, such as `refactor-ui-tests`.
- If a branch relates to an issue, use
  `issueNumber-some-keywords-from-issue-title`, such as
  `1234-ui-freeze-error`.
- Keep course-required branch names exactly as specified by the assignment, even
  when they do not follow kebab-case.

## Tags and Pushes

- Use lightweight tags unless the user requests annotated tags.
- Do not commit, tag, push, merge, or amend unless the user explicitly asks.
- When asked to push a milestone branch workflow, remember that pushing a target
  branch does not automatically push the source branch or tags.
