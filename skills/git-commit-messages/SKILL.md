---
name: git-commit-messages
description: Create or validate Git commit messages against this project's subject-line and body standards before a requested commit.
---

# Git Commit Messages

Use this skill whenever preparing, proposing, validating, or creating a Git
commit in this repository. Read the [local commit-message standard](references/commit-message-standard.md)
before drafting the message.

## Before the commit

1. Inspect the staged diff and any relevant issue or task context. Do not stage,
   amend, commit, or push unless the user has authorized that action.
2. Draft a concise imperative subject that explains the change. Add an optional
   scope or category only when it makes the subject clearer.
3. For a non-trivial change, write a body that explains the current situation,
   why it needs to change, what the commit does, and why that approach was
   chosen. Focus on what and why, not implementation mechanics.
4. Validate the final message against the checklist below. Revise it before
   invoking `git commit`.

## Subject checklist

- Starts with a capital letter, uses imperative mood, and has no trailing period.
- Is 50 characters or fewer where practical and never exceeds 72 characters.
- May use a meaningful `<scope>:` or `<category>:` prefix when useful.

## Body checklist

- Required for non-trivial commits; separated from the subject by one blank line.
- Wrap prose at 72 characters and separate paragraphs with blank lines.
- Uses bullets where they make multiple changes easier to scan.
- Gives enough context to judge the change without inspecting the diff, while
  avoiding duplicated code-comment detail.

If a user supplies a non-compliant message, propose a compliant replacement
before committing. Do not rewrite published commit history unless explicitly
asked.
