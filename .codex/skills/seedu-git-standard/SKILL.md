---
name: seedu-git-standard
description: The Git commit and branch conventions all commits in this project must follow, based on se-education.org's Git conventions. Use whenever writing a commit message or naming a branch in this repository.
---

# se-edu Git Standard

Source: https://se-education.org/guides/conventions/git.html

Apply these to every commit and branch in this repository.

## Commit subject line

- **Imperative mood.** `Add README.md` — not `Added README.md`, not `Adding README.md`.
- **Capitalize** the first letter: `Move index.html to root` — not `move index.html to root`.
- **No trailing period:** `Update sample data` — not `Update sample data.`
- **Length:** aim for <= 50 characters; hard limit 72.
- **Optional scope prefix** when it adds clarity: `Person class: Remove static imports`, `bug fix: Add space after name`.

## Commit body

- Separate the subject from the body with **one blank line**.
- **Wrap the body at 72 characters.**
- Explain **what** changed and **why** — not *how* (the diff shows how).
- Separate paragraphs with blank lines; bullet points are fine.
- Rough flow when useful: current situation -> why a change is needed -> what this commit does -> why done this way -> anything else relevant.
- se-edu says non-trivial commits *should* have a body. In this project (see `AGENTS.md` and Claude memory) the user prefers **very short messages**: include a body only when the subject genuinely cannot carry the rationale, and keep it to a line or two.

## Branch names

- kebab-case, meaningful keywords: `refactor-ui-tests`, `add-find-command`.
- Issue branches: `<issueNumber>-<keywords-from-issue-title>`, e.g. `1234-ui-freeze-error`.
- Note: this project's course increments use the given fixed names (`branch-A-JavaDoc`, `branch-A-CodingStandard`, `branch-Level-9`, ...); keep those as specified.

## Project rules (from `AGENTS.md`)

- Draft the commit message and get the user's explicit approval before running `git commit`.
- Do **not** add a `Co-Authored-By` trailer — the user is the sole author.
- Use lightweight tags unless an annotated tag is requested.
- Commit or push only when the user asks.

## Examples

Good subject lines:

```
Add seedu Java coding standard and apply it
Storage: Reject rows containing the pipe character
Fix off-by-one in task numbering
```

Bad:

```
Added coding standard.          # past tense + trailing period
fix bug                         # not capitalized, vague
Update Altair.java to change the parser and also tweak the Ui class and tests   # too long, lists how
```
