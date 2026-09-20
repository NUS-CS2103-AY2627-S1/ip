---
name: seedu-git-standard
description: >-
  Apply SE-EDU Git commit and branch conventions for this project. Use when
  writing commit messages, creating branches, or when the user asks to commit,
  push, or propose a commit message.
---

# SE-EDU Git standard

**Source:** https://se-education.org/guides/conventions/git.html

## Commit message: subject (mandatory)

Every commit needs a well-written subject line:

| Rule | Good | Bad |
|------|------|-----|
| Imperative mood | `Add README.md` | `Added README.md` |
| Capitalize first letter | `Fix parser bug` | `fix parser bug` |
| No trailing period | `Update sample data` | `Update sample data.` |
| ≤ 50 chars (hard limit 72) | `Extract Parser class` | long rambling subject |

Optional prefix with colon: `Parser: Extract task number parsing`

## Commit message: body (non-trivial commits)

Separate subject from body with a blank line. Wrap body at 72 characters.

Explain **what** and **why**, not how (the diff shows how). Use bullet points when helpful.

Structure:

```
{current situation — present tense}

{why it needs to change}

{what is being done — imperative mood, often starting with "Let's"}

{why done that way}

{other relevant info}
```

### Example

```
Parser: Extract task number validation

Task number parsing is duplicated across mark, unmark, and delete handlers.

Centralising validation in Parser reduces duplication and keeps error messages
consistent.

Let's move parseTaskNumber() into Parser and update Axiom to delegate to it.
```

## Branch names

- kebab-case with meaningful keywords: `refactor-ui-tests`
- Issue-linked: `1234-ui-freeze-error`

## Checklist before committing

- [ ] Subject is imperative, capitalized, no period, ≤ 72 chars
- [ ] Non-trivial commits have a body explaining what and why
- [ ] Commit message follows SE-EDU format (not just a list of file names)
