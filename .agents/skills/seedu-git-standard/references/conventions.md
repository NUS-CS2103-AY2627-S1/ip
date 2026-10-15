# SE-EDU Git Conventions

These rules summarize the official
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
for this project.

## Commit subject

- Give every commit a clear, specific subject.
- Aim for at most 50 characters; never exceed 72 characters.
- Use imperative mood, describing the action as a command: `Add parser tests`,
  not `Added parser tests` or `Adding parser tests`.
- Capitalize the first letter.
- Do not end the subject with a period.
- An optional `<scope>:` or `<category>:` prefix may be used when it makes the
  subject clearer, for example `Parser: Reject blank dates`.

## Commit body

Add a body for non-trivial commits.

- Separate the body from the subject with one blank line.
- Wrap body text at 72 characters.
- Separate paragraphs with blank lines and use bullet points when they improve
  clarity.
- Explain what changed and why it was needed. Leave implementation mechanics
  to the diff unless they are important to the decision.
- Describe the existing situation in present tense and the action taken in
  imperative mood.
- Avoid filler such as `currently` and `originally` when the timing is already
  implied.
- Give enough rationale for a reviewer to judge the change without first
  reverse-engineering its purpose from the diff.
- Avoid duplicating explanations already present in code comments.
- If the body becomes excessively long or covers unrelated rationales, split
  the changes into smaller coherent commits when the user authorizes it.

A useful body structure is:

1. State the situation before the change.
2. Explain why it needs to change.
3. State what this commit does.
4. Explain why that approach was chosen.
5. Add other relevant context, risks, or follow-up information.

## Branch names

- Use meaningful keywords in kebab case, for example `refactor-ui-tests`.
- For work tied to an issue, start with the issue number and follow it with
  relevant issue-title keywords, for example `1234-ui-freeze-error`.

## Final check

Before presenting or using a commit message, confirm that:

- the subject is imperative, capitalized, specific, unpunctuated, and no more
  than 72 characters;
- a non-trivial change has a body wrapped at 72 characters;
- the body explains what and why rather than merely narrating how; and
- the message accurately describes only the staged changes.
