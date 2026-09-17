---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches or drafting, reviewing, or creating commits in this project.
---

# SE-EDU Git Standard

Use this skill whenever naming a branch or drafting, reviewing, or creating a
commit in this project. The rules are based on the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Preserve authorization and history

- A request to edit code does not authorize a commit or push. Commit or push
  only when the user explicitly asks.
- Do not amend, rebase, force-push, or otherwise rewrite existing history just
  to repair an old message unless the user explicitly requests it.

## Keep each commit cohesive

1. Inspect `git status` and the staged diff before committing.
2. Put one logical change in each commit. Split changes that serve unrelated
   purposes, but keep implementation and its directly related tests together.
3. Do not include unrelated user changes or generated files accidentally.

## Write the commit message

- Start the subject with a capital letter and an imperative verb, as though it
  completes the sentence, "If applied, this commit will ..."
- Aim for 50 characters or fewer and never exceed 72 characters.
- Do not end the subject with a period.
- An optional category or scope prefix may be used when it adds clarity, but
  use it consistently.
- For a nontrivial change, add a body separated from the subject by one blank
  line.
- Keep every nonblank commit-message line at 72 characters or fewer. This
  includes the subject, body paragraphs, bullet items, and trailers. Separate
  ideas with blank lines and use bullets where they improve readability.
- Explain what changed and why. Leave implementation details that are obvious
  from the diff out of the message.
- Describe the existing situation in the present tense and the requested
  change in the imperative mood.

Before committing, review the message and staged scope:

```bash
git diff --cached --check
git diff --cached --stat
git diff --cached
```

After committing, verify the result:

```bash
git show --stat --format=fuller HEAD
```

## Name branches

- Use a short, meaningful kebab-case branch name.
- For issue-specific work, prefer `issueNumber-keywords`, for example
  `42-fix-storage-error`.
- Avoid vague names such as `changes`, `work`, or a person's name.
