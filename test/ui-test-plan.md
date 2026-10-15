# Jelly UI test plan

The tests run in order. The first case creates and modifies the saved task
list; the second case starts Jelly again and verifies that the saved state is
loaded from disk.

## Additional error-handling cases

The following inputs must be rejected without changing the task count:

```text
 todo buy milk
todo  buy milk
event meeting /from 2026-02-30 1000 /to 2026-02-30 1100
event meeting /from 2026-02-01 1000 /to 2026-02-01 1000
todo valid task
bye
```

The first two commands are rejected for invalid spacing. The next two are
rejected for an impossible date and a non-positive event duration. Only the
valid to-do is added.

## Test case: Create, modify, validate, and save tasks

### Aim
Verify that Jelly creates all supported task types, rejects malformed input,
marks and unmarks tasks, deletes a task, and saves the final task list.

### Input
```text
todo buy milk
deadline submit report /by 2019-12-02 1800
event team meeting /from 2019-12-02 1000 /to 2019-12-02 1100
blah
todo bad | data
help
mark 1
unmark 1
delete 2
list
find milk
bye
```

### Expected output
```text
╭──────────────────────╮
│      J E L L Y       │
│                      │
│       .-""""-.       │
│     .'  o  o  '.     │
│    /      ∆     \    │
│    \    '---'   /    │
│     '._      _.'     │
│        `----`        │
╰──────────────────────╯

Hello! I'm Jelly, your squishy little assistant!
Let's make your tasks stick! :)
Boop! This task is now stuck to your jar:
   [T][ ] buy milk
Now you have 1 tasks in your Jelly jar~
Boop! This deadline is now stuck to your jar:
   [D][ ] submit report (by: Dec 02 2019 18:00)
Now you have 2 tasks in your Jelly jar~
Boop! This event is now tucked into your jar:
   [E][ ] team meeting (from: Dec 02 2019 10:00 to: Dec 02 2019 11:00)
Now you have 3 tasks in your Jelly jar~
Jelly doesn't recognize your command. Try again~
Please enter a valid command: Task description must be non-blank and cannot contain ' | '.
Available Jelly commands:
todo <description>
deadline <description> /by yyyy-MM-dd HHmm
event <description> /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm
list
find <keyword>
mark <task number>
unmark <task number>
tag <task number> <tag>
untag <task number>
delete <task number>
help
bye
Nice wobble! Jelly marked this task as done~
   [X] buy milk
No wobble—Jelly marked this task as not done yet~
   [ ] buy milk
Splat! Jelly removed that task from the jar :)
[D][ ] submit report (by: Dec 02 2019 18:00)
Now you have 2 tasks in your Jelly jar~
Your Jelly Jar :)

1.[T][ ] buy milk
2.[E][ ] team meeting (from: Dec 02 2019 10:00 to: Dec 02 2019 11:00)

Here are the tasks Jelly found in your jar:
1.[T][ ] buy milk
Bye! Stay jiggly~
```

## Test case: Load saved tasks after restarting

### Aim
Verify that the remaining to-do and event are loaded from disk with their
correct task types and completion states.

### Input
```text
list
bye
```

### Expected output
```text
╭──────────────────────╮
│      J E L L Y       │
│                      │
│       .-""""-.       │
│     .'  o  o  '.     │
│    /      ∆     \    │
│    \    '---'   /    │
│     '._      _.'     │
│        `----`        │
╰──────────────────────╯

Hello! I'm Jelly, your squishy little assistant!
Let's make your tasks stick! :)
Your Jelly Jar :)

1.[T][ ] buy milk
2.[E][ ] team meeting (from: Dec 02 2019 10:00 to: Dec 02 2019 11:00)

Bye! Stay jiggly~
```
