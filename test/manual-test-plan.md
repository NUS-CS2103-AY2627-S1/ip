# Manual Test Plan

These cases cover JavaFX behavior and environment-dependent behavior that is not suitable for unit tests.

## GUI interaction

- Resize the window down to its minimum size and confirm the input field remains visible with the help menu closed and open.
- Enter `help` twice and confirm the help panel opens and closes.
- Resize the window horizontally and confirm the help panel follows the input area width.
- Scroll over the conversation without clicking the scrollbar first.
- Enter an invalid command, invalid task details, invalid date, and invalid event range. Confirm each response has the warning icon and error colors.
- Confirm added and deleted task messages retain normal speech styling.
- Enter `bye`, `quit`, and `exit` separately. Confirm the window closes and the save file is updated.

## Environment coverage

- Run the application on Windows, macOS, and Linux.
- Run it at the minimum supported window size and at a large display resolution.
- Test with English and Chinese operating-system language settings.
- Start without a save file and confirm the application creates one after saving.
- Start with a malformed save file and confirm it reports the problem without crashing.
