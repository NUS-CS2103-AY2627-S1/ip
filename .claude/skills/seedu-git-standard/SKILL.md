---
name: seedu-git-standard
description: Use when writing a git commit message, naming a git branch, or reviewing either in this project — the SE-EDU Git conventions (subject-line rules, body rules, branch naming) that the CS2103T course grades against. Invoke before every commit.
---

# SE-EDU Git conventions

This project follows the SE-EDU Git conventions
(<https://se-education.org/guides/conventions/git.html>). The CS2103T course
mandates the **commit-message subject-line** rules and the **branch-naming**
rules. It treats the commit body as optional, but requires that when a body
is present it follows the conventions below.

**This project always writes a body** — its own `AGENTS.md` asks every commit
message to carry enough detail to explain the rationale for the change — so
treat the body rules as mandatory here too.

Invoke this skill before writing any commit message or creating any branch.

## Commit subject line

- Limit to **50 characters**; **72 is a hard limit**.
- Use the **imperative mood**: `Add README.md`, not `Added README.md` or
  `Adding README.md`. Read it as "this commit will _...".
- **Capitalise the first letter.**
- **No trailing period.**
- An optional `Scope: ` (or `Category: `) prefix is allowed and is used
  throughout this repo to name the increment, e.g.
  `A-CodingStandard: Conform JavaDoc summaries to the standard`,
  `Person class: Remove static imports`.

```
good:  Level-9: Add a find command to search task descriptions
bad:   added find command.
```

## Commit body

- Separate the subject from the body with **one blank line**.
- **Wrap the body at 72 characters.**
- Separate paragraphs with blank lines; use bullet points where they help.
- Explain **what** changed and **why**, not **how** — the diff shows the how.
  A useful shape: current situation → reason for the change → what was done →
  why this approach.

### Required trailer lines

This project appends two trailer lines to every commit message, after a
blank line at the end of the body:

```
Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>
Claude-Session: <current session URL>
```

The `Co-Authored-By` line is verbatim. The `Claude-Session` value is the
URL of the Claude Code session making the commit — it differs per session
and the harness supplies it in its instructions; never copy a URL from a
past commit or from this file.

## Branch names

- Use a meaningful name of relevant keywords in **kebab-case**:
  `refactor-ui-tests`.
- For an issue-linked branch, prefix the issue number:
  `1234-ui-freeze-error` (`issueNumber-keywords-from-issue-title`).
- Note: CS2103T increment branches in this repo use the fixed form
  `branch-<Increment-Id>` (e.g. `branch-A-CodingStandard`), which the
  grading script expects — keep that pattern for increments.

## Project-specific reminders

- Never `git add -A` or `git add .` — stage explicit paths only.
- Never stage `docs/CS2103T-iP-guide.md` (local-only course reference).
- Do not commit or push unless explicitly asked.
- Increments are tracked by **git tags** matching the exact increment ID;
  tags must be pushed explicitly with `git push --tags`.
- Branch-based increments merge with `--no-ff`.
