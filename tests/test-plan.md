# C-Sort Manual Test Plan

## Sort tasks alphabetically

1. Start tt.TT with an empty save file.
2. Enter `todo write report`.
3. Enter `todo Buy milk`.
4. Enter `todo call Alice`.
5. Enter `sort`.

Expected: The displayed order is `Buy milk`, `call Alice`, then `write report`.

Restart tt.TT and enter `list`.

Expected: The tasks remain in the same alphabetical order.

## Reject additional arguments

Enter `sort deadline`.

Expected: tt.TT reports that the `sort` command does not take additional arguments and does not reorder the tasks.
