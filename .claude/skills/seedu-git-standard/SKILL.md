---
name: seedu-git-standard
description: The SE-EDU (se-education.org) Git conventions for commit messages and branch names, which are MANDATORY for every commit and branch in this project. Use whenever writing or proposing a commit message, running git commit, amending or rewording a commit, creating or naming a branch, or when the user asks about commit style, commit message format, or branch naming.
---

# SE-EDU Git Conventions

Source: <https://se-education.org/guides/conventions/git.html>

**These conventions are mandatory for every commit and branch in this project.**
Never propose or create a commit message that violates them.

## Quick checklist

Before any `git commit`:

- [ ] Subject line in the **imperative mood** (`Add`, not `Added`/`Adding`)
- [ ] Subject line **capitalised**
- [ ] Subject line has **no trailing period**
- [ ] Subject line <= 50 chars (hard limit 72)
- [ ] Body present for any non-trivial commit, separated by a **blank line**
- [ ] Body wrapped at **72 chars**
- [ ] Body explains **WHAT and WHY**, not HOW

---

## 1. Subject line

**Every commit must have a well-written subject line.**

**Limit it to 50 characters** (hard limit: 72).

**Use the imperative mood** - the subject should complete the sentence
"If applied, this commit will ___".

```
Add README.md          # Good
Added README.md        # Bad
Adding README.md       # Bad
```

**Capitalise the first letter.**

```
Move index.html file to root   # Good
move index.html file to root   # Bad
```

**Do not end with a period.**

```
Update sample data     # Good
Update sample data.    # Bad
```

**An optional `<scope>:` or `<category>:` prefix** may be added when it helps:

```
Person class: Remove static imports
Main.java: Remove blank lines
bug fix: Add space after name
chore: Update release date
```

---

## 2. Body

**Non-trivial commits should have a body giving details of the commit.**

- **Separate subject from body with a blank line.**
- **Wrap the body at 72 characters.**
- Use blank lines to separate paragraphs.
- Use bullet points as necessary.
- **Explain WHAT and WHY, not HOW.** The diff already shows how.

**Recommended flow for the body:**

> {current situation} -- {why a change is needed} -- {what is being done} --
> {why it is done that way} -- {other supporting info}

**Avoid words like "currently" or "originally"** when describing the existing
state - write the current state in plain present tense and let the change
paragraph provide the contrast.

### Example

```
Parser: Extract date parsing into a helper

Parser duplicates the yyyy-mm-dd parsing logic in three places,
once for each of the deadline, event start, and event end fields.
Any change to the accepted date format has to be made in three
places, which is error-prone.

Extract the parsing into a single private helper that throws
InvalidDateException on failure. The helper is private rather than
a new public utility class because no other class needs it yet.
```

---

## 3. Branch names

**Use a meaningful name of relevant keywords, in kebab-case.**

```
refactor-ui-tests
```

**If the branch relates to an issue, prefix the issue number:**

```
issueNumber-some-keywords-from-issue-title
1234-ui-freeze-error
```

---

## 4. Project rules that sit alongside this standard

- Use **lightweight tags** unless an annotated tag is explicitly requested.
- **Do not commit or push unless explicitly asked.** Propose the message and wait.
- When proposing a message, include enough detail to explain the rationale.

## Applying this skill

To check the recent history against the standard:

```bash
git log --pretty=format:'%h %s' -20                 # inspect subject lines
git log --pretty=format:'%s' -20 | awk 'length>50'  # over-long subjects
git log --pretty=format:'%s' -20 | grep -E '\.$'    # trailing periods
git log --pretty=format:'%s' -20 | grep -E '^[a-z]' # uncapitalised (ignore scope: prefixes)
```

To fix the most recent commit message: `git commit --amend`.
Only rewrite already-pushed history when the user explicitly asks.
