# UI test plan

## Test case: Delete multiple tasks atomically

**Aim:** Verifies that one command deletes several original task positions in list order and that a duplicate
selection rejects the entire command.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/Tony.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
todo read book
deadline submit report /by 2099-09-20
event orientation /from 2099-09-21 /to 2099-09-22
todo buy groceries
delete 4 2
list
delete 1 1
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
Good day, Chief. What shall I arrange for you?
________________________________________________
________________________________________________
Certainly, Chief. I've added this item to the agenda:
  [T][ ] read book
The agenda now contains 1 task.
________________________________________________
________________________________________________
Consider it scheduled, Chief. I'll keep watch over this deadline:
  [D][ ] submit report (by: Sep 20 2099)
The agenda now contains 2 tasks.
________________________________________________
________________________________________________
Your calendar is updated, Chief. I've arranged this event:
  [E][ ] orientation (from: Sep 21 2099 to: Sep 22 2099)
The agenda now contains 3 tasks.
________________________________________________
________________________________________________
Certainly, Chief. I've added this item to the agenda:
  [T][ ] buy groceries
The agenda now contains 4 tasks.
________________________________________________
________________________________________________
As requested, Chief. I've removed these matters:
  [D][ ] submit report (by: Sep 20 2099)
  [T][ ] buy groceries
The agenda now contains 2 tasks.
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][ ] read book
2.[E][ ] orientation (from: Sep 21 2099 to: Sep 22 2099)
________________________________________________
________________________________________________
My apologies, Chief. Please give me each task number only once.
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][ ] read book
2.[E][ ] orientation (from: Sep 21 2099 to: Sep 22 2099)
________________________________________________
________________________________________________
The office is in order, Chief. Enjoy your evening.
________________________________________________
```

## Test case: Normalize input and reject invalid task data

**Aim:** Verifies that extra whitespace is accepted while duplicate tasks, repeated parameters, impossible dates,
and event ranges without a later end date are rejected without changing the agenda.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/Tony.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
  todo   Read   Book
todo read book
deadline report /by 2099-02-30
deadline report /by 2099-09-20 /by 2099-09-21
event meeting /from 2099-09-20 /to 2099-09-20
deadline archived report /by 2000-01-01
event archived meeting /from 2000-01-01 /to 2099-01-02
list
  bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
Good day, Chief. What shall I arrange for you?
________________________________________________
________________________________________________
Certainly, Chief. I've added this item to the agenda:
  [T][ ] Read Book
The agenda now contains 1 task.
________________________________________________
________________________________________________
My apologies, Chief. That matter is already on the agenda.
________________________________________________
________________________________________________
My apologies, Chief. Please give me dates as yyyy-MM-dd, for example 2019-10-15.
________________________________________________
________________________________________________
My apologies, Chief. Please specify /by only once.
________________________________________________
________________________________________________
My apologies, Chief. I need the event's end date to be after its start date.
________________________________________________
________________________________________________
My apologies, Chief. I cannot schedule a deadline before today.
________________________________________________
________________________________________________
My apologies, Chief. I cannot schedule an event before today.
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][ ] Read Book
________________________________________________
________________________________________________
The office is in order, Chief. Enjoy your evening.
________________________________________________
```

## Test case: Add and list all task types

**Aim:** Verifies that to-dos, deadlines, and events are stored and displayed with their type, status, and formatted dates.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/Tony.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
todo borrow book
deadline return book /by 2099-10-15
event project meeting /from 2099-10-16 /to 2099-10-17
mark 1
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
Good day, Chief. What shall I arrange for you?
________________________________________________
________________________________________________
Certainly, Chief. I've added this item to the agenda:
  [T][ ] borrow book
The agenda now contains 1 task.
________________________________________________
________________________________________________
Consider it scheduled, Chief. I'll keep watch over this deadline:
  [D][ ] return book (by: Oct 15 2099)
The agenda now contains 2 tasks.
________________________________________________
________________________________________________
Your calendar is updated, Chief. I've arranged this event:
  [E][ ] project meeting (from: Oct 16 2099 to: Oct 17 2099)
