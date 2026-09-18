# UI Test Session

## TC-01: Interleaved valid and invalid commands preserve task state — PASS

Aim: Verify empty and unknown input, every task format, malformed separators, all task-number errors, additional spaces, continuation after errors, and unchanged task count, ordering, and star statuses.

Command:

```text
/bin/zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && ./gradlew -q classes && TOOTHLESS_TEST_ROOT=$(mktemp -d) && TOOTHLESS_CLASSES="$PWD/build/classes/java/main:$PWD/build/resources/main" && cd "$TOOTHLESS_TEST_ROOT" && java -Dos.name="Mac OS X" -cp "$TOOTHLESS_CLASSES" toothless.Toothless'
```

Console input:

```text

blah
list extra
mark
unmark
todo
todo
todo    read book
todo read  book
deadline
deadline return book
deadline /by Sunday
deadline return book /by
deadline return book /by Sunday /by Monday
deadline return book /by 2019-12-02
deadline return  book /by 2019-12-02
event
event project meeting
event project meeting /from Mon 2pm
event project meeting /to 4pm
event /from Mon 2pm /to 4pm
event project meeting /from /to 4pm
event project meeting /from Mon 2pm /to
event project meeting /to 4pm /from Mon 2pm
event project meeting /from Mon /from Tue /to 4pm
event project meeting /from 2pm /to 2pm
event project meeting /from 25:00 /to 4pm
event project meeting /from tomorrow /to 4pm
event project meeting /from tomorrow /to 4pm
mark
mark abc
mark 0
mark -1
mark 2.5
mark 999
mark 1
   mark    1
unmark
unmark hello
unmark 0
unmark -1
unmark 2.5
unmark 999
unmark 1
   unmark    1
mark 2
delete
delete 0
delete 999
delete 1
list
bye
```

Actual console output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! ★
____________________________________________________________
Toothless heard a tiny silence. What should he do?
Try todo, deadline, event, list, find, mark, unmark, delete, edit, or bye.
____________________________________________________________
Toothless tilted his head—he doesn’t recognise that command.
Try todo, deadline, event, list, find, mark, unmark, delete, edit, or bye.
____________________________________________________________
The list command doesn't need extra words.
Try: list
____________________________________________________________
Toothless's cave is empty, so there is no task to mark.
Add a task first, then try again.
____________________________________________________________
Toothless's cave is empty, so there is no task to unmark.
Add a task first, then try again.
____________________________________________________________
Toothless couldn’t find a description for that todo.
Try: todo borrow book
____________________________________________________________
Toothless couldn’t find a description for that todo.
Try: todo borrow book
____________________________________________________________
Got it! Toothless has added this task for you:
  [T][ ] read book
Now you have 1 task in the list. ★
____________________________________________________________
Toothless already remembers that exact quest.
Change its details or edit the existing task instead.
____________________________________________________________
This deadline is missing '/by' and its date.
Try: deadline return book /by 2019-12-02
____________________________________________________________
This deadline is missing '/by' and its date.
Try: deadline return book /by 2019-12-02
____________________________________________________________
Toothless couldn’t find a description for that deadline.
Try: deadline return book /by 2019-12-02
____________________________________________________________
This deadline is missing its date.
Try: deadline return book /by 2019-12-02
____________________________________________________________
This deadline's format has Toothless puzzled.
Please use: deadline DESCRIPTION /by yyyy-MM-dd
____________________________________________________________
Got it! Toothless has added this task for you:
  [D][ ] return book (by: Dec 2 2019)
Now you have 2 tasks in the list. ★
____________________________________________________________
Toothless already remembers that exact quest.
Change its details or edit the existing task instead.
____________________________________________________________
This event is missing its starting time after '/from'.
Try: event DESCRIPTION /from START /to END
____________________________________________________________
This event is missing its starting time after '/from'.
Try: event DESCRIPTION /from START /to END
____________________________________________________________
This event is missing its ending time after '/to'.
Try: event DESCRIPTION /from START /to END
____________________________________________________________
This event is missing its starting time after '/from'.
Try: event DESCRIPTION /from START /to END
____________________________________________________________
Toothless couldn’t find a description for that event.
Try: event DESCRIPTION /from START /to END
____________________________________________________________
This event is missing its starting time.
Try: event DESCRIPTION /from START /to END
____________________________________________________________
This event is missing its ending time.
Try: event DESCRIPTION /from START /to END
____________________________________________________________
The event's '/from' must come before '/to'.
Try: event DESCRIPTION /from START /to END
____________________________________________________________
This event's format has Toothless puzzled.
Try: event DESCRIPTION /from START /to END
____________________________________________________________
The event's start must be before its end.
Try: event meeting /from 2pm /to 3pm
____________________________________________________________
That event time made Toothless tilt his head.
Use 2pm, 14:30, or 21 September 2026 2pm with a real date and time.
____________________________________________________________
Got it! Toothless has added this task for you:
  [E][ ] project meeting (from: tomorrow to: 4pm)
Now you have 3 tasks in the list. ★
____________________________________________________________
Toothless already remembers that exact quest.
Change its details or edit the existing task instead.
____________________________________________________________
Toothless needs a task number to mark.
Try: mark 1
____________________________________________________________
That task number looks a little unusual.
Please use a whole number, like: mark 1
____________________________________________________________
Toothless can’t find task 0 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
Toothless can’t find task -1 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
That task number looks a little unusual.
Please use a whole number, like: mark 1
____________________________________________________________
Toothless can’t find task 999 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
A happy little roar! I've starred this task as done:
  [T][★] read book
