# Historical verification

**Tested tag:** Level-3 (`89ec1dd`)
# UI test session

## 1. Store a Task and mark it complete

**Aim:** Confirm that task text is represented by `Task`, and that marking a task changes its displayed status from incomplete to complete.

**Command:**
```sh
javac -d /tmp/oreo-level3-classes src/main/java/*.java && java -cp /tmp/oreo-level3-classes Oreo
```

**Console input:**
```text
read book
mark 1
list
bye
```

**Expected output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________ 
added: read book 
____________________________________________ 

____________________________________________
Nice! I've marked this task as done:
  [X] read book
____________________________________________
____________________________________________
Here are the tasks in your list:
1.[X] read book
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 


```

**Actual output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________ 
added: read book 
____________________________________________ 

____________________________________________
Nice! I've marked this task as done:
  [X] read book
____________________________________________
____________________________________________
Here are the tasks in your list:
1.[X] read book
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 


```

**Exit status:** `0`

**Result:** PASS

All 1 test case(s) passed.

## 2. Mark and unmark a Task

**Aim:** Confirm that `Task.markAsDone()` displays a completed task and `Task.markAsNotDone()` restores the incomplete status.

**Command:**
```sh
javac -d /tmp/oreo-level3-unmark-classes src/main/java/*.java && java -cp /tmp/oreo-level3-unmark-classes Oreo
```

**Console input:**
```text
read book
mark 1
unmark 1
list
bye
```

**Expected output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________ 
added: read book 
____________________________________________ 

____________________________________________
Nice! I've marked this task as done:
  [X] read book
____________________________________________
____________________________________________
OK, I've marked this task as not done yet:
  [ ] read book
____________________________________________
____________________________________________
Here are the tasks in your list:
1.[ ] read book
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 


```

**Actual output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________ 
added: read book 
____________________________________________ 

____________________________________________
Nice! I've marked this task as done:
  [X] read book
____________________________________________
____________________________________________
OK, I've marked this task as not done yet:
  [ ] read book
____________________________________________
____________________________________________
Here are the tasks in your list:
1.[ ] read book
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 


```

**Exit status:** `0`

**Result:** PASS
