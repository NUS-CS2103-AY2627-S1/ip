# Manual GUI test matrix

Use this matrix for behavior that is impractical to verify with headless JUnit tests.

| Area | Variants | Expected result |
| --- | --- | --- |
| Window sizing | Minimum size, 800×700, maximized | Composer remains visible and messages wrap without horizontal scrolling. |
| Long output | Add 20 tasks, then run `list` | The newest response is visible and the conversation scrolls vertically. |
| Error styling | Run `todo`, `mark x`, and an unknown command | Each reply uses the red error bubble and `!` badge. |
| Input controls | Submit using Enter and the button | Each method sends exactly one command and clears the field. |
| Exit state | Run `bye` | Input and button become disabled after the farewell. |
| Display scaling | 100%, 125%, and 200% OS scaling | Text remains readable with no clipped controls. |
| Locale | English and Chinese OS language | ISO dates parse consistently and displayed month names remain English. |
| Platforms | macOS, Windows, and Linux | The window opens with equivalent layout and command behavior. |
