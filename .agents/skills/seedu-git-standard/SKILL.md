---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions to this project. Use whenever proposing, drafting, reviewing, creating, or amending Git commits or commit messages, and whenever working with branches or tags in this repository.
---

# Apply the SE-EDU Git Standard

Read [references/rules.md](references/rules.md) before preparing a commit or branch name.

## Commit workflow

1. Identify the current situation, why it needs to change, what the commit does, and why that approach was chosen.
2. Keep the commit focused. If its explanation becomes too long, split the work into finer-grained commits.
3. Draft and check the subject against every subject rule in the reference.
4. Add a body for every non-trivial commit and check it against every body rule in the reference.
5. Review the final message for accurate WHAT and WHY explanations without narrating implementation details visible in the diff.
6. Pass the exact final message through standard input to `.githooks/commit-msg -`. Do not present or use the message unless the hook passes.

Do not create, amend, or push a commit unless the user has explicitly authorized that action. When asked only to propose a message, return the compliant message without changing repository state.

Use lightweight tags unless the user requests an annotated tag.