The agenda now contains 3 tasks.
________________________________________________
________________________________________________
Excellent, Chief. I've recorded this matter as complete:
  [T][X] borrow book
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][X] borrow book
2.[D][ ] return book (by: Oct 15 2099)
3.[E][ ] project meeting (from: Oct 16 2099 to: Oct 17 2099)
________________________________________________
________________________________________________
The office is in order, Chief. Enjoy your evening.
________________________________________________
```

## Test case: Reject malformed task commands and unknown commands

**Aim:** Verifies that missing task details and unknown commands show specific error messages without adding or changing tasks; valid commands between the errors still work.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/Tony.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
todo
todo write report
deadline
list
deadline submit form /by 2099-10-18
event meeting /from 2pm
event standup /from 2099-10-19 /to 2099-10-20
blah
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
Good day, Chief. What shall I arrange for you?
________________________________________________
________________________________________________
My apologies, Chief. I need a description for the to-do. For example: todo read chapter 3
________________________________________________
________________________________________________
Certainly, Chief. I've added this item to the agenda:
  [T][ ] write report
The agenda now contains 1 task.
________________________________________________
________________________________________________
My apologies, Chief. I need a description and due date for the deadline. Use: deadline <task> /by <yyyy-MM-dd>
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][ ] write report
________________________________________________
________________________________________________
Consider it scheduled, Chief. I'll keep watch over this deadline:
  [D][ ] submit form (by: Oct 18 2099)
The agenda now contains 2 tasks.
________________________________________________
________________________________________________
My apologies, Chief. I need a description, start date, and end date for the event. Use: event <task> /from <yyyy-MM-dd> /to <yyyy-MM-dd>
________________________________________________
________________________________________________
Your calendar is updated, Chief. I've arranged this event:
  [E][ ] standup (from: Oct 19 2099 to: Oct 20 2099)
The agenda now contains 3 tasks.
________________________________________________
________________________________________________
My apologies, Chief. I don't recognize that instruction. Try todo, deadline, event, list, find, mark, unmark, delete, or bye.
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][ ] write report
2.[D][ ] submit form (by: Oct 18 2099)
3.[E][ ] standup (from: Oct 19 2099 to: Oct 20 2099)
________________________________________________
________________________________________________
The office is in order, Chief. Enjoy your evening.
________________________________________________
```

## Test case: Find tasks by description keyword

**Aim:** Verifies that `find` searches descriptions without regard to letter case, preserves match order and status, excludes non-matches, handles no results, and rejects a missing keyword.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/Tony.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
todo Read Book
deadline return book /by 2099-06-06
todo write report
mark 1
mark 2
find book
find REPORT
find calendar
find
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
Good day, Chief. What shall I arrange for you?
________________________________________________
________________________________________________
Certainly, Chief. I've added this item to the agenda:
  [T][ ] Read Book
The agenda now contains 1 task.
________________________________________________
________________________________________________
Consider it scheduled, Chief. I'll keep watch over this deadline:
  [D][ ] return book (by: Jun 06 2099)
The agenda now contains 2 tasks.
________________________________________________
________________________________________________
Certainly, Chief. I've added this item to the agenda:
  [T][ ] write report
The agenda now contains 3 tasks.
________________________________________________
________________________________________________
Excellent, Chief. I've recorded this matter as complete:
  [T][X] Read Book
________________________________________________
________________________________________________
Excellent, Chief. I've recorded this matter as complete:
  [D][X] return book (by: Jun 06 2099)
________________________________________________
________________________________________________
I found these matching matters, Chief:
1.[T][X] Read Book
2.[D][X] return book (by: Jun 06 2099)
________________________________________________
________________________________________________
I found these matching matters, Chief:
1.[T][ ] write report
________________________________________________
________________________________________________
I found no matching matters, Chief.
________________________________________________
________________________________________________
My apologies, Chief. Please give me a keyword to search for.
________________________________________________
________________________________________________
The office is in order, Chief. Enjoy your evening.
________________________________________________
```

## Test case: Delete a task and renumber the remaining tasks

**Aim:** Verifies that `delete` removes the requested task, displays its details and the updated task count, and leaves the remaining tasks consecutively numbered.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/Tony.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
todo read book
deadline return book /by 2099-06-06
event project meeting /from 2099-08-06 /to 2099-08-07
todo borrow book
delete 3
list
delete 8
delete three
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
Good day, Chief. What shall I arrange for you?
________________________________________________
________________________________________________
Certainly, Chief. I've added this item to the agenda:
  [T][ ] read book
The agenda now contains 1 task.
________________________________________________
________________________________________________
Consider it scheduled, Chief. I'll keep watch over this deadline:
  [D][ ] return book (by: Jun 06 2099)
The agenda now contains 2 tasks.
________________________________________________
________________________________________________
Your calendar is updated, Chief. I've arranged this event:
  [E][ ] project meeting (from: Aug 06 2099 to: Aug 07 2099)
The agenda now contains 3 tasks.
________________________________________________
________________________________________________
Certainly, Chief. I've added this item to the agenda:
  [T][ ] borrow book
The agenda now contains 4 tasks.
________________________________________________
________________________________________________
As requested, Chief. I've removed this matter:
  [E][ ] project meeting (from: Aug 06 2099 to: Aug 07 2099)
The agenda now contains 3 tasks.
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2099)
3.[T][ ] borrow book
________________________________________________
________________________________________________
My apologies, Chief. That task number is not on the agenda.
________________________________________________
________________________________________________
My apologies, Chief. Please give me a whole-number task number to delete.
________________________________________________
________________________________________________
The office is in order, Chief. Enjoy your evening.
________________________________________________
```

