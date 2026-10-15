# Jonk GUI Test Plan

## Session configuration

- Run the application with Java 25 using `./gradlew run`.
- Before starting, replace `./data/jonk.txt` with the startup fixture below and restore the original file afterward.

**Startup data-file fixture:**

```text
T | 1 | write report
D | 0 | return notes | 2019-10-15
E | 1 | project demo | 2019-10-16 | 2019-10-17
```

## GUI-01: Open the chatbot window

**Aim:** Verify that the application starts with all controls needed to use the chatbot.

**Inputs:** Launch the application.

**Expected output:** A non-resizable window titled `Jonk • Mission Control` uses a midnight-blue control-deck theme
with cyan and violet accents. It displays a `✦ JONK // MISSION CONTROL` header, Jonk's greeting and
`Type help to see the available commands.`, a command field, and a `Transmit` button. Jonk uses a star avatar,
the user uses a rocket avatar, and message text uses the Avenir Next font when it is available.

## GUI-02: Submit a command with Enter

**Aim:** Verify that the command field accepts input and displays both sides of the conversation.

**Inputs:** Type `list` in the command field and press Enter.

**Expected output:** A right-aligned `list` bubble and a left-aligned Jonk response bubble display all three startup
tasks with their completion states and dates.

## GUI-03: Submit a command with the button

**Aim:** Verify that the Transmit button processes a command and persistent task changes still work.

**Inputs:** Type `todo GUI smoke test` and click `Transmit`.

**Expected output:** The conversation displays the user command in a violet bubble and Jonk's mission-logged response
in a cyan bubble, reporting that the flight plan holds four missions. The data file ends with
`T | 0 | GUI smoke test`.

## GUI-HELP-01: Read the help page

**Aim:** Verify that the shared help response is readable in the graphical conversation and preserves tasks.

**Inputs:** Type `help` and press Enter. Scroll through the response from top to bottom, then submit `list`.

**Expected output:** A Jonk response bubble contains the complete help page documented in `docs/README.md`,
including all ten commands, examples, and guidance. Text wraps within the window, and every line can be reached
by scrolling. The subsequent list still contains the four tasks, and the data file is unchanged.

## GUI-04: Exit using the bye command

**Aim:** Verify that the CLI-style exit command remains available in the GUI.

**Inputs:** Type `bye` and press Enter.

**Expected output:** Jonk's farewell appears, input controls become disabled, and the window closes after a brief
delay.
