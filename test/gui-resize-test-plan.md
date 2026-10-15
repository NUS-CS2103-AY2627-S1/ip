# GUI resize checks

Run the JavaFX application with Java 25 using `./gradlew run`.

1. At the default window size, check that the full banner uses its fixed 140px height, stays inside the window, and leaves the input and Send button visible.
2. Submit the first command without resizing or scrolling. Check that the full banner remains fixed above the conversation and the latest reply becomes visible within the chat's scrollable area.
3. Resize to 400 × 480. Check that the complete banner remains visible without changing to a cropped face, margins and avatars shrink, and the chat, input, and Send button remain usable below it.
4. Display a task with a long description, including a long word without spaces. Check that the message wraps inside its card and that all lines can be reached by scrolling.
5. Build up several exchanges, scroll to roughly the middle, and resize narrower, wider, shorter, and taller. Check that the scrollbar keeps approximately the same relative position instead of jumping to the latest reply.
6. Submit another command while viewing earlier messages. Check that the latest reply becomes visible.
7. Scroll between the top and bottom of the conversation. Check that only the chat content moves and the full banner stays fixed above it.

The console interface is unaffected; its regression cases remain in `test/ui-test-plan.md`.

## Platform and language checks

These checks require real graphical environments. Record the OS version, Java version,
display resolution/scaling, language, result, and any screenshot or defect for each run.
Use a disposable working directory so testing does not change personal tasks.

| Environment | Display settings | Language | Status |
| --- | --- | --- | --- |
| macOS | Retina default scaling and a larger text setting | English, Chinese | Pending |
| Windows | 1920 × 1080, 100% and 150% scaling | English, Chinese | Pending |
| Linux | 1366 × 768 and 1920 × 1080 | English, Chinese | Pending |

For each available environment:

1. Launch the app with Java 25 and complete the seven resize checks above.
2. Use both Enter and Send to submit commands. Verify one response per submission,
   input focus returns, and blank submissions do not create tasks.
3. Add todo, deadline, and event tasks. Include `学习 Java 📚` and a long description.
   Check glyphs, wrapping, and input through a Chinese IME; composing text should not
   submit an unfinished command.
4. Enter a valid leap day (`29-02-2024 0000`), an invalid one (`29-02-2025 1200`),
   and a late time (`31-12-2026 2359`). Dates must retain their values; displayed month
   names may follow the system language, while saved dates keep the numeric format.
5. View, sort, find, mark, unmark, and remove tasks. Restart the app and verify that
   descriptions, dates, order, and completion states survive unchanged.
6. Check long conversations, scrolling, window close, and the `bye` response for
   clipping, freezes, or unexpected exceptions.

These rows are a manual test plan, not evidence of completed cross-platform testing.
