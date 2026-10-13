---
name: seedu-git-standard
description: Mandates the SE-EDU Git commit message and branch naming conventions for all commits in this project.
---

# Git Standard (SE-EDU)

Authoritative reference: <https://se-education.org/guides/conventions/git.html>

## Commit subject line

* Required for every commit.
* Limit to 50 characters (hard limit 72).
* Imperative mood: `Add README.md`, not `Added ...` or `Adding ...`.
* Capitalize the first letter; no trailing period.
* Optional `<scope>:` prefix when useful (e.g. `Parser: ...`).

## Commit body

* Non-trivial commits need a body giving WHAT and WHY, not HOW (the diff shows HOW).
* Blank line between subject and body; wrap body at 72 characters.
* Structure: current situation, why it needs to change, what is being done
  (imperative), why it is done that way.
* `Let's` may introduce the change section; use bullet lists when they help.

## Branch names

* Meaningful keywords in kebab-case, e.g. `refactor-ui-tests`.
* For issue-driven branches: `issueNumber-keywords` (e.g. `1234-ui-freeze-error`).
