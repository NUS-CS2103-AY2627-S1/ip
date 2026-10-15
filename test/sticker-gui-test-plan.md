# Sticker GUI test plan

Run with Java 25. Use temporary task storage for the test session.

| Aim | Input / setup | Expected result |
| --- | --- | --- |
| Standalone size | `random_sticker` | One sticker in a 160 × 160 fit area; no response text; no profile picture. |
| Error before sticker | `unknown` | Nankore in a 120 × 120 fit area below the unchanged error text. |
| Successful additions | `todo read`, valid `deadline`, valid `event` | Yatta below each existing confirmation. |
| Completion and reopening | `mark 1`, `unmark 1` | Naisu, then Otsu, below the existing confirmations. |
| Successful deletion/restoration | `delete 1`, `undo` | Naisu below each existing confirmation. |
| Queries | `list`, `find read`, matching `list_by_date` | Otsu when results exist; Gomen when empty. |
| Invalid input | `mark 99`, `random_sticker extra` | Nankore below the existing error, overriding success mapping. |
| Unavailable action | Duplicate task, empty undo history, failed storage write | Gomen below the existing response. |
| Text-only responses | Start and `bye` | Greeting and goodbye have no sticker; exit behavior remains unchanged. |
| Layout | Long task description and repeated replies; resize window | Text wraps above the sticker, with no overlap; conversation scrolls to the latest reply. |
| Composer alignment | Resize from 500 × 600 to 417 × 220; submit `todo read` using Send and another task using Enter | Button and input have equal top/bottom bounds, an 8 px gap, and remain fully inside the window; each submission adds exactly one task and displays Yatta plus confirmation. |
| Image integrity | Render all five stickers in both display modes | Every PNG loads, preserves its aspect ratio, and uses the correct fit dimensions. |
| Personality | Start, `todo read`, `mark 1`, `unknown`, `bye` | Window title and greeting name James the ぱんどろぼう; bread-themed English replies include Yatta, Gomen, and Mata ne while preserving task details and actionable errors. |
| Message gap | Consecutive text-only messages | 14 px between bubbles: 6 px bottom padding + 2 px container spacing + 6 px top padding. |
| Reply spacing | `todo read`, `list`, `mark 1`, `delete 1`, `list_by_date 2026-09-16`, `unknown` | No extra trailing text line in any reply; internal message line breaks remain. |
| Startup warning | Start with malformed storage | Warning appears after the greeting, identifies invalid line numbers, and explains recovery. |
| Bubble styling | Greeting, user input, and contextual reply | No profile pictures; James’s bubbles are warm ivory with a tan border, user bubbles are pale toast with a brown border, and errors are pale peach with a muted red border; James’s text bubbles have a square top-left corner and three rounded corners. |

All contextual stickers use a 120 × 120 fit area inside a compact ivory bubble; their text remains visible in a separate bubble above, with a 4 px gap. Standalone stickers retain their 160 × 160 fit area inside the same compact bubble style.
Console behavior is covered by `ui-test-plan.md`.

## Previous verification record (before profile removal)

Verified with Java 25 using the actual MainWindow FXML and temporary task storage.
All five sticker assets decoded in both display modes. Assertions verified 160/120 px
fit dimensions, preserved aspect ratio, 40 px profile dimensions, sticker-before-text
ordering, unchanged response text, and no excess vertical space.

Visual inspection confirmed the random-sticker and unknown-command replies, then
a long task reply after narrowing the window to 330 px. Text wrapped below the
sticker without overlap, and the latest reply remained visible through automatic scrolling.

The command mapping and failure cases are covered by StickerResponseTest and
CommandProcessorTest. All 164 unit tests and all 17 exact-output console cases passed;
Checkstyle passed for main and test sources.

Preview images: `build/reports/stickers/chat.png` and
`build/reports/stickers/narrow-chat.png` (generated local artifacts).

## Composer regression verification

Verified the shared input row at 500 × 600 and 417 × 220 window sizes.
The input and Send button share top and bottom bounds, have an 8 px gap, and
remain inside the window. Submitting distinct todos using Send and Enter
creates and persists exactly two tasks, each with a Yatta confirmation.
All 164 unit tests, 17 console cases, and both Checkstyle tasks pass.
The runnable JAR was rebuilt. Local previews: `build/reports/composer/normal.png`
and `build/reports/composer/small.png`.

The current user data file separately blocks saving because of four duplicate
records and one event with equal start/end dates. The original file remains
unchanged pending the user's recovery choice.

## Profile-free reply verification

Java 25: all three MainWindowTest cases passed, covering profile removal,
text-before-sticker ordering, sticker-only replies, computed bubble corner radii,
and scrolling. Both Checkstyle tasks passed; the runnable JAR was rebuilt.

