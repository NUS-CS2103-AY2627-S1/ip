---
name: seedu-git-standard
description: Git conventions and commit message rules for SE-EDU / CS2103 projects based on https://se-education.org/guides/conventions/git.html.
---

# SE-EDU Git Standard

Enforce these Git conventions across all commits and branches in this project. Reference: https://se-education.org/guides/conventions/git.html

## 1. Commit Message Subject Line
- **Length**: Limit subject line to ~50 characters (hard limit: 72 characters).
- **Imperative Mood**: Use imperative mood (e.g., `Add README.md`, `Fix parse bug`, not `Added`, `Adding`, `Fixes`).
- **Capitalization**: Capitalize the first letter of the subject line (e.g., `Move file to root`, not `move file...`).
- **No Trailing Period**: Do NOT end the subject line with a period (`.`).
- **Optional Scope/Category**: Prefix with `<scope>:` or `<category>:` when applicable:
  - Examples: `Parser: Fix deadline delimiter check`, `Ui.java: Remove redundant blank lines`, `bug fix: Handle negative task index`, `chore: Update build dependencies`.

## 2. Commit Message Body
- Mandatory for non-trivial commits to explain context, motivation, and details.
- **Separation**: Separate subject line from body with exactly one blank line.
- **Line Wrapping**: Wrap the body at 72 characters per line.
- **Paragraphs & Bullets**: Separate paragraphs with a blank line; use bullet points where helpful.
- **Explain WHAT and WHY, not HOW**:
  - Readers can inspect the diff to see *how* changes were implemented.
  - Explain *what* the commit does and *why* that approach was chosen.
- **Recommended Body Structure**:
  1. `{current situation}` (in present tense; avoid 'currently'/'originally' as they are implied).
  2. `{why it needs to change}`.
  3. `{what is being done about it}` (imperative mood; `Let's` can introduce this section).
  4. `{why it is done that way}`.
  5. `{any other relevant info / references}`.
- **Example**:
  ```text
  Find command: Make matching case-insensitive

  Find command is case-sensitive. A case-insensitive find is more
  user-friendly because users cannot be expected to remember the exact
  case of the keywords.

  Let's,
  * update the search algorithm to use case-insensitive matching
  * add tests covering case variations
  ```

## 3. Branch Naming Conventions
- Meaningful names in `kebab-case` (e.g., `refactor-ui-tests`, `add-gradle-support`).
- If tied to an issue: `issueNumber-keywords-from-issue-title` (e.g., `1234-ui-freeze-error`).
