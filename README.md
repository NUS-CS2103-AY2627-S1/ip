## Acknowledgements

### AI-assisted code

Code in this repository that an AI tool generated is listed here, one entry per commit,
newest first. Each entry names the files and the methods involved. The same
information appears as a comment beside the code itself. Entries start with the
commits of 27 August 2026; earlier commits carry the code comments only.

#### Correct the example in the deadline error message

Claude (Anthropic), used through Claude Code, generated the code below on
18 September 2026, after I noticed the complaint about a malformed deadline
still told the user to write `/by Sunday`, which Level-8 stopped accepting.

* `src/main/java/gudgoi/exception/DeadlineFormatException.java` — the
  corrected example, and the comment saying why it has to be a working
  command
* `src/test/java/gudgoi/GudGoiTest.java` — `isExampleFromComplaintAccepted`
  and the two tests that feed each complaint's own example back in

#### Test the command layer

Claude (Anthropic), used through Claude Code, generated the code below on
17 September 2026, for A-MoreTesting, from my decision that the command layer
had no tests at all, that the quiz and the undo-on-failure paths are where a
fault would cost the user their data, and that a failed write is best forced by
putting the save path inside a plain file rather than by changing file
permissions, which behave differently on each operating system.

* `src/test/java/gudgoi/GudGoiTest.java` — whole file
* `src/test/java/gudgoi/task/TaskTest.java` — whole file
* `src/test/java/gudgoi/ParserTest.java` — the eight `parseCard` tests

#### Style the window, and set refusals apart

Claude (Anthropic), used through Claude Code, generated the code below on
15 September 2026, for A-BetterGui, following part 5 of the JavaFX tutorial at
<https://se-education.org/guides/tutorials/javaFxPart5.html>, which is course
material. The two stylesheets, the edge anchoring and the per-reply style class
follow that tutorial. My decisions were that the one thing worth a format of
its own is a refused command, that the faces should be small and round because
the window is narrow, and that every color belongs in one block so that a
later change of personality is a single edit.

* `src/main/resources/view/main.css` — whole file
* `src/main/resources/view/dialog-box.css` — whole file
* `src/main/resources/view/MainWindow.fxml` — the edge anchors that let the
  window be resized, and the stylesheet link
* `src/main/resources/view/DialogBox.fxml` — the smaller face, the spacing,
  and the stylesheet link
* `src/main/java/gudgoi/GudGoi.java` — `wasLastAnswerAnError` and its getter
* `src/main/java/gudgoi/gui/Main.java` — `MIN_WIDTH`, `MIN_HEIGHT`, and the
  removal of the fixed window size
* `src/main/java/gudgoi/gui/MainWindow.java` — the error flag passed through
  `showBotSaying`, and the smaller requested face size
* `src/main/java/gudgoi/gui/DialogBox.java` — `markAsError`, the circular clip
  on the face, and the `reply-label` class added in `flip`

#### Move the window layout into FXML

Claude (Anthropic), used through Claude Code, generated the code below on
14 September 2026, for A-BetterGui, following part 4 of the JavaFX tutorial at
<https://se-education.org/guides/tutorials/javaFxPart4.html>, which is course
material. The two FXML files and the split into a loader and a controller come
from that tutorial. My decisions were that the controller should not extend a
layout class, and that the greeting should still run as soon as the bot is
handed over.

* `src/main/resources/view/MainWindow.fxml` — whole file
* `src/main/resources/view/DialogBox.fxml` — whole file
* `src/main/java/gudgoi/gui/Main.java` — whole file
* `src/main/java/gudgoi/gui/MainWindow.java` — the rewrite from an
  `Application` that builds its own layout into an FXML controller:
  `initialize`, `setGudGoi`, the `@FXML` fields, and the removal of
  `layOutWindow`, `wireHandlers` and `startSession`
* `src/main/java/gudgoi/gui/DialogBox.java` — the FXML loading in the
  constructor, the `@FXML` fields, and the removal of the hand-built row
* `src/main/java/gudgoi/gui/Launcher.java` — the change of the launched class
  from `MainWindow` to `Main`
* `build.gradle` — the three `javafx-fxml` dependencies

#### Guard a save path that has no folder

Claude (Anthropic), used through Claude Code, generated the code below on
10 September 2026, from my decision to fix this after the three week 5 pull
requests were merged rather than inside the A-CodeQuality one.

* `src/main/java/gudgoi/Storage.java` — the null check on the parent folder in
  `save`
* `src/test/java/gudgoi/StorageTest.java` — `save_pathWithNoFolder_doesNotFail`

#### Add trivia cards

Claude (Anthropic), used through Claude Code, generated the code below on
10 September 2026, for the D-Trivia extension, from my decisions that a card is
its own kind of thing rather than a fourth kind of task, that the deck lives in
its own file, and that a quiz asks one question and grades the next line typed.

