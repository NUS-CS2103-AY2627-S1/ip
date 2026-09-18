# Codex prompt framework

Use the following bootstrap prompt once at the beginning of a project chat.
It gives Codex the context, constraints, and completion criteria it needs to
work consistently.

```text
You are working in my Java school project repository.

Before doing anything:

1. Read AGENTS.md and follow it as the repository contract.
2. Inspect the relevant files, tests, documentation, build files, and Git
   status. Do not assume that a file or tool exists.
3. Treat these references as the project's style hierarchy:
   - https://se-education.org/guides/conventions/java/index.html
   - https://se-education.org/guides/conventions/java/intermediate.html
   - https://se-education.org/guides/conventions/markdown.html
   - https://se-education.org/guides/conventions/git.html
   - https://developers.google.com/style
4. Prefer the simplest design that satisfies the requirements and preserves
   existing behavior unless I explicitly request a behavior change.
5. Explain important assumptions and design choices briefly at my beginner
   Java level.

When you change files:

1. Make the smallest coherent change.
2. Add or update tests when behavior changes.
3. Use Java 25 for Java and Gradle commands.
4. Run the relevant Gradle validations and the documented command-line UI
   tests when application code changes.
5. Report the files changed, validations run, and any remaining limitations.
6. Do not commit or push unless I explicitly ask you to do so.

Do not silently change requirements, expected test output, or unrelated files.
If a requirement conflicts with the repository instructions, identify the
conflict before proceeding.
```

For each individual task, use this template and replace the bracketed text:

```text
Task: [one clear outcome]

Context:
- [relevant feature or current behavior]
- [relevant issue, requirement, or user story]

Acceptance criteria:
- [observable behavior or file-level result]
- [tests or validations that must pass]
- [documentation or UI-test-plan updates, if applicable]

Constraints:
- Follow AGENTS.md and the project's style references.
- Preserve unrelated behavior and changes.
- Do not commit or push.

Workflow:
1. Inspect the relevant files and Git status.
2. Propose a short implementation approach and call out assumptions.
3. Implement the change.
4. Run the relevant tests and validations.
5. Summarize the result, changed files, and any follow-up needed.
```

For larger changes, split the work into small prompts. For example:

```text
First, inspect the repository and produce a short implementation plan for
[feature]. Do not edit files yet. Identify the affected classes, tests,
documentation, and risks, and tell me what you need to confirm.
```

```text
Implement the approved plan for [feature]. Keep the public behavior unchanged
outside the requested feature. Add focused tests, update the UI test plan if
the CLI changes, run Gradle validation and the UI tests, and report exact
results. Do not commit.
```

```text
Review the current diff for [feature] against AGENTS.md and the project's
convention references. Look for correctness, regressions, missing tests,
unclear documentation, and unnecessary complexity. Do not edit files; report
findings by priority with file paths and line numbers.
```

## Useful follow-up prompts

* “The test failed. Diagnose the first mismatch using the actual output and
  expected output; do not change expected output without explaining why.”
* “Explain this implementation at a beginner Java level, then suggest the
  smallest safe improvement. Do not implement it yet.”
* “Run the formatter check and show me the first violation. Apply formatting
  only after I approve changes that are not mechanical.”
* “Prepare a Git commit message following the repository’s Git convention, but
  do not create the commit.”

The prompt is a supplement to `AGENTS.md`; it does not replace the repository
instructions.
