# Manual Test Plan

Use this plan for JavaFX behavior that is difficult to verify reliably with unit tests. Run the packaged application
with Java 25 in every environment being checked. Use a fresh data file before each test session.

## Core GUI checks

| ID | Action | Expected result |
| --- | --- | --- |
| GUI-01 | Launch the application. | The window opens with the Tianyi title, aligned ASCII banner, welcome message, and input field visible. |
| GUI-02 | Enter a valid command such as `todo read book`. | User and Tianyi dialog cards appear in order, with distinct avatars and no clipped text. |
| GUI-03 | Enter an invalid command such as `todo`. | The reply uses the error styling and shows the complete corrective message. |
| GUI-04 | Submit an empty or whitespace-only input. | No dialog is added and the application remains responsive. |
| GUI-05 | Add enough tasks to make the conversation taller than the window. | The newest short reply is visible and the conversation can be scrolled normally. |
| GUI-06 | Run `help` after adding enough tasks to enable scrolling. | The long reply starts at a readable position and remains fully reachable by scrolling. |
| GUI-07 | Scroll upward, then add another task. | The jump-to-latest control appears and moves the view to the newest reply when selected. |
| GUI-08 | Resize the window down to its minimum size and then enlarge it. | Controls remain usable, text wraps without overlap, and dialog cards resize with the window. |
| GUI-09 | Enter `bye`. | The farewell appears, input is disabled, and the application closes after the short delay. |
| GUI-10 | Close and reopen the application after adding and marking tasks. | Saved tasks, order, types, dates, and completion states are restored. |
| GUI-11 | Put an invalid record such as `X | 0 | invalid` in `Data/tianyi.txt`, launch the application, and enter `todo replacement task`. | An error reply asks the user to enter `[bye]`; the invalid file is unchanged until `bye` is entered, then it is cleared and Tianyi exits normally. |

## Environment matrix

Repeat GUI-01, GUI-02, GUI-06, GUI-08, and GUI-10 in each available environment:

- macOS, Windows, and Linux.
- A small display at approximately 1280 x 720 and a high-resolution or scaled display.
- 100%, 125%, 150%, and 200% display scaling where the operating system supports them.
- English and Simplified Chinese operating-system display languages and regional formats.
- Light and dark operating-system appearance settings.
- Keyboard-only use: tab to the input and controls, enter commands, scroll, and activate the jump button.

The application deliberately formats task dates in English. Changing the operating-system language or region must
not change stored dates, accepted command formats, or displayed English date names.
