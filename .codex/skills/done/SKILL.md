---
name: done
description: Commit related project changes, create the requested lightweight Git tag, and push both the commit and tag to master. Use when the user invokes done with a tag such as "done Level-4".
---

# Finish and publish a milestone

Use this skill only for an explicit request in the form `done <tag>` or an equivalent request that clearly asks for this workflow.

1. Read and follow the repository instructions in `AGENTS.md`.
2. Treat the supplied value as the exact Git tag name. If no tag is supplied, ask the user for one.
3. Inspect `git status` and the diff. Identify only changes related to the current milestone; do not stage unrelated user changes.
4. Run the relevant verification for the changed code before committing. For this Java project, use Java 25.
5. Create a concise commit whose message explains the milestone and follows the Git conventions in `AGENTS.md`.
6. Create a lightweight tag with the exact supplied name pointing to the new commit.
7. Push the commit to the `master` branch and push the tag to `origin`.
8. Verify and report the commit ID, commit message, tag, push result, and any unrelated changes left uncommitted.

Do not amend, delete, or overwrite an existing tag without explicit user approval. If the requested tag already exists, stop and ask how to proceed. Do not commit or push unrelated changes.

Example:

`done Level-4`

creates a milestone commit, the lightweight tag `Level-4`, and pushes both to `origin/master`.
