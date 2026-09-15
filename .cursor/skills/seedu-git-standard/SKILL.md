---
name: seedu-git-standard
description: >-
  Apply the SE-EDU Git conventions to commit messages and branch names in this
  CS2103T iP. Use when proposing, drafting, or creating a commit or branch, when
  writing a commit message, or when the user mentions Git standard, A-CodingStandard
  git rules, or SE-EDU Git conventions.
---

# SE-EDU Git conventions

Follow [Git conventions](https://se-education.org/guides/conventions/git.html) for **all future commits** and new branches in this project.

Do not commit or push unless the user explicitly asks. Use a **lightweight** tag unless the user asks for an annotated tag. Do not commit JAR files or other build artifacts.

## Subject (required on every commit)

- Imperative mood: `Add README.md`, not `Added` / `Adding`.
- Capitalize the first letter.
- No trailing period.
- Aim for 50 characters; hard limit **72**.
- A `Type:` prefix is allowed when useful: `Person class: Remove static imports`, `bug fix: Add space after name`.

## Body (required for non-trivial commits)

- Blank line between subject and body.
- Wrap the body at **72** characters.
- Blank lines between paragraphs. Use bullets when they are clearer than prose.
- Explain **WHAT** and **WHY**, not HOW (the diff shows how).
- Do not restate comments that already appear in the same commit.
- Detailed enough that a reviewer can judge the change without reading the diff. If the body gets long, split the commit.

### Body structure

```
{current situation} -- present tense
  (do not say "currently" / "originally")

{why it needs to change}

{what is being done about it} -- imperative mood
  (you may start this part with "Let's")

{why it is done that way}

{any other relevant info}
```

### Example

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

- kebab-case keywords: `refactor-ui-tests`.
- If tied to an issue: `1234-ui-freeze-error`.

This course also uses increment branches such as `branch-A-CodingStandard` and `branch-Level-9` when the weekly iP instructions require them.

## When suggesting a Git command

State what the command does in one short sentence before or after the command.
