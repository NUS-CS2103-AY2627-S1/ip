---
name: test-ui
description: Run Lynn's documented command-line UI test cases after behavior changes, checking expected output and stopping at the first failure.
---

# Test UI

Run this project-specific skill whenever a code update can affect Lynn's command-line behavior or persisted data.

1. Update `test/ui-test-plan.md` and `test/ui-test-cases.tsv` when user-visible behavior or an edge case changes.
2. Run `scripts/run-ui-tests.sh` from this skill directory.
3. Report the printed console input/output. If a case fails, stop and show its expected and actual output before making further changes.

The script builds the runnable JAR and executes every case in a temporary working directory, so it does not alter the repository's `data/lynn.txt` file. The TSV manifest is the executable form of the test cases documented in the Markdown plan.
