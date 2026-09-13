# SE-EDU Git conventions

Source: <https://se-education.org/guides/conventions/git.html>

## Commit subject

- Give every commit a well-written subject.
- Aim for at most 50 characters; never exceed 72 characters.
- Use the imperative mood, as if completing the sentence “If applied, this commit will ...”.
- Capitalize the first letter of the subject.
- Do not end the subject with a period.
- Add a meaningful `<scope>:` or `<category>:` prefix when it improves clarity; it is optional.

Examples:

- `Add task persistence`
- `Parser: Reject empty deadline dates`
- `chore: Update Gradle wrapper`

## Commit body

- Add a body for every non-trivial commit.
- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters.
- Separate paragraphs with blank lines and use bullet points when they improve readability.
- Explain what changed and why. Leave implementation mechanics to the diff unless they are important context.
- Give enough context for a reviewer to judge the change without first reading the diff.
- Avoid repeating information already clear from code comments.
- Describe the existing situation in the present tense.
- Describe the change in the imperative mood.
- Avoid redundant time words such as “currently” and “originally”.
- Organize a substantial body around:
  1. the existing situation;
  2. why it needs to change;
  3. what the commit does;
  4. why that approach was chosen;
  5. other relevant context.
- If the body becomes excessively long or covers unrelated reasons, split the work into finer-grained commits.

## Branch names

- Use meaningful keywords in kebab-case, such as `refactor-ui-tests`.
- For an issue-related branch, start with the issue number followed by relevant keywords, such as `1234-ui-freeze-error`.

