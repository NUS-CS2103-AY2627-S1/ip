---
name: seedu-git-standard
description: "Review and write commit messages and branch names in this project using the SE-EDU Git conventions."
---

# SE-EDU Git Standard

Apply this skill whenever you plan, review, suggest, amend, or create a commit,
or when you name a branch in this project. Use the source guide for details not
repeated here: [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subject

- Every commit must have a clear, well-written subject line.
- Aim for 50 characters or fewer; never exceed 72 characters.
- Use the imperative mood, capitalize the first letter, and do not end with a
  period. For example, use `Add README.md`, not `Added README.md`.
- Add a relevant `<scope>:` or `<category>:` prefix when it improves clarity.

## Commit body

- Give every non-trivial commit a body separated from the subject by one blank
  line. Wrap body lines at 72 characters and use blank lines between paragraphs.
- Explain WHAT changed and WHY it changed; leave HOW to the diff. Give enough
  context for a reviewer to judge the change without reading the diff first.
- Describe the current situation in present tense, then explain why it needs to
  change, what to do, and why that approach is appropriate. Avoid filler words
  such as `currently` and `originally` when they add no information.
- Use bullet points when they make several related changes easier to scan. Avoid
  repeating information already present in code comments.

## Branch names

- Use meaningful keywords in kebab-case, such as `refactor-ui-tests`.
- When a branch is tied to an issue, use
  `issueNumber-some-keywords-from-issue-title`.

## Project workflow

- Before drafting or executing a commit, inspect the staged diff and confirm the
  subject and body follow these rules. Keep unrelated changes out of the commit.
- Install the repository hook with `git config core.hooksPath .githooks` and
  treat a failed local commit-message check as blocking. The hook validates
  subject and body length before Git creates the commit.
- Do not commit or push unless the user explicitly authorizes that action. When
  authorization is given, use this skill to validate the final message before
  running Git.
