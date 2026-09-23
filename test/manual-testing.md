# saLLMan manual testing

JUnit covers the logic, and [`ui-test-plan.md`](ui-test-plan.md) covers the
console. This page records the checks that neither can make: how the GUI
looks, and whether the released JAR runs on each operating system.

Only checks that were actually carried out are listed.

## What automated tests do not reach

JUnit covers 98.9% of the lines and 96.5% of the branches outside the GUI.
The rest cannot be reached from a test without a reason not to:

| Not covered | Why |
|---|---|
| The GUI (`sallman.gui`) | Tested by hand, as below. |
| The failure branches of `assert` statements | They only run if the code has a bug. |
| The `catch (RuntimeException e)` handlers in `Sallman` | A last resort for bugs; no valid or invalid input reaches them. |
| `Storage` given a bare file name, with no folder | Saving would write a file into the working directory. |

## The JAR on each operating system

The JAR built by CI was copied into an empty folder and started with
`java -jar sallman.jar` from that folder. In each case the window opened and
stayed open, and the error stream held nothing but the warnings JavaFX prints
when it is bundled inside a JAR on Java 25.

| Build | Operating system | Java |
|---|---|---|
| `1e2a33e` | Windows 11 Home | Oracle JDK 25.0.4 |
| `1e2a33e` | Ubuntu 24.04.3 LTS (WSL2, displayed through WSLg) | Temurin 25.0.4.1 |

The bundled JavaFX native libraries were also counted in the JAR: 54 `.dll`
files for Windows, 11 `.so` files for Linux, and 7 `.dylib` files for macOS.
The macOS libraries are built for Intel Macs, and the JAR has not been run on a
Mac.

## The window at different sizes

The main window was drawn offscreen from its real layout and stylesheets after
a series of commands, and the images were checked by eye.

| Width x height | Checked |
|---|---|
| 320 x 520 (the minimum width) | Long replies wrap inside their bubbles; the input box and Send button both fit; nothing is clipped. |
| 400 x 600 (the default) | The header, conversation, input row and disclaimer are all in view; errors stand out in red. |
| 900 x 420 | Bubbles stay sized to their text rather than stretching across the window; the input row spans the full width. |
