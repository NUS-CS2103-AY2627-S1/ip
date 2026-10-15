---
name: seedu-git-standard
description: The SE-EDU Git conventions that all commits and branches in this project must follow - commit message subject and body format, imperative mood, wrapping, and branch naming. Use when writing or proposing a commit message, when creating a branch, or when asked whether a commit message follows the convention.
---

# SE-EDU Git conventions

Every commit and branch in this project follows this standard. It is the
convention at <https://se-education.org/guides/conventions/git.html>, recorded
here so it can be applied without fetching the page.

## Commit message: subject

Every commit has a well-written subject line.

- **Imperative mood.** `Add README.md` — not `Added README.md`, not
  `Adding README.md`. Read it as completing the sentence *"If applied, this
  commit will …"*.
- **Capitalise the first letter.** `Move index.html file to root`, not
  `move index.html file to root`.
- **No full stop at the end.** `Update sample data`, not `Update sample data.`
- **Aim for 50 characters; 72 is the hard limit.**
- A `<scope>:` or `<category>:` prefix may be added where it helps:
  `Person class: Remove static imports`, `Main.java: Remove blank lines`,
  `bug fix: Add space after name`, `chore: Update release date`.

## Commit message: body

A non-trivial commit has a body giving the details.

- **A blank line separates the subject from the body.**
- **Wrap the body at 72 characters.**
- **Blank lines separate paragraphs.** Use bullet points where they help.
- **Explain WHAT and WHY, not HOW.** The diff already shows how. Do not repeat
  what the code comments in the same commit already say.

The recommended shape:

```
{current situation}          -- present tense
{why it needs to change}
{what is being done about it} -- imperative mood
{why it is done that way}
{any other relevant info}
```

- **Avoid "currently" and "originally"** when describing the present situation.
  Write "The parser rejects …", not "Currently the parser rejects …".

### Worked example

```
Point the Gradle build at Elsa

The provided Gradle branch was written for the Duke template, so its
mainClass named seedu.duke.Duke and its fat jar was called duke.jar.
Neither exists in this project: the build compiled cleanly but nothing
could be launched from it, because mainClass is only read at run time.

Also add settings.gradle to name the project Elsa. Without it Gradle
falls back to the directory name, so the distributions were being built
as CS2103T_ip.zip rather than Elsa.zip.
```

Subject is imperative, capitalised, no full stop, under 50 characters. The body
says what was wrong, why it mattered, and what was done — not which lines
changed.

## Branch names

- **Kebab case, meaningful keywords**: `refactor-ui-tests`.
- Related to an issue: `issueNumber-some-keywords-from-issue-title`, for example
  `1234-ui-freeze-error`.

### This project's increment branches

Increment work uses `branch-<TagName>`, for example `branch-A-JUnit`. This
predates the convention above and is kept because:

- the course's own instructions name the branches this way;
- the progress dashboard checks the fork for branches called
  `branch-Level-7`, `branch-Level-9`, `branch-A-CodingStandard` and the like.

Any branch that is **not** an increment branch uses plain kebab case.

## Working practice in this project

- **The user makes the commits.** Prepare the change, show what changed, draft
  the message, and hand over the `git` commands. Do not run `git commit`,
  `git tag` or `git push`.
- One increment per branch, named `branch-<TagName>`. Merge with `--no-ff` so
  the branch stays visible in the graph, then tag with a lightweight tag.
- **`git push` does not carry tags.** Push the branch and the tag separately.
  Push the branch too — the dashboard looks for it.
- Do not commit generated files. The jar goes out as a GitHub release
  attachment, never as a commit.
