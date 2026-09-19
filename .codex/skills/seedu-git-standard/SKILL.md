---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing or creating branches, commits, merges, and tags in this project.
---

# SE-EDU Git Standard

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).
An explicit branch name or commit message supplied by the user takes precedence.

## Commit messages

- Write an imperative, capitalized subject without a trailing period.
- Aim for at most 50 characters and never exceed 72 characters.
- Add an optional scope or category when it clarifies the affected area.
- For a non-trivial change, separate the body with a blank line, wrap it at 72
  characters, and explain what changed and why rather than how it was coded.
- Keep each commit to one complete logical change that can stand on its own.

## Git workflow

- Use meaningful kebab-case branch names unless the user or assignment specifies
  an exact name.
- Inspect the staged diff and run `git diff --cached --check` before committing.
- Run the relevant verification before committing functional code.
- Keep generated artifacts and unrelated working-tree changes out of commits.
- Use lightweight tags unless the user requests annotated tags.
- Do not rewrite published history or push unless the user explicitly authorizes it.
