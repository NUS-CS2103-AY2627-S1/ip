---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing or creating commit messages and when suggesting or creating branch names in this project.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever handling commits or branches in this repository.

## Commit workflow

1. Inspect the actual changes included in the intended commit before writing its message.
2. Keep each commit focused. If a clear message requires a long or unrelated explanation, recommend splitting the changes into smaller commits.
3. Write a subject that:
   - summarizes the whole commit;
   - uses imperative mood;
   - begins with a capital letter;
   - has no trailing period;
   - aims for at most 50 characters and never exceeds 72 characters.
4. Optionally prefix the subject with a meaningful `<scope>:` or `<category>:` when it improves clarity.
5. For a non-trivial commit, add a body separated from the subject by one blank line. Wrap body lines at 72 characters and separate paragraphs with blank lines.
6. Explain what changed and why in the body. Leave implementation details visible in the diff out unless they are important context.
7. Describe the situation in present tense and the action in imperative mood. Avoid redundant qualifiers such as “currently” and “originally”. Use bullet points when they improve readability.

Do not create a commit, amend history, push, or otherwise mutate Git state unless the user explicitly authorizes that action.

## Branch names

- Use meaningful keywords in kebab-case, such as `refactor-ui-tests`.
- For work tied to an issue, use `issueNumber-keywords-from-issue-title`, such as `1234-ui-freeze-error`.

