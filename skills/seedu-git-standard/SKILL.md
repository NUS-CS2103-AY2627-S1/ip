---
name: seedu-git-standard
description: Prepare, review, and create Git commits and branches in this repository according to the SE-EDU Git conventions. Use whenever proposing commit messages, committing, amending or squashing commits, or naming branches for this project.
---

# SE-EDU Git Standard

Follow the project's Git conventions, distilled from the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html).

## Commit subjects

- Write a meaningful subject for every commit.
- Prefer at most 50 characters; never exceed 72 characters.
- Use imperative mood, as if completing the phrase “This commit will ...”.
- Capitalize the first letter.
- Do not end with a period.
- Add a meaningful `<scope>:` or `<category>:` prefix only when it improves clarity. Conventional
  Commits are optional, not required.

Before proposing or creating a commit, verify the subject against every rule above.

## Commit bodies

Add a body for every non-trivial commit.

- Separate the subject and body with one blank line.
- Wrap body text at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why it was necessary or appropriate. Leave implementation mechanics to
  the diff unless they are important context.
- Describe the existing situation in present tense. Describe the change in imperative mood.
- Avoid redundant qualifiers such as “currently” and “originally”.
- Use bullet points when they make multiple related changes easier to scan.
- Do not repeat details already clear from code comments.

If the body becomes unwieldy or covers unrelated rationales, recommend splitting the work into
smaller, focused commits.

## Branch names

- Use meaningful keywords in kebab-case, such as `refactor-ui-tests`.
- For issue-related branches, use `issueNumber-keywords-from-issue-title`, such as
  `1234-ui-freeze-error`.

## Workflow and authority

Inspect the staged diff before writing a final commit message so the message describes the actual
commit. Do not include unrelated working-tree changes in the message.

This skill governs Git conventions only. It never grants permission to stage files, commit, amend,
rewrite history, create branches, tag, or push. Follow the repository's authorization rules and the
user's explicit request for those actions.
