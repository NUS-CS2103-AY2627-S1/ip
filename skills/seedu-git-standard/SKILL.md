---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing or creating commits, tags, or branch history in this repository.
---

# SE-EDU Git Standard

Follow the authoritative standard at
https://se-education.org/guides/conventions/git.html.

For every commit:

- Make one cohesive change and inspect the staged diff before committing.
- Write an imperative, capitalized subject without a trailing period.
- Keep the subject near 50 characters and never exceed 72 characters.
- For non-trivial changes, add a body separated by a blank line and wrap it at
  72 characters.
- Explain what changed and why; leave implementation details to the diff.
- Do not commit generated binaries, build output, runtime data, credentials, or
  unrelated user changes.
- Use lightweight tags unless the user explicitly asks for annotated tags.

Preserve intentional branch topology, and verify the resulting graph before
pushing branches or tags.
