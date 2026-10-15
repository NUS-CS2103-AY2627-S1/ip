---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches or proposing, reviewing, or creating commits in this repository.
---

# SE-EDU Git Standard

Apply this skill whenever naming a branch or proposing, reviewing, or creating a commit in this repository. Use the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) as the authority.

This skill controls formatting and quality only. It does not grant permission to create commits, branches, tags, or
pushes; obtain or follow the user's authorization separately.

## Commit subjects

- Summarize the commit accurately in one line.
- Use imperative mood, as if completing the sentence "This commit will ...".
- Capitalize the first letter.
- Do not end with a period.
- Aim for 50 characters or fewer; never exceed 72 characters.
- Add a meaningful `<scope>:` or `<category>:` prefix only when it improves clarity.

## Commit bodies

- Include a body for every non-trivial commit.
- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
- Explain what changes and why; leave implementation mechanics to the diff.
- Describe the existing situation in present tense and the change in imperative mood.
- Include enough rationale for a reviewer to judge the decision without reading the diff.
- Use bullets when they make several related changes easier to scan.
- If the message becomes excessively long or covers unrelated rationales, split the work into focused commits.

## Branch names

- Use a meaningful kebab-case name made from relevant keywords, such as `refactor-ui-tests`.
- For issue-related branches, use `issueNumber-keywords-from-issue-title`, such as `1234-ui-freeze-error`.

## Before proposing or creating a commit

1. Inspect the actual diff and status so the message describes only included changes.
2. Check that the commit is cohesive; recommend splitting unrelated changes.
3. Draft the subject and body using the rules above.
4. Verify subject and body line lengths before presenting or using the message.
