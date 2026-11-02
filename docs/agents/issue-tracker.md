# Issue tracker: GitHub

Issues and specifications for this repository live as GitHub issues in
`ewonglh/ip`. Use the `gh` CLI for all operations.

## Conventions

- Create: `gh issue create --title "..." --body "..."`
- Read: `gh issue view <number> --comments`
- List: `gh issue list --state open`
- Comment: `gh issue comment <number> --body "..."`
- Add or remove labels: `gh issue edit <number> --add-label "..."` or
  `--remove-label "..."`
- Close: `gh issue close <number> --comment "..."`

Infer the repository from `git remote -v`; `gh` does this automatically when
run inside the clone.

## Pull requests as a triage surface

**PRs as a request surface: no.**

GitHub uses one number space for issues and pull requests. If `#42` is
ambiguous, try `gh pr view 42` and then `gh issue view 42`.

## Skill terminology

When a skill says “publish to the issue tracker,” create a GitHub issue.

When a skill says “fetch the relevant ticket,” run:

`gh issue view <number> --comments`

## Wayfinding

- Maps use the `wayfinder:map` label.
- Child tickets use `wayfinder:<type>`, such as `research`, `prototype`,
  `grilling`, or `task`.
- Use GitHub sub-issues and native issue dependencies when available.
- Otherwise, use task lists and `Blocked by: #<number>` lines.
- Claim work with `gh issue edit <number> --add-assignee @me`.
- Resolve work by commenting with the result and closing the issue.
