---
name: seedu-git-standard
description: Write and review Git commit messages and branch names using the SE-EDU project conventions.
---

# Seedu Git Standard

Use this skill whenever you propose, review, or create a commit, or when you
create or rename a project branch.

Apply the SE-EDU Git conventions at
<https://se-education.org/guides/conventions/git.html>.

## Commit subjects

- Every commit has a meaningful subject in imperative mood.
- Capitalize the first letter and do not end the subject with a period.
- Prefer 50 characters or fewer; never exceed 72 characters.
- Add a concise scope or category prefix only when it improves clarity.

## Commit bodies

- Give every non-trivial commit a body separated from the subject by one blank
  line. Wrap body lines at 72 characters and separate paragraphs with blank
  lines.
- Explain WHAT changed and WHY; do not narrate HOW the diff implements it.
- Structure the explanation around the present situation, why it needs to
  change, what to do, why that approach is appropriate, and other relevant
  information. Use imperative mood for the change description and avoid words
  such as “currently” and “originally”. Use bullets when they improve clarity.
- Keep the message focused enough that its purpose can be judged without
  reading the diff. Split unrelated changes into separate commits.

## Branch names

Use meaningful kebab-case keywords, or
`issueNumber-some-keywords-from-issue-title` when tied to an issue.

Before committing, review the staged diff and validate the subject and body
length, imperative mood, capitalization, punctuation, WHAT/WHY explanation, and
commit scope. Do not commit unless the user has authorized it.
