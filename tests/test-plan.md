# C-Sort manual acceptance checks

Use a temporary working directory to avoid changing your real tasks.

1. On an empty list, run `sort name` and `sort status`: both show the empty-list message.
2. Add `todo zebra`, `todo apple`, `todo Apple`; run `mark 3`.
3. Run `sort name`: expect apple (unfinished), Apple (completed), zebra (unfinished).
4. Run `sort status`: expect apple, zebra, Apple, preserving order within each status.
5. Restart: `list` must retain that order and completion states.
6. Run `mark 2`: zebra must be marked, using its new index.
7. Add `todo aardvark`: it must appear last, without automatic sorting.
8. Try `sort`, `sort date`, `sort NAME`, and `sort name extra`: expect
   `⚠️ No idea 🧇 Try "sort name" or "sort status".` with no change to the list or saved order.
9. Repeat sorting on a list with todos, deadlines, and events; details and task counts stay intact.
10. Repeat the command checks in both the terminal interface and JavaFX interface.
