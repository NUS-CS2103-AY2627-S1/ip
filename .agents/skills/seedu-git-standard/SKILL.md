---
name: seedu-git-standard
description: The mandatory Git convention for this project, from se-education.org's Git guide. Read this BEFORE writing any commit message, proposing a commit, naming a branch, or creating a tag. Covers subject line rules (imperative mood, capitalised, no trailing period, 50-char target and 72-char hard limit), the five-paragraph body structure wrapped at 72 characters, explaining WHAT and WHY rather than HOW, and kebab-case branch naming. Triggers on "commit", "commit message", "commit msg", "branch name", "tag", "git conventions", or any request to record work in git.
---

# se-education Git Convention

Source: <https://se-education.org/guides/conventions/git.html>

This convention is **mandatory** for every commit, branch, and tag in this
repository.

## Quick checklist

Before handing over any commit message:

- [ ] Subject in imperative mood (`Add`, not `Added` or `Adding`)
- [ ] Subject's first letter capitalised
- [ ] No period at the end of the subject
- [ ] Subject under 50 characters if possible, never over 72
- [ ] Blank line between subject and body
- [ ] Body wrapped at 72 characters
- [ ] Body follows the five-paragraph structure below
- [ ] Body explains WHAT and WHY, never HOW
- [ ] Branch name is kebab-case and meaningful

## 1. Subject line

| Rule | Good | Bad |
|---|---|---|
| Imperative mood | `Add README.md` | `Added README.md`, `Adding README.md` |
| Capitalise first letter | `Move index.html file to root` | `move index.html file to root` |
| No trailing period | `Update sample data` | `Update sample data.` |

- **Length: aim for 50 characters, hard limit 72.**
- An optional `<scope>:` or `<category>:` prefix is allowed:
  `Person class: Remove static imports`, `Main.java: Remove blank lines`,
  `bug fix: Add space after name`, `chore: Update release date`.

## 2. Body

- **Separate the subject from the body with a blank line.**
- **Wrap the body at 72 characters.**
- **Separate paragraphs with blank lines.**
- **Explain WHAT and WHY, not HOW.** A reader should be able to judge whether
  the change was worth making without opening the diff. The diff already shows
  how.
- Use bullet points where they help.
- Do not repeat what code comments already say.

### Required paragraph structure

```
{current situation} -- use present tense

{why it needs to change}

{what is being done about it} -- use imperative mood

{why it is done that way}

{any other relevant info}
```

Keep each paragraph to one or two short sentences. Do not pad a paragraph
simply to fill the slot; if a slot genuinely has nothing to say, leave it out
rather than restating a neighbour.

### Worked example

```
Reject backwards events and fix an error message typo

Parser accepts an event whose end precedes its start, and the
storage-delimiter error is missing a space after "Oh dear!".

A backwards event covers no dates, so it never matches 'on': the
task saves but the user can never find it again.

Reject an event whose end falls before its start, and add the
missing space.

Compare with isBefore so a zero-length event stays legal. Guard in
the parser only, since every saved event passes through it and
storage cannot receive a backwards one.

Update the affected unit tests and the event and delimiter cases
in the UI test plan. 200 tests pass and both cases were rerun.
```

## 3. Branch names

- Use meaningful keywords in **kebab-case**: `refactor-ui-tests`.
- For a branch addressing a tracked issue, lead with the issue number:
  `issueNumber-some-keywords-from-issue-title`, e.g. `1234-ui-freeze-error`.
- This project additionally uses `branch-<Increment>` for course increments,
  e.g. `branch-A-JavaDoc`. Keep that pattern for increment work.

## 4. Points the standard leaves open

The guide says nothing about tags or pull requests, so this project's own
rules apply:

- **Use lightweight tags** unless an annotated tag is explicitly requested.
- Tag the commit at which an increment was achieved. If the increment needed
  no code change, an existing commit may carry a second tag.

## 5. Working practice in this repository

- **Do not run `git commit` or `git push`.** Produce the message text for the
  user to review and commit themselves, even when permission to commit exists.
- Print the message in a fenced block, and flag anything staged that the user
  may want to exclude before committing.
- Never use `--no-verify` or skip signing unless explicitly asked.
