# Console UI test plan

## Execution details

- Run `./gradlew classes` with Java 25 before starting this test session.
- Run each case from the repository root. Each command creates a temporary working directory so saved tasks from one case do not affect another.
- The `Console input` block shows text sent to standard input. It is not expected to be echoed by the program.
- Compare combined standard output and standard error with the `Expected output` block exactly after converting CRLF line endings to LF. Blank lines are significant.
- Run cases in order and stop at the first mismatch.

## Test case 1 — Welcome and exit

**Aim:** Verify that the application greets the user and exits politely.

**Console input:**

```text
bye
```

**Run:**

```sh
repo_root=$(pwd); case_dir=$(mktemp -d); (cd "$case_dir" && printf 'bye\n' | java -cp "$repo_root/build/classes/java/main" koko.Koko) 2>&1
```

**Expected output:**

```text
 _  __     _          
| |/ /___ | | _____   
| ' // _ \| |/ / _ \  
| . \ (_) |   < (_) | 
|_|\_\___/|_|\_\___/  

Chirp! I'm Koko the Taskbird. What shall we get flying today?
Fly high! Koko will keep the nest cozy. See you soon!
```

## Test case 2 — Partially update every task type

**Aim:** Verify that `update` changes only supplied fields, preserves an event's done status, and supports to-dos, deadlines, and events.

**Console input:**

```text
event project meeting /from 2026-09-20 1400 /to 2026-09-20 1600
mark 1
update 1 /to 2026-09-20 1800
deadline submit draft /by 2026-09-18 2359
update 2 /desc submit final draft /by 2026-09-19 1200
todo read chapter 2
update 3 /desc read chapter 3
list
bye
```

**Run:**

```sh
repo_root=$(pwd); case_dir=$(mktemp -d); (cd "$case_dir" && printf 'event project meeting /from 2026-09-20 1400 /to 2026-09-20 1600\nmark 1\nupdate 1 /to 2026-09-20 1800\ndeadline submit draft /by 2026-09-18 2359\nupdate 2 /desc submit final draft /by 2026-09-19 1200\ntodo read chapter 2\nupdate 3 /desc read chapter 3\nlist\nbye\n' | java -cp "$repo_root/build/classes/java/main" koko.Koko) 2>&1
```

**Expected output:**

```text
 _  __     _          
| |/ /___ | | _____   
| ' // _ \| |/ / _ \  
| . \ (_) |   < (_) | 
|_|\_\___/|_|\_\___/  

Chirp! I'm Koko the Taskbird. What shall we get flying today?
____________________________________________________________
Chirp-chirp! I've tucked this task into your nest:
  [E][ ] project meeting (from: Sept 20 2026, 2:00 pm to: Sept 20 2026, 4:00 pm)
Now you have 1 tasks in the nest.
____________________________________________________________
____________________________________________________________
Wing-tastic! This task is now done:
  [E][X] project meeting (from: Sept 20 2026, 2:00 pm to: Sept 20 2026, 4:00 pm)
____________________________________________________________
____________________________________________________________
Freshly fluffed! I've updated this task:
  [E][X] project meeting (from: Sept 20 2026, 2:00 pm to: Sept 20 2026, 6:00 pm)
____________________________________________________________
____________________________________________________________
Chirp-chirp! I've tucked this task into your nest:
  [D][ ] submit draft (by: Sept 18 2026, 11:59 pm)
Now you have 2 tasks in the nest.
____________________________________________________________
____________________________________________________________
Freshly fluffed! I've updated this task:
  [D][ ] submit final draft (by: Sept 19 2026, 12:00 pm)
____________________________________________________________
____________________________________________________________
Chirp-chirp! I've tucked this task into your nest:
  [T][ ] read chapter 2
Now you have 3 tasks in the nest.
____________________________________________________________
____________________________________________________________
Freshly fluffed! I've updated this task:
  [T][ ] read chapter 3
____________________________________________________________
____________________________________________________________
Nest check! Here are the tasks in your nest:
1.[E][X] project meeting (from: Sept 20 2026, 2:00 pm to: Sept 20 2026, 6:00 pm)
2.[D][ ] submit final draft (by: Sept 19 2026, 12:00 pm)
3.[T][ ] read chapter 3
____________________________________________________________
Fly high! Koko will keep the nest cozy. See you soon!
```

## Test case 3 — Reject invalid updates

**Aim:** Verify that update rejects fields unsuitable for a task, missing fields, repeated fields, and invalid event time ranges.

**Console input:**

```text
todo read book
update 1 /by 2026-09-20 1800
update 1
update 1 /desc first /desc second
event meeting /from 2026-09-20 1400 /to 2026-09-20 1600
update 2 /to 2026-09-20 1300
bye
```

**Run:**

```sh
repo_root=$(pwd); case_dir=$(mktemp -d); (cd "$case_dir" && printf 'todo read book\nupdate 1 /by 2026-09-20 1800\nupdate 1\nupdate 1 /desc first /desc second\nevent meeting /from 2026-09-20 1400 /to 2026-09-20 1600\nupdate 2 /to 2026-09-20 1300\nbye\n' | java -cp "$repo_root/build/classes/java/main" koko.Koko) 2>&1
```

**Expected output:**

```text
 _  __     _          
| |/ /___ | | _____   
| ' // _ \| |/ / _ \  
| . \ (_) |   < (_) | 
|_|\_\___/|_|\_\___/  

Chirp! I'm Koko the Taskbird. What shall we get flying today?
____________________________________________________________
Chirp-chirp! I've tucked this task into your nest:
  [T][ ] read book
Now you have 1 tasks in the nest.
____________________________________________________________
____________________________________________________________
Squawk! That field cannot be updated for this task.
____________________________________________________________
____________________________________________________________
Squawk! I need at least one field to update. Try: update 2 /desc new description.
____________________________________________________________
____________________________________________________________
Squawk! Each update field can be specified only once.
____________________________________________________________
____________________________________________________________
Chirp-chirp! I've tucked this task into your nest:
  [E][ ] meeting (from: Sept 20 2026, 2:00 pm to: Sept 20 2026, 4:00 pm)
Now you have 2 tasks in the nest.
____________________________________________________________
____________________________________________________________
Squawk! An event cannot end before it starts.
____________________________________________________________
Fly high! Koko will keep the nest cozy. See you soon!
```
