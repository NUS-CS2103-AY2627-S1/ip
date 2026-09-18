---
name: seedu-git-standard
description: Apply the project's SE-EDU Git conventions whenever proposing or creating commits in this repository.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
for every commit proposed or created in this project.

## Prepare a cohesive commit

1. Use `git status` and inspect the staged diff before committing.
2. Stage only files that serve one logical purpose. Keep source changes separate
   from skills, agent instructions, documentation, generated output, and other
   configuration unless they are inseparable parts of the same change.
3. Split changes when the message would need to explain unrelated reasons. Do
   not hide unrelated work in a convenient commit.
4. Recheck the staged file list and diff immediately before the commit. Do not
   amend or rewrite existing commits unless the user explicitly asks.

## Write the subject

- State the change in the imperative mood, as an instruction to the codebase:
  `Add parser validation`, not `Added parser validation` or `Adding parser
  validation`.
- Capitalize the first letter of an unscoped subject and do not end it with a
  period.
- Aim for 50 characters or fewer; 72 characters is the hard limit.
- Add a short `<scope>:` or `<category>:` prefix only when it makes the subject
  clearer. Keep the action after the prefix direct and imperative.

## Add a body when needed

- Give every non-trivial commit a body. Separate it from the subject with one
  blank line and wrap body text at 72 characters.
- Explain what situation motivates the change and why the chosen change is
  appropriate. Let the diff show implementation details.
- Describe the existing situation in present tense. Describe the action in the
  imperative mood. Avoid filler such as `currently` and `originally`.
- Separate paragraphs with blank lines. Use bullets when they make several
  related points easier to scan.
- Keep the explanation proportional. If the body grows into multiple unrelated
  narratives, split the work into separate commits.
