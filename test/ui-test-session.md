# UI Test Session

The console test cases were reviewed against the current KDB command
responses and personality phrases.

## Results

| Test | Result |
| --- | --- |
| Exits on `bye` | PASS |
| Saves and deletes a task | PASS |
| Finds matching tasks | PASS |
| Rejects invalid commands | PASS |
| Handles invalid task data | PASS |

The GUI should be checked manually with `./gradlew run`, including command
entry, error highlighting, ASCII-art alignment, scrolling, and window
resizing.
