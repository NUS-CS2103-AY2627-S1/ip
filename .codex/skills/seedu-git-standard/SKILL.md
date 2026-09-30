---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing or creating commit messages or branch names in this project.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever preparing a commit message or naming a branch in this project.

This skill governs formatting and content only. It does not authorize creating a commit, branch, tag, pull request, or push; obtain the authorization required by the project instructions before changing Git state.

## Commit subject

Every commit must have a well-written subject line:

- Express the change in the imperative mood, as if completing the sentence, "If applied, this commit will ..."
- Capitalize the first letter.
- Do not end with a period.
- Aim for 50 characters or fewer and never exceed 72 characters.
- Optionally prefix a relevant scope or category followed by a colon, such as `Parser: Handle blank input` or `chore: Update release date`.

Before using a proposed subject, count its characters and revise it if it exceeds 72.

## Commit body

Include a body for every non-trivial commit. A commit is non-trivial when its purpose or rationale is not fully clear from a concise subject alone.

- Separate the subject from the body with one blank line.
- Wrap body text at 72 characters.
- Separate paragraphs with blank lines and use bullet points when they improve clarity.
- Explain what changes and why; leave implementation details that are obvious from the diff out of the message.
- Give enough context for a reviewer to judge the decision without reading the diff.
- Avoid repeating code comments and avoid words such as "currently" or "originally" when describing the existing state.
- If the explanation becomes long or covers unrelated reasons, split the work into finer-grained commits when the user has authorized doing so.

For a substantial change, organize the body around:

1. The existing situation, written in the present tense.
2. Why it needs to change.
3. What this commit does, written in the imperative mood.
4. Why this approach is appropriate.
5. Other relevant context, if any.

## Branch names

- Use a meaningful kebab-case name made from relevant keywords, such as `refactor-ui-tests`.
- For work tied to an issue, use `issueNumber-keywords-from-title`, such as `1234-ui-freeze-error`.

## Final check

Before presenting or using a commit message, verify the subject mood, capitalization, punctuation, and length; verify body separation and 72-character wrapping; and confirm the message explains what and why.
