---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches or proposing, reviewing, or creating commits in this project.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) for
branch names and commit messages. Preserve stricter project-specific Git requirements, including
the requirement to obtain explicit user approval before committing or pushing.

## Commit subject

- Write a meaningful subject for every commit.
- Aim for 50 characters or fewer; never exceed 72 characters.
- Use the imperative mood, as in `Add README.md` rather than `Added README.md` or
  `Adding README.md`.
- Capitalize the first letter and do not end the subject with a period.
- Add an optional `<scope>:` or `<category>:` prefix only when it makes the subject clearer, for
  example `Parser: Handle empty input` or `chore: Update release date`.

## Commit body

Include a body for every non-trivial commit.

- Separate the subject and body with a blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what the change is and why it is needed or designed that way. Leave implementation
  details that are evident from the diff out of the message.
- Give enough context for a reviewer to judge the change without first reading the diff, while
  avoiding information already captured in code comments.
- Describe the existing situation in the present tense and the change in the imperative mood.
  Avoid redundant qualifiers such as `currently` and `originally`.
- Use paragraphs or bullet points, whichever communicates the rationale more clearly.
- Split the work into finer-grained commits if its message becomes excessively long.

For a substantial change, organize the body around the relevant parts of this sequence:

1. The existing situation.
2. Why it needs to change.
3. What the commit does.
4. Why that approach was chosen.
5. Other relevant context.

## Branch names

- Use a meaningful kebab-case name made from relevant keywords, such as `refactor-ui-tests`.
- For an issue-related branch, start with the issue number followed by keywords from its title,
  such as `1234-ui-freeze-error`.

## Final check

Before proposing or creating a commit, inspect the complete message for compliance with the rules
above. Before creating or suggesting a branch, check its name against the branch-name rules.
