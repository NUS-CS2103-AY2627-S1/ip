---
name: git-commit-message
description: Help with this project's Git commit messages and individual-project branch workflow using SE-EDU conventions.
---

# Git Commit Message

Use this skill when the user asks for help writing, choosing, polishing, or
reviewing a Git commit message for this project, or when the user asks how to
handle the individual-project Git branch, merge, tag, and push workflow.

## Source Convention

Follow the SE-EDU Git conventions for commit messages:
https://se-education.org/guides/conventions/git.html

The user-provided project requirement is:

- The commit message subject is required and must follow the SE-EDU subject
  conventions.
- A commit message body is optional, but if one is written, it must follow at
  least the basic SE-EDU body conventions.

## Subject Rules

Draft subjects that satisfy all of these rules:

- Prefer 50 characters or fewer; never exceed 72 characters.
- Use the imperative mood, as if completing "If applied, this commit will ...".
- Capitalize the first letter of the subject after any scope or category prefix.
- Do not end the subject with a period.
- Add a useful `<scope>:` or `<category>:` prefix only when it clarifies the
  change, such as `Parser:`, `Main.java:`, `bug fix:`, or `chore:`.

Avoid Conventional Commits syntax unless the user explicitly asks for it or the
repository already requires it; SE-EDU conventions are the project baseline.

## Body Rules

Only propose a body when the commit is non-trivial, the user asks for one, or
the diff needs context that the subject cannot carry.

When writing a body:

- Separate the subject and body with a blank line.
- Wrap body text at 72 characters.
- Use blank lines between paragraphs.
- Use present tense for the current situation.
- Explain what the change is and why it is needed. Do not spend the body
  restating how the diff implements it.
- Use bullet points when they make the explanation clearer.
- A useful body shape is: current situation, why it needs to change, what is
  being done, why that approach fits, and any other relevant context.

## Workflow

If the user provides a diff or summary, draft directly from it. If the user only
asks for a commit message and the repository is available, inspect the relevant
Git state first with read-only commands such as `git status --short` and
`git diff --stat`; use `git diff` or `git diff --cached` when more detail is
needed.

Provide one recommended subject first. If there are multiple plausible scopes or
levels of specificity, include up to two alternatives and briefly explain the
tradeoff. Include an optional body only when useful or requested.

If the changes appear unrelated or too broad for one commit, say so and suggest
splitting the commit before drafting separate subjects.

## Individual Project Branch Workflow

For individual-project levels or milestones done on a new branch, generalize
the course workflow from the example `branch-Level-7` and tag `Level-7`.

When working on a level or milestone named `<LevelName>`:

- Start a branch named `branch-<LevelName>`.
- Implement the work while committing to that branch at appropriate points.
- Merge the branch back into `master` using a merge commit. Do not use a
  fast-forward merge for this workflow.
- Tag the merge commit on `master` with the corresponding milestone tag, such
  as `<LevelName>`.
- Push all three required refs to the fork: `master`, `branch-<LevelName>`,
  and the `<LevelName>` tag.
- Do not delete the branch after merging.

The grading scripts detect only merged branches. Pushing `master` does not
automatically push the merged feature branch, so explicitly push both `master`
and `branch-<LevelName>` in addition to the tag.