____________________________________________________________
This task is already starred as done, little rider:
  [T][★] read book
____________________________________________________________
Toothless needs a task number to unmark.
Try: unmark 1
____________________________________________________________
That task number looks a little unusual.
Please use a whole number, like: unmark 1
____________________________________________________________
Toothless can’t find task 0 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
Toothless can’t find task -1 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
That task number looks a little unusual.
Please use a whole number, like: unmark 1
____________________________________________________________
Toothless can’t find task 999 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
All right, little rider! I've unstarred this task for now:
  [T][ ] read book
____________________________________________________________
This task wasn't marked as done before, little rider:
  [T][ ] read book
____________________________________________________________
A happy little roar! I've starred this task as done:
  [D][★] return book (by: Dec 2 2019)
____________________________________________________________
Toothless needs a task number to delete.
Try: delete 1
____________________________________________________________
Toothless can’t find task 0 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
Toothless can’t find task 999 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
A tiny farewell roar! Toothless has removed this task:
  [T][ ] read book
Now you have 2 tasks in the list.
____________________________________________________________
Here are the tasks in your list:
1.[D][★] return book (by: Dec 2 2019)
2.[E][ ] project meeting (from: tomorrow to: 4pm)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________

```

Expected output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! ★
____________________________________________________________
Toothless heard a tiny silence. What should he do?
Try todo, deadline, event, list, find, mark, unmark, delete, edit, or bye.
____________________________________________________________
Toothless tilted his head—he doesn’t recognise that command.
Try todo, deadline, event, list, find, mark, unmark, delete, edit, or bye.
____________________________________________________________
The list command doesn't need extra words.
Try: list
____________________________________________________________
Toothless's cave is empty, so there is no task to mark.
Add a task first, then try again.
____________________________________________________________
Toothless's cave is empty, so there is no task to unmark.
Add a task first, then try again.
____________________________________________________________
Toothless couldn’t find a description for that todo.
Try: todo borrow book
____________________________________________________________
Toothless couldn’t find a description for that todo.
Try: todo borrow book
____________________________________________________________
Got it! Toothless has added this task for you:
  [T][ ] read book
Now you have 1 task in the list. ★
____________________________________________________________
Toothless already remembers that exact quest.
Change its details or edit the existing task instead.
____________________________________________________________
This deadline is missing '/by' and its date.
Try: deadline return book /by 2019-12-02
____________________________________________________________
This deadline is missing '/by' and its date.
Try: deadline return book /by 2019-12-02
____________________________________________________________
Toothless couldn’t find a description for that deadline.
Try: deadline return book /by 2019-12-02
____________________________________________________________
This deadline is missing its date.
Try: deadline return book /by 2019-12-02
____________________________________________________________
This deadline's format has Toothless puzzled.
Please use: deadline DESCRIPTION /by yyyy-MM-dd
____________________________________________________________
Got it! Toothless has added this task for you:
  [D][ ] return book (by: Dec 2 2019)
Now you have 2 tasks in the list. ★
____________________________________________________________
Toothless already remembers that exact quest.
Change its details or edit the existing task instead.
____________________________________________________________
This event is missing its starting time after '/from'.
Try: event DESCRIPTION /from START /to END
____________________________________________________________
This event is missing its starting time after '/from'.
Try: event DESCRIPTION /from START /to END
____________________________________________________________
This event is missing its ending time after '/to'.
Try: event DESCRIPTION /from START /to END
____________________________________________________________
This event is missing its starting time after '/from'.
Try: event DESCRIPTION /from START /to END
____________________________________________________________
Toothless couldn’t find a description for that event.
Try: event DESCRIPTION /from START /to END
____________________________________________________________
This event is missing its starting time.
Try: event DESCRIPTION /from START /to END
____________________________________________________________
This event is missing its ending time.
Try: event DESCRIPTION /from START /to END
____________________________________________________________
The event's '/from' must come before '/to'.
Try: event DESCRIPTION /from START /to END
____________________________________________________________
This event's format has Toothless puzzled.
Try: event DESCRIPTION /from START /to END
____________________________________________________________
The event's start must be before its end.
Try: event meeting /from 2pm /to 3pm
____________________________________________________________
That event time made Toothless tilt his head.
Use 2pm, 14:30, or 21 September 2026 2pm with a real date and time.
____________________________________________________________
Got it! Toothless has added this task for you:
  [E][ ] project meeting (from: tomorrow to: 4pm)
Now you have 3 tasks in the list. ★
____________________________________________________________
Toothless already remembers that exact quest.
Change its details or edit the existing task instead.
____________________________________________________________
Toothless needs a task number to mark.
Try: mark 1
____________________________________________________________
That task number looks a little unusual.
Please use a whole number, like: mark 1
____________________________________________________________
Toothless can’t find task 0 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
Toothless can’t find task -1 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
That task number looks a little unusual.
Please use a whole number, like: mark 1
____________________________________________________________
Toothless can’t find task 999 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
A happy little roar! I've starred this task as done:
  [T][★] read book
____________________________________________________________
This task is already starred as done, little rider:
  [T][★] read book
____________________________________________________________
Toothless needs a task number to unmark.
Try: unmark 1
____________________________________________________________
That task number looks a little unusual.
Please use a whole number, like: unmark 1
____________________________________________________________
Toothless can’t find task 0 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
Toothless can’t find task -1 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
That task number looks a little unusual.
Please use a whole number, like: unmark 1
____________________________________________________________
Toothless can’t find task 999 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
All right, little rider! I've unstarred this task for now:
  [T][ ] read book
____________________________________________________________
This task wasn't marked as done before, little rider:
  [T][ ] read book
____________________________________________________________
A happy little roar! I've starred this task as done:
  [D][★] return book (by: Dec 2 2019)
____________________________________________________________
Toothless needs a task number to delete.
Try: delete 1
____________________________________________________________
Toothless can’t find task 0 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
Toothless can’t find task 999 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
A tiny farewell roar! Toothless has removed this task:
  [T][ ] read book
Now you have 2 tasks in the list.
____________________________________________________________
Here are the tasks in your list:
1.[D][★] return book (by: Dec 2 2019)
2.[E][ ] project meeting (from: tomorrow to: 4pm)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Exit code: `0` (expected `0`)

## TC-09: Edit task fields without changing task identity or state — PASS

Aim: Verify Todo, Deadline, and Event edits; multiple and omitted fields; one-based task-number errors; empty,

Command:

```text
/bin/zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && ./gradlew -q classes && TOOTHLESS_TEST_ROOT=$(mktemp -d) && TOOTHLESS_CLASSES="$PWD/build/classes/java/main:$PWD/build/resources/main" && cd "$TOOTHLESS_TEST_ROOT" && java -Dos.name="Mac OS X" -cp "$TOOTHLESS_CLASSES" toothless.Toothless'
```

Console input:

```text
todo old todo
deadline old deadline /by 2026-09-20
event old event /from 21 September 2026 2pm /to 21 September 2026 3pm
mark 2
edit
edit /description changed
edit first /description changed
edit 0 /description changed
edit 4 /description changed
edit 1
edit 1 /description
edit 2 /by 2026-02-30
edit 1 /by 2026-09-21
edit 2 /from 4pm
edit 3 /by 2026-09-21
edit 1 /when tomorrow
edit 1 /description first /description second
edit 1 change /description changed
edit 1 /description read the new textbook
edit 2 /by 2026-09-21
edit 3 /to 21 September 2026 5pm
edit 3 /description project consultation /from 21 September 2026 4pm /to 21 September 2026 6pm
list
bye
```

Actual console output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! ★
____________________________________________________________
Got it! Toothless has added this task for you:
  [T][ ] old todo
Now you have 1 task in the list. ★
____________________________________________________________
Got it! Toothless has added this task for you:
  [D][ ] old deadline (by: Sep 20 2026)
Now you have 2 tasks in the list. ★
____________________________________________________________
Got it! Toothless has added this task for you:
  [E][ ] old event (from: 21 September 2026 2pm to: 21 September 2026 3pm)
Now you have 3 tasks in the list. ★
____________________________________________________________
A happy little roar! I've starred this task as done:
  [D][★] old deadline (by: Sep 20 2026)
____________________________________________________________
Toothless needs a task number to edit.
Try: edit 1
____________________________________________________________
Toothless needs a task number to edit.
Try: edit 1
____________________________________________________________
That task number looks a little unusual.
Please use a whole number, like: edit 1
____________________________________________________________
Toothless can’t find task 0 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
Toothless can’t find task 4 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
Toothless needs at least one field to edit.
Try: edit 1 /description read the new textbook
____________________________________________________________
This edit is missing a value after '/description'.
Please add the new value and try again.
____________________________________________________________
That edited deadline date made Toothless tilt his head.
Please use a real date in yyyy-MM-dd format.
____________________________________________________________
Only deadline tasks have a '/by' date to edit.
____________________________________________________________
Only event tasks have '/from' or '/to' times to edit.
____________________________________________________________
Only deadline tasks have a '/by' date to edit.
____________________________________________________________
This edit's format has Toothless puzzled.
Use: edit TASK_NUMBER /FIELD NEW_VALUE
____________________________________________________________
Each edit field can appear only once.
Try: edit 1 /description read the new textbook
____________________________________________________________
This edit's format has Toothless puzzled.
Use: edit TASK_NUMBER /FIELD NEW_VALUE
____________________________________________________________
A clever little roar! Toothless has updated this task:
  [T][ ] read the new textbook
____________________________________________________________
A clever little roar! Toothless has updated this task:
  [D][★] old deadline (by: Sep 21 2026)
____________________________________________________________
A clever little roar! Toothless has updated this task:
  [E][ ] old event (from: 21 September 2026 2pm to: 21 September 2026 5pm)
____________________________________________________________
A clever little roar! Toothless has updated this task:
  [E][ ] project consultation (from: 21 September 2026 4pm to: 21 September 2026 6pm)
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read the new textbook
2.[D][★] old deadline (by: Sep 21 2026)
3.[E][ ] project consultation (from: 21 September 2026 4pm to: 21 September 2026 6pm)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________

```

