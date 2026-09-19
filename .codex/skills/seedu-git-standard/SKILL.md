---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing, creating, reviewing, or revising commit messages and branch names in this project.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) for every commit message and branch name in this repository.

## Commit subjects

- Give every commit a clear subject that summarizes one cohesive change.
- Aim for at most 50 characters; never exceed 72 characters.
- Use imperative mood, as if completing the sentence "This commit will ...".
- Capitalize the first letter and do not end the subject with a period.
- Add a meaningful `<scope>:` or `<category>:` prefix only when it improves clarity.

## Commit bodies

- Add a body for every non-trivial commit.
- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Use paragraphs or bullet points according to whichever communicates the change more clearly.
- Explain what changed and why it was needed; leave implementation details to the diff unless they are important to the rationale.
- Give enough context for a reviewer to judge the decision without reading the diff, while avoiding repetition of code comments.
- Describe the existing situation in the present tense and the action taken in the imperative mood. Avoid filler such as "currently" and "originally".
- If the body becomes long or describes unrelated changes, split the work into smaller cohesive commits.

## Branch names

- Use meaningful keywords in kebab case, for example `refactor-ui-tests`.
- For issue-specific branches, use `issueNumber-keywords-from-issue-title`, for example `1234-ui-freeze-error`.

## Workflow

Before proposing or creating a commit:

1. Inspect the exact staged or intended changes and identify their single purpose.
2. Split unrelated changes before committing rather than hiding them in one message.
3. Draft the subject and any required body using the rules above.
4. Check every line length before presenting or using the message.
5. Do not create, amend, tag, or push a commit unless the user explicitly authorizes that action.
