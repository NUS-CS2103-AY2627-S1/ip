# UI Test Plan

## Test configuration

Run every test case in a fresh process with an isolated data file so its task list starts empty.

### Program command

```shell
rm -f build/ui-test-data/toot.txt && JAVA_HOME=/Users/darrny/.sdkman/candidates/java/25.0.3.fx-zulu ./gradlew --quiet classes && /Users/darrny/.sdkman/candidates/java/25.0.3.fx-zulu/bin/java -ea -Dtoot.data.path=build/ui-test-data/toot.txt -cp build/classes/java/main toot.Toot
```

- Timeout (seconds): 10
- Comparison: Exact combined stdout/stderr after normalizing CRLF line endings to LF.
- Runtime: Java 25 (`25.0.3.fx-zulu` on macOS).

## Test cases

### TC-01: Exit immediately

**Aim:** Verify that the chatbot starts successfully and exits when the user enters `bye`.

#### Inputs

```text
bye
```

#### Expected output

```text
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
 _____           _
|_   _|__   ___ | |_
  | |/ _ \ / _ \| __|
  | | (_) | (_) | |_
  |_|\___/ \___/ \__|

Hewwo!! I'm Toot, ur teeny-tiny computey baby! ૮₍ ˶•⤙•˶ ₎ა
Gib me a command... Toot do a BIG twy!! (•̀ᴗ•́)و
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Otay bye-bye! Toot go eepy now... zZz (｡-ω-)ﾉ
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
```

### TC-02: Reject malformed task commands without changing the list

**Aim:** Verify specific errors for every required task field while interleaved valid commands still create the correct list.

#### Inputs

```text
todo
todo read book
deadline return book
deadline /by Sunday
deadline return book /by
deadline return book /by Sunday
deadline return book /by 2019-12-02
event meeting
event /from Mon /to Tue
event meeting /from /to Tue
event meeting /from Mon
event meeting /from Mon /to
event meeting /from Mon /to Tue
list
bye
```

#### Expected output

```text
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
 _____           _
|_   _|__   ___ | |_
  | |/ _ \ / _ \| __|
  | | (_) | (_) | |_
  |_|\___/ \___/ \__|

Hewwo!! I'm Toot, ur teeny-tiny computey baby! ૮₍ ˶•⤙•˶ ₎ა
Gib me a command... Toot do a BIG twy!! (•̀ᴗ•́)و
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! The todo description cannot be empty. Try: todo DESCRIPTION
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Toot addeded:
  [T][ ] read book
Toot has 1 task in the list now! (｡•̀ᴗ-)✧
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! A deadline needs '/by' before its due date. Try: deadline DESCRIPTION /by YYYY-MM-DD
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! The deadline description cannot be empty. Try: deadline DESCRIPTION /by YYYY-MM-DD
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! The deadline date cannot be empty after '/by'.
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! The deadline date must use YYYY-MM-DD and be a real calendar date. Try: deadline DESCRIPTION /by 2019-12-02
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Toot addeded:
  [D][ ] return book (by: Dec 02 2019)
Toot has 2 tasks in the list now! (｡•̀ᴗ-)✧
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! An event needs '/from' before its start time. Try: event DESCRIPTION /from START /to END
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! The event description cannot be empty. Try: event DESCRIPTION /from START /to END
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! The event start time cannot be empty after '/from'.
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! An event needs '/to' before its end time. Try: event DESCRIPTION /from START /to END
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! The event end time cannot be empty after '/to'.
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Toot addeded:
  [E][ ] meeting (from: Mon to: Tue)
Toot has 3 tasks in the list now! (｡•̀ᴗ-)✧
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 02 2019)
3.[E][ ] meeting (from: Mon to: Tue)
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Otay bye-bye! Toot go eepy now... zZz (｡-ω-)ﾉ
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
```

### TC-03: Reject unknown commands and invalid task numbers

**Aim:** Verify empty and unknown command errors plus missing, nonnumeric, and out-of-range task numbers without corrupting task state.

