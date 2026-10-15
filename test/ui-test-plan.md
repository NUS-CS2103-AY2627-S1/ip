# UI Test Plan

Console-output test cases for `Ace` (`src/main/java/Ace.java`), run by the `test-ui` skill.

Each test case starts the program fresh with an empty task list, feeds it a fixed sequence of
commands as stdin, and records the exact stdout the program should produce. Comparison is exact
string equality — spacing inside `[T][ ]`-style status brackets and the `____...` separator lines
are part of the spec, not incidental formatting.

## Format

```markdown
### Test Case N: <short name>

**Aim:** <what this case checks, and why>

**Inputs:**
\```
<command 1>
<command 2>
...
bye
\```

**Expected Output:**
\```
<exact full stdout for the session above>
\```
```

### Test Case 1: Add a todo task and list it

**Aim:** Verify that `todo` adds a task with the correct confirmation message/format, and that `list` displays it correctly.

**Inputs:**
```
todo borrow book
list
bye
```

**Expected Output:**
```
Hello from
   ___   _____  _____
  / _ \ /  __ \|  ___|
 / /_\ \| /  \| |__  
 |  _  || |    |  __| 
 | | | || \__/\| |___ 
 \_| |_/ \____/\____/

____________________________________________________________
 Hello! I'm ACE, your personal poker-themed task manager!
 Shall we begin our game?
____________________________________________________________
____________________________________________________________
Great! I've added a new To-Do card to your hand!
[T][ ] borrow book
You now have a total of 1 cards in your hand!
____________________________________________________________
____________________________________________________________
 1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Cashing out! See you again soon!
```

### Test Case 2: Add a deadline and an event, then list both

**Aim:** Verify that `deadline` and `event` parse their `/by`, `/from`, `/to` arguments correctly (including a real parseable date/time for `deadline`) and format the details as expected, and that `list` displays multiple task types correctly.

**Inputs:**
```
deadline return book /by 2/12/2019 1800
event project meeting /from Mon 2pm /to 4pm
list
bye
```

**Expected Output:**
```
Hello from
   ___   _____  _____
  / _ \ /  __ \|  ___|
 / /_\ \| /  \| |__  
 |  _  || |    |  __| 
 | | | || \__/\| |___ 
 \_| |_/ \____/\____/

____________________________________________________________
 Hello! I'm ACE, your personal poker-themed task manager!
 Shall we begin our game?
____________________________________________________________
____________________________________________________________
Great! I've added a new Deadline card to your hand!
[D][ ] return book (by: Dec 02 2019 at 6:00pm)
You now have a total of 1 cards in your hand!
____________________________________________________________
____________________________________________________________
Great! I've added a new Event card to your hand!
[E][ ] project meeting (from: Mon 2pm to: 4pm)
You now have a total of 2 cards in your hand!
____________________________________________________________
____________________________________________________________
 1.[D][ ] return book (by: Dec 02 2019 at 6:00pm)
 2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Cashing out! See you again soon!
```