Expected output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! ★
____________________________________________________________
Got it! Toothless has added this task for you:
  [T][ ] old todo
Now you have 1 task in the list. ★
____________________________________________________________
Got it! Toothless has added this task for you:
  [D][ ] old deadline (by: Sep 20 2026)
Now you have 2 tasks in the list. ★
____________________________________________________________
Got it! Toothless has added this task for you:
  [E][ ] old event (from: 21 September 2026 2pm to: 21 September 2026 3pm)
Now you have 3 tasks in the list. ★
____________________________________________________________
A happy little roar! I've starred this task as done:
  [D][★] old deadline (by: Sep 20 2026)
____________________________________________________________
Toothless needs a task number to edit.
Try: edit 1
____________________________________________________________
Toothless needs a task number to edit.
Try: edit 1
____________________________________________________________
That task number looks a little unusual.
Please use a whole number, like: edit 1
____________________________________________________________
Toothless can’t find task 0 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
Toothless can’t find task 4 in the cave.
Please choose a number from 1 to 3.
____________________________________________________________
Toothless needs at least one field to edit.
Try: edit 1 /description read the new textbook
____________________________________________________________
This edit is missing a value after '/description'.
Please add the new value and try again.
____________________________________________________________
That edited deadline date made Toothless tilt his head.
Please use a real date in yyyy-MM-dd format.
____________________________________________________________
Only deadline tasks have a '/by' date to edit.
____________________________________________________________
Only event tasks have '/from' or '/to' times to edit.
____________________________________________________________
Only deadline tasks have a '/by' date to edit.
____________________________________________________________
This edit's format has Toothless puzzled.
Use: edit TASK_NUMBER /FIELD NEW_VALUE
____________________________________________________________
Each edit field can appear only once.
Try: edit 1 /description read the new textbook
____________________________________________________________
This edit's format has Toothless puzzled.
Use: edit TASK_NUMBER /FIELD NEW_VALUE
____________________________________________________________
A clever little roar! Toothless has updated this task:
  [T][ ] read the new textbook