* `src/main/java/gudgoi/trivia/Card.java` — whole file
* `src/main/java/gudgoi/trivia/CardList.java` — whole file
* `src/main/java/gudgoi/trivia/CardStorage.java` — whole file
* `src/main/java/gudgoi/exception/CardFormatException.java` — whole file
* `src/main/java/gudgoi/exception/CardSaveException.java` — whole file
* `src/main/java/gudgoi/exception/CardLoadException.java` — whole file
* `src/test/java/gudgoi/trivia/CardTest.java` — whole file
* `src/test/java/gudgoi/trivia/CardListTest.java` — whole file
* `src/test/java/gudgoi/trivia/CardStorageTest.java` — whole file
* `src/main/java/gudgoi/GudGoi.java` — `addCard`, `listCards`, `deleteCard`,
  `startQuiz`, `gradeAnswer`, `loadCards`, and the card fields and dispatch
* `src/main/java/gudgoi/Parser.java` — `parseCard`
* `src/main/java/gudgoi/exception/OutOfBoundException.java` — the `noun`
  parameter, so a deck reports a bad position in its own words
* `src/main/java/gudgoi/gui/MainWindow.java` — `CARD_PATH` and the deck restore

#### Use streams

Claude (Anthropic), used through Claude Code, generated the code below on
10 September 2026, for A-Streams, from my decision on which loops in this
codebase a stream actually makes clearer.

* `src/main/java/gudgoi/GudGoi.java` — `numberMatching`, and the rewrite of
  `listTasks` and `findTasks` to call it
* `src/main/java/gudgoi/Storage.java` — the stream in `save`
* `src/main/java/gudgoi/Ui.java` — the stream in `show`

#### Improve code quality

Claude (Anthropic), used through Claude Code, generated the code below on
10 September 2026, for A-CodeQuality, from my decision on which of the course
code quality guidelines this codebase actually breaches.

* `src/main/java/gudgoi/gui/MainWindow.java` — the split of `start` into
  `layOutWindow`, `wireHandlers` and `startSession`, and the named size
  constants
* `src/main/java/gudgoi/Storage.java` — the save-file layout constants, and
  `hasReadableShape`, `buildTask`, `buildTodo`, `buildDeadline`, `buildEvent`,
  `descriptionOf` and `timeAt`
* `src/main/java/gudgoi/GudGoi.java` — `describeTotal`
* `src/main/java/gudgoi/task/Task.java` — the change of `getTypeIcon` and
  `getTypeLetter` to abstract methods

#### Add assertions

Claude (Anthropic), used through Claude Code, generated the code below on
10 September 2026, for A-Assertions, from my decision about which assumptions
in the save-and-undo protocol are the program's own rather than the user's.

* `src/main/java/gudgoi/GudGoi.java` — the assertions in `addAndConfirm`,
  `mark`, `unmark` and `deleteTask` that check each undo path
* `src/main/java/gudgoi/TaskList.java` — the assertions in `insert` and
  `removeLast`, and the Javadoc that explains them
* `src/main/java/gudgoi/Parser.java` — the assertions in `parseCommandWord`
  and `parseArguments` that the line arrives trimmed
* `src/test/java/gudgoi/TaskListTest.java` —
  `removeLast_onAnEmptyList_failsTheAssertion`
* `build.gradle` — `enableAssertions` on the `test` and `run` tasks

#### Let Ui.show take a line for each argument

Claude (Anthropic), used through Claude Code, generated the code below on
5 September 2026, from my decision that a caller should be able to hand over a
response line by line instead of joining it first.

* `src/main/java/gudgoi/Ui.java` — the change of `show` to a variable arity
  method, and the nested loop that breaks each part at its own newline

#### Add a window, and keep the console

Claude (Anthropic), used through Claude Code, generated the code below on
5 September 2026, for Level-10, from the JavaFX tutorial and from my decision
that the window and the console must run the same commands. The window layout
comes from the tutorial; the wiring to `GudGoi` is new.

* `src/main/java/gudgoi/gui/Launcher.java` — whole file
* `src/main/java/gudgoi/gui/MainWindow.java` — whole file
* `src/main/java/gudgoi/gui/DialogBox.java` — whole file
* `src/main/java/gudgoi/GudGoi.java` — the change of `addAndConfirm`,
  `listTasks`, `findTasks`, `mark`, `unmark`, `deleteTask` and `handle` from
  printing to returning their text, and the new `getGreeting`, `loadTasks`,
  `getResponse` and `isExit`
* `src/main/java/gudgoi/Ui.java` — the `GREETING` and `FAREWELL` constants, and
  `getGreeting` and `getFarewell`
