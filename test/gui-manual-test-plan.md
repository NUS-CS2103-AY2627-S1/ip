# Herta GUI and Platform Manual Test Plan

This matrix covers behavior that is intentionally outside deterministic JUnit
coverage. Run it with a clean disposable data directory and record the result,
OS, Java version, display scale, locale, and any relevant screenshot or
transcript beside each case.

## Manual execution record — 2026-09-14

| Environment | Result |
| --- | --- |
| Agent workspace, Windows, Java 25.0.4, no interactive JavaFX/display session | Not executed here; requires a user-controlled GUI and OS matrix. |

## Test matrix

| Area | Manual cases | Expected evidence | Result |
| --- | --- | --- | --- |
| Startup/resources | Launch from Gradle and the packaged application. Check title, icon, FXML, CSS, avatar, welcome dialog, and the graceful startup-error screen from an intentionally incomplete temporary distribution. | Startup screenshot and error screenshot. | Pending interactive run |
| Command interaction | Add todo/deadline/event; list/find/filter/upcoming/sort; mark/unmark/delete; archive/archived/restore; invalid input. Check message text, category colors, formatting, and restart persistence. | Command transcript and screenshots of representative responses. | Pending interactive run |
| Input history | Test Up/Down navigation, draft restoration, empty history, adjacent duplicate suppression, 200-entry eviction, and modifier-key behavior. | Short transcript noting each key sequence. | Pending interactive run |
| Zoom and layout | Test Ctrl+wheel, Ctrl+`+`, Ctrl+`-`, Ctrl+`0`, lower/upper bounds, normal scrolling without Ctrl, text/avatar sizing, wrapping, and resizing. | Screenshots at minimum, default, and maximum zoom. | Pending interactive run |
| Dialog history | Submit more than 500 dialogs and verify only the newest 500 remain without broken scrolling or layout. | Screenshot of the retained tail and a count note. | Pending interactive run |
| Clipboard | Right-click a user and a Herta dialog, copy multiline text, and paste into a text editor. Record behavior when OS clipboard access is denied. | Pasted text sample and permission-error note, if applicable. | Pending interactive run |
| Exit | Enter `bye`; verify input/send controls disable, goodbye appears, and the process exits after the intended delay. | Exit transcript and timing note. | Pending interactive run |
| Persistence and recovery | Restart after active/archive changes; edit files between sessions; test invalid UTF-8, malformed records, directory paths, lock contention, and transaction-recovery journals in a disposable directory. | Before/after file bytes, startup screenshots, and recovery transcript. | Pending interactive run |
| Locale and Unicode | Run under English and Chinese OS/user locales. Verify English date display remains stable, Chinese and other Unicode descriptions persist and reload as UTF-8, and input/output are not garbled. | Locale, file-encoding, and display screenshots. | Pending interactive run |
| Display/OS matrix | Where available, test Windows, macOS, and Linux at 100%, 125%, 150%, and 200% scaling; minimum window size; a narrow 1280×720 display; and a high-resolution display. | Matrix of screenshots with OS, scale, and resolution. | Pending interactive run |

## Manual-only exclusions

These behaviors are deliberately not forced into unit tests because their
results depend on JavaFX, the operating system, display hardware, or failure
injection that has no safe deterministic seam:

- JavaFX FXML loading, CSS application, dialog rendering, animation timing,
  keyboard/mouse event wiring, resize behavior, zoom rendering, and dialog
  trimming.
- System clipboard availability and failure behavior.
- `Launcher` and `Main` JavaFX process startup.
- Atomic-move fallback behavior, permission errors, disk-full errors, antivirus
  interference, symlink/filesystem-provider differences, and true
  cross-process locking.
- Defensive private runtime-error and rollback-failure branches without a safe
  deterministic test seam.

Each exclusion is represented by one or more cases in the matrix above or is
explicitly marked environment-dependent in the execution record.
