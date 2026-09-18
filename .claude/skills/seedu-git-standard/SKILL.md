---
name: seedu-git-standard
description: The SE-EDU Git conventions that all commits and branches in this project must follow - subject line form and length, body structure and wrapping, what to explain, and branch naming. Use whenever writing or proposing a commit message, creating a branch, or reviewing commit history.
---

# SE-EDU Git conventions

Source: https://se-education.org/guides/conventions/git.html

Every commit in this repository follows these rules.

## Subject line

Every commit needs a well-written subject line.

- **Limit to 50 characters**, 72 absolute maximum. Some tools show only a
  limited number of characters.
- **Imperative mood.** `Add README.md`, not `Added README.md` or
  `Adding README.md`.
- **Capitalize the first letter.** `Move index.html file to root`, not
  `move index.html file to root`.
- **No trailing period.** `Update sample data`, not `Update sample data.`
- An optional `<scope>:` or `<category>:` prefix may go in front:
  `Person class: Remove static imports`, `Main.java: Remove blank lines`,
  `bug fix: Add space after name`, `chore: Update release date`.

## Body

Non-trivial commits need a body.

- Separate subject from body with a **blank line**.
- **Wrap the body at 72 characters.**
- Blank lines between paragraphs; bullet lists where they help.

### Structure

```
{current situation} -- use present tense

{why it needs to change}

{what is being done about it} -- use imperative mood

{why it is done that way}

{any other relevant info}
```

- Avoid *currently* and *originally* when describing the present situation.
  They are implied.
- `Let's` may open the section describing what the commit does.

### What to say

- Explain **WHAT** the commit is about and **WHY** it was done that way. The
  reader can refer to the diff to understand **HOW**.
- Give enough detail that a reader can judge whether the change is a good idea
  without reading the diff.
- **If the description starts getting too long, that is a sign the commit
  should be split into finer-grained pieces.**
- Minimize repeating information already in the code comments of the same
  commit.

### Worked examples from the guide

Part of a multi-commit PR:

```
Unify variations of toSet() methods

There are several methods that convert a collection to a set. In some
cases the conversion is in-lined as a code block in another method.

Unifying all those duplicated code improves the code quality.

As a step towards such unification, let's extract those duplicated code
blocks into separate methods in their respective classes. Doing so will
make the subsequent unification easier.
```

A bug fix, using bullets:

```
Find command: make matching case-insensitive

Find command is case-sensitive.

A case-insensitive find is more user-friendly because users cannot be
expected to remember the exact case of the keywords.

Let's,
* update the search algorithm to use case-insensitive matching
* add a script to migrate stress tests to the new format
```

A refactoring, showing the full structure:

```
Person attributes classes: extract a parent class PersonAttribute

Person attribute classes (e.g. Name, Address, Age etc.) have some common
behaviors (e.g. isValid()).

The common behaviors across person attribute classes cause code duplication.

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

## Branch names

- Meaningful keywords in **kebab-case**: `refactor-ui-tests`.
- For a branch tied to an issue: `issueNumber-some-keywords-from-issue-title`,
  e.g. `1234-ui-freeze-error`.

## Checking before committing

```bash
git log --format='%s' -10 | awk '{ printf "%3d  %s\n", length, $0 }'
git log -1 --format=%b | awk 'length > 72 { printf "over 72: %s\n", $0 }'
```

The first lists recent subject lengths; anything over 50 wants a second look
and anything over 72 must be shortened. The second finds body lines that need
rewrapping.
