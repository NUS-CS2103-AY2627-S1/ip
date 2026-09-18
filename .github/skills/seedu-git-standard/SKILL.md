---
name: seedu-git-standard
description: >-
  Use when creating or amending commits, writing commit messages, naming
  branches, or proposing git history in this project. Also use when the user
  asks to commit, branch, tag, or follow SE Education Git conventions.
disable-model-invocation: false
---

# SE-EDU Git conventions

Follow the [SE Education Git conventions](https://se-education.org/guides/conventions/git.html) for **every commit message and branch name** in this project.

Still do not commit or push unless the user explicitly asks. This skill governs the message and branch **when** a commit or branch is created.

## Commit subject

Every commit must have a meaningful subject line that:

- Uses the **imperative mood**: `Add README.md`, not `Added README.md` or `Adding README.md`
- **Capitalizes** the first letter: `Move index.html file to root`, not `move index.html file to root`
- Does **not** end with a period
- Targets **50 characters** or fewer (hard limit **72**)
- Describes one coherent change

An optional `<scope>:` or `<category>:` may precede the subject:

```text
Person class: Remove static imports
Main.java: Remove blank lines
bug fix: Add space after name
chore: Update release date
```

## Commit body

Non-trivial commits need a body.

- Separate subject and body with one blank line
- Wrap body text at **72 characters**
- Separate paragraphs with blank lines
- Use bullet points when they help

Explain **what** and **why**, not how (the diff shows how). Give enough detail that a reviewer can judge the change without reading the diff first. Do not repeat information already in code comments of the same commit. If the body grows too long, split the commit.

Structure:

```text
{current situation} -- present tense

{why it needs to change}

{what is being done about it} -- imperative mood

{why it is done that way}

{any other relevant info}
```

Avoid `currently` and `originally` when describing the present situation. `Let's` may introduce the section that describes the change.

```text
Find command: make matching case-insensitive

Find command is case-sensitive.

A case-insensitive find is more user-friendly because users cannot be
expected to remember the exact case of the keywords.

Let's,
* update the search algorithm to use case-insensitive matching
* add a script to migrate stress tests to the new format
```

## Branch names

- Meaningful keywords in **kebab-case**: `refactor-ui-tests`
- If the branch tracks an issue: `issueNumber-keywords-from-title`, e.g. `1234-ui-freeze-error`

## Common mistakes

| Mistake | Required |
| --- | --- |
| `Fixed parser bug.` | `Fix parser bug` (imperative, no period) |
| Subject-only commit for a non-trivial change | Add a body with situation, why, and what |
| Body that restates the diff | Explain why the approach was chosen |
| `FixParserBug` or `fix_parser` as a branch | `fix-parser-bug` |
