---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing, creating, or amending commits and when naming branches in this project.
---

# SE-EDU Git Standard

Apply these rules whenever preparing or creating Git commits or naming
branches in this repository.

The authoritative source is the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).
This skill paraphrases those rules as a working checklist.

## Commit subject

- Give every commit a well-written subject.
- Aim for at most 50 characters and never exceed 72 characters.
- Use the imperative mood, as if completing the sentence "This commit will
  ...".
- Capitalize the first letter.
- Do not end with a period.
- Add an applicable `<scope>:` or `<category>:` prefix when it improves
  clarity; it is optional.

## Commit body

Add a body for every non-trivial commit. Omit it only when a small,
self-explanatory change is fully described by its subject.

- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters.
- Use blank lines between paragraphs and bullets when they make the message
  easier to understand.
- Explain what changed and why it was needed. Leave implementation mechanics
  to the diff.
- Give enough context for a reviewer to judge the change without first reading
  the diff, while avoiding repetition of code comments.
- Describe the pre-change situation in the present tense, explain why it needs
  to change, describe the change in the imperative mood, and record relevant
  rationale or follow-up context.
- Avoid words such as "currently" and "originally" when the time frame is
  already implied.
- If the body becomes too long or covers unrelated reasons, split the work into
  finer-grained commits.

## Branch names

- Use a meaningful kebab-case name made from relevant keywords, such as
  `refactor-ui-tests`.
- For work tied to an issue, use
  `issueNumber-keywords-from-issue-title`, such as
  `1234-ui-freeze-error`.

## Before finishing

Inspect the exact changes included in the commit, then check the complete
message against the subject and body rules. Creating or suggesting a commit
message does not authorize committing, amending, pushing, or changing branch
history; obtain the user's explicit request for those actions.