____________________________________________________________
A clever little roar! Toothless has updated this task:
  [D][★] old deadline (by: Sep 21 2026)
____________________________________________________________
A clever little roar! Toothless has updated this task:
  [E][ ] old event (from: 21 September 2026 2pm to: 21 September 2026 5pm)
____________________________________________________________
A clever little roar! Toothless has updated this task:
  [E][ ] project consultation (from: 21 September 2026 4pm to: 21 September 2026 6pm)
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read the new textbook
2.[D][★] old deadline (by: Sep 21 2026)
3.[E][ ] project consultation (from: 21 September 2026 4pm to: 21 September 2026 6pm)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Exit code: `0` (expected `0`)

## TC-02: Validate deadline dates without changing task state — PASS

Aim: Verify missing, incorrectly formatted, impossible, non-leap, and malformed dates are rejected without stack traces or task-list changes; leap-day and whitespace-surrounded dates remain usable through listing, marking, unmarking, and deletion.

Command:

```text
/bin/zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && ./gradlew -q classes && TOOTHLESS_TEST_ROOT=$(mktemp -d) && TOOTHLESS_CLASSES="$PWD/build/classes/java/main:$PWD/build/resources/main" && cd "$TOOTHLESS_TEST_ROOT" && java -Dos.name="Mac OS X" -cp "$TOOTHLESS_CLASSES" toothless.Toothless'
```

Console input:

```text
deadline no separator
deadline no date /by
deadline wrong format /by 02-12-2019
deadline impossible /by 2019-02-30
deadline extra content /by 2019-12-02 evening
deadline extra separator /by 2019-12-02 /to evening
deadline leap day /by 2020-02-29
deadline non-leap day /by 2019-02-29
deadline    spaced date    /by    2019-12-02
list
mark 1
unmark 1
mark 2
unmark 2
delete 1
list
bye
```

Actual console output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! ★
____________________________________________________________
This deadline is missing '/by' and its date.
Try: deadline return book /by 2019-12-02
____________________________________________________________
This deadline is missing its date.
Try: deadline return book /by 2019-12-02
____________________________________________________________
That deadline date made Toothless tilt his head.
Please use a real date in yyyy-MM-dd format.
____________________________________________________________
That deadline date made Toothless tilt his head.
Please use a real date in yyyy-MM-dd format.
____________________________________________________________
That deadline date made Toothless tilt his head.
Please use a real date in yyyy-MM-dd format.
____________________________________________________________
This deadline's format has Toothless puzzled.
Please use: deadline DESCRIPTION /by yyyy-MM-dd
____________________________________________________________
Got it! Toothless has added this task for you:
  [D][ ] leap day (by: Feb 29 2020)
Now you have 1 task in the list. ★
____________________________________________________________
That deadline date made Toothless tilt his head.
Please use a real date in yyyy-MM-dd format.
____________________________________________________________
Got it! Toothless has added this task for you:
  [D][ ] spaced date (by: Dec 2 2019)
Now you have 2 tasks in the list. ★
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] leap day (by: Feb 29 2020)
2.[D][ ] spaced date (by: Dec 2 2019)
____________________________________________________________
A happy little roar! I've starred this task as done:
  [D][★] leap day (by: Feb 29 2020)
____________________________________________________________
All right, little rider! I've unstarred this task for now:
  [D][ ] leap day (by: Feb 29 2020)
____________________________________________________________
A happy little roar! I've starred this task as done:
  [D][★] spaced date (by: Dec 2 2019)
____________________________________________________________
All right, little rider! I've unstarred this task for now:
  [D][ ] spaced date (by: Dec 2 2019)
____________________________________________________________
A tiny farewell roar! Toothless has removed this task:
  [D][ ] leap day (by: Feb 29 2020)
Now you have 1 task in the list.
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] spaced date (by: Dec 2 2019)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________

