# GUI checks

Run `./gradlew run` with Java 25.

1. Check that the greeting appears on the left with a purple rule and dark
   text. Enter `todo borrow book`: the command should appear in a blue bubble
   on the right, with Sine's response using the greeting's format on the left.
2. Resize the window from its minimum width to a wide window. Enter a long
   task description and run `list`. Messages should wrap without horizontal
   scrolling, and replies should use the additional width when enlarged.
3. Check that the small circular avatars leave space for text and that the
   Send button and command field remain visible at the minimum window size.
4. Use Tab to focus the command field and Send button. Each should have a
   visible focus border. Submit a command using Enter and by clicking Send.
5. Add enough messages to fill the window. New replies should scroll into
   view, and earlier messages should remain accessible by scrolling upward.
