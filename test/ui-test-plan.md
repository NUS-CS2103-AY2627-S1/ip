# UI test plan

Compile all files in `src/main/java` into an isolated temporary directory with Java 25. Run each test case from its own empty temporary working directory so its `data/otaku.txt` file cannot affect another case.

## Greeting and exit

- **Aim:** Verify that Otaku starts and exits cleanly.
- **Console input:**

  ```text
  bye
  ```

- **Expected output:**

  ```text
____________________________________________________________
  ___  _        _
 / _ \| |_ __ _| | ___   _
| | | | __/ _` | |/ / | | |
| |_| | || (_| |   <| |_| |
 \___/ \__\__,_|_|\_\\__,_|
Hello! I'm Otaku, your questkeeper.
What mission shall we tackle next?
____________________________________________________________
Quest log sealed. Mata ne!
____________________________________________________________
  ```

## Save and load tasks across sessions

- **Aim:** Verify that all task types, completion status, delimiter characters in descriptions, and deletion are persisted across separate Otaku processes.
- **First-session console input:**

  ```text
  todo buy milk | tea
  deadline submit report /by 2026-09-30
  event workshop /from 2026-10-01 /to 2026-10-02
  mark 2
  bye
  ```

- **Expected first-session output:**

  ```text
____________________________________________________________
  ___  _        _
 / _ \| |_ __ _| | ___   _
| | | | __/ _` | |/ / | | |
| |_| | || (_| |   <| |_| |
 \___/ \__\__,_|_|\_\\__,_|
Hello! I'm Otaku, your questkeeper.
What mission shall we tackle next?
____________________________________________________________
 Quest accepted! I've added this task:
   [T][ ] buy milk | tea
 Your log now holds 1 quest.
____________________________________________________________
 Quest accepted! I've added this task:
   [D][ ] submit report (by: Sep 30 2026)
 Your log now holds 2 quests.
____________________________________________________________
 Quest accepted! I've added this task:
   [E][ ] workshop (from: Oct 01 2026 to: Oct 02 2026)
 Your log now holds 3 quests.
____________________________________________________________
 Quest cleared! This task is now complete:
   [D][X] submit report (by: Sep 30 2026)
____________________________________________________________
Quest log sealed. Mata ne!
____________________________________________________________
  ```

- **Second-session console input (in the same working directory):**

  ```text
  list
  delete 1
  bye
  ```

- **Expected second-session output:**

  ```text
____________________________________________________________
  ___  _        _
 / _ \| |_ __ _| | ___   _
| | | | __/ _` | |/ / | | |
| |_| | || (_| |   <| |_| |
 \___/ \__\__,_|_|\_\\__,_|
Hello! I'm Otaku, your questkeeper.
What mission shall we tackle next?
____________________________________________________________
 Your quest log:
1.[T][ ] buy milk | tea
2.[D][X] submit report (by: Sep 30 2026)
3.[E][ ] workshop (from: Oct 01 2026 to: Oct 02 2026)
____________________________________________________________
 Quest retired. I've removed this task:
   [T][ ] buy milk | tea
 Your log now holds 2 quests.
____________________________________________________________
Quest log sealed. Mata ne!
____________________________________________________________
  ```

- **Third-session console input (in the same working directory):**

  ```text
  list
  bye
  ```

- **Expected third-session output:**

  ```text
____________________________________________________________
  ___  _        _
 / _ \| |_ __ _| | ___   _
| | | | __/ _` | |/ / | | |
| |_| | || (_| |   <| |_| |
 \___/ \__\__,_|_|\_\\__,_|
Hello! I'm Otaku, your questkeeper.
What mission shall we tackle next?
____________________________________________________________
 Your quest log:
1.[D][X] submit report (by: Sep 30 2026)
2.[E][ ] workshop (from: Oct 01 2026 to: Oct 02 2026)
____________________________________________________________
Quest log sealed. Mata ne!
____________________________________________________________
  ```

## Task types, errors, status changes, deletion, and sorting

- **Aim:** Verify all inherited task types render their type icon, incorrect input produces helpful errors without
  changing the list, marking and unmarking affect only the selected task, deletion removes only the selected task,
  and sorting places dated tasks chronologically before undated tasks.
- **Console input:**

  ```text
  todo read book
  todo
  deadline return book /by
  deadline return book /by Friday
  deadline return book /by 2019-12-02
  event study /from Monday
  event study /from 2019-02-30 /to 2019-03-01
  event study /from 2019-12-03 /to 2019-12-02
  event study /from 2019-12-02 /to 2019-12-03
  mark one
  mark 1
  mark 4
  mark 2
  unmark 2
  delete two
  delete 4
  delete 2
  blah
  list
  sort extra
  list
  sort
  list
  bye
  ```

- **Expected output:**

  ```text
____________________________________________________________
  ___  _        _
 / _ \| |_ __ _| | ___   _
| | | | __/ _` | |/ / | | |
| |_| | || (_| |   <| |_| |
 \___/ \__\__,_|_|\_\\__,_|
Hello! I'm Otaku, your questkeeper.
What mission shall we tackle next?
____________________________________________________________
 Quest accepted! I've added this task:
   [T][ ] read book
 Your log now holds 1 quest.
____________________________________________________________
 I need a description after `todo`.
____________________________________________________________
 A deadline needs a description and a time after `/by`.
____________________________________________________________
 Please enter dates as yyyy-MM-dd, for example 2019-10-15.
____________________________________________________________
 Quest accepted! I've added this task:
   [D][ ] return book (by: Dec 02 2019)
 Your log now holds 2 quests.
____________________________________________________________
 An event needs a description, a start time after `/from`, and an end time after `/to`.
____________________________________________________________
 Please enter dates as yyyy-MM-dd, for example 2019-10-15.
____________________________________________________________
 An event's end date must be after its start date.
____________________________________________________________
 Quest accepted! I've added this task:
   [E][ ] study (from: Dec 02 2019 to: Dec 03 2019)
 Your log now holds 3 quests.
____________________________________________________________
 Please give a whole task number after `mark`.
____________________________________________________________
 Quest cleared! This task is now complete:
   [T][X] read book
____________________________________________________________
 Task 4 does not exist. Choose a number from 1 to 3.
____________________________________________________________
 Quest cleared! This task is now complete:
   [D][X] return book (by: Dec 02 2019)
____________________________________________________________
 Quest reopened. This task is active again:
   [D][ ] return book (by: Dec 02 2019)
____________________________________________________________
 Please give a whole task number after `delete`.
____________________________________________________________
 Task 4 does not exist. Choose a number from 1 to 3.
____________________________________________________________
 Quest retired. I've removed this task:
   [D][ ] return book (by: Dec 02 2019)
 Your log now holds 2 quests.
____________________________________________________________
 I don't recognize that command. Try todo, deadline, event, list, find, mark, unmark, delete, sort, or bye.
____________________________________________________________
 Your quest log:
1.[T][X] read book
2.[E][ ] study (from: Dec 02 2019 to: Dec 03 2019)
____________________________________________________________
 The `sort` command does not accept arguments.
____________________________________________________________
 Your quest log:
1.[T][X] read book
2.[E][ ] study (from: Dec 02 2019 to: Dec 03 2019)
____________________________________________________________
 Your quest log:
1.[E][ ] study (from: Dec 02 2019 to: Dec 03 2019)
2.[T][X] read book
____________________________________________________________
 Your quest log:
1.[E][ ] study (from: Dec 02 2019 to: Dec 03 2019)
2.[T][X] read book
____________________________________________________________
Quest log sealed. Mata ne!
____________________________________________________________
  ```

- **Follow-up console input (in the same working directory):**

  ```text
  list
  bye
  ```

- **Expected follow-up output:**

  ```text
____________________________________________________________
  ___  _        _
 / _ \| |_ __ _| | ___   _
| | | | __/ _` | |/ / | | |
| |_| | || (_| |   <| |_| |
 \___/ \__\__,_|_|\_\\__,_|
Hello! I'm Otaku, your questkeeper.
What mission shall we tackle next?
____________________________________________________________
 Your quest log:
1.[E][ ] study (from: Dec 02 2019 to: Dec 03 2019)
2.[T][X] read book
____________________________________________________________
Quest log sealed. Mata ne!
____________________________________________________________
  ```

## Whitespace, duplicate parameters, and duplicate tasks

- **Aim:** Verify blank input is handled safely, surrounding and repeated whitespace is accepted, duplicate tasks
  and command parameters are rejected, equal event dates are rejected, and later output proves errors do not alter
  the quest log.
- **Console input:**

  ```text

    todo   plan next arc
  todo   plan next arc
  deadline report /by 2026-09-20 /by 2026-09-21
  deadline report /by 2026-09-20
  event sync /from 2026-09-20 /from 2026-09-21 /to 2026-09-22
  event sync /from 2026-09-20 /to 2026-09-21 /to 2026-09-22
  event sync /from 2026-09-20 /to 2026-09-20
  event sync /from 2026-09-20 /to 2026-09-21
    list
  bye
  ```

- **Expected output:**

  ```text
____________________________________________________________
  ___  _        _
 / _ \| |_ __ _| | ___   _
| | | | __/ _` | |/ / | | |
| |_| | || (_| |   <| |_| |
 \___/ \__\__,_|_|\_\\__,_|
Hello! I'm Otaku, your questkeeper.
What mission shall we tackle next?
____________________________________________________________
 I need a command before I can update the quest log.
____________________________________________________________
 Quest accepted! I've added this task:
   [T][ ] plan next arc
 Your log now holds 1 quest.
____________________________________________________________
 That quest is already in your log.
____________________________________________________________
 A deadline accepts exactly one `/by` parameter.
____________________________________________________________
 Quest accepted! I've added this task:
   [D][ ] report (by: Sep 20 2026)
 Your log now holds 2 quests.
____________________________________________________________
 An event accepts exactly one `/from` parameter.
____________________________________________________________
 An event accepts exactly one `/to` parameter.
____________________________________________________________
 An event's end date must be after its start date.
____________________________________________________________
 Quest accepted! I've added this task:
   [E][ ] sync (from: Sep 20 2026 to: Sep 21 2026)
 Your log now holds 3 quests.
____________________________________________________________
 Your quest log:
1.[T][ ] plan next arc
2.[D][ ] report (by: Sep 20 2026)
3.[E][ ] sync (from: Sep 20 2026 to: Sep 21 2026)
____________________________________________________________
Quest log sealed. Mata ne!
____________________________________________________________
  ```

## Find tasks by keyword

- **Aim:** Verify that find shows only descriptions containing the case-sensitive keyword, reports a missing
  keyword without changing the list, and handles a keyword with no matches.
- **Console input:**

  ```text
  todo read book
  deadline return book /by 2026-09-30
  event book club /from 2026-10-01 /to 2026-10-02
  todo read magazine
  find book
  find
  find Book
  list
  bye
  ```

- **Expected output:**

  ```text
____________________________________________________________
  ___  _        _
 / _ \| |_ __ _| | ___   _
| | | | __/ _` | |/ / | | |
| |_| | || (_| |   <| |_| |
 \___/ \__\__,_|_|\_\\__,_|
Hello! I'm Otaku, your questkeeper.
What mission shall we tackle next?
____________________________________________________________
 Quest accepted! I've added this task:
   [T][ ] read book
 Your log now holds 1 quest.
____________________________________________________________
 Quest accepted! I've added this task:
   [D][ ] return book (by: Sep 30 2026)
 Your log now holds 2 quests.
____________________________________________________________
 Quest accepted! I've added this task:
   [E][ ] book club (from: Oct 01 2026 to: Oct 02 2026)
 Your log now holds 3 quests.
____________________________________________________________
 Quest accepted! I've added this task:
   [T][ ] read magazine
 Your log now holds 4 quests.
____________________________________________________________
 Matching quests:
1.[T][ ] read book
2.[D][ ] return book (by: Sep 30 2026)
3.[E][ ] book club (from: Oct 01 2026 to: Oct 02 2026)
____________________________________________________________
 I need a keyword after `find`.
____________________________________________________________
 Matching quests:
____________________________________________________________
 Your quest log:
1.[T][ ] read book
2.[D][ ] return book (by: Sep 30 2026)
3.[E][ ] book club (from: Oct 01 2026 to: Oct 02 2026)
4.[T][ ] read magazine
____________________________________________________________
Quest log sealed. Mata ne!
____________________________________________________________
  ```
