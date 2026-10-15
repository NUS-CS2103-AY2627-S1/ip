---
name: seedu-git-standard
description: The SE-EDU Git conventions this project follows for commit messages, branch names and tags. Use before writing or proposing any commit message, when naming a branch, when creating a tag, or when asked whether a commit message follows the project's Git standard.
---

# SE-EDU Git conventions

Every commit in this project follows the SE-EDU Git conventions, from
<https://se-education.org/guides/conventions/git.html>.

Apply this whenever you write or propose a commit message, name a branch, or
create a tag. Write the message before reaching for the commit command, not
after: a message assembled at the last moment describes the diff, and the diff
is the one thing the reader can already see.

**Propose the subject line on its own by default.** This project's commits are
subject-only, which the standard allows; see the body section for the cases that
earn more.

## Subject line

* **Limit to 50 characters.** Hard limit 72.
* **Imperative mood** — write the instruction the commit carries out.
  `Add README.md`, not `Added README.md` or `Adds README.md`. It should complete
  the sentence "If applied, this commit will ...".
* **Capitalize the first letter.**
* **No full stop at the end.**
* Optionally prefix with a scope or category, `<scope>: <subject>`:
  * `Person class: Remove static imports`
  * `bug fix: Add space after name`

```
Good                                    Bad
Spell comments the American way         spelled the comments american way.
TaskList: Refuse a task number of 0     Fixed bug
```

## Body

**A body is optional.** The standard says how to format one, not that every
commit needs one. Most commits here are a single clear change whose subject line
tells the whole story, and those are committed subject-only:

```bash
git commit -m "Spell comments the American way"
```

Write a body only when the subject cannot carry the reasoning by itself — when
the change looks wrong without an explanation, when an obvious alternative was
rejected, or when a rule was deliberately not followed. Padding an obvious
commit with a paragraph restating its subject makes the log harder to skim, not
easier.

When there is a body:

* **Blank line between subject and body.** Without it, `git log --oneline` and
  most tools treat the whole thing as one long subject.
* **Wrap at 72 characters.** Git does not wrap for you, and tools indent the
  body when displaying it, so longer lines are read off the edge of the screen.
  Note that `-m` never wraps: type the newlines yourself inside the quotes, or
  the paragraph is stored as one very long line.
* **Blank lines between paragraphs.** Bullet points where they help.
* Each `-m` becomes one paragraph, so a subject plus two paragraphs is three
  `-m` flags.

## What to say

* **Explain WHAT and WHY, not HOW.** The diff already shows how.
* Give enough that a reader can judge the change **without opening the diff**.
* **Do not repeat the code comments.** If the reasoning belongs beside the code,
  put it there and let the message say why the change was needed at all.
* **Avoid "currently" and "originally"** when describing how things stand. The
  message is read long after today, when "currently" means something else.
  Describe the existing situation in plain present tense instead.

A body that works, in order:

1. The situation as it stands, in present tense.
2. Why that needs to change.
3. What is being done about it, in imperative mood — often "Let's ...".
4. Why it is done that way rather than another way.
5. Anything else the reader needs, such as a rule that could not be followed.

```
TaskList: Refuse a task number of 0

Task numbers are what the user sees, counting from 1, but the list
underneath counts from 0. Passing 0 reaches the first task rather
than being refused, so a mistyped command silently acts on the wrong
one.

Let's check the number against the range the user was shown, and
throw when it falls outside. The check lives in TaskList rather than
in each command, since the list is what knows how many tasks exist.
```

## Branch names

* Meaningful, in **kebab-case**: `refactor-ui-tests`.
* For work on an issue, lead with the number:
  `issueNumber-some-keywords-from-issue-title`, e.g. `1234-ui-freeze-error`.
* This project's course increments have their own fixed names, given by the
  increment itself, e.g. `branch-A-CodingStandard`.

## Tags

* **Lightweight tags** unless an annotated tag is explicitly asked for.
* Increment tags carry the increment's exact name, e.g. `A-Gradle`, and go on
  the commit where that increment is complete — the merge commit when it was
  done on a branch.

## Working practice

* **Do not commit or push unless explicitly asked.** Propose the message and
  the commands, and let the user run them.
* Every commit message ends with the trailer:

```
Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>
```

* Keep one commit to one coherent change. A commit that needs "and" in its
  subject line is usually two commits.

## Checking a message

```bash
git log -1 --format=%s | awk '{ print length": "$0 }'
```

Anything over 50 wants a second look, over 72 must be shortened.

```bash
git log -1 --format=%b | awk 'length > 72 { print FNR": "length" chars" }'
```

Prints any body line past the 72-character wrap.
