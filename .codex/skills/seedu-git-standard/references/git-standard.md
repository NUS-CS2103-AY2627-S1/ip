# SE-EDU Git Conventions: Project Reference

This reference summarizes the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).
The canonical page takes precedence if this summary is ambiguous.

## Commit subject

- Give every commit a well-written subject.
- Prefer no more than 50 characters; 72 characters is the hard limit.
- Use imperative mood, such as `Add parser tests`, not `Added parser tests` or `Adding parser tests`.
- Capitalize the first letter.
- Do not end with a period.
- An optional scope or category prefix is allowed when it improves clarity, for example
  `Parser: Handle missing dates` or `chore: Update release date`.

## Commit body

- Add a body for every non-trivial commit.
- Separate the body from the subject with one blank line.
- Wrap body lines at 72 characters and use blank lines between paragraphs. Use bullets when they communicate the
  change more clearly.
- Explain what changed and why it was necessary. Do not narrate implementation details that are already visible in
  the diff.
- Describe the existing situation in present tense and the change in imperative mood.
- Avoid filler such as `currently` and `originally`, and avoid repeating code comments.
- If the message becomes too long or covers unrelated motivations, split the work into smaller commits.

Before creating a commit, verify that the staged changes match the message and form one coherent change.

## Branch names

- Use a meaningful kebab-case name made from relevant keywords, such as `refactor-ui-tests`.
- For an issue-related branch, start with the issue number, such as `1234-ui-freeze-error`.
