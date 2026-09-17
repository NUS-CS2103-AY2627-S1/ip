---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to commits and branch names. Use whenever preparing, proposing, reviewing, or creating a Git commit or commit message in this project, and whenever creating or renaming a branch.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
for every commit and branch created in this project.

## Prepare a commit

1. Review the complete staged diff and identify the single logical change it
   represents.
2. Split unrelated changes into separate commits. Treat an excessively long
   message as a sign that the commit may need to be divided further.
3. Draft and check the subject and, for every non-trivial commit, the body
   against the rules below.
4. Do not create the commit unless the user has explicitly authorized it.

## Write the subject

- Summarize the change clearly in imperative mood.
- Capitalize the first word.
- Do not end with a period.
- Aim for at most 50 characters; never exceed 72 characters.
- Add an appropriate `<scope>:` or `<category>:` prefix when it improves
  clarity, for example `Parser: Handle empty input` or
  `chore: Update dependencies`.

## Write the body

- Include a body for every non-trivial commit.
- Separate it from the subject with one blank line.
- Wrap every line at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why it was needed or designed that way. Leave
  implementation mechanics to the diff unless they are relevant context.
- Give enough context for a reviewer to judge the purpose of the change
  without first reading the diff.
- Prefer this order when the information applies:
  1. Describe the existing situation in present tense.
  2. Explain why it needs to change.
  3. State the change in imperative mood.
  4. Explain why this approach was chosen.
  5. Add other relevant context, such as issue references.
- Use concise bullet points when they communicate multiple changes more
  clearly than prose.
- Avoid repeating explanations already captured by code comments.
- Avoid time-relative qualifiers such as `currently` and `originally` when
  describing the existing situation.

## Name branches

- Use a meaningful kebab-case name built from relevant keywords, such as
  `refactor-ui-tests`.
- For issue-related work, start with the issue number, followed by keywords
  from its title, such as `1234-ui-freeze-error`.

## Verify before committing

- Check the final message using `git diff --cached` and the staged file list.
- Recheck the 72-character hard limit for the subject and every body line.
- Confirm the message describes the staged change accurately and provides the
  rationale for any non-trivial change.
