---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when naming branches or proposing, editing, reviewing, or creating commits in this repository. Do not use for read-only Git inspection that does not produce a branch name or commit message.
---

# SE-EDU Git Standard

Make branch names and agent-authored commit messages comply with the project's
Git conventions.

## Workflow

1. Read `references/conventions.md` completely before proposing a branch name
   or composing, reviewing, or creating a commit.
2. Before writing a commit message, inspect the staged changes with
   `git status --short`, `git diff --cached --stat`, and the relevant staged
   diff. Base the message on what is actually staged.
3. Check that the staged changes form one coherent commit. If the explanation
   becomes unwieldy, recommend splitting the work instead of hiding multiple
   concerns behind a vague message.
4. Write and validate the subject. Add a body for every non-trivial commit.
5. Do not stage, unstage, commit, amend, tag, push, or otherwise mutate Git
   state unless the user has authorized that action. Message-writing requests
   alone do not authorize creating a commit.

Apply the standard to all future agent-authored commits, including merge and
maintenance commits. Preserve Git-generated metadata when it cannot reasonably
be controlled, and explain any unavoidable exception.
