# Update AI guidance for the JUnit coverage target

## Decision

The repository AI instructions should preserve the agreed test strategy:
JUnit tests should cover roughly the top 50% highest-value methods, with
priority given to complex, core, and critical business logic.

The instructions now also require reviewing and updating relevant JUnit tests
after every application-code change. This makes the coverage target an ongoing
requirement rather than a one-time test addition.

## Change made

Updated the JUnit guidance in `AGENTS.md`. No production code or test files were
modified, so no test run was needed.

## Suggested commit message

```text
Document JUnit coverage target
```

