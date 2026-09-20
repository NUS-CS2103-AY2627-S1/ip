# Persist version-control instructions in `AGENTS.md`

## Decision

The repository instructions should explicitly require a suggested commit
message for every file-changing task, keep changes uncommitted by default, and
prohibit pushes. `AGENTS.md` is the appropriate location because it applies to
future work in this repository.

## Change made

Updated the `Git` section of `AGENTS.md` on the `master` branch. No application
code or tests were changed, so the UI test suite was not rerun.

## Suggested commit message

```text
docs: clarify repository version-control workflow
```
