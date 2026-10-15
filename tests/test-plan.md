# Test plan

## C-Sort

### Automated tests

1. `sort` orders all tasks alphabetically by description.
2. `sort deadline` displays only deadline tasks in ascending date order.
3. `sort event` displays only event tasks in ascending start-date order.
4. `sort todo` displays only todo tasks alphabetically.
5. Tasks with equal sort values are ordered alphabetically by description.
6. Non-selected task types remain in the task list after a filtered sort.
7. `mark`, `unmark`, and `delete` use the numbering from the filtered sorted result.
8. Sorting an empty or unmatched result does not crash.
9. Invalid sort commands do not change the task list.
10. Events reject dates that are not in `yyyy-mm-dd` format.

### Manual tests

1. Add todos, deadlines, and events through the GUI.
2. Run each sort command and verify that only the selected type is displayed.
3. Run `mark 1`, `unmark 1`, and `delete 1` after a filtered sort and verify that the displayed task is changed.
4. Restart the application and verify that the sorted task order is retained.
