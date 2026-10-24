---
name: seedu-git-standard
description: Enforces the SE-EDU git convention (https://se-education.org/guides/conventions/git.html) for every commit message and branch name in this project — subject-line length/mood/capitalization, body wrapping and structure, and kebab-case branch naming. Always consult this before writing any git commit message or creating any branch in this repo, and use it as the checklist when asked to check "commit message", "commit convention", "git standard", or "branch name" compliance. This is a project-wide mandate (see AGENTS.md), not optional style guidance.
---

# SE-EDU Git Standard

This project follows the SE-EDU git convention
(https://se-education.org/guides/conventions/git.html) for every commit
made in this repo. Apply these rules to every commit message you draft —
don't wait to be asked to "check the commit style."

## Commit message: subject line

- **Length**: aim for ≤50 characters; 72 is the hard limit. Count it
  before proposing the message, don't estimate.
- **Mood**: imperative — "Add README.md", not "Added README.md" or
  "Adds README.md".
- **Capitalization**: capitalize the first letter.
- **Punctuation**: no trailing period.
- **Optional scope prefix**: a short category/scope like `Person class:`
  or `bug fix:` before the imperative verb is allowed when it adds
  clarity (this project's own convention has been using increment names
  this way, e.g. `Level 7: fix Java convention violations`).

## Commit message: body

Required for any non-trivial commit (more than a one-line, self-evident
change); a bare subject is fine for genuinely trivial commits (typo
fixes, single-line tweaks).

- **Blank line** between subject and body — always.
- **Wrap body lines at 72 characters.** Check every line, not just the
  first.
- **Blank lines separate paragraphs** within the body.
- **Structure**: current situation (present tense) → why the change is
  needed → what's being done (imperative mood, same as the subject) →
  why it's being done that way → other relevant info. Not every commit
  needs every part, but that's the order when multiple parts apply.
- **Explain WHAT and WHY, not HOW** — the diff already shows how;
  readers reviewing history want the reasoning, not a restatement of
  the code.
- **Bullet points** are fine and often clearer than prose for a commit
  touching several distinct things.
- **Don't duplicate code comments** — if the "why" is already captured
  in a comment right next to the change, the commit body doesn't need
  to repeat it verbatim.

## Branch names

- **kebab-case**, meaningful keywords from what the branch is for, e.g.
  `refactor-ui-tests`.
- **Issue-related branches**: `issueNumber-some-keywords-from-title`,
  e.g. `1234-ui-freeze-error`.
- This project's course-mandated increment branches (`branch-Level-7`,
  `branch-A-MoreOOP`, etc.) are an explicit exception required by the
  course itself — don't rename those to kebab-case, they follow the
  course's own naming instruction instead. Apply the kebab-case rule to
  any *other* branch (e.g. one you create for an ad hoc fix or
  exploration outside the course's increment structure).

## Checking a commit before proposing it

1. Count the subject line's characters.
2. Confirm it's imperative mood, capitalized, no trailing period.
3. If the change is non-trivial, draft a body: blank line, then
   what/why in the structure above, each line ≤72 characters.
4. Re-read the body for anything that just restates the diff instead of
   explaining the reasoning — cut it.
