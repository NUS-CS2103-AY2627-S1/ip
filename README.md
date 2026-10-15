# Pip - your chipmunk task buddy

Small paws. Big plans. Pip helps you gather tasks, find them in your stash,
and chip away at your day with cheerful woodland encouragement.

## Run

Use Java 25. Run `./gradlew run` (Windows: `./gradlew.bat run`), or launch
`pip.gui.Launcher` in your IDE. Tasks are saved to `data/Pip.txt`.
If upgrading an older installation, back up its task file and rename it to
`Pip.txt` inside the `data` folder before starting Pip.

## Commands

| Command | Example | Woodland alias |
| --- | --- | --- |
| Add a todo | `todo read a book` | `gather read a book` |
| List tasks | `list` | `stash` |
| Search tasks | `find book` | `sniff book` |
| Mark complete | `mark 1` | Existing short alias: `m 1` |
| Mark incomplete | `unmark 1` | Existing short alias: `um 1` |
| Delete | `delete 1` | Existing short alias: `d 1` |
| Add a deadline | `deadline return book /by 2026-10-01` | `dl` |
| Add an event | `event picnic /from 2026-10-01 /to 2026-10-02` | `e` |
| Farewell / exit console | `bye` | `scamper` |

Original commands and short aliases still work. Dates use `yyyy-MM-dd`.
In the GUI, close the window to exit; the farewell command displays Pip's goodbye.

## Design

Pip uses forest-green controls, acorn-colored user bubbles, a warm cream background,
and compact circular profile pictures. Error panels keep clear command guidance.
The responsive layout and readable Segoe UI font keep longer replies easy to follow.

## Checks

Run `./gradlew check javadoc` with Java 25. See `test/ui-test-plan.md` for
console transcripts and manual GUI checks.

See the [Pip User Guide](docs/README.md) for examples, error recovery, and storage details.
