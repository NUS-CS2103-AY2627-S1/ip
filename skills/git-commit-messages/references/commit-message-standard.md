# Project commit-message standard

Use this reference as the source of truth for commit messages.

## Subject line

- Every commit has a well-written subject.
- Use imperative mood, capitalize its first letter, and do not end it with a
  period. Write `Add README.md`, not `Added README.md` or `Adding README.md`.
- Aim for 50 characters; 72 characters is the hard limit.
- An optional meaningful scope or category prefix is allowed, for example
  `Person class: Remove static imports`, `bug fix: Add space after name`, or
  `chore: Update release date`.

## Body for non-trivial commits

- Put one blank line between subject and body. Wrap body prose at 72 characters
  and separate paragraphs with blank lines.
- Explain what changed and why, not how the diff implements it. Include enough
  context for a reader to evaluate the change without reading the code.
- A useful order is: current situation (present tense), why it must change,
  imperative description of the change (which may start with `Let's`), why this
  approach was chosen, then other relevant information.
- Do not use `currently` or `originally` to describe the current situation;
  they are implied. Avoid repeating details already present in code comments.
- Use bullets when they clarify multiple related changes.

## Related branch convention

Use a meaningful kebab-case branch name such as `refactor-ui-tests`. For an
issue branch, use `issueNumber-keywords-from-issue-title`, such as
`1234-ui-freeze-error`.