#### Inputs

```text

blah
list extra
mark
mark one
mark 1
todo test
mark 0
mark 2
mark 1
unmark
unmark one
unmark 2
unmark 1
list
bye
```

#### Expected output

```text
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
 _____           _
|_   _|__   ___ | |_
  | |/ _ \ / _ \| __|
  | | (_) | (_) | |_
  |_|\___/ \___/ \__|

Hewwo!! I'm Toot, ur teeny-tiny computey baby! ૮₍ ˶•⤙•˶ ₎ა
Gib me a command... Toot do a BIG twy!! (•̀ᴗ•́)و
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! Toot didn't hear a command. Type a command such as 'todo read book'. (・・?)
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! Toot doesn't know that command. Try: todo, deadline, event, list, find, mark, unmark, delete, help, or bye. (・・?)
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! Toot doesn't know that command. Try: todo, deadline, event, list, find, mark, unmark, delete, help, or bye. (・・?)
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! Toot needs a task number after 'mark'. Try: mark 1
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! The task number for 'mark' must be a whole number. Try: mark 1
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! The task list is empty, so there is nothing to mark.
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Toot addeded:
  [T][ ] test
Toot has 1 task in the list now! (｡•̀ᴗ-)✧
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! Task 0 does not exist. Choose a number from 1 to 1.
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! Task 2 does not exist. Choose a number from 1 to 1.
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Nice! I've marked this task as done:
  [T][X] test
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! Toot needs a task number after 'unmark'. Try: unmark 1
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! The task number for 'unmark' must be a whole number. Try: unmark 1
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! Task 2 does not exist. Choose a number from 1 to 1.
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
OK, I've marked this task as not done yet:
  [T][ ] test
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Here are the tasks in your list:
1.[T][ ] test
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Otay bye-bye! Toot go eepy now... zZz (｡-ω-)ﾉ
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
```

### TC-04: Add, list, mark, and unmark a task

**Aim:** Verify the main task workflow and its displayed completion markers.

#### Inputs

```text
todo read book
list
mark 1
list
unmark 1
list
bye
```

#### Expected output

```text
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
 _____           _
|_   _|__   ___ | |_
  | |/ _ \ / _ \| __|
  | | (_) | (_) | |_
  |_|\___/ \___/ \__|

Hewwo!! I'm Toot, ur teeny-tiny computey baby! ૮₍ ˶•⤙•˶ ₎ა
Gib me a command... Toot do a BIG twy!! (•̀ᴗ•́)و
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Toot addeded:
  [T][ ] read book
Toot has 1 task in the list now! (｡•̀ᴗ-)✧
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Here are the tasks in your list:
1.[T][ ] read book
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Nice! I've marked this task as done:
  [T][X] read book
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Here are the tasks in your list:
1.[T][X] read book
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
OK, I've marked this task as not done yet:
  [T][ ] read book
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Here are the tasks in your list:
1.[T][ ] read book
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Otay bye-bye! Toot go eepy now... zZz (｡-ω-)ﾉ
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
```

### TC-05: Add and mark a dated deadline

**Aim:** Verify that an ISO deadline date is parsed as a calendar date and displayed in a user-friendly format.

#### Inputs

```text
deadline do homework /by 2026-08-31
list
mark 1
list
bye
```

#### Expected output

```text
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
 _____           _
|_   _|__   ___ | |_
  | |/ _ \ / _ \| __|
  | | (_) | (_) | |_
  |_|\___/ \___/ \__|

Hewwo!! I'm Toot, ur teeny-tiny computey baby! ૮₍ ˶•⤙•˶ ₎ა
Gib me a command... Toot do a BIG twy!! (•̀ᴗ•́)و
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Toot addeded:
  [D][ ] do homework (by: Aug 31 2026)
Toot has 1 task in the list now! (｡•̀ᴗ-)✧
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Here are the tasks in your list:
1.[D][ ] do homework (by: Aug 31 2026)
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Nice! I've marked this task as done:
  [D][X] do homework (by: Aug 31 2026)
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Here are the tasks in your list:
1.[D][X] do homework (by: Aug 31 2026)
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Otay bye-bye! Toot go eepy now... zZz (｡-ω-)ﾉ
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
```

