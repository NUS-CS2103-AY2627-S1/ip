---
name: seedu-git-standard
description: Use when proposing, reviewing, or creating Git commit messages or branch names in this project, and before making an explicitly authorized commit, to apply the SE-EDU Git conventions.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
for every future commit message and branch name in this project.

## Before writing a commit message

1. Inspect the relevant diff and Git status when repository context is
   available.
2. Identify the single purpose of the commit. If the changes contain
   independent purposes, recommend splitting them before composing messages.
3. Explain the rationale in enough detail for a reviewer to judge the change
   without reading the diff.

Do not commit, tag, push, rewrite history, or create a branch unless the user
has explicitly authorized that action. A request for a message suggestion is
not authorization to commit.

## Subject line

- Use imperative mood, such as `Add`, `Fix`, `Extract`, or `Update`.
- Capitalize the first word.
- Do not end with a period.
- Aim for at most 50 characters; never exceed 72 characters.
- Describe the result of the whole commit, not an individual edit.
- Add a meaningful `<scope>:` or `<category>:` prefix only when it improves
  clarity.

## Body

Include a body for every non-trivial commit. A commit is non-trivial when its
reasoning is not fully evident from a short subject, including feature,
refactoring, tooling, configuration, or multi-file changes.

- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters.
- Explain WHAT changed and WHY it was needed; leave HOW to the diff.
- Describe the existing situation in present tense.
- Describe the chosen change in imperative mood.
- Use separate paragraphs or bullets when they make the rationale clearer.
- Avoid repeating code comments or implementation details.

Example:

```text
Improve Javadocs for public APIs

Public APIs can reject malformed input, but their documentation does not
state those exceptional outcomes.

Document the exceptions and add explicit constructors so generated
Javadocs describe the full API without warnings.
```

## Branch names

- Use a meaningful kebab-case name, such as `refactor-ui-tests`.
- For issue-related work, start with the issue number, such as
  `1234-ui-freeze-error`.

## Final review

Check subject length, imperative mood, capitalization, punctuation, body
wrapping, WHAT/WHY rationale, and branch-name format before presenting or
using them.
