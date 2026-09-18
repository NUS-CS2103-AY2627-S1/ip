---
name: seedu-git-standard
description: Write and review Git commit messages and branch names using the SE-EDU Git conventions for this project.
---

# SE-EDU Git standard

Use this skill whenever proposing, creating, reviewing, or amending commits or
branches in this repository. The authoritative reference is the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

Commit subjects must:

- Be well-written, preferably 50 characters or fewer and never over 72.
- Use imperative mood, capitalize the first word, and omit a final period.
- Optionally use a meaningful scope or category prefix.

Every commit should have a full message: a body separated from the subject by a
blank line. Wrap the body at 72 characters, use paragraphs or bullets as useful,
and explain what changed and why, not implementation mechanics already visible
in the diff. A useful order is the current situation, why it needs to change,
what to do, why that approach was chosen, and relevant context.

Example:

```text
Add JavaFX launcher

Start the application through a separate launcher class so JavaFX can initialise
reliably across supported environments.
```

Use meaningful kebab-case branch names. For issue branches, use
`issueNumber-relevant-kebab-case-description`.

Do not commit or push unless the user explicitly authorizes it.