The full unit suite could not compile because CommandRecoveryTest calls the
missing CommandProcessor.getLoadWarning() method. GUI tests ran separately
with that unrelated test source excluded through a temporary Gradle init script.
The console run passed 15 cases and stopped at TC-16's existing storage-warning
mismatch. Complete inputs, actual outputs, and the failing expected output are
recorded in ui-test-record.md. Console expectations were not changed.

## Reply newline and startup warning verification

Reply payloads now omit trailing CR/LF characters in every display mode while
preserving internal line breaks. Updated command response assertions and added
regressions for LF, CRLF, repeated endings, empty text, and preserved spaces.
Storage warning tests cover line numbering and clearing warnings after repair.

Java 25: 184 of 187 unit tests passed; both Checkstyle checks passed and the JAR
was rebuilt. The remaining failures concern the existing writer-lock behavior
(two cases) and parser error wording (one case). All three GUI tests passed.
The console run passed 17 cases, including TC-16, before stopping at TC-18's
unrelated slash-prefixed-path parsing failure. See ui-test-record.md for the
complete input/output record and the failing expected output.

## Bread-thief personality verification

Updated console and GUI greetings, window title, task replies, search headings,
undo replies, sticker fallback, farewell, and conversational error wording.
Exact console expectations and affected unit assertions use the new speech.
Java 25: 184/187 unit tests passed; the same three unrelated parser/file-lock
failures remain. Console cases TC-01 through TC-17 (including TC-13 in plan order)
passed; the run stopped at the existing TC-18 path-parsing failure. Full inputs,
actual outputs, and the failing expected output are in ui-test-record.md.

## Bakery theme checks

| Aim | Input / setup | Expected result |
| --- | --- | --- |
| Header | Start at 500 × 600 and resize to 417 × 220 | Naisu artwork and James title remain visible; below 360 px content height, the subtitle hides and the header shrinks; composer stays inside the window. |
| Readability | Greeting, long task, and invalid command | Cocoa text, wrapped lines, and muted red error text remain legible on the cream background. |
| Controls | Tab through input and Send; hover and press Send | Clear focus border and darker hover/pressed button states; Enter and Send still submit once. |

## Bakery theme verification

Java 25: all 198 unit tests and both Checkstyle checks pass. The new GUI regression
checks header artwork loading, error text color, and composer alignment and bounds
at 500 × 600 and 417 × 220. Existing tests cover message corners, sticker display
modes, and automatic/manual scrolling. No production Java logic changed.
All 22 exact-output console cases pass; the complete input/output record is in
`ui-test-record.md`. Console expectations were reviewed and remain unchanged.
The runnable JAR was rebuilt. Visual inspection of the actual FXML confirmed the
cream palette, header, readable bubbles, and small-window scrolling layout.
Previews: `build/reports/bakery/normal.png` and `build/reports/bakery/small.png`.

## Grouped sticker bubble checks

| Aim | Input / setup | Expected result |
| --- | --- | --- |
| Grouped reply | Add a task with a long description at 417 and 500 px widths | Text wraps in its own bubble; a compact ivory sticker bubble sits 4 px below it, aligned left, without overlap. |
| Standalone reply | `random_sticker` | Only one compact sticker bubble appears, with 8 px padding and a tan border. |
| Artwork sizing | All five stickers in both display modes | Bubble fits the artwork instead of filling the row; artwork remains proportional and unclipped. |

### Grouped sticker verification

Java 25: all 199 JUnit tests, both Checkstyle checks, and all 22 exact-output
console cases pass. Console expectations were reviewed and remain unchanged;
see `ui-test-record.md` for the complete input/output transcript.
GUI tests cover all five stickers in both display modes at 417 and 500 px widths,
including compact bubble sizing, image containment, long-text wrapping, and the
4 px gap. The existing corner, composer, error styling, and scrolling checks pass.
Visual inspection confirmed the grouped bubbles at 500 × 600 and scrolling at
417 × 220. Previews are in `build/reports/grouped-stickers/`. The runnable JAR
was rebuilt.

## Conversation UX checks

| Aim | Input / setup | Expected result |
| --- | --- | --- |
| Asymmetric messages | `list`, then a long todo command | User commands fit their text, align right, and wrap within 82% of available width; app replies stay wide and left-aligned. |
| Errors without color | Invalid command or startup storage warning | A bold Error heading precedes the unchanged explanation; regular replies have no error heading. |
| Responsive header | Resize below 360 px content height, then enlarge | Header artwork and padding shrink; subtitle is hidden without reserving space, and restores when enlarged. |

### Conversation UX verification

Java 25: all 201 JUnit tests, both Checkstyle checks, and all 22 exact-output
console cases pass. Reviewed the console plan; expectations remain unchanged.
The complete console transcript is in `ui-test-record.md`. Added regression
coverage for short/long right-aligned commands at two widths and shrinking then
restoring the header. Updated error-response assertions for the visible heading.
Visual inspection at 500 × 600 and 417 × 220 confirmed compact user commands,
the bold Error heading, and the smaller header. Previews are in
`build/reports/chat-ux/`. The runnable JAR was rebuilt.
