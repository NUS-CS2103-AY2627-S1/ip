---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to commits and branches in this project.
---

# SE-EDU Git standard

Apply this skill whenever creating a commit or branch in this project. The authoritative reference is the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subjects

- Write a meaningful subject line, preferably within 50 characters and never over 72 characters.
- Use imperative mood, capitalize the first letter, and do not end the subject with a period.
- Add a scope or category prefix only when it improves clarity.

## Commit bodies

- Add a body for every non-trivial commit, separated from the subject by a blank line.
- Wrap body lines at 72 characters and use blank lines or bullets to separate ideas.
- Explain what changed and why it changed, rather than narrating implementation steps.
- Describe the current situation in present tense, state why it needs to change, and describe the change in imperative mood.
- Make the explanation detailed enough for a reviewer to judge the change without reading the diff.
- Avoid redundant explanations already present in code comments.

## Branch names

- Use meaningful kebab-case names based on the work, such as `refactor-ui-tests`.
- For issue-related work, use `issueNumber-keywords-from-title`.
- Preserve any higher-priority repository branch prefix while keeping the meaningful portion in kebab case.

## Commit workflow

Before committing, inspect the staged diff, check the subject and body against these rules, and verify the relevant tests. Do not create a commit unless the user has authorized it.
