# Audit architecture, tests, and decision explanations

## Decision

The project should adopt the useful, maintainable patterns visible in the
peer pull requests without adding code merely to increase the line count. The
audit therefore prioritizes testable core behavior, clear documentation, and
FXML where the layout is stable.

## Changes made

1. Added `Storage(Path)` while preserving the default production path. Tests
   can now use temporary files instead of sharing `data/duchess.txt`, which
   makes persistence tests isolated and repeatable.
2. Added tests for `Task`, `Deadline`, `Event`, statistics, persistence, legacy
   records, malformed records, and the command-response boundary. These target
   state transitions and edge cases rather than only counting test methods.
3. Used streams for task-list formatting where the operation is a direct
   transformation and joining operation. The command dispatcher remains
   centralized because it is still a small, readable boundary shared by the
   CLI and JavaFX interfaces; splitting every command into a separate class
   would add indirection without a current requirement.
4. Kept the reusable JavaFX controls in FXML components. `MainWindow.fxml`
   composes the header, quick-command buttons, conversation view, and composer.
   Dynamic dialogs remain JavaFX code because their contents depend on the
   current task or command response.
5. Replaced stale user-guide placeholders and updated the `Task` design note to
   describe the current `ArrayList`-backed task list and file persistence.

## Existing decisions confirmed

- `Task` owns task state and task-specific display behavior; `TaskList` owns
  ordering and collection operations.
- `Parser` owns command interpretation, while `Duchess` coordinates state
  changes and produces responses for both user interfaces.
- JavaFX/FXML is used for the stable view structure, with Java code handling
  behavior and dynamic content.
- JUnit coverage prioritizes the highest-value domain, persistence, and
  application-boundary methods, with the repository target of roughly the top
  50% by value.

## Verification scope

The user-visible command behavior is intentionally unchanged by this audit,
so `test/ui-test-plan.md` does not require a new scenario. The JavaFX smoke
test and the complete console UI plan remain part of the verification pass.

## Suggested commit message

```text
Expand tests and document architecture decisions
```
