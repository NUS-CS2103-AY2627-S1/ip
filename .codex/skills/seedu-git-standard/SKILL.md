---
name: seedu-git-standard
description: Apply the mandatory SE-EDU Git conventions when proposing or creating commits, commit messages, or branch names in this project.
---

# SE-EDU Git Standard

Use the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) for every commit message and
branch name in this repository. Explicit user and repository instructions still take precedence, including limits on
when commits, branches, tags, or pushes are authorized.

## Prepare a commit

Review the changes that will be included before drafting the message. Keep each commit cohesive. If explaining the
change requires an overly long body or unrelated rationales, split it into finer-grained commits when the user has
authorized that commit structure.

### Subject

- Write a meaningful subject for every commit.
- Aim for at most 50 characters; never exceed 72 characters.
- Use the imperative mood, as if completing the sentence “If applied, this commit will ...”.
- Capitalize the first letter.
- Do not end with a period.
- Optionally prefix an applicable scope or category followed by a colon, such as `Storage: Handle malformed records`
  or `chore: Update release date`.

### Body

Add a body for every non-trivial commit.

- Separate the subject and body with one blank line.
- Wrap body text at 72 characters and separate paragraphs with blank lines.
- Use bullet points when they make the explanation clearer.
- Explain what the change is and why it is needed or designed that way. Leave implementation details visible in the
  diff unless they are relevant to understanding the decision.
- Give enough context for a reviewer to judge the change without first reading the diff.
- Prefer this progression when it fits: describe the existing situation in present tense, explain why it needs to
  change, state what the commit does in imperative mood, explain why that approach was chosen, and add other relevant
  context.
- Avoid redundant qualifiers such as `currently` and `originally`, and do not repeat information already captured in
  code comments.

## Name branches

- Use a meaningful kebab-case name made from relevant keywords, such as `refactor-ui-tests`.
- For work tied to an issue, use `issueNumber-keywords-from-issue-title`, such as `1234-ui-freeze-error`.
- Retain any repository-required branch prefix while keeping the remaining name in this format.

## Verify before handoff

- Re-read the exact staged or proposed commit contents and confirm the message describes that scope.
- Check the subject’s mood, capitalization, punctuation, and length.
- Check that every non-trivial commit has a useful body and that all body lines are at most 72 characters.
- Do not create a commit, branch, tag, or push unless the user has authorized that action.
