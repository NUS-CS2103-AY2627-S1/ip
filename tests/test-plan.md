# Archive Feature Test Plan

## Scope

This plan verifies individual and bulk archiving, viewing archived tasks,
storage compatibility, validation, and failure safety. Archived tasks use the
same timestamp-free format as active tasks.

## Automated JUnit coverage

| Area | Scenario | Expected result |
|---|---|---|
| Parser | Parse `archive <index>` | Returns an `ArchiveCommand` |
| Parser | Parse `archive all` | Returns an `ArchiveCommand` |
| Parser | Parse `list archived` | Returns a `ListArchivedCommand` |
| Parser | Parse unsupported list arguments | Reports the list-command usage |
| Task list | Clear a populated list | Removes every active task |
| Storage | Archive multiple batches | Appends records in archive order |
| Storage | Archive a description containing the field separator | Preserves the full description |
| Storage | Load a missing archive | Returns an empty list |
| Storage | Load a malformed archive | Reports invalid stored data |
| Command | Archive write fails | Leaves active tasks unchanged |
| Command | Active save fails after archiving | Leaves the in-memory active list unchanged |
| Integration | Archive one task | Archives the selected task and saves the remainder |
| Integration | Archive all tasks | Archives every task and saves an empty active list |
| Integration | Archive an empty list | Returns a no-op response without adding records |
| Integration | List archived tasks | Displays persisted archive entries |
| UI | Display archived and empty archives | Produces the documented response text |

Run all automated checks with Java 25:

```powershell
.\gradlew.bat check
```

## Manual acceptance test

Start Luna with these active tasks:

```text
1. [T][X] ok
2. [T][ ] yy
3. [T][ ] cs2100
```

1. Enter `archive 2`.
   Expected: Luna reports that one task was archived and two remain.
2. Enter `list`.
   Expected: `ok` and `cs2100` appear as tasks 1 and 2; `yy` is absent.
3. Enter `list archived`.
   Expected: `yy` appears as the first archived task.
4. Restart Luna and enter `list archived`.
   Expected: `yy` remains available.
5. Enter `archive all`, followed by `list`.
   Expected: the active list has no tasks.
6. Enter `list archived`.
   Expected: `yy`, `ok`, and `cs2100` appear in that order.
7. Enter `archive all` again.
   Expected: Luna reports `There are no tasks to archive.` and does not
   append another archive entry.
8. Enter `archive 0`, `archive 99`, and `archive book`.
   Expected: each command reports the documented validation error and neither
   data file changes.

## Regression checks

- Existing `todo`, `deadline`, `event`, `mark`, `unmark`, `find`,
  `list`, `delete`, and `bye` behavior remains unchanged.
- Existing `data/luna.txt` files load without migration.
- `delete <index>` remains permanent and does not write to the archive.
- `find <keyword>` searches active tasks only.
