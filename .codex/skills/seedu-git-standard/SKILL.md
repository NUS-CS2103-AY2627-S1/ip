---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches or drafting, reviewing, amending, or creating commits in this project.
---

# SE-EDU Git Standard

Follow the authoritative [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subject

- Give every commit a well-written subject.
- Aim for no more than 50 characters; never exceed 72 characters.
- Use imperative mood, as if completing: “If applied, this commit will ...”.
- Capitalize the first letter.
- Do not end with a period.
- Optionally prefix a relevant scope or category followed by a colon.

## Commit body

For a non-trivial commit, add a body that:

- is separated from the subject by one blank line;
- wraps at 72 characters;
- uses blank lines between paragraphs and bullets when they improve clarity;
- explains what changed and why, leaving implementation details to the diff;
- gives enough context to judge the change without first reading the diff; and
- avoids repeating code comments or other information already evident in the change.

When useful, describe the present situation, why it should change, what the
commit does, why that approach was chosen, and other relevant context. A body
that becomes unwieldy is a signal to consider smaller, coherent commits.

## Branch names

- Use meaningful keywords in kebab-case, such as `refactor-ui-tests`.
- For an issue-related branch, use
  `issueNumber-some-keywords-from-issue-title`, such as
  `1234-ui-freeze-error`.

## Workflow

Before creating or recommending a commit:

1. Inspect the actual staged changes so the message describes their scope.
2. Check the subject against every subject rule above.
3. Decide whether the change is non-trivial and therefore needs a body.
4. Check every subject and body line length.
5. Preserve the user's authorization boundary: do not stage, commit, amend,
   tag, or push unless the user explicitly requested that action.
