# GUI polish verification

Verified on macOS with Java `25.0.3.fx-zulu` on 2026-09-13.

## Automated checks

`./gradlew desktopTest` runs the opt-in JavaFX acceptance tests with `FRIDAY_GUI_TESTS=true`.
It requires a graphical desktop session. The full suite can still be run with
`FRIDAY_GUI_TESTS=true ./gradlew test` when needed.
This includes 46 source-of-truth console cases and two opt-in JavaFX desktop acceptance tests.
Normal `./gradlew test` runs the business/console suite and skips the desktop tests unless
`FRIDAY_GUI_TESTS=true` is set. The desktop tests require a graphical display.

New business tests prioritize validation, recovery, storage integrity, and response compatibility:
invalid commands leave state unchanged; the next response resets severity; empty search results stay neutral;
message words do not determine severity; structured responses execute mutations once; corrupt task/contact files
remain protected; and failed saves retain state and retry successfully.
Existing parser, task, contact, date-validation, and storage tests remain the focus of the project's risk-based
method selection. No assertion of line or branch coverage is made.

The `test-ui` workflow also ran all 46 console cases sequentially with exact output comparison and a transcript,
stopping on any mismatch. The legacy Python skill runner cannot interpret this project's restart/storage directives
and still assumes the old default-package entry point. A temporary adapter invoked the existing fixture-aware
`ConsoleUiRegressionTest` runner instead, preserving inputs and expectations from `test/ui-test-plan.md`.
The transcript is available locally at `build/console-ui-transcript.txt`. Console expectations did not change.

## Desktop and visual checks

The actual production FXML/controller was loaded against temporary data. JavaFX rendered screenshots in
`build/gui-review`; selected screenshots are retained in `docs/images`.

- Initial outer window: 480 × 640. Minimum: 360 × 440. Enlarged check: 800 × 760.
- Both Enter action and Send button submit; blank commands add no rows; focus returns to the composer.
- User bubbles fit short commands; assistant panels use the available width.
- Long descriptions without spaces and multi-line help wrap without horizontal overflow or clipped label height.
- The composer remains visible after resizing. Native window resize events are awaited before assertions.
- Manual scrolling remains available; the next submission scrolls to the latest reply.
- Red command-error panels and amber startup/save-warning panels have explicit text labels.
- `bye` disables both submission controls and displays the session-ended hint.
- The generated user avatar and FRIDAY core have PNG alpha transparency and remain recognizable at 36 px.

Reviewed the default, minimum, enlarged, warning, and ended-session JavaFX snapshots.
Screenshots show scene contents; macOS title-bar height is excluded.

## Limits

Desktop rendering was verified on macOS only. Windows/Linux appearance was not visually tested.
The generated user artwork retains a transparent background within the rounded crop, rather than being
regenerated or background-removed. Its original illustrator was not supplied; provenance is documented in
`docs/images/artwork.md`.

No commit, merge, tag, or push has been performed.
