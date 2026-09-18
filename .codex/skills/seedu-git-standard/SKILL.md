---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches or proposing, reviewing, or creating commits in this project.
---

# SE-EDU Git Standard

Use the [official SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
as the authority for commit messages and branch names in this repository.

## Before a commit

1. Confirm that the user has explicitly authorized creating the commit. This skill does not
   grant permission to commit, amend, tag, push, or otherwise rewrite Git history.
2. Inspect `git status` and the staged diff. Keep unrelated user changes out of the commit.
3. Make the commit a coherent, fine-grained change. Split unrelated work into separate
   commits; an overly long explanation is a sign that the commit may be too broad.
4. Draft the complete message, then check it with
   `bash .codex/skills/seedu-git-standard/scripts/check-commit-message.sh <message-file>`.
5. Review the semantic rules below, which cannot be verified reliably by a script.

## Subject line

- Write a well-formed subject for every commit.
- Use imperative mood, as if completing the phrase "This commit will ...".
- Capitalize the first letter of the subject's action.
- Aim for 50 characters and never exceed 72 characters.
- Do not end the subject with a period.
- Add a meaningful `<scope>:` or `<category>:` prefix only when it improves clarity, for
  example `Parser: Reject blank dates` or `chore: Update Gradle wrapper`.

## Body

- Add a body for every non-trivial commit.
- Separate the subject and body with one blank line.
- Wrap body lines at 72 characters and separate paragraphs with blank lines. Use bullets
  when they make the explanation clearer.
- Explain what changed and why it was needed. Leave implementation mechanics to the diff.
- Give enough context for a reviewer to judge the change without first reading the diff.
- Prefer this sequence when useful: present situation, reason to change, action taken,
  rationale for that approach, and other relevant information.
- Use present tense for the situation and imperative mood for the action.
- Avoid redundant details already stated in code comments, and avoid filler words such as
  "currently" or "originally" when the timing is already implied.

## Branch names

- Use meaningful keywords in kebab case, such as `refactor-ui-tests`.
- For an issue-specific branch, use
  `issueNumber-some-keywords-from-issue-title`, such as `1234-ui-freeze-error`.
- Follow an explicitly required course branch name when it conflicts with the general
  kebab-case recommendation.

## After an authorized commit

- Inspect the resulting commit and working-tree status to confirm the intended files and
  message were recorded.
- Do not amend or force-push to repair a problem without explicit user authorization;
  explain the safe options first.
