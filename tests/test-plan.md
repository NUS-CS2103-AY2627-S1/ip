# Manual test plan

This file did not exist before the `archive` command was added, so it
currently only covers that feature. It is not (yet) a full manual test
plan for the whole app.

## `archive`

Archives every task currently in the list: appends each one to
`data/archive.txt` (relative to wherever Echo is run from) and clears
the active list. Archiving is cumulative — running `archive` again
later appends to the same archive file rather than overwriting it.

| # | Steps | Expected result |
|---|-------|------------------|
| 1 | Add a few tasks (`todo`, `deadline`, `event`), then run `archive`. | Echo replies `Archived <n> task(s). Your list is now empty.` where `<n>` matches the number of tasks added. |
| 2 | Run `list` right after test 1. | Echo replies `Here are the tasks in your list:` with no tasks listed. |
| 3 | Inspect `data/echo.txt`. | The file is empty (or contains no task lines). |
| 4 | Inspect `data/archive.txt`. | Contains one save-file-format line per task archived in test 1, in the order they were added. |
| 5 | With an empty task list, run `archive`. | Echo replies `There's nothing to archive - your list is already empty.` `data/archive.txt` is unchanged. |
| 6 | Add a new task, run `archive`, then inspect `data/archive.txt` again. | The file now contains the lines from test 4 **plus** the new task's line appended after them — nothing from the earlier archive run is lost or overwritten. |
| 7 | Restart Echo (reload from `data/echo.txt`) after archiving. | The reloaded list is empty; archived tasks do not reappear (they only exist in `data/archive.txt`). |
