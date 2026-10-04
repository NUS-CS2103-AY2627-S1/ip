---
name: seedu-git-standard
description: Apply and review the SE-EDU Git conventions when proposing or creating commits and when naming branches in this project.
---

# SE-EDU Git Standard

Apply the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever proposing or creating a commit or naming a branch in this repository.

## Commit subject

- Write a meaningful subject for every commit.
- Use the imperative mood, as if completing the sentence "If applied, this commit will ...".
- Capitalize the first letter of the subject text and do not end the subject with a period.
- Aim for at most 50 characters; never exceed 72 characters.
- Add a useful `<scope>:` or `<category>:` prefix when it improves clarity, but do not require one. Preserve the prefix's natural casing instead of capitalizing it merely because it begins the line. Category prefixes such as `style:`, `chore:`, and `bug fix:` remain lowercase; capitalize the imperative subject text after the colon, for example `style: Indent switch cases consistently`.

## Commit body

- Include a body for every non-trivial commit and separate it from the subject with one blank line.
- Wrap body text at 72 characters and separate paragraphs with blank lines. Use bullet points when they improve clarity.
- Explain what changes and why it is needed; leave implementation details that are evident from the diff out of the message.
- Give enough context to judge the change without reading the diff, including why the chosen approach fits. Avoid repeating information already in code comments.
- Describe the existing situation in the present tense and the change in the imperative mood.
- Avoid redundant temporal words such as "currently" and "originally".
- If the body becomes overly long or covers unrelated motivations, split the work into smaller, coherent commits.

## Branch names

- Use meaningful keywords in kebab-case, for example `refactor-ui-tests`.
- For an issue-related branch, use `issueNumber-keywords-from-issue-title`, for example `1234-ui-freeze-error`.

## Before committing

Inspect the proposed diff and repository status so the commit includes only intended changes. Present or create a compliant commit message based on the actual change. Do not create commits, branches, tags, or pushes unless the user has authorized that action.
