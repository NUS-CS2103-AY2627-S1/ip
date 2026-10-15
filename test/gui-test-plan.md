# GUI regression test plan

Run on Windows, macOS, and Linux with Java 25 and the matching JAR. Use a
temporary working folder so these checks do not change your real task data.

1. Launch the app. A greeting should appear immediately, suggesting
   `todo read book` and `list`, without creating a task.
2. At the default window size, the button must display `Send` in full. Resize
   to the minimum width and maximize: the label must remain visible and the
   input field must not overlap the button.
3. Submit `todo read book` with Enter, then `list` with Send. Both must work.
   Repeat `list` until the conversation exceeds the viewport height.
   With the field empty or containing only spaces, Send must be disabled and
   Enter must add no messages. After a valid submission clears the field,
   Send must become disabled again. After `bye`, both controls stay disabled.
4. Keep focus in the input field after submitting a command. Scroll upward
   with a mouse wheel or trackpad without clicking the scrollbar. Older
   messages must become visible. Scroll downward again. Repeat after several
   submissions and after resizing; scrolling must remain usable.
5. While viewing older messages, submit `list`. The latest response should
   become visible automatically, and manual scrolling must still work.
6. Add a long deadline using
   `deadline submit the detailed project report and supporting notes /by 18-09-2026 1800`.
   Run `list`, then `delete 2`. Wrapped lines, task lines, and the remaining
   task count must share the bubble's padded left edge without clipping.

Record OS, Java architecture/version, screenshots, and any failing step.
