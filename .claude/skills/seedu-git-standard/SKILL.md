---
name: seedu-git-standard
description: Git commit and branch-naming conventions for this project, based on the SE-EDU git conventions guide (se-education.org/guides/conventions/git.html). Apply to every commit and branch created in this project.
---

# SE-EDU Git Conventions

This project follows the [SE-EDU git conventions guide](https://se-education.org/guides/conventions/git.html). Apply this to every commit made in this repository.

## Commit subject line

- Hard limit 72 characters, aim for ~50.
- Imperative mood: "Add README.md", not "Added README.md".
- Capitalize the first letter: "Move index.html file to root".
- No period at the end: "Update sample data", not "Update sample data.".
- Optional scope prefix when it adds clarity: `Person class: Remove static imports`.

## Commit body

- Blank line between subject and body.
- Wrap body text at 72 characters.
- Blank line between paragraphs; bullet points are fine for a list of distinct changes.
- Explain **what and why**, not how — the diff already shows how; don't restate it line by line.
- Give enough context that someone can judge the change without reading the code.
- If the body is getting too long to summarize cleanly, that's a sign the change should be split into multiple commits instead.
- Useful shape to aim for (not a rigid template): current situation → why a change is needed → what this commit does → why this approach → anything else relevant.

## Branch names

- `kebab-case` with meaningful keywords, e.g. `refactor-ui-tests`.
- Issue-related branches: `issueNumber-keywords-from-title`, e.g. `1234-ui-freeze-error`.
- This project's course-specific increment branches (`branch-Level-N`, `branch-A-Name`) are named per the assignment's own instructions and take precedence over the kebab-case rule above.

## When applying this skill

Apply this to every commit from now on in this project: draft the subject/body per the rules above before running `git commit`.
