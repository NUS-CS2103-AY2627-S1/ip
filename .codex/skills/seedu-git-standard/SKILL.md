---
name: seedu-git-standard
description: Enforce the SE-EDU Git conventions for this project. Use this skill when proposing, writing, or evaluating commit messages and branch names.
---

# SE-EDU Git Conventions

This project strictly follows the SE-EDU Git Conventions. Apply these rules when creating branch names or writing commit messages.

## Branch Names
- Use meaningful names consisting of relevant keywords in kebab-case (e.g., `refactor-ui-tests`).
- If related to an issue, use the format `issueNumber-keywords` (e.g., `1234-ui-freeze-error`).

## Commit Message: Subject Line
- **Length**: Limit to 50 characters (hard limit: 72 characters).
- **Mood**: Use the imperative mood (e.g., `Add README.md`, not `Added` or `Adding`).
- **Capitalization**: Capitalize the first letter of the subject line.
- **Punctuation**: Do NOT end the subject line with a period.
- **Prefixes (Optional)**: You may add a `<scope>:` or `<category>:` in front (e.g., `Person class: Remove static imports`, `bug fix: Add space`).

## Commit Message: Body
- **When to use**: Include a body for non-trivial commits to provide details.
- **Formatting**: 
  - Separate the subject from the body with a blank line.
  - Wrap the body at 72 characters.
  - Use blank lines to separate paragraphs.
  - Use bullet points as necessary instead of relying entirely on paragraphs.
- **Content Guidelines**:
  - Explain **WHAT** and **WHY**, not **HOW**. (The diff shows how).
  - Give enough detail so the reader can judge if it's a good change without reading the diff.
  - Minimize repeating information that is already in code comments.
- **Structure**:
  1. `{current situation}` (use present tense, avoid terms like 'currently' or 'originally' as they are implied).
  2. `{why it needs to change}`
  3. `{what is being done about it}` (use imperative mood, starting with "Let's ..." is a good practice).
  4. `{why it is done that way}`
  5. `{any other relevant info}` (e.g. links to discussions).
