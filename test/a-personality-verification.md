# A-Personality verification

Verified on macOS with Java **25.0.3.fx-zulu**, before the increment commit.

## Automated checks

- `FRIDAY_GUI_TESTS=true ./gradlew test check`: **379 tests passed**, no failures, errors, or skips.
- All **46 console cases** in `test/ui-test-plan.md` passed with updated explicit wording expectations.
- The `test-ui` sequential workflow also passed all 46 cases. A temporary Java adapter invoked the existing
  fixture-aware `ConsoleUiRegressionTest` runner, printed each aim/input/output, compared exact text with
  normalized line endings and one trailing newline, and would stop at the first mismatch.
  The legacy Python runner cannot interpret the plan's storage/restart directives, so it was not used.
  Transcript: `build/console-ui-transcript.txt` (generated, not committed).
- Existing severity, error recovery, storage protection/retry, persistence, Unicode, and string-wrapper
  compatibility tests passed. Updated parser/contact tests check the revised validation wording.
- Extended `FridayTest` verifies exact greeting/thanks/welcome wording and the full command reference.
  Existing high-value parsing, mutation, date, and storage coverage remains in place.
- `git diff --check`: clean.

## Desktop and visual review

The two opt-in desktop tests use the production FXML and isolated temporary data. They exercise Enter
and Send, blank input, restored focus, long unbroken commands, wrapped replies, manual history scrolling,
scrolling to new responses, invalid commands, storage warnings, and disabled input/Send after `bye`.

Actual JavaFX scene snapshots were visually inspected at default 480 x 640, minimum 360 x 440, and
expanded 800 x 760 window sizes. Scene images exclude the native title bar. The mission-control header
fits; help wraps without horizontal overflow; input remains visible; error and warning panels remain
clearly distinct. The window title is set to `FRIDAY | Mission Control` in the production entry point.

Updated guide screenshots:

- [Default conversation and command error](../docs/images/friday-default.png)
- [Minimum window with wrapped help](../docs/images/friday-minimum.png)
- [Expanded window with command briefing](../docs/images/friday-enlarged.png)
- [Storage warnings](../docs/images/friday-warning.png)

The exit-state snapshot remains available at `build/gui-review/ended.png`.
Native GUI checks were performed on macOS only. No command syntax, persistence format, or response API changed.

## Delivery status

Implementation commit `c3e6bd0` was pushed to `origin/codex/a-personality` after user review.
The merge into `master` was not performed because its tool approval was declined. The lightweight
`A-Personality` tag has not been created. Cross-platform CI results for this increment have not yet
been verified; the local test results above do not claim Windows or Linux execution.
