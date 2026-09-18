---
name: seedu-git-standard
description: Apply and review the SE-EDU Git conventions when creating commits, writing commit messages, or naming branches in this project.
---

# SE-EDU Git Standard

Apply the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever
creating a commit, proposing or reviewing a commit message, or naming a branch in this project.

## Commit subjects

- Give every commit a well-written subject in the imperative mood.
- Capitalize the subject's first letter and do not end it with a period.
- Aim for no more than 50 characters and never exceed 72 characters.
- Add a meaningful `<scope>:` or `<category>:` prefix when it improves clarity.

## Commit bodies

- Add a body for every non-trivial commit, separated from the subject by a blank line.
- Wrap body text at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why it was necessary. Leave implementation details to the diff unless
  they are important context for evaluating the change.
- Describe the existing situation in the present tense and describe the change in the imperative
  mood. Include relevant tradeoffs or context.
- Use bullet points when they communicate a set of changes more clearly than prose.
- Keep the message proportional to the commit. Split changes into smaller focused commits when the
  body becomes overly long or describes unrelated work.

## Branch names

- Use a meaningful kebab-case name built from relevant keywords.
- For issue-related work, use `issueNumber-keywords-from-issue-title`, such as
  `1234-ui-freeze-error`.

## Workflow

Before creating any commit, inspect the staged diff to confirm that it is focused and that the
proposed subject and body accurately describe it. Do not commit or push unless the user explicitly
authorizes that action.
