---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to commit messages and branch names in this project. Use whenever writing or proposing a commit message, creating a branch, or reviewing whether git history follows the standard.
---

# SE-EDU Git Standard

The rules below are the Git conventions published at
<https://se-education.org/guides/conventions/git.html>. They apply to every commit and branch
in this project.

Apply them when proposing a commit message, not only when asked to check one. Propose the
message and let the user decide: do not commit or push unless the user explicitly asks.

## Commit message subject

* Limit the subject to 50 characters. The hard limit is 72.
* Write the subject in the imperative mood, as an instruction to the codebase.
  Write `Add README.md`, not `Added README.md` or `Adding README.md`.
  A good test: the subject should complete the sentence "If applied, this commit will ...".
* Capitalize the first letter.
  Write `Move index.html file to root`, not `move index.html file to root`.
* Do not end the subject with a period.
  Write `Update sample data`, not `Update sample data.`
* A `<scope>:` or `<category>:` prefix is optional, such as
  `Person class: Remove static imports` or `chore: Update release date`.

## Commit message body

* Separate the subject from the body with one blank line.
* Wrap the body at 72 characters.
* Separate paragraphs with blank lines.
* Explain WHAT and WHY, not HOW. The diff already shows how.
* Order the body as: the current situation in the present tense, why it needs to change, what
  this commit does about it in the imperative mood, why it is done that way, and anything else
  the reader needs.
* Describe the existing situation in plain present tense. Avoid words such as "currently" or
  "originally".
* Use bullet points where they read better than prose.

## Branch names

* Name a branch with relevant keywords in kebab case, such as `refactor-ui-tests`.
* When the branch belongs to an issue, use `issueNumber-some-keywords-from-issue-title`,
  such as `1234-ui-freeze-error`.

## Check the history against the standard

Run these from the repository root. Each should print nothing.

```bash
git log --format="%h %s" -30 | awk '{ s = substr($0, 9) } length(s) > 50 { print $0 "  <- " length(s) " chars" }'
```

```bash
git log --format="%h %s" -30 | grep -E ":? [a-z]" | grep -vE "^[0-9a-f]+ ([A-Z]|Merge)"
```

```bash
git log --format="%h %s" -30 | grep "\.$"
```

```bash
git log --format="%h %s" -30 | grep -iE "^[0-9a-f]+ (Added|Adding|Fixed|Fixing|Updated|Updating|Removed|Removing|Changed|Changing)\b"
```

The first finds subjects over 50 characters, the second finds an uncapitalized first letter,
the third finds a trailing period, and the fourth finds a subject written in the past or
continuous tense instead of the imperative. Body width and content still need reading.
