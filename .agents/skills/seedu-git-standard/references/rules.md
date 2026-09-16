# SE-EDU Git Conventions

Source: [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html), reviewed 2026-08-28.

## Commit subject

- Give every commit a well-written subject.
- Aim for at most 50 characters and never exceed 72 characters.
- Use the imperative mood, such as `Add user validation`, rather than `Added user validation` or `Adding user validation`.
- Capitalize the first letter of the subject.
- Do not end the subject with a period.
- Optionally prefix the subject with a relevant `<scope>:` or `<category>:`.

## Commit body

- Add a body for every non-trivial commit.
- Separate the subject from the body with one blank line.
- Wrap body lines at 72 characters.
- Separate paragraphs with blank lines and use bullet points when they improve clarity.
- Explain WHAT changed and WHY it changed, not HOW the code implements it.
- Give enough context for a reader to judge the decision without inspecting the diff.
- Split the work into finer-grained commits if the message becomes too long.
- Avoid repeating details already captured in code comments changed by the same commit.
- Organize the body as applicable:

  1. Describe the situation in the present tense.
  2. Explain why it needs to change.
  3. State what the commit does in the imperative mood.
  4. Explain why that approach was chosen.
  5. Add other relevant information.

- Avoid words such as `currently` and `originally` when describing the present situation because that timing is implied.
- Optionally use `Let's` to introduce the section describing the change.

## Branch names

- Use a meaningful kebab-case name made from relevant keywords, such as `refactor-ui-tests`.
- For a branch associated with an issue, use `issueNumber-keywords-from-title`, such as `1234-ui-freeze-error`.
