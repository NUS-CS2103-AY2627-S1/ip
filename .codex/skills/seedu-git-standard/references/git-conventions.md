# Git conventions checklist

This checklist summarizes the basic and intermediate rules from the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html). Consult the source when a rule is ambiguous or an edge case is not covered here.

## Commit-message subject

- Write a clear subject for every commit.
- Aim for 50 characters or fewer and never exceed 72 characters.
- Use the imperative mood, such as `Add README.md` rather than `Added README.md`.
- Capitalize the first letter.
- Do not end the subject with a period.
- Optionally prefix a meaningful scope or category, such as `Parser: Handle empty input` or `chore: Update release date`.

## Commit-message body

- Add a body for every non-trivial commit.
- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters and use blank lines between paragraphs.
- Use bullet points when they communicate multiple related changes more clearly than prose.
- Explain what the change accomplishes and why it is needed; let the diff show how it is implemented.
- Give enough context for a reviewer to judge the decision without reading the diff first.
- Prefer present tense for the existing situation and imperative mood for the proposed change.
- Avoid redundant qualifiers such as `currently` and `originally`, and do not repeat details already documented in code comments.
- Split the work into finer-grained commits when the message becomes too long or describes unrelated changes.

For a non-trivial change, organize the body around the existing situation, why it needs to change, what the commit does, why that approach is suitable, and any other relevant context. Omit sections that add no useful information rather than filling a rigid template.

## Branch names

- Choose a meaningful branch name containing relevant keywords.
- Use lowercase kebab-case, such as `refactor-ui-tests`.
- For issue-related work, start with the issue number, such as `1234-ui-freeze-error`.