* `build.gradle` — the JavaFX dependencies, and the change of `mainClass`

#### Apply the peer review feedback and set up Checkstyle

Claude (Anthropic), used through Claude Code, generated the code below on
3 and 4 September 2026, from the review comments on my pull request and from my
decision that `Task` should be abstract. The renames, the Javadoc periods, the
American spellings and the Checkstyle reindentation are edits to code that
already existed; the files below are the ones that received newly written code.

* `src/main/java/gudgoi/GudGoi.java` — the rewritten command loop in `run`
* `src/main/java/gudgoi/task/Task.java` — the header comment on the constructor,
  now `protected`
* `.claude/skills/git-commit/scripts/check-conventions.sh` — the locale guard,
  and the three new checks for Javadoc periods, British spelling and boolean
  names

#### Add a find command

Claude (Anthropic), used through Claude Code, generated the code below on
28 August 2026, for Level-9, from my decision that a match keeps its real
position in the agenda.

* `src/main/java/gudgoi/GudGoi.java` — `findTasks`, and the `find` case in the
  dispatch
* `src/main/java/gudgoi/Parser.java` — `parseKeyword`
* `src/main/java/gudgoi/exception/FindFormatException.java` — whole file
* `src/test/java/gudgoi/ParserTest.java` — the four `parseKeyword` tests

#### Correct the Javadoc and cover every method

Claude (Anthropic), used through Claude Code, generated the code below on
28 August 2026, for A-JavaDoc. Nothing but comments changed.

* `src/main/java/gudgoi/GudGoi.java` — the corrected `@throws` and `{@link}` in
  `mark`, `unmark`, `deleteTask` and `handle`
* `src/main/java/gudgoi/task/Task.java` — the corrected references in
  `toSaveFormat` and `escape`
* `src/main/java/gudgoi/task/Todo.java`, `Deadline.java`, `Event.java` — header
  comments on the ten overriding methods

#### Add JUnit tests for parser, storage and task list

Claude (Anthropic), used through Claude Code, generated the code below on
28 August 2026, for A-JUnit. The cases come from edge cases I had already found
by running the program by hand.

* `src/test/java/gudgoi/ParserTest.java` — whole file
* `src/test/java/gudgoi/StorageTest.java` — whole file
* `src/test/java/gudgoi/TaskListTest.java` — whole file

#### Point Gradle at this project

Claude (Anthropic), used through Claude Code, generated the code below on
28 August 2026, for A-Gradle. The rest of `build.gradle` and the Gradle wrapper
came from the course repository and are not my work or Claude's.

* `build.gradle` — the two names that identify this project: `mainClass` and the
  name of the packaged JAR

#### Organize the classes into packages

Claude (Anthropic), used through Claude Code, generated the code below on
28 August 2026, for A-Packages. The change is mechanical: no method body was
altered.

* all 19 files under `src/main/java/gudgoi/` — the `package` declaration, and
  the `import` lines for the project classes each file now reaches across a
  package boundary
* `src/main/java/gudgoi/task/Task.java` — the change of `DISPLAY_FORMAT` from
  `protected` to `public`, and the comment explaining why

#### Turn GudGoi into an object with a constructor

Claude (Anthropic), used through Claude Code, generated the code below on
27 August 2026, for A-MoreOOP.

* `src/main/java/GudGoi.java` — the constructor, the change of `ui`, `storage`
  and `tasks` from static fields to instance fields, the move of the session
  into `run`, and the reduction of `main` to one line

#### Extract command reading into a Parser class

Claude (Anthropic), used through Claude Code, generated the code below on
27 August 2026, for A-MoreOOP. The method bodies inside `Parser` are my earlier
Level-4 to Level-8 code, moved; the class, the split of a line into a command
word and its arguments, and the header comments were generated.

* `src/main/java/Parser.java` — whole file
* `src/main/java/GudGoi.java` — the removal of `addTodo`, `addDeadline`,
  `addEvent`, `parseDateTime` and `parsePosition`, and the rewrite of `handle`
  to ask `Parser` for the command word, the arguments and the task

#### Extract the task list into a TaskList class

Claude (Anthropic), used through Claude Code, generated the code below on
27 August 2026, for A-MoreOOP. The bounds check inside `get` and `remove` is my
Level-5 code, moved from `taskAt`; the class, its constructors and the rest were
generated.

* `src/main/java/TaskList.java` — whole file
* `src/main/java/GudGoi.java` — the change of `tasks` to a `TaskList`, the split
  of `taskAt` into `parsePosition` plus the list's own bounds check, and the undo
  paths in `addAndConfirm`, `mark`, `unmark` and `deleteTask`

#### Extract file handling into a Storage class