```

Expected output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! ★
____________________________________________________________
This deadline is missing '/by' and its date.
Try: deadline return book /by 2019-12-02
____________________________________________________________
This deadline is missing its date.
Try: deadline return book /by 2019-12-02
____________________________________________________________
That deadline date made Toothless tilt his head.
Please use a real date in yyyy-MM-dd format.
____________________________________________________________
That deadline date made Toothless tilt his head.
Please use a real date in yyyy-MM-dd format.
____________________________________________________________
That deadline date made Toothless tilt his head.
Please use a real date in yyyy-MM-dd format.
____________________________________________________________
This deadline's format has Toothless puzzled.
Please use: deadline DESCRIPTION /by yyyy-MM-dd
____________________________________________________________
Got it! Toothless has added this task for you:
  [D][ ] leap day (by: Feb 29 2020)
Now you have 1 task in the list. ★
____________________________________________________________
That deadline date made Toothless tilt his head.
Please use a real date in yyyy-MM-dd format.
____________________________________________________________
Got it! Toothless has added this task for you:
  [D][ ] spaced date (by: Dec 2 2019)
Now you have 2 tasks in the list. ★
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] leap day (by: Feb 29 2020)
2.[D][ ] spaced date (by: Dec 2 2019)
____________________________________________________________
A happy little roar! I've starred this task as done:
  [D][★] leap day (by: Feb 29 2020)
____________________________________________________________
All right, little rider! I've unstarred this task for now:
  [D][ ] leap day (by: Feb 29 2020)
____________________________________________________________
A happy little roar! I've starred this task as done:
  [D][★] spaced date (by: Dec 2 2019)
____________________________________________________________
All right, little rider! I've unstarred this task for now:
  [D][ ] spaced date (by: Dec 2 2019)
____________________________________________________________
A tiny farewell roar! Toothless has removed this task:
  [D][ ] leap day (by: Feb 29 2020)
Now you have 1 task in the list.
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] spaced date (by: Dec 2 2019)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Exit code: `0` (expected `0`)

## TC-03: Delete tasks safely from an ArrayList — PASS

Aim: Verify deletion from an empty list; invalid, missing, decimal, negative, zero, out-of-range, and extra arguments; deletion of first, middle, and last Todo, Deadline, and Event tasks; completed-task deletion; re-numbering; order; singular/plural counts; multiple deletions; and other commands after deletion.

Command:

```text
/bin/zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && ./gradlew -q classes && TOOTHLESS_TEST_ROOT=$(mktemp -d) && TOOTHLESS_CLASSES="$PWD/build/classes/java/main:$PWD/build/resources/main" && cd "$TOOTHLESS_TEST_ROOT" && java -Dos.name="Mac OS X" -cp "$TOOTHLESS_CLASSES" toothless.Toothless'
```

Console input:

```text
delete
todo first task
deadline middle deadline /by 2019-12-06
event last event /from 2pm /to 3pm
todo tail task
mark 2
delete
delete abc
delete 1.5
delete 0
delete -1
delete 99
delete 2 extra
list
   delete    2
list
delete 1
delete 2
list
mark 1
delete 1
list
delete 1
todo new task
delete 1
bye
```

Actual console output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! ★
____________________________________________________________
Toothless's cave is empty, so there is no task to delete.
Add a task first, then try again.
____________________________________________________________
Got it! Toothless has added this task for you:
  [T][ ] first task
Now you have 1 task in the list. ★
____________________________________________________________
Got it! Toothless has added this task for you:
  [D][ ] middle deadline (by: Dec 6 2019)
Now you have 2 tasks in the list. ★
____________________________________________________________
Got it! Toothless has added this task for you:
  [E][ ] last event (from: 2pm to: 3pm)
Now you have 3 tasks in the list. ★
____________________________________________________________
Got it! Toothless has added this task for you:
  [T][ ] tail task
Now you have 4 tasks in the list. ★
____________________________________________________________
A happy little roar! I've starred this task as done:
  [D][★] middle deadline (by: Dec 6 2019)
____________________________________________________________
Toothless needs a task number to delete.
Try: delete 1
____________________________________________________________
That task number looks a little unusual.
Please use a whole number, like: delete 1
____________________________________________________________
That task number looks a little unusual.
Please use a whole number, like: delete 1
____________________________________________________________
Toothless can’t find task 0 in the cave.
Please choose a number from 1 to 4.
____________________________________________________________
Toothless can’t find task -1 in the cave.
Please choose a number from 1 to 4.
____________________________________________________________
Toothless can’t find task 99 in the cave.
Please choose a number from 1 to 4.
____________________________________________________________
That task number looks a little unusual.
Please use a whole number, like: delete 1
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] first task
2.[D][★] middle deadline (by: Dec 6 2019)
3.[E][ ] last event (from: 2pm to: 3pm)
4.[T][ ] tail task
____________________________________________________________
A tiny farewell roar! Toothless has removed this task:
  [D][★] middle deadline (by: Dec 6 2019)
Now you have 3 tasks in the list.
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] first task
2.[E][ ] last event (from: 2pm to: 3pm)
3.[T][ ] tail task
____________________________________________________________
A tiny farewell roar! Toothless has removed this task:
  [T][ ] first task
Now you have 2 tasks in the list.
____________________________________________________________
A tiny farewell roar! Toothless has removed this task:
  [T][ ] tail task
Now you have 1 task in the list.
____________________________________________________________
Here are the tasks in your list:
1.[E][ ] last event (from: 2pm to: 3pm)
____________________________________________________________
A happy little roar! I've starred this task as done:
  [E][★] last event (from: 2pm to: 3pm)
____________________________________________________________
A tiny farewell roar! Toothless has removed this task:
  [E][★] last event (from: 2pm to: 3pm)
Now you have 0 tasks in the list.
____________________________________________________________
Your task list is empty. Ready for a new adventure!
____________________________________________________________
Toothless's cave is empty, so there is no task to delete.
Add a task first, then try again.
____________________________________________________________
Got it! Toothless has added this task for you:
  [T][ ] new task
Now you have 1 task in the list. ★
____________________________________________________________
A tiny farewell roar! Toothless has removed this task:
  [T][ ] new task
Now you have 0 tasks in the list.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________

```

