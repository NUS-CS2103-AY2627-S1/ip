---
name: seedu-git-standard
description: The SE-EDU Git conventions that all commits and branches in this project must follow — commit subject line form, commit body structure and wrapping, and branch naming. Use whenever writing or proposing a commit message, amending or rewording a commit, creating a branch, or when asked whether a commit message follows the standard.
---

# SE-EDU Git conventions

Source: <https://se-education.org/guides/conventions/git.html>

These conventions govern **every** commit and branch in this repository.

## How to use this skill

- **Before proposing or writing any commit message**, draft it against the
  rules below, then check it with the review checklist at the end.
- **Before creating a branch**, name it by the branch rules below.
- **When asked whether a message is compliant**, report each violation with the
  rule it breaks and a corrected version, rather than silently rewriting it.
- **Never commit, push, amend or rebase unless the user explicitly asks** —
  that is a separate project rule in `AGENTS.md` and this skill does not
  override it. Producing a compliant message is not permission to commit it.

## Commit message: subject line

Every commit must have a well-written subject line.

| Rule | Good | Bad |
| --- | --- | --- |
| Limit to **50 characters** (hard limit **72**) | `Add README.md` | a subject that runs past the edge of `git log --oneline` |
| **Imperative mood** | `Add README.md` | `Added README.md`, `Adding README.md` |
| **Capitalize** the first letter | `Move index.html file to root` | `move index.html file to root` |
| **No trailing period** | `Update sample data` | `Update sample data.` |

The 50-character limit exists because some tools show only that much of the
message.

### Optional scope prefix

A `<scope>:` or `<category>:` may be put in front when it helps:

```text
Person class: Remove static imports
Main.java: Remove blank lines
bug fix: Add space after name
chore: Update release date
```

Note that the scope is written as it naturally reads (`Person class`,
`Main.java`, `bug fix`) — this is not Conventional Commits, which is a
different, more elaborate convention with its own benefits.

## Commit message: body

**Every non-trivial commit needs a body.** A one-line commit message is only
acceptable for a genuinely trivial change.

### Mechanics

- Separate the subject from the body with **one blank line**.
- **Wrap the body at 72 characters.** Hard-wrap it yourself; do not write one
  long unwrapped line per paragraph and rely on the reader's viewer.
- Separate paragraphs with blank lines.
- **Use bullet lists where they read better than prose.**

### Content: explain WHAT and WHY, never HOW

The reader has the diff; it already tells them *how*. The body exists to say
*what* the commit is about and *why* it was done that way.

Give enough explanation that a reader can judge whether the change is a good
idea **without reading the diff** to work out what it really does.

> If your description starts to get too long, that is a sign you probably need
> to split the commit into finer-grained pieces.

Do not repeat at length what the code comments in the same commit already say.

### Structure the body in this order

```text
{current situation}          -- present tense
{why it needs to change}
{what is being done about it} -- imperative mood
{why it is done that way}
{any other relevant info}
```

- Do **not** write `currently` or `originally` when describing the current
  situation — the present tense already implies it.
- `Let's` is the conventional marker for where the description of the change
  itself begins.

### Worked examples

A commit that is one step of a multi-commit change:

```text
Unify variations of toSet() methods

There are several methods that convert a collection to a set. In some
cases the conversion is in-lined as a code block in another method.
Unifying all those duplicated code improves the code quality.

As a step towards such unification, let's extract those duplicated code
blocks into separate methods in their respective classes. Doing so will
make the subsequent unification easier.
```

A bug fix, using a scope prefix and a bullet list:

```text
Find command: make matching case-insensitive

Find command is case-sensitive.

A case-insensitive find is more user-friendly because users cannot be
expected to remember the exact case of the keywords.

Let's,
* update the search algorithm to use case-insensitive matching
* add a script to migrate stress tests to the new format
```

A code-quality refactoring, showing the full five-part structure:

```text
Person attributes classes: extract a parent class PersonAttribute

Person attribute classes (e.g. Name, Address, Age etc.) have some common
behaviors (e.g. isValid()).

The common behaviors across person attribute classes cause code
duplication.

Extracting the common behavior into a super class allows us to use
polymorphism when dealing with person attributes. For example, validity
checking can be done for all attributes of a person in one loop.

Let's pull up behaviors common to all person attribute classes into a new
parent class named PersonAttribute.

Using inheritance is preferable over composition in this situation
because the common behaviors are not composable.

Refer to this S/O discussion on dealing with attributes
http://stackoverflow.com/some/question
```

Further reading: [How to Write a Git Commit
Message](https://cbea.ms/git-commit/).

## Branch names

- Use a **meaningful name of relevant keywords in kebab-case**, e.g.
  `refactor-ui-tests`.
- If the branch relates to an issue, use
  `issueNumber-some-keywords-from-issue-title`, e.g. `1234-ui-freeze-error`.

## Trailers

Trailers such as `Co-Authored-By:` go in a block of their own at the very end,
after a blank line following the last body paragraph. They are metadata, not
prose: they do not count towards the body, they are exempt from the 72-column
wrap, and they never replace a body that the commit needed anyway.

## Review checklist

Run through this before showing the user a commit message.

- [ ] Subject is ≤ 50 characters (≤ 72 at the absolute most), including any
      scope prefix.
- [ ] Subject is in the imperative mood — it completes the sentence
      "If applied, this commit will ...".
- [ ] Subject's first letter is capitalized and it has no trailing period.
- [ ] Subject and body are separated by a blank line.
- [ ] Body is present, unless the commit is genuinely trivial.
- [ ] **Every body line is hard-wrapped at 72 characters.**
- [ ] Body says WHAT and WHY, not HOW; a reader could judge the change without
      opening the diff.
- [ ] Body follows the order: current situation → why it must change → what is
      being done (imperative) → why done that way → other info.
- [ ] Body does not use `currently` or `originally`.
- [ ] Body does not restate the code comments added in the same commit.
- [ ] Bullet lists used where they read better than prose.
- [ ] If the body has grown long and sprawling, the commit is split instead.
- [ ] Any branch created is kebab-case and meaningful, prefixed with the issue
      number when there is one.
