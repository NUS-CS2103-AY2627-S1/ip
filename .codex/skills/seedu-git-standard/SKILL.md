---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing, reviewing, or creating commits in this repository.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) for every proposed or actual commit message.

- Write a clear imperative subject, capitalize its first letter, and omit the final period.
- Aim for 50 characters or fewer in the subject; never exceed 72 characters. Add a useful scope or category prefix when it improves clarity.
- Give every non-trivial commit a body separated from the subject by a blank line. Wrap body text at 72 characters and separate paragraphs with blank lines.
- Explain what the change is and why it is needed; leave implementation mechanics to the diff.
- Describe the existing situation in present tense and the requested change in imperative mood. Avoid redundant words such as “currently” and “originally”.
- Use bullets when they make several related changes easier to understand.
- Split a commit when its message needs to cover unrelated purposes.

Before proposing or creating a commit, inspect the exact diff in scope so the message accurately describes it. Do not commit, push, merge, tag, or otherwise mutate Git history unless the user has authorized that action.
