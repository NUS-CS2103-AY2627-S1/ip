# Manual test plan

Use Java 25 (`sdk use java 25.0.3.fx-zulu`) and launch the packaged application with
`./gradlew shadowJar`, followed by `java -jar build/libs/tony.jar`.

Record the operating system, display scaling, screen resolution, and OS display language for every session.

## Test case: Responsive JavaFX layout

**Aim:** Verify that the graphical interface remains readable and operable at supported window sizes.

1. Start at the default window size and enter enough commands to require scrolling.
2. Resize to the minimum 360 x 440 window size.
3. Resize to at least 1920 x 1080 and maximize the window.
4. At each size, scroll to an earlier message and submit another command.
5. Verify that messages wrap without clipping, the composer remains visible, buttons remain reachable, and the
   conversation automatically scrolls to its absolute bottom so Tony's complete newest response is visible.

## Test case: Display scaling and screen resolution

**Aim:** Verify layout at common desktop scaling settings that cannot be reproduced reliably in a unit test.

1. Repeat the responsive-layout case at 100%, 125%, 150%, and 200% display scaling where the OS supports them.
2. Cover at least 1366 x 768 and 1920 x 1080 resolutions, plus a Retina/HiDPI display when available.
3. Verify that text, avatars, controls, focus indicators, and the help dialog remain sharp and unobstructed.

## Test case: Operating systems

**Aim:** Verify the packaged JavaFX application on each supported desktop platform.

1. Run the add, list, find, mark, unmark, delete, help, and bye workflows on macOS, Windows, and Linux.
2. Restart after adding and marking tasks, then verify that the same task state is restored.
3. Verify that no platform-specific terminal or JavaFX errors appear.

## Test case: OS language and locale

**Aim:** Verify that commands, stored dates, and displayed dates remain stable when the OS language changes.

1. Run the main workflow with the OS language set to English.
2. Repeat with the OS language set to Simplified Chinese.
3. Verify that commands still use `yyyy-MM-dd`, displayed month names remain in English, and saved tasks load unchanged.

## Test case: Keyboard and accessibility

**Aim:** Verify core keyboard navigation and accessibility metadata in the live JavaFX toolkit.

1. Navigate every composer and quick-action control using only Tab and Shift+Tab.
2. Submit a command with Enter and close the help dialog with the keyboard.
3. With VoiceOver, Narrator, or Orca enabled, verify that command input, buttons, task counts, avatars, and responses
   have meaningful names and are announced in conversation order.
