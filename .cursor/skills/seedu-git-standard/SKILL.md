---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to branch names and commit messages. Use when committing, amending, proposing a commit message, naming or renaming a branch, writing a PR title that matches a commit, or when the user mentions Git style, commit message, or A-GitStandard.
---

# SE-EDU Git conventions

Follow **[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)** for every commit and branch in this project.

Do not commit or push unless the user asked. When they do, **read this skill first** and use it for the message and any new branch name. When only *proposing* a message, still write it in this form.

For extra advice on tone, see [How to Write a Git Commit Message](https://cbea.ms/git-commit/). Full examples are in [reference.md](reference.md).

## Checklist

Copy and complete before `git commit`:

```
SE-EDU Git:
- [ ] Subject: imperative, capitalized, no period, <= 50 chars (hard 72)
- [ ] Blank line between subject and body
- [ ] Body present if the commit is not trivial; wrap at 72 chars
- [ ] Body explains WHAT and WHY (not HOW); present tense then Let's + imperative
- [ ] Branch name is kebab-case (or CS2103T increment form)
```

## Commit subject (every commit)

- Limit the subject to **50 characters** when possible. **Hard limit: 72.**
- **Imperative mood**, as if completing “If applied, this commit will …”:
  - Good: `Add README.md`
  - Bad: `Added README.md` / `Adding README.md`
- **Capitalize** the first letter. Do **not** end with a period.
- An optional prefix is allowed: `Person class: Remove static imports`, `Main.java: Remove blank lines`, `bug fix: Add space after name`, `chore: Update release date`.

Pass the full message with a HEREDOC (subject, blank line, body). Do not use `-m` twice as a substitute for a real body.

## Commit body (non-trivial commits)

Trivial one-line changes may be subject-only. Anything that changes behavior, structure, or needs a rationale **must** have a body.

- Separate subject and body with a **blank line**.
- Wrap the body at **72 characters**. Separate paragraphs with blank lines.
- Use bullets when they are clearer than a paragraph.
- Explain **WHAT** and **WHY**, not **HOW** (the diff already shows how).
- Give enough detail that a reader can judge the change without opening the diff. If the explanation is getting long, split the work into smaller commits.
- Do not restate code comments from the same commit.

Structure:

```
{current situation}          -- present tense; do not say "currently"/"originally"
{why it needs to change}

Let's {what is being done}   -- imperative mood; "Let's" starts the change
{why it is done that way}
{any other relevant info}
```

Example shape (see [reference.md](reference.md) for full samples):

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

- Meaningful kebab-case keywords: `refactor-ui-tests`.
- If the branch tracks an issue: `issueNumber-some-keywords-from-issue-title` (e.g. `1234-ui-freeze-error`).
- CS2103T increment branches already in this repo stay in course form: `branch-A-JavaDoc`, `branch-A-CodingStandard`, `branch-Level-8`. New increment branches follow that same `branch-<id>` pattern.

## Do not

- Do not invent Conventional Commits (`feat:`, `fix:`) unless the user asks. A short `chore:` / `bug fix:` prefix is optional, not required.
- Do not force-push, skip hooks, or amend a published commit unless the user explicitly asks.
- Do not commit secrets, `data/wodan.txt`, or other gitignored runtime files.
