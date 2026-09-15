---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches or writing, reviewing, or creating commits in this project.
---

# SE-EDU Git standard

Apply these rules to every future commit and branch name in this project:

## Commit subjects

- Every commit must have a well-written subject.
- Use imperative mood and capitalize the first letter.
- Do not end the subject with a period.
- Aim for 50 characters and never exceed 72 characters.
- Add a meaningful `<scope>:` or `<category>:` prefix when useful.

## Commit bodies

Non-trivial commits must have a body separated from the subject by a blank line. Wrap body lines at 72 characters and separate paragraphs or bullet lists with blank lines.

Explain what changed and why, rather than describing implementation mechanics visible in the diff. Use this order where applicable: current situation, why it needs to change, what the commit does, why that approach was chosen, and other relevant context. Keep commits focused; split an overlong or unrelated change into smaller commits when appropriate.

## Branch names

Use meaningful kebab-case names made from relevant keywords, such as `refactor-ui-tests`. For issue-related branches, use `<issue-number>-<keywords-from-issue-title>`, such as `1234-ui-freeze-error`.

Refer to the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) for the source rules and examples.