Claude (Anthropic), used through Claude Code, generated the code below on
27 August 2026, for A-MoreOOP. The method bodies inside `Storage` are my earlier
Level-7 and Level-8 code, moved; the class, its constructor and the change from
printing to raising an Exception were generated.

* `src/main/java/Storage.java` — whole file
* `src/main/java/TaskLoadException.java` — whole file
* `src/main/java/GudGoi.java` — the `storage` field, and the change of the save
  and load call sites

#### Extract user interaction into a Ui class

Claude (Anthropic), used through Claude Code, generated the code below on
27 August 2026, for A-MoreOOP. The method bodies inside `Ui` are my earlier
code, moved unchanged; the class, its header comments and the call-site changes
were generated.

* `src/main/java/Ui.java` — whole file
* `src/main/java/GudGoi.java` — the `ui` field, the class header comment, and
  the change of every print and every read to go through `Ui`

#### Replace free-text times with real dates

Claude (Anthropic), used through Claude Code, generated the code below on
27 August 2026, from my specification of the Level-8 date handling: accept a
date on its own or a date with a time, store both as a date and time, default a
deadline to 00:00 and an event's end to 23:59, and reject an event that ends
before it starts.

* `src/main/java/GudGoi.java` — `INPUT_WITH_TIME`, `INPUT_DATE_ONLY`,
  `DEADLINE_DEFAULT_TIME`, `EVENT_END_DEFAULT_TIME`, `parseDateTime`, the date
  handling and the ordering check in `addDeadline` and `addEvent`, the ISO
  parsing in `parseSavedTask`, and the older-version line in
  `discardCorruptFile`
* `src/main/java/DateFormatException.java` — whole file
* `src/main/java/EventFormatException.java` — the constructor for an event that
  ends before it starts
* `src/main/java/Deadline.java` — the change of `by` to `LocalDateTime`, and the
  matching changes to the constructor, `getBy`, `toSaveFormat` and `toString`
* `src/main/java/Event.java` — the change of `from` and `to` to `LocalDateTime`,
  and the matching changes to the constructor, `getFrom`, `getTo`,
  `toSaveFormat` and `toString`
* `src/main/java/Task.java` — `DISPLAY_FORMAT`

#### Add saving and loading of the task list

Claude (Anthropic), used through Claude Code, generated the code below on
27 August 2026, from my specification of the Level-7 save format.

* `src/main/java/GudGoi.java` — `SAVE_PATH`, `save`, `parseSavedTask`,
  `unescape`, `load`, `discardCorruptFile`, and the undo-on-failure blocks in
  `addAndConfirm`, `mark`, `unmark` and `deleteTask`
* `src/main/java/TaskSaveException.java` — whole file
* `src/main/java/Task.java` — `getTypeLetter`, `toSaveFormat`, `escape`
* `src/main/java/Deadline.java` — `getTypeLetter`, `toSaveFormat`
* `src/main/java/Event.java` — `getTypeLetter`, `toSaveFormat`
* `src/main/java/Todo.java` — `getTypeLetter`

### Third-party libraries

* **[JavaFX](https://openjfx.io/)** 17.0.7, GPLv2 with the Classpath Exception.
  The window, its layout and its event handling all come from this library.
  It is pulled in by `build.gradle` and is bundled in the shadow JAR, which is
  why the JAR carries the native libraries for Windows, macOS and Linux.

### Course material used

* The layout of the window and the two-shape dialog box follow the
  [JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html)
  from se-education.org.
* `src/main/resources/images/DaUser.jpg` and `DaDuke.jpg` are the sample
  avatars from the same tutorial.

### Development-only tools — not part of the product

> **These are writing aids used while working on the project. None of them ship with the application.
> No code from them appears in `src/`, none is bundled in the released JAR, and no part of the product
> is derived from them. They affect documentation and notes only.**

* **Claude Code skills written for this project**, generated by Claude (Anthropic) at my direction
  and kept at `.claude/skills/`: `ip-mentor` (how the assistant and I split the work),
  `edge-case-probe` (a testing checklist), and `git-commit` (the checks to run before a commit,
  with two shell scripts that read the coding and commit standards). They are notes and checks for
  me, not part of the chatbot. No code from them reaches `src/`.

* **[asd-ste100-skill](https://github.com/danyuchn/asd-ste100-skill)** by Dustin Yuchen Teng, MIT License.
  A Claude Code skill that rewrites English into [ASD-STE100 Simplified Technical English](https://www.asd-ste100.org/),
  used to keep project documentation unambiguous.
  Kept at `.claude/skills/asd-ste100/`, at commit [`d5ce157`](https://github.com/danyuchn/asd-ste100-skill/commit/d5ce157870cf9c41efd1d6e836706a2be3c7b9da) (13 Aug 2026).
  The MIT licence text is retained in `.claude/skills/asd-ste100/LICENSE`.
