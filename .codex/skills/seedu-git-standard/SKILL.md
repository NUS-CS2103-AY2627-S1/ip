---
name: seedu-git-standard
description: Write and review Git branch names and commit messages using the required SE-EDU Git conventions for this project.
---

# SE-EDU Git standard

Use this skill before creating a commit or branch, and when reviewing a proposed commit message in this project.

Follow the required subject conventions and the relevant body and branch conventions in the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit messages

- Write a meaningful subject in imperative mood, beginning with a capital letter and without a final period.
- Aim for a subject of 50 characters or fewer; never exceed 72 characters.
- Add an optional, meaningful scope or category followed by a colon when it improves clarity, for example `Parser: Reject blank task descriptions`.
- For non-trivial commits, add a body separated from the subject by one blank line. Wrap body lines at 72 characters, use blank lines between paragraphs, and use bullets when useful.
- Explain what changed and why; leave implementation details to the diff. If a body becomes too long, consider whether the change should be split into smaller commits.

## Branch names

- Use meaningful, relevant keywords in kebab case, for example `refactor-ui-tests`.
- For issue-related work, use `issueNumber-keywords-from-issue-title`, for example `1234-ui-freeze-error`.

If a project instruction gives a required branch name, use that name even when it is an exception to the convention.