### TC-06: Store all task types together

**Aim:** Verify that todos, deadlines, and events coexist in the polymorphic task list and retain their type-specific formatting.

#### Inputs

```text
todo borrow book
deadline return book /by 2026-08-30
event project meeting /from Mon 2pm /to 4pm
list
mark 3
list
bye
```

#### Expected output

```text
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
 _____           _
|_   _|__   ___ | |_
  | |/ _ \ / _ \| __|
  | | (_) | (_) | |_
  |_|\___/ \___/ \__|

Hewwo!! I'm Toot, ur teeny-tiny computey baby! ૮₍ ˶•⤙•˶ ₎ა
Gib me a command... Toot do a BIG twy!! (•̀ᴗ•́)و
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Toot addeded:
  [T][ ] borrow book
Toot has 1 task in the list now! (｡•̀ᴗ-)✧
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Toot addeded:
  [D][ ] return book (by: Aug 30 2026)
Toot has 2 tasks in the list now! (｡•̀ᴗ-)✧
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Toot addeded:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Toot has 3 tasks in the list now! (｡•̀ᴗ-)✧
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Aug 30 2026)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Nice! I've marked this task as done:
  [E][X] project meeting (from: Mon 2pm to: 4pm)
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Aug 30 2026)
3.[E][X] project meeting (from: Mon 2pm to: 4pm)
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Otay bye-bye! Toot go eepy now... zZz (｡-ω-)ﾉ
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
```

### TC-07: Delete a task from the list

**Aim:** Verify delete input validation, removal of a middle task, the remaining count, and list renumbering.

#### Inputs

```text
delete
delete one
delete 1
todo read book
deadline return book /by 2026-06-06
event project meeting /from Aug 6th 2pm /to 4pm
todo borrow book
mark 1
mark 2
delete 3
list
bye
```

#### Expected output

```text
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
 _____           _
|_   _|__   ___ | |_
  | |/ _ \ / _ \| __|
  | | (_) | (_) | |_
  |_|\___/ \___/ \__|

Hewwo!! I'm Toot, ur teeny-tiny computey baby! ૮₍ ˶•⤙•˶ ₎ა
Gib me a command... Toot do a BIG twy!! (•̀ᴗ•́)و
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! Toot needs a task number after 'delete'. Try: delete 1
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! The task number for 'delete' must be a whole number. Try: delete 1
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! The task list is empty, so there is nothing to delete.
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Toot addeded:
  [T][ ] read book
Toot has 1 task in the list now! (｡•̀ᴗ-)✧
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Toot addeded:
  [D][ ] return book (by: Jun 06 2026)
Toot has 2 tasks in the list now! (｡•̀ᴗ-)✧
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Toot addeded:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Toot has 3 tasks in the list now! (｡•̀ᴗ-)✧
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Toot addeded:
  [T][ ] borrow book
Toot has 4 tasks in the list now! (｡•̀ᴗ-)✧
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Nice! I've marked this task as done:
  [T][X] read book
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Nice! I've marked this task as done:
  [D][X] return book (by: Jun 06 2026)
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Jun 06 2026)
3.[T][ ] borrow book
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Otay bye-bye! Toot go eepy now... zZz (｡-ω-)ﾉ
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
```

### TC-08: Find tasks by description keyword

**Aim:** Verify missing-keyword validation, case-insensitive description matching, result numbering, and no matches.

#### Inputs

```text
find
todo Read Book
deadline return book /by 2026-06-06
event book club /from Mon /to Tue
event meeting /from book club /to midnight
mark 1
find BOOK
find club
find 2026
bye
```

#### Expected output

