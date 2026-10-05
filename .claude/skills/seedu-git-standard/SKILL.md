---
name: seedu-git-standard
description: The SE-EDU Git conventions (https://se-education.org/guides/conventions/git.html) that every commit message and branch name in this repository must follow. Load this whenever writing, proposing, reviewing or amending a commit message, or naming a branch. This includes when only drafting a message for the user to approve rather than committing. Trigger on any request to commit, write or improve a commit message, add a commit body or create/rename a branch, even when the conventions are not named explicitly.
---

# SE-EDU Git Conventions

CS2103T requires the **commit message subject** conventions. Writing a body is optional, but
if you write one, it must follow at least the basic body conventions. Source:
<https://se-education.org/guides/conventions/git.html>

## Commit message: subject

Every commit must have a well-written subject line.

- **Imperative mood.** Good: `Add README.md`. Bad: `Added README.md`, `Adding README.md`.
- **Capitalize the first letter.** Good: `Move index.html file to root`.
  Bad: `move index.html file to root`.
- **No period at the end.** Good: `Update sample data`. Bad: `Update sample data.`
- **Aim for 50 characters; hard limit 72.** Rationale: some tools show only a limited number
  of characters from the commit message.
- **An optional `<scope>:` or `<category>:` prefix** is allowed where useful:
  `Person class: Remove static imports`, `Main.java: Remove blank lines`,
  `bug fix: Add space after name`, `chore: Update release date`.

## Commit message: body

Non-trivial commits should have a body giving details of the commit.

- Separate subject from body with **one blank line**.
- **Wrap the body at 72 characters.**
- Use blank lines to separate paragraphs; use bullet points where they help.

### Explain WHAT and WHY, not HOW

The diff already shows how. The body exists so a reader can judge whether the change is a
good thing to do *without* reading the diff. If the description starts getting too long,
that is a sign the commit should be split into finer-grained pieces.

Minimize repeating information already given in code comments in the same commit.

### Structure to follow

1. `{current situation}` - present tense
2. `{why it needs to change}`
3. `{what is being done about it}` - imperative mood
4. `{why it is done that way}`
5. `{any other relevant info}`

Avoid `currently` and `originally` when describing the current situation — they are implied.
The word `Let's` can mark the start of the section describing the change.

### Worked example

```
Find command: make matching case-insensitive

Find command is case-sensitive.

A case-insensitive find is more user-friendly because users cannot be
expected to remember the exact case of the keywords.

Let's,
* update the search algorithm to use case-insensitive matching
* add a script to migrate stress tests to the new format
```

## Branch names

- **kebab-case**, made of a few meaningful keywords: `refactor-ui-tests`.
- If the branch relates to a tracked issue, use
  `issueNumber-some-keywords-from-issue-title`, e.g. `1234-ui-freeze-error`.

### This project's additional layer

Graded iP increments use `branch-<Level-N>` or `branch-<A-Something>`, e.g.
`branch-Level-8`, `branch-A-JavaDoc`. That is this repository's own convention sitting on
top of kebab-case, not a contradiction of it.

Increment tags are lightweight and match the increment name exactly: `Level-8`,
`A-JavaDoc`, `A-FullCommitMessage`.

## When drafting a message

Base it on what is actually staged. Check `git status` and `git diff` rather than guessing
from the conversation. Then give the message to the user to review.

Do not run `git commit`, `git push`, or any other state-changing git command unless the user
explicitly asks.
