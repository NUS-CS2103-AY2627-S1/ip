# Orbit GUI Test Plan

Use JDK 25 and a desktop display. Run `./gradlew guiTest` (Windows: `gradlew.bat guiTest`).
The GUI tests load application classes and FXML from the packaged Orbit.jar.
All test data belongs in fresh temporary directories, never the user's data folder.

1. Launch the FXML view. Check greeting, command field, Send button, and distinguishable speakers.
2. Add a todo using Enter, then list using Send. Both handlers must echo the command, show the
   actual response, and clear the field. Only one command must execute per action.
3. Submit blank/invalid commands, malformed dates, and event ranges whose end is not after the
   start. Errors must use the distinct warning bubble and `!` badge; valid commands afterward must
   still work.
4. Add todos, deadlines and events, then mark, unmark, delete, find and sort. Verify the same results
   as the command-line plan. Restart with the same temporary data file and verify saved state,
   including the sorted order.
5. Send long messages until the conversation exceeds its viewport. Verify wrapping and auto-scroll;
   resize the window to check that messages remain within its width and grow vertically.
6. Send `bye now`: it is an error, not an exit. Send `bye`: show the farewell, disable input,
   and invoke the exit action. In the application the window closes after a brief farewell.
7. Save an actual JavaFX scene snapshot with sample tasks to `build/screenshots/Orbit-GUI.png`.
8. Build `shadowJar` and launch `java -jar build/libs/Orbit.jar` from a fresh working folder.
   Verify FXML/CSS are bundled, there is no startup exception, and the GUI appears.
   Also run the retained CLI with `java -jar build/libs/Orbit.jar --cli`.

The default `check` task does not require a graphical display: it runs backend/unit tests and
Checkstyle. `guiTest` is explicit so non-graphical build environments can still check the backend.
