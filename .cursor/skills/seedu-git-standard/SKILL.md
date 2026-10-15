---
name: seedu-git-standard
description: >-
  Applies the SE-EDU Git conventions from se-education.org. Use when creating
  or proposing commits, writing commit messages, naming branches, or tagging
  in this project.
---

# SE-EDU Git conventions

Follow the [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
for **every commit** in this project. Do not skip this skill when proposing or
creating a commit, writing a commit message, or naming a branch.

Full examples: [reference.md](reference.md).

Still **do not commit or push unless the user explicitly asks**. When they do,
this skill governs the message (and branch name, if you create one).

## Workflow

1. Read this skill before writing a commit message or branch name.
2. Draft the subject (and a body for non-trivial commits) using the rules below.
3. Recheck the checklist, then create the commit with a HEREDOC so wrapping is preserved.

## Checklist

- [ ] Subject is imperative, capitalized, no trailing period
- [ ] Subject ≤ 50 characters when possible (hard limit 72)
- [ ] Optional `Scope:` / `file:` prefix only when it helps
- [ ] Non-trivial commits have a body separated by a blank line
- [ ] Body wrapped at 72 characters; paragraphs separated by blank lines
- [ ] Body explains WHAT and WHY (not HOW); present tense for the situation, imperative for the change
- [ ] Branch names are kebab-case keywords (`issueNumber-keywords` when tied to an issue)

---

## Commit subject

Every commit must have a well-written subject line.

| Rule | Good | Bad |
| --- | --- | --- |
| Imperative mood | `Add README.md` | `Added README.md`, `Adding README.md` |
| Capitalize the first letter | `Move index.html file to root` | `move index.html file to root` |
| No trailing period | `Update sample data` | `Update sample data.` |
| Prefer ≤ 50 chars (hard limit 72) | — | — |

You may prefix with a scope or file when it helps, then a colon:

- `Person class: Remove static imports`
- `Main.java: Remove blank lines`
- `bug fix: Add space after name`
- `chore: Update release date`

Conventional Commits is allowed but not required.

## Commit body

Non-trivial commits **must** have a body. Separate it from the subject with a blank line. Wrap at **72** characters. Use blank lines between paragraphs. Use bullets when they are clearer than prose.

Explain **WHAT** and **WHY**, not **HOW** (the diff shows how). Give enough detail that a reader can judge the change without reading the diff. If the body gets too long, split the commit.

Do not restate information already in code comments of the same commit.

Body structure:

```
{current situation}          -- present tense; avoid "currently" / "originally"

{why it needs to change}

{what is being done about it} -- imperative mood; "Let's" may start this part

{why it is done that way}

{any other relevant info}
```

When creating the commit, pass the message via HEREDOC:

```bash
git commit -m "$(cat <<'EOF'
Subject line in imperative mood

Body wrapped at 72 characters, explaining what and why.

EOF
)"
```

## Branch names

- Meaningful keywords in **kebab-case**: `refactor-ui-tests`
- If the branch tracks an issue: `issueNumber-keywords-from-issue-title`, e.g. `1234-ui-freeze-error`

## Tags

Use lightweight tags unless the user requests an annotated tag.
