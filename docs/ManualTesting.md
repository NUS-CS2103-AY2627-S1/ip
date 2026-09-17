# Manual testing

The JUnit tests cover everything except the graphical interface (`DialogBox`,
`MainWindow`, `Main`, `Launcher`), plus two lines in `Harvey`: the no-argument constructor
and `main`. Those two write to the real `./data/harvey.txt`, so no test runs them.

This checklist covers what the JUnit tests cannot. Run it before a release, and after any
change to the FXML, the CSS or the `ui` package.

## Setup

1. Back up your own tasks: copy `data/harvey.txt` somewhere safe, then delete the original,
   so each run below starts from an empty list.
2. Build and start the app: `./gradlew run`, or `java -jar build/libs/harvey.jar` after
   `./gradlew shadowJar`.
3. When finished, put your copy of `harvey.txt` back.

Each check lists the steps to take, then what should happen.

## 1. Conversation layout

| # | Steps | Expected |
|---|---|---|
| 1.1 | Start the app. | The header shows Harvey's round picture, **Harvey Specter** and the gold tagline. The greeting appears in a navy card on the left, beside a small round avatar. |
| 1.2 | Type `todo read book` and press Enter. | Your message appears on the right as a cream bubble with no picture. Harvey's reply is a green card on the left. The input box empties. |
| 1.3 | Type `todo read book` again, then click **File**. | Clicking the button works the same as pressing Enter. The reply is a red card starting with `Objection!`, saying the task is already task 1. |
| 1.4 | Press Enter with the input empty, or with only spaces. | Nothing is added to the conversation. |
| 1.5 | Type `mark 1`, then `delete 1`. | The mark reply is green and the delete reply is dark red, so each kind of change can be told apart by color. |

## 2. Resizing and scrolling

| # | Steps | Expected |
|---|---|---|
| 2.1 | Drag the window as narrow and as short as it will go. | It stops at a minimum size (360 × 320). The input box and **File** button stay fully visible. |
| 2.2 | While narrow, type a long line such as `todo` followed by 30 words. | Your bubble and Harvey's reply both wrap onto several lines. No text is cut off and there is no sideways scrolling. |
| 2.3 | Drag the window wide. | Harvey's cards stretch to the new width. Your bubbles stay on the right, no wider than about three quarters of the window. |
| 2.4 | Send about 15 commands. | The view scrolls down to each new reply by itself. |
| 2.5 | Scroll back to the top, then wait. | The view stays at the top and does not jump back down. It only moves again when you send the next command. |
| 2.6 | Maximize the window, then restore it. | The layout adjusts both times with no gaps or overlapping controls. |

## 3. Exiting

| # | Steps | Expected |
|---|---|---|
| 3.1 | Type `bye now`. | Harvey refuses (`"bye" takes nothing after it`) and the window stays open. |
| 3.2 | Type `bye`. | The farewell appears, the input and button grey out, and the window closes about a second later. |
| 3.3 | Start the app again and type `list`. | Every task from before the restart is listed. |

## 4. Save file problems

| # | Steps | Expected |
|---|---|---|
| 4.1 | Close the app. Add the line `nonsense` to `data/harvey.txt`, then start the app. | A red warning under the greeting says 1 line could not be understood and gives the path of a `harvey.txt.<timestamp>.bak` file. That file exists and still contains `nonsense`. |
| 4.2 | Close the app. Replace `data/harvey.txt` with a folder of the same name, then start the app. | A red warning says it is a folder and Harvey is starting with an empty list. Adding a task reports that saving failed, without crashing. Delete the folder afterwards. |

## 5. Different environments

JUnit already runs on Ubuntu, macOS and Windows through GitHub Actions
(`.github/workflows/gradle.yml`). These checks cover what CI cannot see.

| # | Environment | Steps | Expected |
|---|---|---|---|
| 5.1 | Each OS you can access (Windows, macOS, Linux) | Run sections 1 to 3 using `harvey.jar`. | Same results on every OS. Fonts may differ slightly, but no text is cut off. |
| 5.2 | Computer language set to Chinese, or start Java with `-Duser.language=zh -Duser.country=CN` | Add `deadline essay /by 2026-10-01` and `event talk /from 2026-10-01 1400 /to 2026-10-01 1600`. | Dates read `Oct 1 2026` and `2:00PM`, in English like the rest of Harvey, not `10月` or `下午`. |
| 5.3 | A high-resolution (Retina / 4K) screen, and a low-resolution one such as 1366 × 768 | Start the app and repeat 2.1 to 2.3. | Text and pictures are sharp, not blurry, and the window fits on screen when it opens. |
| 5.4 | Display scaling set to 150% (Windows) or a larger text size (macOS) | Start the app. | Everything scales together, and the header and input row are not clipped. |
