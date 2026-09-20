# Sort Feature Test Plan

## Automated test

`NexusTest#getResponse_sort_ordersTasksAlphabeticallyAndPersistsOrder` checks
that `sort` orders descriptions without regard to letter case and that a new
Nexus session loads the same order from storage.

## Manual test

1. Start Nexus with an empty task list.
2. Enter `todo write report`.
3. Enter `todo Buy milk`.
4. Enter `todo attend meeting`.
5. Enter `sort`.
6. Confirm that Nexus displays `attend meeting`, `Buy milk`, and `write report`
   in that order.
7. Exit and restart Nexus, then enter `list`.
8. Confirm that the sorted order is retained.