Expected output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! ★
____________________________________________________________
Toothless's cave is empty, so there is no task to delete.
Add a task first, then try again.
____________________________________________________________
Got it! Toothless has added this task for you:
  [T][ ] first task
Now you have 1 task in the list. ★
____________________________________________________________
Got it! Toothless has added this task for you:
  [D][ ] middle deadline (by: Dec 6 2019)
Now you have 2 tasks in the list. ★
____________________________________________________________
Got it! Toothless has added this task for you:
  [E][ ] last event (from: 2pm to: 3pm)
Now you have 3 tasks in the list. ★
____________________________________________________________
Got it! Toothless has added this task for you:
  [T][ ] tail task
Now you have 4 tasks in the list. ★
____________________________________________________________
A happy little roar! I've starred this task as done:
  [D][★] middle deadline (by: Dec 6 2019)
____________________________________________________________
Toothless needs a task number to delete.
Try: delete 1
____________________________________________________________
That task number looks a little unusual.
Please use a whole number, like: delete 1
____________________________________________________________
That task number looks a little unusual.
Please use a whole number, like: delete 1
____________________________________________________________
Toothless can’t find task 0 in the cave.
Please choose a number from 1 to 4.
____________________________________________________________
Toothless can’t find task -1 in the cave.
Please choose a number from 1 to 4.
____________________________________________________________
Toothless can’t find task 99 in the cave.
Please choose a number from 1 to 4.
____________________________________________________________
That task number looks a little unusual.
Please use a whole number, like: delete 1
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] first task
2.[D][★] middle deadline (by: Dec 6 2019)
3.[E][ ] last event (from: 2pm to: 3pm)
4.[T][ ] tail task
____________________________________________________________
A tiny farewell roar! Toothless has removed this task:
  [D][★] middle deadline (by: Dec 6 2019)
Now you have 3 tasks in the list.
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] first task
2.[E][ ] last event (from: 2pm to: 3pm)
3.[T][ ] tail task
____________________________________________________________
A tiny farewell roar! Toothless has removed this task:
  [T][ ] first task
Now you have 2 tasks in the list.
____________________________________________________________
A tiny farewell roar! Toothless has removed this task:
  [T][ ] tail task
Now you have 1 task in the list.
____________________________________________________________
Here are the tasks in your list:
1.[E][ ] last event (from: 2pm to: 3pm)
____________________________________________________________
A happy little roar! I've starred this task as done:
  [E][★] last event (from: 2pm to: 3pm)
____________________________________________________________
A tiny farewell roar! Toothless has removed this task:
  [E][★] last event (from: 2pm to: 3pm)
Now you have 0 tasks in the list.
____________________________________________________________
Your task list is empty. Ready for a new adventure!
____________________________________________________________
Toothless's cave is empty, so there is no task to delete.
Add a task first, then try again.
____________________________________________________________
Got it! Toothless has added this task for you:
  [T][ ] new task
Now you have 1 task in the list. ★
____________________________________________________________
A tiny farewell roar! Toothless has removed this task:
  [T][ ] new task
Now you have 0 tasks in the list.
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Exit code: `0` (expected `0`)

## TC-04: Load valid saved tasks at startup — PASS

Aim: Verify startup restores every task type, its date or time values, and the completed-task display.

Command:

```text
/bin/zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && ./gradlew -q classes && TOOTHLESS_TEST_ROOT=$(mktemp -d) && TOOTHLESS_CLASSES="$PWD/build/classes/java/main:$PWD/build/resources/main" && mkdir "$TOOTHLESS_TEST_ROOT/data" && printf "%s\n" "T | 1 | borrow book" "D | 0 | return book | 2019-12-02" "E | 1 | project meeting | Monday 2pm | Monday 3pm" > "$TOOTHLESS_TEST_ROOT/data/toothless.txt" && cd "$TOOTHLESS_TEST_ROOT" && java -Dos.name="Mac OS X" -cp "$TOOTHLESS_CLASSES" toothless.Toothless'
```

Console input:

```text
list
bye
```

Actual console output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! ★
____________________________________________________________
Here are the tasks in your list:
1.[T][★] borrow book
2.[D][ ] return book (by: Dec 2 2019)
3.[E][★] project meeting (from: Monday 2pm to: Monday 3pm)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________

```

Expected output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! ★
____________________________________________________________
Here are the tasks in your list:
1.[T][★] borrow book
2.[D][ ] return book (by: Dec 2 2019)
3.[E][★] project meeting (from: Monday 2pm to: Monday 3pm)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Exit code: `0` (expected `0`)

## TC-05: Keep tasks usable after storage failures — PASS

Aim: Verify failed reads and writes show friendly messages without a stack trace and do not publish unsaved task changes.

Command:

```text
/bin/zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && ./gradlew -q classes && TOOTHLESS_TEST_ROOT=$(mktemp -d) && TOOTHLESS_CLASSES="$PWD/build/classes/java/main:$PWD/build/resources/main" && mkdir -p "$TOOTHLESS_TEST_ROOT/data/toothless.txt" && cd "$TOOTHLESS_TEST_ROOT" && java -Dos.name="Mac OS X" -cp "$TOOTHLESS_CLASSES" toothless.Toothless'
```

Console input:

```text
list
todo keep this task
list
bye
```

Actual console output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! ★
____________________________________________________________
Toothless had trouble reading his saved quests.
He'll start with an empty cave, but the saved file was left untouched.
Saved changes are paused until the file can be read.
____________________________________________________________
Your task list is empty. Ready for a new adventure!
____________________________________________________________
Toothless couldn’t tuck these changes into his data file.
Nothing changed. Check the saved data or file access, then try again.
____________________________________________________________
Your task list is empty. Ready for a new adventure!
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________

```

