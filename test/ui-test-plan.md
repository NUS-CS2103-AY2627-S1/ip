# UI Test Plan

## Compilation

```bash
find src/main/java -name "*.java" | xargs javac -d out/production/ip
```

## Test Cases

### Test: Greet and exit

**Aim:** Verify the app shows a greeting banner and exits cleanly on `bye`.

**Inputs:**
```
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Add todo and list

**Aim:** Verify todo tasks can be added and listed.

**Inputs:**
```
todo read book
list
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [T][ ] read book
     Your agenda now has 1 task.
    ____________________________________________________________
    ____________________________________________________________
     Here is your current agenda:
     1.[T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Mark task as done

**Aim:** Verify a task can be marked as done.

**Inputs:**
```
todo read book
mark 1
list
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [T][ ] read book
     Your agenda now has 1 task.
    ____________________________________________________________
    ____________________________________________________________
     Excellent. One task completed:
       [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Here is your current agenda:
     1.[T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Unmark task

**Aim:** Verify a task can be unmarked (reversed from done to not done).

**Inputs:**
```
todo read book
mark 1
unmark 1
list
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [T][ ] read book
     Your agenda now has 1 task.
    ____________________________________________________________
    ____________________________________________________________
     Excellent. One task completed:
       [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Very well. This task is pending again:
       [T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     Here is your current agenda:
     1.[T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Add todo, deadline, and event

**Aim:** Verify all three task types can be added.

**Inputs:**
```
todo borrow book
deadline return book /by 2019-12-02
event project meeting /from 2024-08-06 /to 2024-08-07
list
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [T][ ] borrow book
     Your agenda now has 1 task.
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [D][ ] return book (by: Dec 02 2019)
     Your agenda now has 2 tasks.
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [E][ ] project meeting (from: Aug 06 2024 to: Aug 07 2024)
     Your agenda now has 3 tasks.
    ____________________________________________________________
    ____________________________________________________________
     Here is your current agenda:
     1.[T][ ] borrow book
     2.[D][ ] return book (by: Dec 02 2019)
     3.[E][ ] project meeting (from: Aug 06 2024 to: Aug 07 2024)
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Error - empty todo

**Aim:** Verify error when todo description is empty.

**Inputs:**
```
todo
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: The description of a todo cannot be empty.
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Error - unrecognized command

**Aim:** Verify error for unknown commands.

**Inputs:**
```
blah
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: I couldn't identify that command.
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Delete task

**Aim:** Verify a task can be deleted and the list updates accordingly.

**Inputs:**
```
todo read book
todo return book
delete 1
list
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [T][ ] read book
     Your agenda now has 1 task.
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [T][ ] return book
     Your agenda now has 2 tasks.
    ____________________________________________________________
    ____________________________________________________________
     Removed from the agenda:
       [T][ ] read book
     Your agenda now has 1 task.
    ____________________________________________________________
    ____________________________________________________________
     Here is your current agenda:
     1.[T][ ] return book
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Error - mark without number

**Aim:** Verify error when mark is called with no argument.

**Inputs:**
```
mark
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: Please provide a task number.
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Error - mark with non-numeric argument

**Aim:** Verify error when mark is called with a non-numeric argument.

**Inputs:**
```
mark abc
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: 'abc' is not a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Error - mark with out-of-range index

**Aim:** Verify error when mark is called with an index beyond the list size.

**Inputs:**
```
todo read book
mark 5
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [T][ ] read book
     Your agenda now has 1 task.
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: Task number 5 is out of range. You have 1 task.
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Error - deadline without /by

**Aim:** Verify error when deadline is missing the /by delimiter.

**Inputs:**
```
deadline homework
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: Invalid deadline format. Use: deadline <description> /by <date>
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Error - event without /from and /to

**Aim:** Verify error when event is missing the /from and /to delimiters.

**Inputs:**
```
event meeting
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: Invalid event format. Use: event <description> /from <date> /to <date>
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Corrupted save file skips bad lines

**Aim:** Verify that corrupted lines in the save file are skipped gracefully.

**Setup:**
Create `./data/johnny.txt` with the following content before running:
```
T | 1 | read book
badline
T | yes | invalid status
T | 0 |
T | 0 | extra fields | unexpected
E | 0 | reversed event | 2024-03-03 | 2024-03-01
D | 0 | homework | 2024-09-15
```

**Inputs:**
```
list
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     Here is your current agenda:
     1.[T][X] read book
     2.[D][ ] homework (by: Sept 15 2024)
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Load saved tasks on startup

**Aim:** Verify tasks saved from a previous session are loaded when the app starts.

**Setup:**
Create `./data/johnny.txt` with the following content before running:
```
T | 1 | read book
D | 0 | return book | 2019-12-02
E | 0 | project meeting | 2024-08-06 | 2024-08-07
```

**Inputs:**
```
list
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     Here is your current agenda:
     1.[T][X] read book
     2.[D][ ] return book (by: Dec 02 2019)
     3.[E][ ] project meeting (from: Aug 06 2024 to: Aug 07 2024)
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Date parsing and formatting

**Aim:** Verify that dates in yyyy-MM-dd format are parsed and displayed as MMM dd yyyy.

**Inputs:**
```
deadline return book /by 2019-10-15
event conference /from 2024-03-01 /to 2024-03-03
list
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [D][ ] return book (by: Oct 15 2019)
     Your agenda now has 1 task.
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [E][ ] conference (from: Mar 01 2024 to: Mar 03 2024)
     Your agenda now has 2 tasks.
    ____________________________________________________________
    ____________________________________________________________
     Here is your current agenda:
     1.[D][ ] return book (by: Oct 15 2019)
     2.[E][ ] conference (from: Mar 01 2024 to: Mar 03 2024)
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Find tasks by keyword

**Aim:** Verify the find command returns matching tasks.

**Inputs:**
```
todo read book
todo return book
deadline borrow book /by 2024-09-15
todo buy groceries
find book
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [T][ ] read book
     Your agenda now has 1 task.
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [T][ ] return book
     Your agenda now has 2 tasks.
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [D][ ] borrow book (by: Sept 15 2024)
     Your agenda now has 3 tasks.
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [T][ ] buy groceries
     Your agenda now has 4 tasks.
    ____________________________________________________________
    ____________________________________________________________
     These entries match your request:
     1.[T][ ] read book
     2.[T][ ] return book
     3.[D][ ] borrow book (by: Sept 15 2024)
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Error - find without keyword

**Aim:** Verify error when find is called with no keyword.

**Inputs:**
```
find
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: Please provide a keyword to search for.
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Error - invalid date format

**Aim:** Verify error when deadline date is not in yyyy-MM-dd format.

**Inputs:**
```
deadline homework /by Sunday
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _                       
    | | ___ | |__  _ __  _ __  _   _ 
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/ 
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: Invalid date format. Please use yyyy-MM-dd (e.g., 2019-10-15).
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Undo the latest task-changing command

**Aim:** Verify undo restores the latest task change, survives read-only commands, and reports an empty history.

**Inputs:**
```
undo
todo first
todo second
delete 1
list
undo
mark 1
undo
list
undo
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _
    | | ___ | |__  _ __  _ __  _   _
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: There is no command to undo.
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [T][ ] first
     Your agenda now has 1 task.
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [T][ ] second
     Your agenda now has 2 tasks.
    ____________________________________________________________
    ____________________________________________________________
     Removed from the agenda:
       [T][ ] first
     Your agenda now has 1 task.
    ____________________________________________________________
    ____________________________________________________________
     Here is your current agenda:
     1.[T][ ] second
    ____________________________________________________________
    ____________________________________________________________
     As you wish. The last change has been undone.
    ____________________________________________________________
    ____________________________________________________________
     Excellent. One task completed:
       [T][X] first
    ____________________________________________________________
    ____________________________________________________________
     As you wish. The last change has been undone.
    ____________________________________________________________
    ____________________________________________________________
     Here is your current agenda:
     1.[T][ ] first
     2.[T][ ] second
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: There is no command to undo.
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Accept harmless command whitespace

**Aim:** Verify leading, trailing, and repeated spaces do not prevent valid commands from working.

**Inputs:**
```
   todo    read book
deadline    return book    /by    2024-09-15
event    meeting    /from    2024-09-15    /to    2024-09-16
list
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _
    | | ___ | |__  _ __  _ __  _   _
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [T][ ] read book
     Your agenda now has 1 task.
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [D][ ] return book (by: Sept 15 2024)
     Your agenda now has 2 tasks.
    ____________________________________________________________
    ____________________________________________________________
     Consider it noted:
       [E][ ] meeting (from: Sept 15 2024 to: Sept 16 2024)
     Your agenda now has 3 tasks.
    ____________________________________________________________
    ____________________________________________________________
     Here is your current agenda:
     1.[T][ ] read book
     2.[D][ ] return book (by: Sept 15 2024)
     3.[E][ ] meeting (from: Sept 15 2024 to: Sept 16 2024)
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Error - repeated command parameters

**Aim:** Verify task commands reject parameters that are specified more than once.

**Inputs:**
```
deadline homework /by 2024-09-15 /by 2024-09-16
event meeting /from 2024-09-15 /from 2024-09-16 /to 2024-09-17
event meeting /from 2024-09-15 /to 2024-09-16 /to 2024-09-17
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _
    | | ___ | |__  _ __  _ __  _   _
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: The /by parameter must be specified only once.
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: The /from parameter must be specified only once.
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: The /to parameter must be specified only once.
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

### Test: Error - invalid event range and unexpected parameters

**Aim:** Verify invalid event chronology and parameters on parameterless commands are rejected.

**Inputs:**
```
event meeting /from 2024-09-15 /to 2024-09-15
event meeting /from 2024-09-16 /to 2024-09-15
todo wash | fold laundry
list all
undo now
bye later
bye
```

**Expected output:**
```
    ____________________________________________________________
     _       _
    | | ___ | |__  _ __  _ __  _   _
 _  | |/ _ \| '_ \| '_ \| '_ \| | | |
| |_| | (_) | | | | | | | | | | |_| |
 \___/ \___/|_| |_|_| |_|_| |_|\__, |
                                |___/
     Good day. Johnny at your service.
     How may I keep your day in order?
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: The event start date must be before the end date.
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: The event start date must be before the end date.
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: Task descriptions cannot contain the '|' character.
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: The list command does not accept parameters.
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: The undo command does not accept parameters.
    ____________________________________________________________
    ____________________________________________________________
     I'm afraid something is amiss: The bye command does not accept parameters.
    ____________________________________________________________
    ____________________________________________________________
     Until next time. I'll keep things in order.
    ____________________________________________________________
```

---

## Manual GUI Test Cases

### Test: Enter commands and exit through the GUI

**Aim:** Verify that Johnny can be operated entirely through its JavaFX interface.

**Steps:**
1. Run `./gradlew run`.
2. Enter `todo read book` in the command field and press Enter.
3. Enter `list` and click the **Send** button.
4. Enter `bye` and press Enter.

**Expected result:**
- The window displays the `Johnny · At your service` header and butler-style greeting.
- The ASCII-art banner is aligned using a monospace font.
- Johnny's responses appear beside a small gold `J` badge in left-aligned tan bubbles.
- User commands appear in narrower, right-aligned navy bubbles without a `You:` prefix.
- The window uses a warm cream background and muted-gold controls.
- Long messages wrap within their bubbles when the window is resized.
- Both Enter and the **Send** button submit commands.
- The `bye` command displays the farewell and closes the window.

---

### Test: Ignore blank GUI input

**Aim:** Verify blank input does not create an empty conversation entry or run a command.

**Steps:**
1. Run `./gradlew run`.
2. Leave the command field empty and press Enter.
3. Enter spaces only and click the **Send** button.

**Expected result:**
- No user or Johnny message is added for either submission.
- The application remains open and responsive.

---

### Test: Resize and scroll through a long conversation

**Aim:** Verify the conversation remains readable at the supported window-size limits.

**Steps:**
1. Run `./gradlew run`.
2. Add several tasks with long descriptions, then enter `list`.
3. Resize the window to its minimum width and height.
4. Expand the window again and scroll through the conversation.

**Expected result:**
- Message text wraps within its bubble without being clipped horizontally.
- User messages remain right-aligned and Johnny messages remain left-aligned.
- The header, command field, and **Send** button remain visible at the minimum size.
- The conversation automatically scrolls to each newest response and can still be scrolled manually.
