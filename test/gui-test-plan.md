# JavaFX GUI test plan

Run `.\gradlew.bat run` from the repository root before each test case unless stated otherwise.

## GUI-001 — Send commands using the keyboard and button

- Confirm the window title is `Sumo` and the initial Sumo dialog says `My name is Sumo.` followed by
  `I'm here to help you stay on task.`.
- Enter `todo read book` and press **Enter**. Confirm a user dialog and Sumo's task-added response
  appear.
- Enter `list` and click **Send**. Confirm a user dialog and a response containing the added task
  appear.
- Enter `bye`. Confirm the farewell appears and the input field and button become disabled. Confirm the window and Java process exit after five seconds.

## GUI-002 — Continue without avatar files

Ensure `UserPfp.png` and `SumoPfp.png` are absent from `src/main/resources/images`, then start the GUI.
Confirm it starts and accepts commands, with blank avatar areas beside the dialogs.

## GUI-003 — Keep the newest dialog visible

Enter enough valid commands to exceed the visible dialog area. Confirm that the scroll pane moves to
show the newest dialog after each response is added.

Scroll up and down with the mouse wheel or trackpad. Confirm scrolling moves slightly faster
and stops at both ends without jumping. Confirm the scrollbar can still be dragged normally,
and scrolling a conversation shorter than the viewport does not move it.

## GUI-004 — Show saved-data warnings and preserve rejected records

Back up `data/sumo.txt`, then put a valid record followed by an invalid record in the file:
`T | 0 | read book` followed by `invalid record` on its own line. Start Sumo and confirm the chat
shows a warning about line 2 and says saving is disabled. Send `list` and confirm the valid task appears.
Send `delete 1` and confirm a saving error appears; `list` must still show the task and the original
file must remain unchanged. Close Sumo and restore the backup after this check.

## GUI-005 — Android theme, resizing, and keyboard access

- Confirm a slate background, cyan triangle, SUMO header, and blue ring with the text Ready.
- Confirm assistant bubbles have a cyan top border and user bubbles align right.
- Confirm both profile pictures fit within 48 × 48 pixels, preserve their aspect ratios,
  and leave a gap beside the message bubbles at the minimum window width.
- Resize from the minimum 400 × 600 window to a larger window. Send a long task description
  and confirm bubbles wrap, the composer stays visible, and no horizontal scrolling is needed.
- Use Tab to reach the command field and Send button; confirm visible focus indicators.
- Send a command with the button and confirm focus returns to the command field.
- Confirm the command examples remain readable and Enter still submits.
- Send bye and confirm the ring becomes gray, the status reads Closing in 5 seconds, and both
  input controls are disabled. The farewell must remain readable for five seconds, then the window and Java process must exit.
- Confirm the theme works offline and has no animation or flashing.

## GUI-006 — Delayed exit and invalid exit input

- Enter `bye now`. Confirm a usage error appears and the app remains usable after five seconds.
- Add `todo keep after exit`, then submit `  bye  `.
- Confirm the farewell, disabled input controls, gray ring, and `Closing in 5 seconds` status.
- During the delay, confirm scrolling and window movement remain responsive and extra commands
  cannot be submitted. The window and Java process must exit after approximately five seconds.
- Relaunch from the same working folder and use `list` to confirm the task was saved.
- Repeat `bye`, then close the window manually during the delay; the process should exit cleanly.