---
name: seedu-git-standard
description: Draft, review, or apply SE-EDU-compliant Git commit messages and branch names in this repository. Use when proposing, creating, amending, squashing, merging, or reviewing commits, or when naming branches; do not use for Git work unrelated to messages or branch names.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
for every commit message and branch name in this repository.

## Before writing a commit message

1. Inspect the staged diff and repository status so the message describes the
   exact commit rather than the wider task or unstaged work.
2. Determine whether the commit is trivial. Non-trivial commits require a body.
3. Do not create, amend, squash, merge, or otherwise change commits unless the
   user has explicitly authorized that Git operation.

## Subject

- Write a concise, meaningful subject. Aim for 50 characters and never exceed
  72 characters.
- Use the imperative mood so the subject completes: “If applied, this commit
  will ...”. For example, use `Add README.md`, not `Added README.md` or
  `Adding README.md`.
- Capitalize the first letter of the change phrase.
- Do not end the subject with a period.
- Add an optional `<scope>:` or `<category>:` prefix when it improves clarity,
  for example `Parser: Reject missing task numbers` or
  `chore: Update release date`.

## Body

- Add a body for every non-trivial commit and separate it from the subject with
  one blank line.
- Wrap body text at 72 characters and use blank lines between paragraphs.
- Use bullet points when they communicate multiple related points more clearly.
- Explain WHAT changed and WHY it was necessary or designed that way. Leave HOW
  details to the diff unless they are needed to justify a decision.
- Give enough context for a reader to judge the change without reading the diff,
  but avoid repeating information already captured in code comments.
- Prefer this order when the information is relevant:
  1. Describe the prior situation in the present tense.
  2. Explain why it needs to change.
  3. State what the commit does in the imperative mood.
  4. Explain why that approach was selected.
  5. Add other relevant consequences, limitations, or references.
- Avoid words such as “currently” and “originally” when describing the prior
  situation because that timing is implied.
- If the message becomes too long, recommend splitting the work into smaller,
  independently meaningful commits.

## Branch names

- Use a meaningful kebab-case name made from relevant keywords, such as
  `refactor-ui-tests`.
- For issue-related branches, use
  `issueNumber-some-keywords-from-issue-title`, such as
  `1234-ui-freeze-error`.

## Final review

Before presenting or applying a message, verify the subject mood, capitalization,
punctuation, and length; the subject/body blank line; every body line's width;
and that the message covers only the staged change.

If a user provides an exact message that conflicts with this standard, follow the
user's explicit instruction and briefly identify the exception.
