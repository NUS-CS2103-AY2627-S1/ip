---
name: seedu-git-standard
description: Write SE-EDU-compliant Git commit messages for changes in this project.
---

# SE-EDU Git commit standard

Apply these rules to every commit message in this project. Use the official
standard for details: <https://se-education.org/guides/conventions/git.html>.

## Subject

- Write a clear subject for every commit.
- Prefer 50 characters or fewer and never exceed 72 characters.
- Use the imperative mood, capitalize the first letter, and do not end with a period.
- Add a scope or category prefix only when it improves clarity, in the form
  `Person class: Remove static imports` or `bug fix: Add space after name`.

## Body

- Add a body for non-trivial commits, separated from the subject by a blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines.
  This is a hard limit on every line, including the first line of a paragraph
  and each bullet point. Insert the line breaks yourself; do not rely on the
  editor or terminal to wrap the text for you.
- Explain what changed and why; leave implementation details to the diff.
- Give enough explanation that a reader can judge whether the change is a good
  idea without reading the diff.
- Minimize repeating information already stated in the code comments of the same
  commit.
- Avoid vague repetition and words such as `currently` or `originally`; they are
  implied by the description of the current situation.

### Body structure

Order the body as follows:

```
{current situation} -- use present tense

{why it needs to change}

{what is being done about it} -- use imperative mood

{why it is done that way}

{any other relevant info}
```

Use the word `Let's` to mark where the description of the change begins.

Use bullet points instead of prose paragraphs where a list reads more clearly,
typically when the commit does several related things.

### Worked example

```
Find command: make matching case-insensitive

Find command is case-sensitive.

A case-insensitive find is more user-friendly because users cannot be
expected to remember the exact case of the keywords.

Let's,
* update the search algorithm to use case-insensitive matching
* add a script to migrate stress tests to the new format
```

## Verify the wrapping before committing

Unwrapped body lines are the most common problem reported against this
repository. Write the message to a file, check it, then commit from that file so
that what you verified is what gets stored.

```
awk 'length > 72 {print NR ": " length " chars"}' /tmp/commitmsg.txt
git commit -F /tmp/commitmsg.txt
```

The command prints nothing when the message is correctly wrapped. If it prints a
line number, break that line and check again. Do not amend past commits to fix
wrapping; rewriting history changes commit timestamps that the course uses.

## Commit scope

Keep commits focused so that each commit represents one coherent change. If the
body grows long enough that it is hard to write, treat that as a signal to split
the commit into finer-grained pieces.
