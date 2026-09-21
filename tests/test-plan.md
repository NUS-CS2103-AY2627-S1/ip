# Caitlyn test plan

The executable CLI cases are maintained in [test/ui-test-plan.md](../test/ui-test-plan.md), the path used by the project's `test-ui` skill. This index avoids keeping two copies of the same cases.

With Java 25 (`sdk use java 25.0.3.fx-zulu`), run:

```bash
./gradlew test checkstyleMain checkstyleTest
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

JUnit runs in `build/test-workspace` so filesystem tests cannot overwrite the user's saved tasks. The CLI runner uses a fresh temporary directory per case and checks required output substrings in order, plus saved file contents when specified. Exact response and rejected-command state assertions are covered by JUnit.

Within-period acceptance coverage:

- `WithinTaskTest`: supported dates, all boundary-precision combinations, whole-day ends, equal endpoints, calendar edges, invalid dates, seconds/fractions, display, and escaping.
- `WithinCommandTest`: exact confirmations/errors, structural whitespace, reserved markers, error precedence, state/file preservation, duplicates, search, status, and deletion.
- `TaskStorageTest`: within-period round trips, invalid records, and legacy compatibility.
- `TaskSessionTest`: protection across every task-changing command, read-only behavior, missing/unreadable files, repair/restart, save rollback, and retry.
- Existing parser, task-type, and UI tests cover integration and the exact loading message.

The manual JavaFX smoke procedure is maintained with the CLI plan. It covers Enter/Send, output, task counts, the command hint, and load-failure protection using temporary saved data.
