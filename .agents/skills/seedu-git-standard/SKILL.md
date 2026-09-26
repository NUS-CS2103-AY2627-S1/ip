---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing or creating commits, commit messages, branches, or tags in this repository.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) for this repository.

## Commit subjects

- Write a meaningful subject for every commit.
- Prefer at most 50 characters; never exceed 72 characters.
- Use imperative mood, as if completing: "If applied, this commit will ...".
- Capitalize the first word and do not end the subject with a period.
- Add a useful `<scope>:` or `<category>:` prefix only when it improves clarity.

## Commit bodies

- Add a body for every non-trivial commit, separated from the subject by a blank line.
- Wrap body lines at 72 characters and separate distinct ideas with blank lines.
- Explain what changes and why it is needed; leave implementation mechanics to the diff.
- Describe the existing situation in present tense, then state the change in imperative mood and justify the choice.
- Include enough context for a reviewer to judge the change without reading the diff.
- Avoid repeating code comments or using redundant words such as "currently" and "originally".
- Use bullets when they communicate parallel changes more clearly than prose.
- Keep each commit cohesive. Split changes when the message needs unrelated explanations or becomes excessively long.

## Branches and tags

- Name branches with meaningful kebab-case keywords, such as `refactor-ui-tests`.
- For issue-related branches, prefer `issueNumber-keywords`, such as `1234-ui-freeze-error`.
- Use lightweight tags unless the user explicitly requests an annotated tag.

## Workflow

1. Review the staged diff before proposing or creating a commit.
2. Ensure the subject and body describe the complete staged change and no unstaged work.
3. Do not commit, push, create branches, or create tags without explicit user authorization.
