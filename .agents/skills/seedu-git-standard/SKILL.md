---
name: seedu-git-standard
description: Apply and review this project's SE-EDU Git conventions when proposing, writing, reviewing, or creating commits and when naming branches in this repository.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) for all commits and branch names in this project.

## Commit subjects

- Write a meaningful subject for every commit.
- Use imperative mood, capitalize the first letter, and do not end with a period.
- Aim for at most 50 characters; never exceed 72 characters.
- Add an optional `<scope>:` or `<category>:` prefix only when it improves clarity.

## Commit bodies

- Add a body for every non-trivial commit. Separate it from the subject with one blank line.
- Wrap body text at 72 characters and use blank lines between paragraphs. Use bullet points when they make the explanation clearer.
- Explain what changed and why it was needed or designed that way. Leave implementation mechanics to the diff.
- Describe the existing situation in present tense and the action taken in imperative mood. Avoid redundant qualifiers such as `currently` and `originally`.
- If the body becomes unwieldy because the commit contains unrelated concerns, split the work into finer-grained commits.

## Branch names

- Use meaningful lowercase keywords in kebab-case, such as `refactor-ui-tests`.
- For issue-related branches, use `issueNumber-keywords-from-issue-title`, such as `1234-ui-freeze-error`.
- Retain the repository-required `codex/` prefix when Codex creates a branch, unless the user requests a different prefix; apply the SE-EDU format to the part after the prefix.

## Before committing

1. Inspect the exact staged diff and confirm it contains one coherent change without unrelated files.
2. Draft and check the full message against the subject and body rules above, including line lengths.
3. Run the verification required by `AGENTS.md` for the affected files.
4. Commit only when the user has explicitly authorized a commit. This skill does not authorize committing or pushing by itself.
