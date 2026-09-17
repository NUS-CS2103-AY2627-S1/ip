---
name: seedu-git-standard
description: >-
  Applies SE-EDU Git conventions for commit subjects, bodies, and branch names
  in this project. Use when creating commits, writing commit messages, naming
  branches, or when the user mentions Git conventions or SE-EDU git style.
---

# SE-EDU Git conventions

Follow https://se-education.org/guides/conventions/git.html for all commits and branch names in this project.

## When to apply

Read this skill before every `git commit` in this repository.
Also use it when suggesting branch names or rewriting commit messages.

## Commit subject

- Required for every commit.
- Soft limit 50 characters; hard limit 72.
- Imperative mood: `Add README.md`, not `Added` / `Adding`.
- Capitalize the first letter.
- No trailing period.
- Optional prefix when useful: `Parser: …`, `bug fix: …`, `chore: …`.

## Commit body (non-trivial commits)

- Separate subject and body with a blank line.
- Wrap body at 72 characters.
- Explain WHAT and WHY, not HOW (diff shows how).
- Prefer present tense for the current situation; imperative for the change.
- Structure:

```
{current situation}

{why it needs to change}

{what is being done about it}

{why it is done that way}

{any other relevant info}
```

- Use `Let's …` to introduce the change section when it helps.
- Bullet lists are fine when clearer than long paragraphs.
- Do not restate what is already obvious from new code comments.

## Branch names

- Meaningful kebab-case keywords: `refactor-ui-tests`.
- Issue-linked form when applicable: `1234-ui-freeze-error`.

## This project's tags

Use lightweight tags unless the user asks for annotated tags.
Do not commit or push unless the user explicitly asks.

## Checklist before committing

- [ ] Subject is imperative, capitalized, ≤ 72 chars, no period
- [ ] Non-trivial change has a WHY-focused body wrapped at 72
- [ ] Branch name is kebab-case if newly created

## Source

Full rule text: https://se-education.org/guides/conventions/git.html
