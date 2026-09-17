---
name: seedu-git-standard
description: Apply the SE-EDU Git conventions when proposing, creating, or reviewing commits and commit messages in this project, and when naming branches. Use for every commit-related task; it does not itself authorize committing or pushing.
---

# Apply the SE-EDU Git standard

Read [references/rules.md](references/rules.md) completely before proposing, creating, or reviewing a commit or branch name.

## Prepare a commit

1. Confirm that the user has explicitly authorized the commit. Never treat invocation of this skill as permission to commit or push.
2. Inspect the working tree, staged files, and staged diff. Preserve unrelated user changes and stage only the intended files.
3. Check that the staged changes form one coherent commit. If the required explanation becomes long or covers unrelated reasons, recommend splitting the commit before proceeding.
4. Draft the subject and, for a non-trivial commit, a body that satisfies every rule in the reference.
5. Recheck the exact message for line lengths, imperative mood, capitalization, punctuation, and WHAT/WHY content before committing.
6. After committing, report the commit hash and subject and verify whether the working tree is clean.

When only proposing or reviewing a message, apply the same message checks without modifying Git state.
