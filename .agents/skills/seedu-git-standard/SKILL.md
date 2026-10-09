---
name: seedu-git-standard
description: Apply and review the SE-EDU Git conventions whenever creating or proposing commits, commit messages, or branch names in this project.
---

# SE-EDU Git Standard

Apply the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever
creating or proposing a commit, commit message, or branch name in this project. This skill does not
authorize committing, branching, tagging, or pushing; follow the user's authorization and the
project's Git instructions.

## Before committing

- Inspect the complete staged diff and confirm it represents one coherent change.
- If the commit needs a long explanation because it combines separate concerns, split it into
  finer-grained commits when doing so is within the user's request.
- Draft and check the full message before running `git commit`.

## Subject line

- Write a meaningful subject for every commit.
- Use imperative mood, as if completing the sentence “This commit will ...”.
- Capitalize the first letter and do not end with a period.
- Aim for at most 50 characters; never exceed 72 characters.
- Add a meaningful `<scope>:` or `<category>:` prefix only when it improves clarity, for example
  `Parser: Reject blank descriptions` or `chore: Update release date`.

## Body

Add a body for every non-trivial commit. Separate it from the subject with one blank line, wrap it
at 72 characters, and separate paragraphs with blank lines. Bullets are allowed when they make the
message clearer.

Explain what the change accomplishes and why it is needed or designed that way. Leave implementation
details that are obvious from the diff out of the message. When relevant, structure the explanation
in this order:

1. Describe the existing situation in present tense.
2. Explain why it needs to change.
3. State what the commit does in imperative mood.
4. Explain why that approach was chosen.
5. Add other relevant context, such as issue references or migration notes.

Avoid filler such as “currently” and “originally” when the present situation is already clear.

## Branch names

- Use meaningful keywords in kebab-case, for example `refactor-ui-tests`.
- For work tied to an issue, use `issueNumber-keywords-from-issue-title`, for example
  `1234-ui-freeze-error`.

When a rule is ambiguous, consult the authoritative guide rather than inventing a local convention.
