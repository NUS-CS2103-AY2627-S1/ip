---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when creating, reviewing, or proposing commit messages and branch names in this project.
---

# SE-EDU Git Standard

Use this skill whenever creating, reviewing, or proposing a commit, commit
message, branch name, or tag in this repository. The authoritative source is
the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subject

- Every commit must have a well-written, meaningful subject.
- Prefer 50 characters or fewer; never exceed the hard limit of 72 characters.
- Use the imperative mood: `Add README.md`, not `Added README.md` or
  `Adding README.md`.
- Capitalize the first letter and do not end with a period.
- A scope or category may precede the subject when useful, such as `Parser: Reject empty todo descriptions`.

## Commit body

- Add a body for every non-trivial commit, separated from the subject by one blank line.
- Wrap body lines at 72 characters and use blank lines between paragraphs.
- Explain what changed and why; leave an explanation of how the diff works to
  the diff itself.
- Structure the body as appropriate:
  1. Describe the current situation in the present tense.
  2. Explain why it needs to change.
  3. Describe the change in the imperative mood.
  4. Explain why the chosen approach is appropriate.
  5. Add other relevant information if needed.
- Use blank lines between paragraphs and bullets when they improve clarity.
- Avoid terms such as `currently` and `originally`; the present situation is
  implied.
- Minimize repetition of information already present in code comments.

## Branch names

- Use meaningful kebab-case keywords, such as `refactor-ui-tests`.
- For issue-related work, use `issueNumber-some-keywords-from-issue-title`.

## Tags

- Use lightweight tags unless the user explicitly requests an annotated tag.

## Workflow

1. Inspect the staged diff and identify the user-visible or project-level purpose.
2. Draft an imperative, capitalized subject without a trailing period.
3. Keep the subject within 72 characters and add a 72-column-wrapped body when the change is non-trivial.
4. Check that the message explains what and why, while leaving implementation details to the diff.
5. Use a meaningful kebab-case branch name; prefix issue-related branches
   with the issue number, such as `1234-ui-freeze-error`.
6. Do not create a commit, tag, or push unless the user explicitly asks for
   it.
