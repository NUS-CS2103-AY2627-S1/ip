---
name: seedu-git-standard
description: Apply SE-EDU Git conventions when creating branches or preparing commits in this repository.
---

# SE-EDU Git Standard

Apply the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever creating a branch, proposing a commit message, or making a commit in this repository. This skill does not authorise commits, pushes, rebases, or other Git mutations; obtain the permission required by the task first.

## Commit messages

- Write a capitalised imperative subject with no ending period. Prefer 50 characters or fewer; never exceed 72 characters. An optional relevant `<scope>:` or `<category>:` prefix is allowed.
- Add a body for non-trivial commits. Leave one blank line after the subject, wrap body lines at 72 characters, and separate paragraphs with blank lines.
- Explain what changed and why, not implementation mechanics. Describe the current situation in present tense and the change in imperative mood. Split a commit when a clear explanation becomes too long.

## Branch names

- Use meaningful, kebab-case names formed from relevant keywords.
- For issue work, use `issueNumber-keywords-from-issue-title`, such as `1234-ui-freeze-error`.
- Retain the repository-required `codex/` prefix when creating a Codex branch; apply the convention to the descriptive branch segment.

Before committing, confirm the commit includes one focused logical change and that its message satisfies these rules.
