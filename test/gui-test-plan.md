# Graphical UI regression test plan

Use Java 25 and a temporary working directory so these checks do not alter saved user data.
These checks supplement the complete console plan in `ui-test-plan.md`.

## Phone punctuation

Enter `contact Vander /phone (65)-8765-4321 /address Somniel`, then `listcontacts`.
Expect the contact to be added and the phone punctuation preserved in the list.

## Wrapped message alignment

Resize the window to its minimum width and enter:

```text
todo Prepare a detailed battle plan with supplies and reinforcements for tomorrow
list
unknown
```

Expect the first and wrapped lines of each message to share the same left margin inside its bubble.
Check the welcome, user command, task-added response, list, and error bubbles.
Resize wider and confirm that wrapping adjusts without clipping the text.

## Farewell delay

Enter `bye` using Enter, then repeat in a fresh session using the Engage button.
Expect the farewell to appear immediately and remain visible for approximately 2.5 seconds
before the window closes. The input field and Engage button must be disabled during this delay.
No further commands should execute while closing, and the window must remain responsive.
The console version should continue to exit immediately after printing its farewell.