## Test case: Reject deletion from an empty list and invalid delete commands

**Aim:** Verifies that an empty list can be displayed, deletion from it is rejected, and missing, out-of-range, and non-numeric delete inputs do not change an existing task.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/Tony.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
list
delete 1
todo revise notes
delete
list
delete 2
list
delete one
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
Good day, Chief. What shall I arrange for you?
________________________________________________
________________________________________________
Your agenda is clear, Chief. There are no matters on file.
________________________________________________
________________________________________________
My apologies, Chief. That task number is not on the agenda.
________________________________________________
________________________________________________
Certainly, Chief. I've added this item to the agenda:
  [T][ ] revise notes
The agenda now contains 1 task.
________________________________________________
________________________________________________
My apologies, Chief. Please give me a task number to delete.
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][ ] revise notes
________________________________________________
________________________________________________
My apologies, Chief. That task number is not on the agenda.
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][ ] revise notes
________________________________________________
________________________________________________
My apologies, Chief. Please give me a whole-number task number to delete.
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][ ] revise notes
________________________________________________
________________________________________________
The office is in order, Chief. Enjoy your evening.
________________________________________________
```

## Test case: Delete completed first and last tasks

**Aim:** Verifies that deletion works at both list boundaries and that its confirmation retains the removed task's completion status.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/Tony.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
todo read book
deadline return book /by 2099-06-06
mark 1
delete 1
list
delete 1
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
Good day, Chief. What shall I arrange for you?
________________________________________________
________________________________________________
Certainly, Chief. I've added this item to the agenda:
  [T][ ] read book
The agenda now contains 1 task.
________________________________________________
________________________________________________
Consider it scheduled, Chief. I'll keep watch over this deadline:
  [D][ ] return book (by: Jun 06 2099)
The agenda now contains 2 tasks.
________________________________________________
________________________________________________
Excellent, Chief. I've recorded this matter as complete:
  [T][X] read book
________________________________________________
________________________________________________
As requested, Chief. I've removed this matter:
  [T][X] read book
The agenda now contains 1 task.
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[D][ ] return book (by: Jun 06 2099)
________________________________________________
________________________________________________
As requested, Chief. I've removed this matter:
  [D][ ] return book (by: Jun 06 2099)
The agenda now contains 0 tasks.
________________________________________________
________________________________________________
Your agenda is clear, Chief. There are no matters on file.
________________________________________________
________________________________________________
The office is in order, Chief. Enjoy your evening.
________________________________________________
```

## Test case: Reject invalid mark commands without changing task state

**Aim:** Verifies that invalid task numbers and non-numeric `mark` inputs are rejected, and that each subsequent list still shows the original incomplete task until a valid mark command succeeds.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/Tony.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
todo review notes
mark 0
list
mark one
list
mark 2
list
mark 1
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
Good day, Chief. What shall I arrange for you?
________________________________________________
________________________________________________
Certainly, Chief. I've added this item to the agenda:
  [T][ ] review notes
The agenda now contains 1 task.
________________________________________________
________________________________________________
My apologies, Chief. That task number is not on the agenda.
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][ ] review notes
________________________________________________
________________________________________________
My apologies, Chief. Please give me a whole-number task number to mark.
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][ ] review notes
________________________________________________
________________________________________________
My apologies, Chief. That task number is not on the agenda.
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][ ] review notes
________________________________________________
________________________________________________
Excellent, Chief. I've recorded this matter as complete:
  [T][X] review notes
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][X] review notes
________________________________________________
________________________________________________
The office is in order, Chief. Enjoy your evening.
________________________________________________
```

## Test case: Reject invalid unmark commands without changing task state

**Aim:** Verifies that malformed and out-of-range `unmark` commands do not undo a completed task, while a later valid command does.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/Tony.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
todo submit assignment
mark 1
unmark -1
list
unmark soon
list
unmark 2
list
unmark 1
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
Good day, Chief. What shall I arrange for you?
________________________________________________
________________________________________________
Certainly, Chief. I've added this item to the agenda:
  [T][ ] submit assignment
The agenda now contains 1 task.
________________________________________________
________________________________________________
Excellent, Chief. I've recorded this matter as complete:
  [T][X] submit assignment
________________________________________________
________________________________________________
My apologies, Chief. That task number is not on the agenda.
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][X] submit assignment
________________________________________________
________________________________________________
My apologies, Chief. Please give me a whole-number task number to unmark.
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][X] submit assignment
________________________________________________
________________________________________________
My apologies, Chief. That task number is not on the agenda.
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][X] submit assignment
________________________________________________
________________________________________________
Understood, Chief. I've returned this matter to the active agenda:
  [T][ ] submit assignment
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][ ] submit assignment
________________________________________________
________________________________________________
The office is in order, Chief. Enjoy your evening.
________________________________________________
```

