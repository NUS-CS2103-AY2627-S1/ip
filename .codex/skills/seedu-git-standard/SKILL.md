---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches or preparing commits in the Margit project.
---

# SE-EDU Git Standard

Use this skill whenever creating a branch or preparing a commit in this project. Apply the SE-EDU Git conventions at <https://se-education.org/guides/conventions/git.html>.

## Branch names

- Use a meaningful kebab-case name built from relevant keywords, such as `refactor-parser-tests`.
- For issue work, use `issueNumber-keywords-from-issue-title`, such as `1234-ui-freeze-error`.

## Commit subject

- Write an imperative, capitalized subject that states the change, such as `Add parser tests`.
- Aim for 50 characters; never exceed 72 characters.
- Do not end the subject with a period.
- Add an optional scope or category when it improves clarity, such as `Parser: Validate event arguments` or `chore: Update Gradle wrapper`.

## Commit body

- Add a body for every non-trivial commit. Leave one blank line between the subject and body, wrap body lines at 72 characters, and separate paragraphs with blank lines.
- Explain **what** is changing and **why** it is needed; do not restate implementation details visible in the diff.
- Describe the situation in present tense, then use imperative mood for the proposed change and explain why that approach is appropriate.
- If a commit message requires excessive detail, split the work into smaller, coherent commits.

## Before committing

- Review the staged diff and confirm it contains one coherent change.
- Follow project authorization and repository instructions for staging, committing, tags, generated files, and remote operations.
