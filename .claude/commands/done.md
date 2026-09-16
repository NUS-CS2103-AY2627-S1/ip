---
description: Commit the current milestone, tag it, and push both to origin/master
argument-hint: <tag> e.g. Level-7
allowed-tools: Read, Grep, Glob, Bash(git rev-parse:*), Bash(git status:*), Bash(git diff:*), Bash(git log:*), Bash(git tag:*), Bash(git add:*), Bash(git commit:*), Bash(git push:*), Bash(javac:*), Bash(java:*), Bash(find:*), Bash(sdk:*)
---

# Finish and publish a milestone

Requested tag: **$1**

## Current state

- Branch: !`git rev-parse --abbrev-ref HEAD`
- Working tree: !`git status --short`
- Recent commits: !`git log --oneline -5`
- Existing tags: !`git tag`

## Your task

Invoking this command is my explicit authorisation to commit, tag, and push —
the standing rule in `CLAUDE.md` against doing so unasked does not apply here.
It authorises **this milestone only**.

1. **Resolve the tag name.** Treat `$1` as the exact tag name, verbatim — do
   not normalise its case or punctuation. If no tag was supplied, ask me for
   one and stop.

2. **Refuse to clobber.** If the tag already exists in the list above, stop and
   ask how to proceed. Never amend, move, or delete an existing tag without my
   explicit approval.

3. **Scope the change.** Review `git status` and the full diff. Identify only
   the changes belonging to this milestone. Leave unrelated edits of mine
   unstaged — do not sweep them in to make the tree clean. If you cannot tell
   which changes belong, ask.

4. **Verify before committing.** Compile with Java 25:
   ```
   javac -d /tmp/ip-build $(find src/main/java -name '*.java')
   ```
   Run the bot and exercise the milestone's feature if it is a behavioural
   change. If compilation fails, stop — do not commit broken code.

5. **Review against the standard.** Re-read the diff against the Java rules in
   `CLAUDE.md` §3 before committing: Javadoc on new public classes and methods,
   4-space indent, no wildcard imports, braces everywhere, ≤120 char lines.
   Fix violations in code you wrote. Do not reformat pre-existing code.

6. **Commit.** Write a message following `CLAUDE.md` §2 — imperative subject,
   capitalized (including after any scope prefix), ≤50 chars, no trailing
   period. Add a body explaining what and why when the change is non-trivial,
   wrapped at 72 chars.

7. **Tag.** Create a *lightweight* tag with the exact supplied name on the new
   commit:
   ```
   git tag $1
   ```

8. **Push both.** Tags do not push with commits:
   ```
   git push origin master
   git push origin $1
   ```

9. **Report.** Give me the commit hash, the commit subject, the tag, the push
   result, and an explicit list of any changes left uncommitted.
