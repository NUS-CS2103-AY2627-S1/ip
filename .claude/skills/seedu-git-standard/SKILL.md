---
name: seedu-git-standard
description: The Git commit message and branch naming conventions this project follows. Use whenever proposing or writing a commit message, or naming a new branch, in this project.
---

# seedu-git-standard

Reference: https://se-education.org/guides/conventions/git.html
(the SE-EDU Git conventions).

## Commit message: subject line

- Try to limit to 50 characters; 72 is a hard limit.
- Imperative mood — "Add README.md", not "Added README.md".
- Capitalize the first letter.
- Do not end with a period.
- May optionally be prefixed with a scope/category, e.g.
  `Person class:` or `bug fix:`.

## Commit message: body (when needed)

- Separate from the subject with one blank line.
- Wrap at 72 characters; use blank lines between paragraphs, and
  bullet points where useful.
- Explain WHAT and WHY, not HOW — the reader can get HOW from the
  diff. Cover: the current situation, why the change is needed, what's
  being done, why it's done that way, and any other relevant info.
- Use imperative mood in the "what's being done" part too.
- Avoid words like "currently" or "originally" (write from the
  post-change point of view, not narrating the change over time).

## Branch names

- kebab-case with meaningful keywords, e.g. `refactor-ui-tests`.
- Issue-related branches: `issueNumber-some-keywords-from-issue-title`,
  e.g. `1234-ui-freeze-error`.

## Applying this in this project

- Use lightweight tags unless the user asks for an annotated tag.
- Do not commit or push unless explicitly asked.
- Follow this standard for every commit from now on: propose the
  message here (or in the tool call) before committing, so it can be
  reviewed.
