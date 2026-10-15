# Project Git standard

This checklist is derived from the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html). The official guide is authoritative.

## Commit subject

- Give every commit a clear, well-written subject.
- Aim for at most 50 characters; 72 characters is the hard limit.
- Use the imperative mood, as if completing “This commit will …”.
- Capitalize the first letter of the subject.
- Do not end the subject with a period.
- Add a meaningful `<scope>:` or `<category>:` prefix when it improves clarity; it is optional.

Example: `Parser: Reject blank commands`

## Commit body

- Add a body for every non-trivial commit.
- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Use paragraphs or bullet points according to whichever communicates the change more clearly.
- Explain what the commit changes and why the change is needed or designed that way. Leave implementation mechanics to the diff unless they are important context.
- Give enough context for a reviewer to judge the change's purpose without first reading the diff, while avoiding duplication of code comments.
- Describe the existing situation in the present tense. Describe the action taken in the imperative mood.
- Avoid redundant time qualifiers such as “currently” and “originally”.
- A useful body order is: existing situation, reason for change, action taken, rationale for that approach, and other relevant context.
- If the body becomes excessively long or covers unrelated ideas, split the work into finer-grained commits.

## Branch names

- Use a meaningful kebab-case name made from relevant keywords, such as `refactor-ui-tests`.
- For issue-related work, prefix the relevant issue number, such as `1234-ui-freeze-error`.
