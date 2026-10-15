---
name: seedu-git-standard
description: The SE-EDU Git commit message and branch naming conventions for this project. Use whenever writing or proposing a commit message, or naming a new branch.
---

# SE-EDU Git Conventions

Source: https://se-education.org/guides/conventions/git.html

Apply this to every commit made in this project, and to every new branch name.

## Commit message: subject line

- Imperative mood — "Add README.md", not "Added README.md" or "Adding README.md".
- Capitalize the first letter — "Move index.html file to root", not "move index.html file to root".
- No trailing period — "Update sample data", not "Update sample data.".
- Aim for ≤50 characters; hard limit 72.
- Optional scope/category prefix is fine (e.g. `Storage:`, `bug fix:`, `chore:`) if it aids scanning.

## Commit message: body

- Blank line between subject and body.
- Wrap body text at 72 characters.
- Blank lines between paragraphs; bullet points where they aid clarity over prose.
- Explain **what** changed and **why** — not **how** (the diff already shows how). Give enough detail that a reader can judge the change's merit without reading the code.
- Don't repeat what's already said in code comments.
- If the explanation is getting long, consider whether the change should be split into multiple commits instead.
- Natural structure to aim for: current situation (present tense) → rationale for changing it → what this commit does (imperative mood) → why done this way → other relevant info.
- Avoid "currently"/"originally" when describing prior behavior — state it plainly.

## Branch names

- Kebab-case, with meaningful keywords — `refactor-ui-tests`, not `refactorUiTests` or `fix1`.
- For issue-linked branches: `issueNumber-some-keywords-from-issue-title`, e.g. `1234-ui-freeze-error`.

## Verification

The 50/72/72 character limits are easy to violate by a character or two when eyeballing prose — don't rely on visual estimation. Before finalizing a commit message, check every subject and body line length mechanically, e.g.:

```bash
git log -1 --format="%s" | awk '{print length, $0}'
git log -1 --format="%b" | awk '{print length": "$0}'
```

or check the drafted message text directly before committing. If any body line exceeds 72 characters or the subject exceeds 72 (ideally 50), rewrap it before committing — don't commit first and fix later, since rewriting an already-pushed commit message requires a force-push.
