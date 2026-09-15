---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing commit messages, creating commits, or naming branches in this project.
---

# SE-EDU Git Standard

Apply these rules whenever proposing or creating Git commits or naming branches in this project. Use the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) as the authority.

Before proposing or creating a commit, inspect the exact staged changes. Keep each commit focused on one coherent
purpose. If the message needs a long explanation of unrelated changes, split the changes into finer-grained commits.
Do not stage, commit, amend, rewrite history, or push unless the user has authorized that action.

## Commit Subject

- Write a meaningful subject for every commit.
- Aim for no more than 50 characters; never exceed 72 characters.
- Use the imperative mood, such as `Add README.md`, not `Added README.md` or `Adding README.md`.
- Capitalize the first letter.
- Do not end with a period.
- Add a meaningful `<scope>:` or `<category>:` prefix only when it improves clarity. Conventional Commits are
  optional and should not be imposed unless the project or user requires them.

## Commit Body

Add a body for every non-trivial commit.

- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what the change does and why it is needed. Leave implementation details to the diff unless they are
  necessary to justify a decision.
- Describe the existing situation in the present tense and describe the action in the imperative mood.
- Include enough context for a reviewer to judge the change without reading the diff first.
- Avoid redundant wording such as `currently` and `originally` when the tense already makes the timing clear.
- Use bullet points when they make several related points easier to scan.
- Avoid repeating information already captured clearly in code comments.

A useful body order is:

1. State the situation.
2. Explain why it should change.
3. State what this commit does.
4. Explain why that approach was chosen.
5. Add any other relevant context.

## Branch Names

- Use a meaningful kebab-case name built from relevant keywords, such as `refactor-ui-tests`.
- For issue-related branches, use `issueNumber-keywords-from-issue-title`, such as `1234-ui-freeze-error`.

## Review Checklist

Before proposing or creating a commit:

1. Confirm the files belong to one coherent change and exclude unrelated work.
2. Check that the subject uses imperative mood, capitalization, no final period, and at most 72 characters.
3. Add a WHAT/WHY body for non-trivial changes and wrap it at 72 characters.
4. Compare the message with the staged diff so it describes the commit accurately.
