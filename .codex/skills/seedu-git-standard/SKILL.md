---
name: seedu-git-standard
description: Create and review Git commit messages for this project using the required SE-EDU subject and body conventions.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html) whenever proposing, reviewing, or creating a commit message.

## Subject

- Describe the change in the imperative mood, as an instruction to the codebase.
- Capitalize the first letter and do not end with a period.
- Aim for 50 characters and never exceed 72 characters.
- Add a concise scope or category prefix only when it improves clarity.

## Body

Include a body for every non-trivial commit.

- Separate it from the subject with one blank line.
- Wrap lines at 72 characters.
- Explain what changed and why; leave implementation mechanics to the diff.
- Describe the existing situation in present tense and the change in imperative mood.
- Use paragraphs or bullets when they make distinct reasons easier to scan.
- If the body becomes too broad, split the work into smaller standalone commits.

## Review workflow

1. Confirm that the commit contains one coherent change.
2. Check the subject against every subject rule.
3. For a non-trivial commit, check that the body gives enough rationale to evaluate the change without reading the diff.
4. Rewrite the message before committing if any required convention is violated.
