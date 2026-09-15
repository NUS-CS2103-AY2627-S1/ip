---
name: seedu-git-standard
description: The SE-EDU Git conventions that ALL commits and branches in this project must follow — commit subject line format, body structure, and branch naming. Use this skill whenever writing or proposing a commit message, running git commit, creating or naming a branch, amending or rewording a commit, or when the user asks how to phrase a commit. Apply it proactively: a commit message should conform the first time it is written, not be rewritten after review.
---

# SE-EDU Git Conventions

Source: https://se-education.org/guides/conventions/git.html

A commit message is read far more often than it is written — usually by someone
trying to work out why a change was made, months later, with no memory of the
context. These rules exist to make `git log` scannable and to make each entry
answer that question on its own.

## Subject line

| Rule | |
|---|---|
| Length | Aim for **50 characters**, hard limit **72** |
| Mood | **Imperative** — "Add", not "Added" or "Adding" |
| Capitalisation | **Capitalise the first letter** |
| Punctuation | **No full stop at the end** |

```
Add README.md                        not  Added README.md
Move index.html file to root         not  Adding README.md
Update sample data                   not  move index.html file to root
                                     not  Update sample data.
```

The imperative reads as an instruction to the codebase — *"apply this commit to
Add README.md"* — which is also how Git's own generated messages are phrased
("Merge branch…", "Revert…"), so your commits match the tooling around them.

An optional scope prefix helps when a log covers many areas:

```
Person class: Remove static imports
Main.java: Remove blank lines
bug fix: Add space after name
chore: Update release date
```

## Body

Include a body whenever the *why* is not obvious from the subject alone. Skip
it for genuinely trivial changes.

- **Blank line between subject and body.** Without it, Git treats the whole
  thing as one subject and `git log --oneline` becomes unreadable.
- **Wrap the body at 72 characters.**
- **Blank lines between paragraphs.** Bullet points where they help.
- **Explain WHAT and WHY, not HOW.** The diff already shows how. What it cannot
  show is the reasoning, the alternative you rejected, or the bug that prompted
  the change.
- **Don't repeat what code comments already say.**

A structure that usually works:

1. The current situation, in present tense
2. Why it needs to change
3. What this commit does, in imperative mood
4. Why it is done this way rather than another way
5. Anything else worth knowing

```
Handle missing and corrupted data files

Loading crashed before the greeting could print. A truncated line such
as "D | 0 | broken" threw ArrayIndexOutOfBoundsException, and only
FileNotFoundException was caught. The data file is hand-editable, so
this is reachable in normal use.

Extract parseDataLine() to decode one line at a time, returning null
for anything malformed so the caller can skip it, count it, and still
load the remaining tasks.
```

## Branch names

- **Kebab case**, made of meaningful keywords: `refactor-ui-tests`
- **Tied to an issue**: `issueNumber-keywords-from-issue-title`, e.g.
  `1234-ui-freeze-error`

## Before committing

1. Subject: imperative, capitalised, no trailing period, ≤ 72 chars
2. Blank line before the body, body wrapped at 72
3. Body explains why, not how
4. Branch name is kebab case and descriptive

Check the subject length before committing:

```bash
git log -1 --pretty=%s | awk '{ print length, $0 }'
```