Expected output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! ★
____________________________________________________________
Toothless had trouble reading his saved quests.
He'll start with an empty cave, but the saved file was left untouched.
Saved changes are paused until the file can be read.
____________________________________________________________
Your task list is empty. Ready for a new adventure!
____________________________________________________________
Toothless couldn’t tuck these changes into his data file.
Nothing changed. Check the saved data or file access, then try again.
____________________________________________________________
Your task list is empty. Ready for a new adventure!
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Exit code: `0` (expected `0`)

## TC-06: Skip malformed saved tasks safely — PASS

Aim: Verify malformed saved entries produce one friendly warning while valid entries remain readable and later writes cannot overwrite the original file.

Command:

```text
/bin/zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && ./gradlew -q classes && TOOTHLESS_TEST_ROOT=$(mktemp -d) && TOOTHLESS_CLASSES="$PWD/build/classes/java/main:$PWD/build/resources/main" && mkdir "$TOOTHLESS_TEST_ROOT/data" && printf "%s\n" "T | 1 | borrow book" "X | 0 | unknown type" "D | 0 | missing time" "T | maybe | invalid status" "E | 0 | truncated event | 2pm" "" "T | 0 | unexpected data | extra field" "D | 0 | return book | 2019-12-06" > "$TOOTHLESS_TEST_ROOT/data/toothless.txt" && cd "$TOOTHLESS_TEST_ROOT" && java -Dos.name="Mac OS X" -cp "$TOOTHLESS_CLASSES" toothless.Toothless'
```

Console input:

```text
list
todo new task
list
bye
```

Actual console output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! ★
____________________________________________________________
Toothless found 6 puzzling lines in his saved quests.
He skipped them and kept every task he could understand.
Saved changes are paused until the file is repaired; your data stays untouched.
____________________________________________________________
Here are the tasks in your list:
1.[T][★] borrow book
2.[D][ ] return book (by: Dec 6 2019)
____________________________________________________________
Toothless couldn’t tuck these changes into his data file.
Nothing changed. Check the saved data or file access, then try again.
____________________________________________________________
Here are the tasks in your list:
1.[T][★] borrow book
2.[D][ ] return book (by: Dec 6 2019)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________

```

Expected output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! ★
____________________________________________________________
Toothless found 6 puzzling lines in his saved quests.
He skipped them and kept every task he could understand.
Saved changes are paused until the file is repaired; your data stays untouched.
____________________________________________________________
Here are the tasks in your list:
1.[T][★] borrow book
2.[D][ ] return book (by: Dec 6 2019)
____________________________________________________________
Toothless couldn’t tuck these changes into his data file.
Nothing changed. Check the saved data or file access, then try again.
____________________________________________________________
Here are the tasks in your list:
1.[T][★] borrow book
2.[D][ ] return book (by: Dec 6 2019)
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Exit code: `0` (expected `0`)

## TC-07: Find tasks by description without changing state — PASS

Aim: Verify missing and blank keywords, one and multiple matches, no matches, case-insensitive partial and multi-word searches, description-only matching, fresh ordered result numbering, unchanged completion state, and existing list behavior.

Command:

```text
/bin/zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && ./gradlew -q classes && TOOTHLESS_TEST_ROOT=$(mktemp -d) && TOOTHLESS_CLASSES="$PWD/build/classes/java/main:$PWD/build/resources/main" && cd "$TOOTHLESS_TEST_ROOT" && java -Dos.name="Mac OS X" -cp "$TOOTHLESS_CLASSES" toothless.Toothless'
```

Console input:

```text
find
   find
todo read book
deadline return book /by 2019-12-06
event book club /from Monday 2pm /to Monday 3pm
todo notebook ideas
mark 2
find Book
find monday
find read bo
find 2019
find book club
find [D]
find ★
find dragon
list
bye
```

Actual console output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! ★
____________________________________________________________
Toothless needs a keyword to sniff out matching tasks.
Try: find book
____________________________________________________________
Toothless needs a keyword to sniff out matching tasks.
Try: find book
____________________________________________________________
Got it! Toothless has added this task for you:
  [T][ ] read book
Now you have 1 task in the list. ★
____________________________________________________________
Got it! Toothless has added this task for you:
  [D][ ] return book (by: Dec 6 2019)
Now you have 2 tasks in the list. ★
____________________________________________________________
Got it! Toothless has added this task for you:
  [E][ ] book club (from: Monday 2pm to: Monday 3pm)
Now you have 3 tasks in the list. ★
____________________________________________________________
Got it! Toothless has added this task for you:
  [T][ ] notebook ideas
Now you have 4 tasks in the list. ★
____________________________________________________________
A happy little roar! I've starred this task as done:
  [D][★] return book (by: Dec 6 2019)
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][★] return book (by: Dec 6 2019)
3.[E][ ] book club (from: Monday 2pm to: Monday 3pm)
4.[T][ ] notebook ideas
____________________________________________________________
Toothless couldn’t find any matching tasks in the cave.
Try another keyword and he'll sniff around again!
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] read book
____________________________________________________________
Toothless couldn’t find any matching tasks in the cave.
Try another keyword and he'll sniff around again!
____________________________________________________________
Here are the matching tasks in your list:
1.[E][ ] book club (from: Monday 2pm to: Monday 3pm)
____________________________________________________________
Toothless couldn’t find any matching tasks in the cave.
Try another keyword and he'll sniff around again!
____________________________________________________________
Toothless couldn’t find any matching tasks in the cave.
Try another keyword and he'll sniff around again!
____________________________________________________________
Toothless couldn’t find any matching tasks in the cave.
Try another keyword and he'll sniff around again!
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][★] return book (by: Dec 6 2019)
3.[E][ ] book club (from: Monday 2pm to: Monday 3pm)
4.[T][ ] notebook ideas
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________

```

