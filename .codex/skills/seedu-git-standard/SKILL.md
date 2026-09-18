---
name: seedu-git-standard
description: Apply the SE-Education Git conventions when naming branches or writing commit messages for this project.
---

# Seedu Git Standard

Use this skill for branch naming and every proposed or created commit message in
this repository. The authoritative standard is the
[SE-Education Git conventions guide](https://se-education.org/guides/conventions/git.html).

For commit subjects:

- Write a meaningful imperative subject, preferably at most 50 characters and never
  more than 72 characters.
- Capitalize the first letter and do not end the subject with a period.
- Add a relevant `<scope>:` or `<category>:` prefix only when it improves clarity.

For non-trivial commits, add a body after a blank line. Wrap it at 72 characters,
separate paragraphs with blank lines, and explain what changed and why. Describe the
current situation in present tense, the reason for the change, the action taken in
imperative mood, and any relevant supporting information. Use bullet points when
they make the explanation clearer, avoid repeating code comments, and explain WHAT
and WHY rather than HOW.

Name branches with meaningful keywords in kebab-case, for example
`refactor-ui-tests`. For issue-related branches, use
`issueNumber-keywords-from-issue-title`.
