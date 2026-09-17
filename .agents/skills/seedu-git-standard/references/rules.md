# SE-EDU Git conventions

This project follows the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subject

- Every commit has a well-written subject line.
- Aim for at most 50 characters; 72 characters is the hard limit.
- Use imperative mood, describing the change as a command: `Add README.md`, not `Added README.md` or `Adding README.md`.
- Capitalize the first letter of the subject.
- Do not end the subject with a period.
- An optional scope or category prefix may be used when useful, such as `Parser: Reject blank input` or `chore: Update release date`. When using a lowercase category prefix, capitalize the imperative description after the colon.

## Commit body

- Include a body for every non-trivial commit.
- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters.
- Use blank lines between paragraphs and bullet points where they improve clarity.
- Explain WHAT changed and WHY it changed. Leave implementation details—the HOW—to the diff unless they are needed to justify a design decision.
- Give enough context for a reader to judge the purpose and rationale without first reading the diff.
- Avoid repeating information already clear from code comments in the same commit.
- If a clear body becomes excessively long or covers multiple unrelated rationales, split the work into finer-grained commits.

## Branch names

- Use a meaningful kebab-case name made from relevant keywords, such as `refactor-ui-tests`.
- For a branch tied to an issue, use `issueNumber-keywords-from-issue-title`, such as `1234-ui-freeze-error`.
