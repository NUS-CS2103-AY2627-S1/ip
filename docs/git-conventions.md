# Git conventions

This project follows the [SE Education Git conventions](https://se-education.org/guides/conventions/git.html).
The course requires a well-written subject line. A commit body is optional,
but any body that is included must follow the rules below; use one for every
nontrivial change in this project.

## Commit subjects

- Give every commit a clear subject.
- Use the imperative mood: `Add README.md`, not `Added README.md` or
  `Adding README.md`.
- Capitalize the first letter.
- Do not end the subject with a period.
- Aim for 50 characters or fewer and never exceed the hard limit of 72.
- Add a scope or category only when it improves clarity, for example
  `Parser: Handle empty input`.

## Commit bodies

For a nontrivial commit:

- Separate the subject and body with one blank line.
- Wrap every body line at 72 characters.
- Use blank lines between paragraphs.
- Explain what changed and why it changed, not how the code was implemented.
- Start with the current situation, explain why it needs to change, describe
  what is being done, and explain why that approach was chosen.
- Use bullet points when they make several related changes easier to scan.
- Avoid repeating details that are already obvious from code comments or the
  diff.

Write real line breaks in the message. Do not put the two characters `\n` in
the message and expect Git to turn them into new lines. For example, the
following creates a correctly separated subject and body:

```sh
git commit -m "Refactor command parsing" \
  -m "Command parsing is difficult to read when validation and execution are
mixed together.

Extract the validation and execution steps into focused helpers so each
command path is easier to understand and test."
```

When using an editor, the equivalent message is:

```text
Refactor command parsing

Command parsing is difficult to read when validation and execution are mixed
together.

Extract the validation and execution steps into focused helpers so each
command path is easier to understand and test.
```

## Branch names

- Use a meaningful kebab-case name containing relevant keywords, such as
  `refactor-ui-tests`.
- For issue-based work, use
  `issueNumber-some-keywords-from-issue-title`, such as
  `1234-ui-freeze-error`.

## Commit checklist

Before committing:

1. Make sure the change is one logical unit; split unrelated changes.
2. Run `./gradlew checkstyleMain checkstyleTest test`.
3. Run the UI test plan when console behavior changed.
4. Review `git diff` and `git diff --check`.
5. Confirm the subject and body follow the rules above.
6. Do not include build output, temporary files, credentials, or unrelated
   user data.

Do not commit or push changes unless the project owner explicitly asks for it.
Do not rewrite shared history without first coordinating with everyone using
the branch.
