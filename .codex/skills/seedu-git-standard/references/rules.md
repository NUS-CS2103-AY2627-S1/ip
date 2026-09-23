# SE-EDU Git Conventions Checklist

Canonical source: <https://se-education.org/guides/conventions/git.html>

## Commit subject

- Write a meaningful subject for every commit.
- Aim for at most 50 characters; never exceed 72 characters.
- Use the imperative mood, such as `Add task validation`, not `Added task validation` or `Adding task validation`.
- Capitalize the first letter of the subject.
- Do not end the subject with a period.
- An optional `<scope>:` or `<category>:` prefix may be used when it improves clarity. Follow the same subject rules after the prefix.

## Commit body

Add a body for non-trivial commits.

- Separate the subject from the body with one blank line.
- Wrap body text at 72 characters.
- Separate paragraphs with blank lines and use bullet points when they improve clarity.
- Explain what changed and why it changed. Leave implementation details that are obvious from the diff out of the message.
- Describe the existing situation in the present tense and the action taken in the imperative mood.
- Avoid redundant qualifiers such as `currently` and `originally` when describing the existing situation.
- Keep the explanation detailed enough to assess the purpose of the change without reading the diff.
- If the message becomes too long or covers unrelated rationales, propose splitting the work into focused commits.

## Branch names

- Use a meaningful name containing relevant keywords.
- Use kebab case, such as `refactor-ui-tests`.
- For an issue-related branch, use `issueNumber-keywords-from-issue-title`, such as `1234-ui-freeze-error`.

## Final audit

Before proposing or creating a commit or branch, verify all applicable length, mood, capitalization, punctuation, body, and naming rules above.
