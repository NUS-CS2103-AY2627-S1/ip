# UI test plan

Run `./test/ui-test.sh` from the project root.

The test checks that Kopi can:

1. Add and display todos, deadlines, and events.
2. Mark and unmark a task.
3. Delete a task and renumber the remaining list.
4. Report an unknown command without corrupting the task list.
5. Save tasks and load them again in a later session.
6. Parse valid dates and reject dates in an unsupported format.
7. Find tasks by a case-insensitive description keyword.
8. Reject duplicate tasks without changing the task list.
