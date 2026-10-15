---
name: seedu-git-standard
description: The SE-EDU Git conventions this project follows - commit message subject and body format, and branch naming. Use when writing or proposing a commit message, creating a branch, or reviewing whether a commit message follows the standard.
---

# SE-EDU Git conventions

The rules below are from <https://se-education.org/guides/conventions/git.html>
and apply to every commit and branch in this project.

## Subject line

* Every commit has a well-written subject line.
* Keep it to 50 characters. The hard limit is 72, because some tools show only
  the first few characters of a message.
* Use the imperative mood — `Add README.md`, not `Added README.md` or
  `Adding README.md`. A subject reads as an instruction to the codebase.
* Capitalize the first letter: `Move index.html file to root`, not
  `move index.html file to root`.
* Do not end with a period: `Update sample data`, not `Update sample data.`
* An optional scope or category prefix is allowed where it helps:

```
Person class: Remove static imports
Main.java: Remove blank lines
bug fix: Add space after name
chore: Update release date
```

## Body

* Any non-trivial commit has a body giving the details.
* Separate the subject from the body with a blank line.
* Wrap the body at 72 characters.
* Separate paragraphs with blank lines. Use bullet lists where they help.

Structure the body like this:

```
{current situation} -- use present tense

{why it needs to change}

{what is being done about it} -- use imperative mood

{why it is done that way}

{any other relevant info}
```

* Explain **what** and **why**, never **how**. The diff already shows how.
* Give enough explanation that a reader can judge whether the change is a good
  idea without reading the diff to work out what it does.
* Do not write "currently" or "originally" when describing the situation before
  the change. Both are implied by the present tense.
* `Let's` may open the paragraph describing the change being made.
* Do not repeat what the code comments in the same commit already say.
* If the explanation is getting long, that is usually a sign the commit should
  be split into smaller ones.

Worked example:

```
Find command: make matching case-insensitive

Find command is case-sensitive.

A case-insensitive find is more user-friendly because users cannot be
expected to remember the exact case of the keywords.

Let's,
* update the search algorithm to use case-insensitive matching
* add a script to migrate stress tests to the new format
```

## Branch names

* Use a meaningful name of relevant keywords in kebab case:
  `refactor-ui-tests`.
* A branch for an issue takes the issue number first:
  `1234-ui-freeze-error`, in the form `issueNumber-some-keywords-from-issue-title`.

## Checking a message before committing

Check the subject is imperative, capitalized, unpunctuated and within 72
characters, and that no body line exceeds 72:

```bash
git log -1 --format='%s' | awk '{ print length($0), $0 }'
git log -1 --format='%b' | awk 'length > 72 { print FNR": "length" chars" }'
```

For a message not yet committed, run the same checks over the draft file.