```text
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
 _____           _
|_   _|__   ___ | |_
  | |/ _ \ / _ \| __|
  | | (_) | (_) | |_
  |_|\___/ \___/ \__|

Hewwo!! I'm Toot, ur teeny-tiny computey baby! ૮₍ ˶•⤙•˶ ₎ა
Gib me a command... Toot do a BIG twy!! (•̀ᴗ•́)و
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! The find keyword cannot be empty. Try: find KEYWORD
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Toot addeded:
  [T][ ] Read Book
Toot has 1 task in the list now! (｡•̀ᴗ-)✧
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Toot addeded:
  [D][ ] return book (by: Jun 06 2026)
Toot has 2 tasks in the list now! (｡•̀ᴗ-)✧
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Toot addeded:
  [E][ ] book club (from: Mon to: Tue)
Toot has 3 tasks in the list now! (｡•̀ᴗ-)✧
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Toot addeded:
  [E][ ] meeting (from: book club to: midnight)
Toot has 4 tasks in the list now! (｡•̀ᴗ-)✧
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Nice! I've marked this task as done:
  [T][X] Read Book
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Here are the matching tasks in your list:
1.[T][X] Read Book
2.[D][ ] return book (by: Jun 06 2026)
3.[E][ ] book club (from: Mon to: Tue)
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Here are the matching tasks in your list:
1.[E][ ] book club (from: Mon to: Tue)
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Here are the matching tasks in your list:
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Otay bye-bye! Toot go eepy now... zZz (｡-ω-)ﾉ
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
```

### TC-09: Help and argument validation

**Aim:** Verify the full command guide, reject help arguments, and keep the task list unchanged.

#### Inputs

```text
help
help extra
list
bye
```

#### Expected output

```text
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
 _____           _
|_   _|__   ___ | |_
  | |/ _ \ / _ \| __|
  | | (_) | (_) | |_
  |_|\___/ \___/ \__|

Hewwo!! I'm Toot, ur teeny-tiny computey baby! ૮₍ ˶•⤙•˶ ₎ა
Gib me a command... Toot do a BIG twy!! (•̀ᴗ•́)و
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Toot command guide:
todo DESCRIPTION - Add a todo.
deadline DESCRIPTION /by YYYY-MM-DD - Add a deadline.
event DESCRIPTION /from START /to END - Add an event.
list - Show all tasks and their numbers.
find KEYWORD - Find descriptions (ignoring case).
mark NUMBER - Mark a task done.
unmark NUMBER - Mark a task not done.
delete NUMBER - Delete a task.
help - Show this guide.
bye - Exit.
Use task numbers from list for mark, unmark, and delete.
Examples: todo read book; deadline return book /by 2026-09-14
Example: event meeting /from Mon 2pm /to Mon 3pm
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! Toot doesn't know that command. Try: todo, deadline, event, list, find, mark, unmark, delete, help, or bye. (・・?)
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Here are the tasks in your list:
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Otay bye-bye! Toot go eepy now... zZz (｡-ω-)ﾉ
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
```

### TC-10: Reject repeated task field delimiters

**Aim:** Reject ambiguous deadline and event fields while keeping the list empty.

#### Inputs

```text
deadline read /by 2026-09-14 /by 2026-09-15
event meet /from Mon /from Tue /to Wed
event meet /from Mon /to Tue /to Wed
list
bye
```

#### Expected output

```text
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
 _____           _
|_   _|__   ___ | |_
  | |/ _ \ / _ \| __|
  | | (_) | (_) | |_
  |_|\___/ \___/ \__|

Hewwo!! I'm Toot, ur teeny-tiny computey baby! ૮₍ ˶•⤙•˶ ₎ა
Gib me a command... Toot do a BIG twy!! (•̀ᴗ•́)و
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! A deadline can have only one '/by'.
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! An event can have only one '/from' and one '/to'.
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Oh crumbs! An event can have only one '/from' and one '/to'.
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Here are the tasks in your list:
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆

⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
Otay bye-bye! Toot go eepy now... zZz (｡-ω-)ﾉ
⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆
```
