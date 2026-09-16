---
name: seedu-git-standard
description: SE-EDU Git conventions that must be followed for all commits and version control in this project.
---

# SE-EDU Git Conventions

All Git operations in this project must follow the SE-EDU Git conventions. These rules apply to commit messages, branch names, and other version control workflows.

## Commit Message: Subject Line

**Every commit must have a well-written subject line.**

- Try to limit the subject line to 50 characters (hard limit: 72 characters). Some tools show only a limited number of characters.
- Use the imperative mood in the subject line (e.g., "Add README.md", not "Added README.md").
- Capitalize the first letter of the subject line.
- Do not end the subject line with a period.
- You may add a `<scope>:` or `<category>:` in front when applicable (e.g., "Person class: Remove static imports", "bug fix: Add space after name").

## Commit Message: Body

**Non-trivial commits should have a body providing details.**

- Separate subject from body with a blank line.
- Wrap the body at 72 characters.
- Use blank lines to separate paragraphs.
- Use bullet points as necessary instead of long paragraphs.
- Explain WHAT and WHY, not HOW.
- Structure the body as follows:
  - Current situation (present tense)
  - Why it needs to change
  - What is being done (imperative mood)
  - Why it is done that way
  - Any other relevant info

## Branch Names

**Follow these rules to improve consistency.**

- Use a meaningful name consisting of relevant keywords in kebab-case format (e.g., "refactor-ui-tests").
- If related to an issue, use "issueNumber-some-keywords-from-issue-title" format (e.g., "1234-ui-freeze-error").

Refer to the [SE-EDU Git conventions guide](https://se-education.org/guides/conventions/git.html) for additional details.