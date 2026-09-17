# GUI regression plan

Use Zulu JDK `25.0.3.fx-zulu`. Run `./gradlew clean test` in a desktop
session; `MainWindowTest` loads real FXML and opens temporary windows.
All test data lives in JUnit temporary directories, never the user's save file.
The separate console plan must also pass, including filtered numbering and
Unicode-description regression cases.
The GUI scenarios allow real JavaFX layout pulses between interaction steps,
including native stage resizing and deferred scrolling or focus updates.

## Automated interaction coverage

| Case | Kind | Distinct behavior | JUnit method |
| --- | --- | --- | --- |
| GUI-01 | Positive | Fresh greeting focuses input; original transparent avatar and centered, covering background load behind an opaque reply | `initialize_freshSession_displaysGreeting` |
| GUI-02 | Positive | Send button adds a normal exchange and clears successful input | `handleUserInput_sendButton_addsExchangeAndClearsInput` |
| GUI-03 | Positive | Enter uses the same command path | `handleUserInput_enterKey_usesSameCommandHandler` |
| GUI-04 | Positive | Compact right-aligned user cards differ from full-width app replies | `dialogBox_speakers_useDistinctAlignmentAndCardWidths` |
| GUI-05 | Positive | Long prose wraps at a narrow stage width without clipping controls | `handleUserInput_longMission_wrapsWithoutLosingText` |
| GUI-06 | Positive | Overflow scrolls until the latest short reply is visible in the viewport | `handleUserInput_manyExchanges_scrollsToLatestResponse` |
| GUI-07 | Positive | Goodbye blocks subsequent submissions | `handleUserInput_bye_disablesFurtherSubmissions` |
| GUI-08 | Negative | Empty input shows an error, preserves missions, and permits recovery | `handleUserInput_blankInput_reportsErrorAndPreservesMissions` |
| GUI-09 | Negative | Invalid deletion highlights and selects the command without changing missions | `handleUserInput_invalidIndex_reportsErrorAndPreservesMissions` |
| GUI-10 | Negative | Corrupt startup storage shows a labeled error, blocks input, and preserves data | `initialize_corruptStorage_disablesInputAndPreservesData` |
| GUI-11 | Negative | Malformed deadline remains selected for correction; subsequent listing resets styling | `handleUserInput_parserError_selectsCommandForCorrection` |
| GUI-12 | Negative | Save failure shows a labeled error, keeps controls usable, and permits read-only recovery | `handleUserInput_saveFailure_highlightsErrorAndAllowsReadOnlyRecovery` |
| GUI-13 | Positive | Error-like words in a mission do not trigger error styling | `handleUserInput_errorWordsInMission_keepsNormalPresentation` |
| GUI-14 | Positive | Send returns keyboard focus to input for the next command | `handleUserInput_sendButton_returnsFocusToCommandField` |
| GUI-15 | Positive | Narrow and wide stage sizes retain a visible composer, bounded transcript, and compact avatar within the header | `resize_narrowAndWideWindow_keepsComposerAndTranscriptWithinBounds` |
| GUI-16 | Positive | A long unbroken token wraps inside a narrow window | `handleUserInput_longUnbrokenMission_wrapsWithinNarrowWindow` |
| GUI-17 | Positive | Reading position survives a resize, then a new command scrolls to its reply | `resize_readingHistory_preservesScrollUntilNextSubmission` |
| GUI-18 | Positive | Focusable history responds to Page Up and keeps keyboard focus | `scrollPane_pageUp_readsOlderRepliesUsingKeyboard` |
| GUI-19 | Positive | Narrowing the stage at the transcript bottom keeps the latest short reply visible | `resize_atTranscriptBottom_keepsLatestReplyVisible` |
| GUI-20 | Positive | Find and date-filter results retain full-log numbers through deletion, marking, and restart | `handleUserInput_filteredNumbers_updatesOnlyDisplayedMissions` |
| GUI-21 | Negative | Unicode-space-only descriptions across all task types stay selected for correction and preserve existing missions | `handleUserInput_unicodeBlankDescriptions_keepsErrorsEditableAndRecovers` |

All fifteen positive and six negative scenarios exercise distinct paths.
Their complete command sequences are unique. Error cases assert both the
`error-card` role and a visible, managed `ATTENTION NEEDED` label. Successful
recovery asserts normal styling and no space reserved for the error label.
Rejected parser and index commands preserve mission state; corrupt startup data
remains unchanged. The save-failure scenario creates a conflicting directory
after startup and verifies that the directory is preserved. It does not claim
that existing command logic rolls back an in-memory mutation after a failed save.

`GlennonTest` additionally tests all mission command families, restart
persistence, and typed error status for validation, startup, and saving failures.
The console plan also verifies the corrected filtered mission numbers and
Unicode-description handling through exact output checks.

Artwork assertions check the original resource URLs, successful image decoding,
the avatar's transparent corner and preserved aspect ratio, and a background
that covers the viewport without repeating. The greeting's reply fill remains
opaque. At both resize extremes, the avatar is at most 48 pixels per side and
the avatar, title, and subtitle remain within the header and window.

## Visual and packaged checks

1. Build `./gradlew shadowJar` and launch the JAR from a temporary directory.
2. Check the greeting, compact header, input focus, and button states. User
   messages should be compact and right-aligned; app replies should use the
   transcript width without avatars taking up text space. Confirm the original
   robot avatar has a transparent background and appears only in the header.
   The original mission background should fill the conversation area without
   tiling, while opaque message cards keep all text readable.
3. Submit a long mission, a deadline, an event, `list`, and invalid input.
   Errors must have a clear `ATTENTION NEEDED` label as well as a distinct color.
   The invalid command should remain selected for immediate correction.
   Correct it and confirm the next reply uses normal styling and clears input.
4. Resize down to the minimum and up to a large window. Check wrapping, no
   horizontal scroll, a visible input area, and no overlapping or clipped text.
   Confirm the compact avatar and header text stay visible and the background
   continues to cover the conversation area without stretching its aspect ratio.
   Include long text with no spaces. Confirm the wrapping command hint includes
   `sort` and remains above the window edge at the minimum height.
5. Fill the chat and verify that the latest reply is visible after submission.
   Scroll back to older replies, resize the window, and confirm it does not jump
   to the bottom. Submit another command and confirm its reply becomes visible.
   Narrow the window while at the bottom and check that the latest short reply
   remains fully visible. Tab into the transcript, check the focus outline, and
   use Page Up to read older replies without using the mouse.
   Click Send, then type the next command immediately to check focus restoration.
6. Enter `bye`: its response appears, inputs disable, and the window closes
   after one second. Closing the window directly must also exit the application.
7. Relaunch from the same temporary directory and verify `list` restores data.

Do not run mutation checks against the project's real `data/glennon.txt`.
