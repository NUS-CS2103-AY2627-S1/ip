# UI Test Plan

Each case runs in a fresh Trackie process. Input and expected-output paths are relative to this `test` directory.

| Aim | Input | Expected output |
| --- | --- | --- |
| Verify the complete Level 4 workflow | ui/level4-input.txt | ui/level4-expected.txt |
| Verify minimal Level 5 errors preserve task state | ui/level5-minimal-input.txt | ui/level5-minimal-expected.txt |
| Verify malformed commands, reserved characters, duplicate parameters, and leading spaces | ui/level5-errors-input.txt | ui/level5-errors-expected.txt |
| Verify deletion and automatic task renumbering | ui/level6-delete-input.txt | ui/level6-delete-expected.txt |
| Verify saved tasks load after restarting Trackie | ui/level7-load-input.txt | ui/level7-load-expected.txt |
| Verify corrupted saved data is handled and recoverable | ui/level7-corrupt-input.txt | ui/level7-corrupt-expected.txt |
| Verify Level 8 date parsing, formatting, range validation, calendar-date validation, and persistence | ui/level8-dates-input.txt | ui/level8-dates-expected.txt |
| Verify case-insensitive search, empty search results, and missing-keyword handling | ui/level9-find-input.txt | ui/level9-find-expected.txt |
| Verify within-period validation, task operations, and persistence | ui/within-period-input.txt | ui/within-period-expected.txt |

## Manual JavaFX checks

Run `./gradlew run` and verify the following Level 10 behavior:

- The Trackie window opens with Trackie's welcome message.
- Trackie's welcome and task responses use its encouraging fox-coach personality.
- Chat bubbles, the command field, and the separate Send button have rounded styling and visible shadows.
- Pressing Enter or clicking **Send** displays both the command and Trackie's response.
- Adding, listing, marking, unmarking, deleting, and finding tasks gives the same responses as the text UI.
- Invalid commands display a helpful error without closing the window.
- Error responses use a red-tinted bubble that is visually distinct from normal replies.
- Entering `bye` displays the farewell before the window closes.
