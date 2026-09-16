# Project Git-standard checklist

Authoritative source: [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subject

- Give every commit a meaningful subject that summarizes one coherent change.
- Aim for at most 50 characters; 72 characters is the hard limit.
- Use imperative mood, as if completing the sentence "If applied, this commit will ...".
- Capitalize the first letter and do not end with a period.
- Add an optional meaningful scope or category before a colon only when it improves clarity, such
  as `Parser: Reject empty deadlines` or `chore: Update test data`.

## Commit body

- Add a body for every non-trivial commit. A small, self-explanatory change may use only a subject.
- Separate the body from the subject with one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why it was needed; leave implementation details that are obvious from
  the diff out of the message.
- A useful order is: present situation, reason it needs to change, what the commit does in
  imperative mood, rationale for that approach, then other relevant context.
- Use bullets when they communicate multiple points more clearly than prose.
- If the body becomes long or covers unrelated reasons, split the work into smaller commits.

## Branch names

- Use a meaningful kebab-case name containing relevant keywords, such as `refactor-ui-tests`.
- For work tied to an issue, use `issueNumber-keywords-from-title`, such as
  `1234-ui-freeze-error`.

## Review workflow

Before creating a commit:

1. Inspect `git status` and the staged diff.
2. Confirm the staged files form one logical change and that no unrelated files are included.
3. Draft and validate the subject and, for non-trivial work, the body.
4. Create the commit only when the user has authorized it.
5. Review the resulting message with `git log -1 --format=fuller` or an equivalent read-only
   command.
