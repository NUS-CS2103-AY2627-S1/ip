# Clarry sea-themed GUI checks

Run the GUI with Java 25 using `./gradlew run` (Windows: `.\gradlew.bat run`).
Use disposable task data when adding sample tasks.

| Check | Action | Expected result |
| --- | --- | --- |
| Theme | Launch Clarry. | Ocean-blue header, seafoam conversation background, readable white reply cards, and a compact input bar. |
| Mascot | Inspect the header at normal and minimum window sizes. | A smiling gray shark appears on a seafoam circle beside Clarry's name; its transparent background blends cleanly, and the subtitle remains readable. |
| Message hierarchy | Send `list`. | A compact blue user command appears on the right; Clarry replies in a wider card on the left. Speaker labels replace avatars. |
| Errors | Send `nonsense` and `deadline return book`. | Each response has an ERROR label, coral border, pale coral background, and dark red text. |
| Reliable error status | Send `todo OOPS!!! pack sunscreen`. | The successful response uses the regular Clarry style despite containing error-like text. |
| Narrow window | Resize to the minimum window size and send `help`. | Text wraps; all reply text is accessible by vertical scrolling; the input and Send button stay visible. |
| Wide window | Widen the window after viewing `help`. | Reply cards expand and text uses fewer lines without unnecessary space inside the card. |
| Long commands | Send a todo with a long description. | The user command wraps within its bubble and the response stays within the conversation width. |
| Keyboard | Submit a command with Enter, then with Send. | Both submit once; focus returns to the cleared input. Blank commands add no messages. |
| Exit | Send `bye`. | The farewell appears and both input controls become disabled. |
| Whitespace | Send `  list  ` and then `  bye  `. | List works normally; the padded goodbye still disables both input controls. |
| Save failure | Using disposable data, replace the loaded save file with a nonempty directory of the same name, then add or mark a task. | An ERROR card reports that saving failed; `list` shows no applied change. |
| Damaged file | Launch with disposable save data containing a valid task and a malformed line, then send `list`. | An ERROR card explains the partial load; valid tasks are visible, mutations are blocked, and the original file remains unchanged. |