## Test case: Save the latest task list after every change

**Aim:** Verifies that adding each task type, marking, unmarking, and deleting result in a data file that represents the latest task list.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/Tony.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony && printf "Saved file:\\n" && sed -n "1,20p" data/tony.txt'`

**Inputs:**
```text
todo read book
deadline return book /by 2099-06-06
event project meeting /from 2099-08-06 /to 2099-08-07
mark 1
unmark 1
delete 2
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
Good day, Chief. What shall I arrange for you?
________________________________________________
________________________________________________
Certainly, Chief. I've added this item to the agenda:
  [T][ ] read book
The agenda now contains 1 task.
________________________________________________
________________________________________________
Consider it scheduled, Chief. I'll keep watch over this deadline:
  [D][ ] return book (by: Jun 06 2099)
The agenda now contains 2 tasks.
________________________________________________
________________________________________________
Your calendar is updated, Chief. I've arranged this event:
  [E][ ] project meeting (from: Aug 06 2099 to: Aug 07 2099)
The agenda now contains 3 tasks.
________________________________________________
________________________________________________
Excellent, Chief. I've recorded this matter as complete:
  [T][X] read book
________________________________________________
________________________________________________
Understood, Chief. I've returned this matter to the active agenda:
  [T][ ] read book
________________________________________________
________________________________________________
As requested, Chief. I've removed this matter:
  [D][ ] return book (by: Jun 06 2099)
The agenda now contains 2 tasks.
________________________________________________
________________________________________________
The office is in order, Chief. Enjoy your evening.
________________________________________________
Saved file:
T | 0 | read book
E | 0 | project meeting | 2099-08-06 | 2099-08-07
```

## Test case: Load saved tasks when the chatbot starts

**Aim:** Verifies that a saved to-do, deadline, and event are reconstructed with their descriptions, date fields, and completion states on startup.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && printf "T | 1 | read book\\nD | 0 | return book | 2099-06-06\\nE | 1 | project meeting | 2099-08-06 | 2099-08-07\\n" > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/Tony.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
Good day, Chief. What shall I arrange for you?
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2099)
3.[E][X] project meeting (from: Aug 06 2099 to: Aug 07 2099)
________________________________________________
________________________________________________
The office is in order, Chief. Enjoy your evening.
________________________________________________
```

## Test case: Start normally when the data file is missing

**Aim:** Verifies that first-time startup treats a missing data file as an empty task list without displaying an error.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && rm -f data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/Tony.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
Good day, Chief. What shall I arrange for you?
________________________________________________
________________________________________________
Your agenda is clear, Chief. There are no matters on file.
________________________________________________
________________________________________________
The office is in order, Chief. Enjoy your evening.
________________________________________________
```

## Test case: Recover valid tasks from a partly corrupted data file

