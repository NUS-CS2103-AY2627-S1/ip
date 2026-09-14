# Git conventions

Use the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
as the source of truth.

## Commit subject

- Write a meaningful subject for every commit.
- Use imperative mood: `Add`, `Fix`, `Update`, or `Refactor`, rather than
  `Added`, `Fixing`, or `Updates`.
- Capitalize the first letter and do not end with a period.
- Aim for at most 50 characters; 72 characters is the hard limit.
- Add a useful `<scope>:` or `<category>:` prefix when it clarifies the change.

## Commit body

Use a body for non-trivial commits.

- Separate it from the subject with a blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what the situation is, why it should change, what the commit does,
  and why that approach was chosen. Leave implementation mechanics to the
  diff.
- Use present tense for the existing situation and imperative mood for the
  change. Avoid filler such as `currently` and `originally`.
- Use bullet points when they make several related changes easier to scan.
- If the explanation becomes long or covers unrelated reasons, split the work
  into focused commits.

## Branch names

- Default to a meaningful kebab-case name made from relevant keywords, such as
  `refactor-ui-tests`.
- For an issue branch, use
  `issueNumber-some-keywords-from-issue-title`, such as
  `1234-ui-freeze-error`.
- Follow an explicit branch name required by the user or assignment even when
  it differs from this default.
