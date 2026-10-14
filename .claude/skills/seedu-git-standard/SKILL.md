---
name: seedu-git-standard
description: Apply the SE-EDU Git commit message and branch-naming conventions required by this project. Use whenever drafting, proposing, or reviewing a git commit message or branch name in this repo.
---

# SE-EDU Git Conventions

This project (per [A-CodingStandard](https://nus-cs2103-ay2627-s1.github.io/website/admin/standardsAndConventions.html))
must follow the commit-message *subject* conventions from the
[SE-EDU Git conventions guide](https://se-education.org/guides/conventions/git.html) for every commit; body
conventions below apply whenever a body is included (bodies are optional).

## Subject line

* Capitalize the first letter.
* Imperative mood: "Add README.md", not "Added README.md" or "Adds README.md".
* No period at the end.
* Soft limit 50 characters, hard limit 72.
* May carry an optional `<scope>:`/`<category>:` prefix, e.g. `Person class: Remove static imports`.

## Body (optional, but follow this structure when one is included)

* Blank line separating the subject from the body.
* Wrap body lines at 72 characters.
* Blank line between paragraphs.
* Explain **WHAT** changed and **WHY** - not **HOW**; let the diff speak for implementation detail.
* Bullet points are fine where they aid clarity.
* A natural shape to reach for: current situation (present tense) -> why a change is needed -> what is being done
  (imperative mood) -> why it's done that way -> anything else relevant.

## Branch names

* kebab-case, meaningful keywords (e.g. `refactor-ui-tests`).
* Issue-related branch: `issueNumber-keywords` (e.g. `1234-ui-freeze-error`).

## Checking a proposed commit message against this skill

Before finalizing a commit message, verify:
1. Subject starts with a capital letter, is in the imperative mood, and has no trailing period.
2. Subject length: aim for <=50 chars; never exceed 72.
3. If there's a body: blank line before it, every line <=72 chars, and it explains what/why rather than how.
