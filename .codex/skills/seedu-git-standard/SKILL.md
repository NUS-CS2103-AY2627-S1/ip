---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing or creating commits, writing commit messages, or naming branches in this project. Use for every future commit and branch operation; it does not grant permission to commit, push, or otherwise mutate Git history.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever proposing or creating commits or branches in this project. Invoking this skill does not authorize a commit, push, tag, history rewrite, or other Git mutation; obtain the authorization required by `AGENTS.md` and the user's request.

## Prepare a commit

Inspect the complete intended diff and status before drafting the message. Keep each commit cohesive. Exclude unrelated changes, and suggest splitting the commit when its message needs excessive detail to explain multiple independent changes.

### Write the subject

- Describe every commit with a well-written subject.
- Use imperative mood, as in `Add README.md`, rather than `Added README.md` or `Adding README.md`.
- Capitalize the first letter and do not end with a period.
- Target at most 50 characters; never exceed 72 characters.
- Add an optional `<scope>:` or `<category>:` prefix only when it improves clarity, such as `Parser: Reject empty commands` or `chore: Update release date`.

### Write the body

Add a body for every non-trivial commit.

- Separate the subject and body with one blank line.
- Wrap body text at 72 characters and separate paragraphs with blank lines. Use bullets when they improve readability.
- Explain WHAT the commit changes and WHY the change is needed or designed that way. Leave implementation mechanics that are obvious from the diff out of the message.
- Give enough context for a reviewer to judge the change without first reading the diff.
- When useful, structure the explanation as: present situation, reason it needs to change, what this commit does in imperative mood, why this approach was chosen, and other relevant information.
- Use present tense for the situation and imperative mood for the action. Avoid redundant qualifiers such as `currently` and `originally`.
- Do not repeat details already documented adequately in code comments.

## Name branches

- Use a meaningful kebab-case name made from relevant keywords, such as `refactor-ui-tests`.
- For issue-related work, begin with the issue number, such as `1234-ui-freeze-error`.
- Preserve any branch prefix required by the environment or user. Apply the SE-EDU name after that prefix, for example `codex/refactor-ui-tests`.

## Final check

Before presenting or using a commit message, verify the subject's mood, capitalization, punctuation, and length; verify that a non-trivial commit has a wrapped WHAT/WHY body; and confirm that the staged scope matches the message. Before creating a branch, verify its meaning, kebab case, issue number when applicable, and required prefix.
