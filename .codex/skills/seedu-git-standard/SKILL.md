---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing, creating, amending, or squashing commits and when naming branches in this repository. Use for every commit-related task.
---

# SE-EDU Git Standard

Follow the project conventions summarized here from the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).
Do not create, amend, squash, or push a commit unless the user has authorized
that operation.

## Commit subject

Every commit must have a clear subject line that satisfies all of these rules:

- Aim for at most 50 characters; 72 characters is the hard limit.
- Use the imperative mood, as in `Add parser tests`, not `Added parser tests`
  or `Adding parser tests`.
- Capitalize the first letter.
- Do not end with a period.
- Add an optional `<scope>:` or `<category>:` prefix only when it improves
  clarity, for example `Parser: Handle blank input` or
  `chore: Update release date`.

## Commit body

Add a body for every non-trivial commit. A trivial commit may use only its
subject when the reason and effect are already self-evident.

- Separate the subject and body with one blank line.
- Wrap body text at 72 characters and separate paragraphs with blank lines.
- Explain WHAT changed and WHY it changed. Leave implementation mechanics to
  the diff unless they are relevant to evaluating the decision.
- Give enough context for a reviewer to judge the change without first reading
  the diff.
- Use paragraphs or bullet points, whichever communicates the rationale more
  clearly.
- Avoid repeating information already captured by code comments.
- If the body becomes overly long or covers unrelated rationales, split the
  work into finer-grained commits.

For a substantial change, organize the body using the applicable parts of this
sequence:

1. Describe the existing situation in the present tense.
2. Explain why it needs to change.
3. State what the commit does, using the imperative mood.
4. Explain why that approach was chosen.
5. Add other relevant context, such as constraints or follow-up work.

Avoid `currently` and `originally` when describing the existing situation;
the tense and context already convey that meaning.

## Branch names

- Use a meaningful kebab-case name made from relevant keywords, such as
  `refactor-ui-tests`.
- For work tied to an issue, start with the issue number followed by keywords
  from the issue title, such as `1234-ui-freeze-error`.

## Commit workflow

Before an authorized commit:

1. Inspect the staged diff and repository status so the message describes the
   exact commit rather than surrounding work.
2. Confirm the commit has one coherent purpose; split unrelated changes when
   needed.
3. Draft the subject and, for a non-trivial commit, the body according to the
   rules above.
4. Check the subject and every wrappable body line against the 72-character
   hard limit.
5. Preserve the reviewed message exactly when running the Git command.

When only proposing a commit message, apply the same validation and present the
complete subject and body for review.
