---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing or creating commit messages and when naming branches in this repository.
---

# SE-EDU Git Standard

Apply the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever
proposing or creating a commit message or branch name in this repository. Do not infer permission
to commit, create a branch or tag, or push; obtain the authorization required by the project.

## Write commit subjects

- Summarize the commit accurately in a well-written subject.
- Aim for at most 50 characters and never exceed 72 characters.
- Use imperative mood, capitalize the first letter, and do not end with a period.
- Add a meaningful `<scope>:` or `<category>:` prefix when it improves clarity; do not add one by
  default.

## Write commit bodies

For every non-trivial commit, add a body that lets a reviewer understand and judge the change
without reading the diff.

- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why it was needed or designed that way; leave implementation mechanics
  to the diff.
- Describe the prior situation in present tense and the action in imperative mood. Avoid redundant
  qualifiers such as `currently` and `originally`.
- Use bullet points when they make several changes or reasons easier to scan.
- Split the work into finer-grained commits when one coherent description becomes too long.
- Avoid repeating details already clear from code comments in the same commit.

Trivial commits may omit the body only when the subject fully explains both the change and its
purpose. When in doubt, include a concise body.

## Name branches

- Use meaningful relevant keywords in kebab case.
- When the branch relates to an issue, use `<issue-number>-<issue-title-keywords>`.
- Preserve any branch namespace required by the environment. In Codex, prefix the kebab-case name
  with `codex/`, for example `codex/1234-ui-freeze-error`.

## Check before handoff

Before proposing or creating a commit, verify the actual diff and ensure the message describes one
coherent change. Check the subject length, mood, capitalization, punctuation, and body wrapping.
