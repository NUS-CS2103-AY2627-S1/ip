# Project Git standard

This project follows the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subject

- Write a clear subject for every commit.
- Aim for no more than 50 characters; 72 characters is the hard limit.
- Use the imperative mood, as in `Add persistent task storage`.
- Capitalize the first letter.
- Omit a trailing period.
- Optionally prefix an applicable scope or category followed by a colon, such as
  `Storage: Handle missing data file` or `chore: Update release date`.

## Commit body

Add a body for every non-trivial commit.

- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters.
- Separate paragraphs with blank lines and use bullet points when they improve clarity.
- Explain what the change is and why it is needed. Leave implementation mechanics to
  the diff.
- Give enough context for a reader to judge the value and intent of the change without
  inspecting the diff.
- Avoid repeating details already captured by code comments.
- Describe the existing situation in the present tense, explain why it needs to change,
  then describe the change in the imperative mood and justify the chosen direction.
- Avoid time-relative words such as `currently` and `originally` when describing the
  existing situation.
- Use `Let's` when it helps mark the transition from context to the proposed change.

If the body becomes overly long or covers unrelated rationales, split the work into
smaller, cohesive commits.

## Branch names

- Use a meaningful kebab-case name made from relevant keywords, such as
  `refactor-ui-tests`.
- For work tied to an issue, start with the issue number and follow it with keywords
  from the issue title, such as `1234-ui-freeze-error`.
