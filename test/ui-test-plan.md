# UI test plan

## Run command

Compile all files in `src/main/java` with Java 25, then run `yawned.Yawned` with the compiled classes on the
classpath. Run every test case in a fresh console session.

Before each case, set `data/Yawned.txt` to the stated **Initial storage** value. `None` means the file must not
exist; restore any pre-existing file after testing.

## Test case: Manage tasks

**Aim:** Verify that a task can be created, marked complete, listed, deleted, and that every confirmation remains
clear while using Yawned's sleepy voice.

**Initial storage:** None

**Inputs:**

```text
todo borrow book
mark 1
list
delete 1
list
```

**Expected output:**

```text
____________________________________________________________

========================
         YAWNED
 Reluctantly organized
========================

*yawn* Yawned is awake enough to help.
What can I do for you?

____________________________________________________________

____________________________________________________________

Noted. I've tucked this into your task list:
  [T][ ] borrow book
You now have 1 task(s).
____________________________________________________________

____________________________________________________________

Finally, I can sleep in peace without this task bothering me... marked complete: [T][X] borrow book
____________________________________________________________

____________________________________________________________

Here's what's keeping you busy:
1.[T][X] borrow book
____________________________________________________________

____________________________________________________________

One less thing to carry around. Removed:
  [T][X] borrow book
0 task(s) remain.
____________________________________________________________

____________________________________________________________

Nothing on the list. A rare moment of peace.
____________________________________________________________

```

## Test case: Explain invalid commands

**Aim:** Verify that invalid input gives an actionable correction without losing Yawned's gentle sleepy personality.

**Initial storage:** None

**Inputs:**

```text
todo
deadline submit report
event meeting /from 2026-01-01 0900
mark
dance
```

**Expected output:**

```text
____________________________________________________________

========================
         YAWNED
 Reluctantly organized
========================

*yawn* Yawned is awake enough to help.
What can I do for you?

____________________________________________________________

____________________________________________________________

I need a task description before I can save it. For example: todo buy milk
____________________________________________________________

____________________________________________________________

I need a /by date and time in yyyy-MM-dd HHmm format. Example: deadline submit report /by 2026-01-01 0900
____________________________________________________________

____________________________________________________________

I need /from and /to times in yyyy-MM-dd HHmm format. Example: event meeting /from 2026-01-01 0900 /to 2026-01-01 1000
____________________________________________________________

____________________________________________________________

Which task should I mark? For example: mark 2
____________________________________________________________

____________________________________________________________

*yawn* I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, find, or alias.
____________________________________________________________

```

## Test case: Recover from unreadable saved tasks

**Aim:** Verify that an unreadable saved task is skipped with a clear, sleepy warning and does not prevent use of
the task list.

**Initial storage:** `T | 2 | damaged`

**Inputs:**

```text
list
```

**Expected output:**

```text
*yawn* I skipped an unreadable saved task on line 1. It seems to have dozed off.
____________________________________________________________

========================
         YAWNED
 Reluctantly organized
========================

*yawn* Yawned is awake enough to help.
What can I do for you?

____________________________________________________________

____________________________________________________________

Nothing on the list. A rare moment of peace.
____________________________________________________________

```

## Test case: List available aliases

**Aim:** Verify that `alias list` shows built-in shortcuts and the empty custom-alias guidance without changing data.

**Initial storage:** None

**Inputs:**

```text
alias list
```

**Expected output:**

```text
____________________________________________________________

========================
         YAWNED
 Reluctantly organized
========================

*yawn* Yawned is awake enough to help.
What can I do for you?

____________________________________________________________

____________________________________________________________

Here are the shortcuts I know:

Built-in:
  t -> todo
  d -> deadline
  e -> event
  l -> list
  m -> mark
  u -> unmark
  del -> delete
  f -> find

No custom aliases yet. Add one with: alias <name> <command>
____________________________________________________________

```

## Test case: Use aliases and find tasks

**Aim:** Verify that custom and built-in aliases retain their behavior and responses are consistent with the new voice.

**Initial storage:** None

**Inputs:**

```text
alias hw todo
HW finish assignment
f assignment
alias remove hw
hw another task
```

**Expected output:**

```text
____________________________________________________________

========================
         YAWNED
 Reluctantly organized
========================

*yawn* Yawned is awake enough to help.
What can I do for you?

____________________________________________________________

____________________________________________________________

All set. Alias 'hw' now runs 'todo'. *yawn* Remembering another alias is tiring.
____________________________________________________________

____________________________________________________________

Noted. I've tucked this into your task list:
  [T][ ] finish assignment
You now have 1 task(s).
____________________________________________________________

____________________________________________________________

I found these before my attention drifted:
1.[T][ ] finish assignment
____________________________________________________________

____________________________________________________________

All set. Alias 'hw' has been removed. One less thing to remember - what a relief.
____________________________________________________________

____________________________________________________________

*yawn* I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, find, or alias.
____________________________________________________________

```
