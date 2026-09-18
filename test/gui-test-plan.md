# Manual GUI checks

Use Java 25 and build with `./gradlew shadowJar`. Launch the resulting
`build/libs/kachow.jar` from a new temporary working directory so the test
cannot modify the normal `data/kachow.txt` file.

| Check | Action | Expected result |
| --- | --- | --- |
| Startup | Launch the application. | The title bar reads `Kachow`. Greeting and avatar are visible; the input and Send button have inset spacing and the racing color theme. |
| Enter submission | Type `todo visual check`, then press Enter. | User message and one task-added reply appear; input clears. |
| Button submission | Type `list`, then click Send. | Exactly one list reply shows task 1 with its original description. |
| Validation | Send `event invalid /from 2026-09-11 1000 /to 1000`. | An amber error bubble with a red border and dark text appears; the interface remains responsive. |
| Continued use | Send `mark 1`, then `list`. | Normal replies return to red bubbles; task 1 is completed and the rejected event was not added. |
| Long conversation | Repeat `list` until replies exceed the viewport. | Latest reply scrolls into view; older replies remain accessible. |
| Resize | Narrow and widen the window. | Input and Send remain accessible; dialog text wraps without overlapping avatars. |
| Restart | Close the window and relaunch from the same temporary directory; send `list`. | Task 1 is restored as completed. |
| Invalid exit | Submit `bye extra`. | An error appears and the window remains open. |
| Enter exit | Submit `bye` with Enter. | The window closes and the application exits. |
| Button exit and persistence | Relaunch from the same temporary directory, send `list`, then submit `bye` using Send. | Task 1 is still completed; Send closes the window and exits the application. |

## Latest attempt — 2026-09-11

Status: **visual checks unverified**, including the restored Improve GUI shelf styling.

The Java 25 build succeeds. The first launch inside the execution sandbox could
not initialize the display. Launching with desktop access starts JavaFX, but the
computer-use tool cannot resolve or attach to the unbundled Java application;
it is absent from its application inventory. Both isolated test processes were
stopped without touching normal task data. No visual pass is claimed.

The desktop launch also logs existing native-access and FXML-version warnings
(FXML version 26 versus runtime 25.0.3). Their visual impact was not verified.
The JUnit suite tests the shared application logic without requiring JavaFX.