Expected output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! ★
____________________________________________________________
Toothless needs a keyword to sniff out matching tasks.
Try: find book
____________________________________________________________
Toothless needs a keyword to sniff out matching tasks.
Try: find book
____________________________________________________________
Got it! Toothless has added this task for you:
  [T][ ] read book
Now you have 1 task in the list. ★
____________________________________________________________
Got it! Toothless has added this task for you:
  [D][ ] return book (by: Dec 6 2019)
Now you have 2 tasks in the list. ★
____________________________________________________________
Got it! Toothless has added this task for you:
  [E][ ] book club (from: Monday 2pm to: Monday 3pm)
Now you have 3 tasks in the list. ★
____________________________________________________________
Got it! Toothless has added this task for you:
  [T][ ] notebook ideas
Now you have 4 tasks in the list. ★
____________________________________________________________
A happy little roar! I've starred this task as done:
  [D][★] return book (by: Dec 6 2019)
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][★] return book (by: Dec 6 2019)
3.[E][ ] book club (from: Monday 2pm to: Monday 3pm)
4.[T][ ] notebook ideas
____________________________________________________________
Toothless couldn’t find any matching tasks in the cave.
Try another keyword and he'll sniff around again!
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] read book
____________________________________________________________
Toothless couldn’t find any matching tasks in the cave.
Try another keyword and he'll sniff around again!
____________________________________________________________
Here are the matching tasks in your list:
1.[E][ ] book club (from: Monday 2pm to: Monday 3pm)
____________________________________________________________
Toothless couldn’t find any matching tasks in the cave.
Try another keyword and he'll sniff around again!
____________________________________________________________
Toothless couldn’t find any matching tasks in the cave.
Try another keyword and he'll sniff around again!
____________________________________________________________
Toothless couldn’t find any matching tasks in the cave.
Try another keyword and he'll sniff around again!
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][★] return book (by: Dec 6 2019)
3.[E][ ] book club (from: Monday 2pm to: Monday 3pm)
4.[T][ ] notebook ideas
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Exit code: `0` (expected `0`)

## TC-08: Use ASCII display symbols on Windows — PASS

Aim: Verify Windows users see an ASCII completion marker and decoration instead of unsupported stars.

Command:

```text
/bin/zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && ./gradlew -q classes && TOOTHLESS_TEST_ROOT=$(mktemp -d) && TOOTHLESS_CLASSES="$PWD/build/classes/java/main:$PWD/build/resources/main" && cd "$TOOTHLESS_TEST_ROOT" && java -Dos.name="Windows 11" -cp "$TOOTHLESS_CLASSES" toothless.Toothless'
```

Console input:

```text
todo windows task
mark 1
list
bye
```

Actual console output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! *
____________________________________________________________
Got it! Toothless has added this task for you:
  [T][ ] windows task
Now you have 1 task in the list. *
____________________________________________________________
A happy little roar! I've starred this task as done:
  [T][X] windows task
____________________________________________________________
Here are the tasks in your list:
1.[T][X] windows task
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________

```

Expected output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! *
____________________________________________________________
Got it! Toothless has added this task for you:
  [T][ ] windows task
Now you have 1 task in the list. *
____________________________________________________________
A happy little roar! I've starred this task as done:
  [T][X] windows task
____________________________________________________________
Here are the tasks in your list:
1.[T][X] windows task
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Exit code: `0` (expected `0`)

## TC-10: Accept uppercase bye with leading whitespace — PASS

Aim: Verify the farewell command is recognized after trimming leading whitespace and without regard to capitalization.

Command:

```text
/bin/zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && ./gradlew -q classes && TOOTHLESS_TEST_ROOT=$(mktemp -d) && TOOTHLESS_CLASSES="$PWD/build/classes/java/main:$PWD/build/resources/main" && cd "$TOOTHLESS_TEST_ROOT" && java -Dos.name="Mac OS X" -cp "$TOOTHLESS_CLASSES" toothless.Toothless'
```

Console input:

```text
  BYE
```

Actual console output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! ★
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________

```

Expected output:

```text
____________________________________________________________
  __/\__           __/\__
 /     \_________/     \
/   /\   O     O   /\   \
\__/  \     ^     /  \__/
       \  \___/  /
    ____|       |____
 __/    |       |    \__
/___/   /|_______|\   \___\
        /_/     \_\

Hi there! I'm Toothless. It's wonderful to meet you!
What can I do for you today?
Ready for our next little adventure? Tell me what to remember:
  - todo [DESCRIPTION]
  - deadline [DESCRIPTION] /by [yyyy-MM-dd]
  - event [DESCRIPTION] /from [START_DATE_OR_TIME] /to [END_DATE_OR_TIME]
  - edit [TASK_NUMBER] [/description NEW_DESCRIPTION] [/by yyyy-MM-dd] [/from START] [/to END]
You can also type list to see all our quests. Tiny roar! ★
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

Exit code: `0` (expected `0`)