**Aim:** Verifies that blank lines are ignored, malformed records are skipped with one warning, and valid records still load.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && printf "%s\\n" "T | 1 | valid task" "" "T | 2 | bad status" "X | 0 | unknown type" "D | 0 | missing due date" "E | 0 | too many | start | end | extra" "T | 0 | " "D | 0 | impossible date | 2099-02-30" "E | 0 | backwards | 2099-10-20 | 2099-10-19" "D | 0 | return book | 2099-10-18" > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/Tony.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
Good day, Chief. What shall I arrange for you?
________________________________________________
Chief, I set aside 7 lines from our records because the data was invalid.
________________________________________________
Here is the current agenda, Chief:
1.[T][X] valid task
2.[D][ ] return book (by: Oct 18 2099)
________________________________________________
________________________________________________
The office is in order, Chief. Enjoy your evening.
________________________________________________
```

## Test case: Parse valid dates and reject invalid dates

**Aim:** Verifies strict `yyyy-MM-dd` parsing, leap-day support, formatted date display, and rejection of event ranges
whose end date is not later than their start date.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/Tony.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
deadline invalid leap day /by 2099-02-29
deadline wrong format /by 15-10-2019
event impossible date /from 2099-02-28 /to 2099-02-30
event backwards /from 2099-10-20 /to 2099-10-19
deadline valid leap day /by 2096-02-29
event one-day workshop /from 2099-10-15 /to 2099-10-15
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
Good day, Chief. What shall I arrange for you?
________________________________________________
________________________________________________
My apologies, Chief. Please give me dates as yyyy-MM-dd, for example 2019-10-15.
________________________________________________
________________________________________________
My apologies, Chief. Please give me dates as yyyy-MM-dd, for example 2019-10-15.
________________________________________________
________________________________________________
My apologies, Chief. Please give me dates as yyyy-MM-dd, for example 2019-10-15.
________________________________________________
________________________________________________
My apologies, Chief. I need the event's end date to be after its start date.
________________________________________________
________________________________________________
Consider it scheduled, Chief. I'll keep watch over this deadline:
  [D][ ] valid leap day (by: Feb 29 2096)
The agenda now contains 1 task.
________________________________________________
________________________________________________
My apologies, Chief. I need the event's end date to be after its start date.
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[D][ ] valid leap day (by: Feb 29 2096)
________________________________________________
________________________________________________
The office is in order, Chief. Enjoy your evening.
________________________________________________
```

## Test case: Continue after data file read and write errors

**Aim:** Verifies that an unreadable storage target starts with an empty list and that a later save failure keeps the chatbot session usable.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && rm -f data/tony.txt && mkdir data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/Tony.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony && rmdir data/tony.txt'`

**Inputs:**
```text
todo session-only task
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
Good day, Chief. What shall I arrange for you?
________________________________________________
Chief, I couldn't read our records, so I have opened a fresh agenda for this session.
________________________________________________
Certainly, Chief. I've added this item to the agenda:
  [T][ ] session-only task
The agenda now contains 1 task.
Chief, I couldn't file that change. It will remain available only for this session.
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][ ] session-only task
________________________________________________
________________________________________________
The office is in order, Chief. Enjoy your evening.
________________________________________________
```

## Test case: Escape storage separator and backslash characters

**Aim:** Verifies that pipe and backslash characters in task fields are escaped when saved instead of corrupting the storage format.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && : > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/Tony.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony && printf "Saved file:\\n" && sed -n "1,20p" data/tony.txt'`

**Inputs:**
```text
todo compare A | B
deadline review C:\notes | archive /by 2099-10-18
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
Good day, Chief. What shall I arrange for you?
________________________________________________
________________________________________________
Certainly, Chief. I've added this item to the agenda:
  [T][ ] compare A | B
The agenda now contains 1 task.
________________________________________________
________________________________________________
Consider it scheduled, Chief. I'll keep watch over this deadline:
  [D][ ] review C:\notes | archive (by: Oct 18 2099)
The agenda now contains 2 tasks.
________________________________________________
________________________________________________
The office is in order, Chief. Enjoy your evening.
________________________________________________
Saved file:
T | 0 | compare A \| B
D | 0 | review C:\\notes \| archive | 2099-10-18
```

## Test case: Load escaped storage fields

**Aim:** Verifies that escaped pipe and backslash characters are decoded when saved tasks are loaded.

**Command:** `zsh -lc 'source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && mkdir -p data && printf "%s\\n" "T | 1 | compare A \\| B" "D | 0 | review C:\\\\notes \\| archive | 2099-10-18" > data/tony.txt && javac -d /private/tmp/tony-ui-classes src/main/java/tony/Tony.java src/main/java/tony/*/*.java && java -cp /private/tmp/tony-ui-classes tony.Tony'`

**Inputs:**
```text
list
bye
```

**Expected output:**
```text
 _____   ___   _   _ __   __
|_   _| / _ \ | \ | |\ \ / /
  | |  | | | ||  \| | \ V /
  | |  | |_| || |\  |  | |
  |_|   \___/ |_| \_|  |_|
________________________________________________
Good day, Chief. What shall I arrange for you?
________________________________________________
________________________________________________
Here is the current agenda, Chief:
1.[T][X] compare A | B
2.[D][ ] review C:\notes | archive (by: Oct 18 2099)
________________________________________________
________________________________________________
The office is in order, Chief. Enjoy your evening.
________________________________________________
```
