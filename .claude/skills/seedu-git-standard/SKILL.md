---
name: seedu-git-standard
description: Apply and review the SE-EDU Git conventions for commit messages and branch names in this project. Use whenever proposing, reviewing, or creating commits or branches here.
---

# SE-EDU Git Standard

Apply the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
whenever proposing, reviewing, or creating a commit message or branch name in this
repository.

## Commit subjects

- Write a meaningful subject in the imperative mood.
- Capitalize the first letter and do not end the subject with a period.
- Aim for at most 50 characters; never exceed 72 characters.
- Add a meaningful `<scope>:` or `<category>:` prefix when it improves clarity.

## Commit bodies

- Add a body for every non-trivial commit and separate it from the subject with a
  blank line.
- Wrap body text at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why it changed. Leave implementation details that are
  already clear from the diff out of the message.
- Describe the existing situation in the present tense and describe the change in
  the imperative mood.
- Include enough rationale for a reviewer to judge the purpose of the change
  without reading the diff. Use bullet points when they improve readability.
- Recommend splitting the commit when its message needs an excessively long or
  unfocused explanation.

## Branch names

- Use a meaningful kebab-case name made from relevant keywords, such as
  `refactor-ui-tests`.
- For work tied to an issue, use
  `issueNumber-keywords-from-issue-title`, such as `1234-ui-freeze-error`.

## Workflow

1. Inspect the staged or proposed diff before drafting or reviewing a commit
   message.
2. Check the subject and, when required, the body against every rule above.
3. Check any proposed branch name against the branch-name rules.
4. Do not create a commit, branch, tag, or push unless the user has authorized that
   action.
