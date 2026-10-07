---
name: seedu-git-standard
description: Apply SE-EDU Git conventions when preparing, reviewing, or creating commits and naming branches in this project.
---

# SE-EDU Git standard

## Source and conventions

Use the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
as the source of truth. Read it when checking compliance or resolving ambiguity.
If unavailable, use this checklist and disclose the limitation.

- Write an imperative subject with an initial capital and no final period.
  Aim for at most 50 characters; never exceed 72. A scope/category prefix is optional.
- Non-trivial changes need a body, separated from the subject by an empty line.
  Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain the change and its motivation rather than narrating the implementation.
  Describe the existing situation in present tense, the need, the proposed change
  in imperative form, the rationale for that approach, then relevant additional
  information. Avoid redundant temporal qualifiers and duplicated code comments.
  Bullets are optional; excessive explanation may indicate a change should be split.
- Choose meaningful kebab-case branch names. For issue-related work, use the issue
  number followed by relevant keywords.

## Project workflow

Read AGENTS.md before acting. This skill does not authorize commits, pushes,
history rewriting, or branch creation. Commit or push only when explicitly requested.
Keep the current branch unless the task requires another. If creating a branch,
apply the host's required prefix (for example, codex/) to the descriptive branch name.
Use lightweight tags unless the user requests annotated ones.

Inspect status, staged changes, and unstaged changes before staging. Respect the
user's requested commit boundaries and preserve unrelated work. Stage explicit
paths or selected hunks, not the entire workspace indiscriminately. Keep necessary
build/run references and regression documentation with their code change.

Before each commit, inspect the full staged diff, check for whitespace errors,
and review the subject/body against the checklist. Record actual test results;
disclose skipped or blocked verification instead of implying tests passed.
Follow the repository's Java and UI-test requirements for code changes.

After each commit, verify its message and file list. At the end, report commit
IDs, purposes, remaining changes, and verification limitations. Do not amend
existing commits or push merely to tidy up this workflow.
