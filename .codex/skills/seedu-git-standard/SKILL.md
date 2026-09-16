---
name: seedu-git-standard
description: Apply or audit the SE-EDU Git conventions when planning branches or creating, reviewing, amending, tagging, or proposing commits in this project.
---

# SE-EDU Git Standard

Apply the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever working with
branches or commits in this repository. Preserve the user's authority over Git mutations: inspecting Git state
does not authorize staging, committing, amending, tagging, merging, or pushing.

## Commit subject

- Write a clear subject for every commit.
- Use imperative mood: `Add tests`, not `Added tests` or `Adding tests`.
- Capitalize the first letter and do not end with a period.
- Aim for at most 50 characters and never exceed 72 characters.
- Add an optional `<scope>:` or `<category>:` prefix only when it improves clarity.

## Commit body

- Add a body for every non-trivial commit. Separate it from the subject with one blank line.
- Wrap body lines at 72 characters. Use blank lines between paragraphs and bullets when they improve readability.
- Explain what changed and why it should change; leave implementation details that are obvious from the diff out.
- Describe the existing situation in present tense. Describe the change in imperative mood.
- Avoid redundant qualifiers such as `currently` and `originally` when the tense already conveys that context.
- Include enough rationale for a reviewer to judge the change without reading the diff. If that requires an
  overly long message, split the work into smaller coherent commits instead.

## Branch names

- Use meaningful keywords in kebab-case, such as `refactor-ui-tests`.
- For issue-related work, use `issueNumber-relevant-keywords`, such as `1234-ui-freeze-error`.
- Follow an exact branch name required by the user or course even when it uses another format.

## Project-specific Git rules

- Do not stage, commit, amend, tag, merge, push, delete a branch, or otherwise mutate Git state unless the user
  explicitly requests that action.
- Inspect the worktree before proposing or creating a commit. Preserve unrelated user changes and do not bundle
  them into the commit.
- Keep each commit focused on one coherent purpose. Stage only the files or hunks that belong to that purpose.
- Use lightweight tags unless the user requests an annotated tag or course instructions require one.
- Do not push unless explicitly requested. When pushing a course branch workflow, verify every specifically
  required branch and tag rather than assuming they accompany another ref.

## Workflow

1. Read the applicable `AGENTS.md` and the user's exact Git or course instructions.
2. Inspect branch, status, diff, and relevant history before deciding what belongs in the operation.
3. Identify unrelated, generated, ignored, or sensitive files and keep them out of the operation.
4. For a commit, check that the staged diff is complete, focused, and tested in proportion to risk.
5. Draft the subject and body using the rules above. Check subject and body line lengths before committing.
6. Perform only the Git mutations the user explicitly authorized, and report the resulting commit, tag, branch,
   or push state accurately.
