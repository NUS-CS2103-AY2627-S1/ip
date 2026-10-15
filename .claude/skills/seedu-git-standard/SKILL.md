---
name: seedu-git-standard
description: The SE-EDU Git conventions for commit messages and branch names in this project, plus this user's subject-only preference. Use whenever proposing, writing, or reviewing a commit message, and when naming a branch.
---

# SE-EDU Git Standard

Every commit in this project follows the SE-EDU Git conventions:
https://se-education.org/guides/conventions/git.html

## Subject line

Always required, and in this project usually the whole message.

* Write it in the imperative mood: "Add README.md", never "Added README.md" or
  "Adding README.md". Read it as completing the sentence "If applied, this
  commit will ...".
* Capitalize the first letter: "Move index.html file to root", not "move ...".
* Do not end it with a full stop: "Update sample data", not "Update sample
  data.".
* Aim for 50 characters. The hard limit is 72.
* An optional `<scope>:` or `<category>:` prefix is allowed where it helps,
  e.g. `Person class: Remove static imports` or `bug fix: Add space after name`.

## Body

**This user prefers subject-only commit messages.** They have twice asked for
multi-paragraph messages to be trimmed back to the first line. Default to no
body. Propose one only when the change genuinely needs explaining, and write it
only when asked.

When a body is wanted:

* Separate it from the subject with one blank line.
* Wrap it at 72 characters.
* Explain WHAT changed and WHY. Do not explain HOW -- the diff already shows
  that.
* Separate paragraphs with blank lines, and use bullet points where they help.
* A useful order: the current situation in present tense, the reason it needs
  to change, what is being done in imperative mood, and why it was done that
  way.
* Do not repeat what a code comment in the same commit already says.
* If the body is growing long, that is a sign the change should be split into
  several commits instead.

## Splitting commits

Make one commit per standalone change. In particular, keep source code changes
in their own commit, separate from changes to agent files such as `AGENTS.md`
or anything under `.claude/skills/`.

## Branch names

* Kebab case, with meaningful keywords: `refactor-ui-tests`.
* For a branch addressing an issue, lead with the issue number:
  `1234-ui-freeze-error`.
* Where the course specifies a branch name for a graded increment, that name
  wins: `branch-Level-9`, `branch-A-CodingStandard`.

## Tags

The guide says nothing about tags. This project uses lightweight tags named
after the increment (`A-JavaDoc`, `Level-8`), placed on the merge commit on
`master` rather than on the last commit of the feature branch.

## Before committing

Check the proposed subject against this list: imperative, capitalized, no
trailing full stop, within 72 characters. Then check that the change really is
one standalone thing.

## Related

See [[seedu-java-coding-standard]] for the Java coding standard applied to the
code being committed.
