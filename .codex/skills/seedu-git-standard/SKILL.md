---
name: seedu-git-standard
description: Use when naming branches or drafting, reviewing, or creating Git commit messages in this repository under the SE-EDU Git conventions and current CS2103T course requirements.
---

# Follow the SE-EDU Git Standard

Apply the current SE-EDU Git conventions when naming branches or preparing
commit messages.

Authoritative source:

- Git conventions: <https://se-education.org/guides/conventions/git.html>

Before acting, check the current CS2103T course instructions for the task. Exact
course-required branch names, commit requirements, and workflows override the
generic SE-EDU conventions. Treat course-specific names as task instructions or
illustrative examples, not as permanent policy. Repository instructions and
the user's authorization boundaries continue to apply.

## Commit message subject

- Every commit must have a well-written subject line.
- Aim for at most 50 characters; 72 characters is the hard limit.
- Use the imperative mood, such as `Add README.md`, rather than `Added` or
  `Adding`.
- Capitalize the first letter.
- Do not end the subject with a period.
- An optional `<scope>:` or `<category>:` prefix may be used when it helps, such
  as `Parser: Handle empty input` or `chore: Update release date`.
- Conventional Commits is an available alternative convention, not a default
  requirement of the SE-EDU standard.

## Commit message body

The SE-EDU convention recommends a body for non-trivial commits. CS2103T course
instructions may make the body optional; when they do, that course rule takes
precedence. If a body is written:

- Separate it from the subject with one blank line.
- Wrap it at 72 characters and use blank lines between paragraphs.
- Use bullet points when they improve clarity.
- Explain what changed and why, not the implementation details of how it was
  changed. Give enough context for a reader to judge the change without first
  reading the diff.
- If the body becomes too long, consider splitting the change into finer-grained
  commits.
- Minimize repetition of information already recorded in code comments.
- Prefer this structure where applicable: present-tense current situation; why
  it needs to change; imperative description of what is being done; why that
  approach is used; then any other relevant information.
- Avoid redundant time qualifiers such as `currently` and `originally` when
  describing the existing situation.
- `Let's` may introduce the section describing the change.

## Branch names

- Use a meaningful name made from relevant keywords in kebab-case, such as
  `refactor-ui-tests`.
- For a branch related to an issue, use
  `issueNumber-some-keywords-from-issue-title`, such as
  `1234-ui-freeze-error`.
- Do not normalize or rename an exact branch name required by current course
  instructions merely to fit the generic convention.

## Apply and verify

Before proposing or creating a branch or commit message, identify the applicable
course and repository requirements, then apply the conventions above. Verify the
actual branch name or complete commit message, including line lengths, before
reporting compliance. Do not create a branch or commit unless the user has
authorized that Git operation.
