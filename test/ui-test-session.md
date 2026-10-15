# UI test session

## 1. exit-greeting - PASS

Aim: Verify that the application starts and exits politely when the user enters `bye`.

### Command

```text
javac -d build/classes $(find src/main/java -name "*.java" -not -path "*/gui/*") && mkdir -p build/ui-tests/exit-greeting/data && : > build/ui-tests/exit-greeting/data/taskData.txt && (cd build/ui-tests/exit-greeting && java -cp ../../classes hu9o.Hu9o)
```

### Input sent to stdin

```text
bye
```

### Program output

```text
_________________________________
 _   _           ___ 
| | | | | | | | / _ \   ___  
| |_| | | | | || (_) | / _ \ 
|  _  | | |_| | \__,| | (_) |
|_| |_|  \___/   /_/   \___/ 
_________________________________

Give me a second....Loading tasks...
Successful! Use list to view the tasks.
Woof! I'm Hu9o!
What can I do for you?

> 
 Saving data...
 Successfully saved data
_________________________________

Bye. Hope to see you again soon! (wags tail)
_________________________________

```

Exit code: `0`

## 2. load-saved-tasks - PASS

Aim: Verify that Hu9o loads saved todo, deadline, and event tasks, including their completion statuses.

### Command

```text
javac -d build/classes $(find src/main/java -name "*.java" -not -path "*/gui/*") && mkdir -p build/ui-tests/load-saved-tasks/data && printf 'T| |read book|\nD|X|submit assignment|12/08/26 3 PM|\nE| |project meeting|12/08/26 2 PM|12/08/26 4 PM|\n' > build/ui-tests/load-saved-tasks/data/taskData.txt && (cd build/ui-tests/load-saved-tasks && java -cp ../../classes hu9o.Hu9o)
```

### Input sent to stdin

```text
list
bye
```

### Program output

```text
_________________________________
 _   _           ___ 
| | | | | | | | / _ \   ___  
| |_| | | | | || (_) | / _ \ 
|  _  | | |_| | \__,| | (_) |
|_| |_|  \___/   /_/   \___/ 
_________________________________

Give me a second....Loading tasks...
Successful! Use list to view the tasks.
Woof! I'm Hu9o!
What can I do for you?

> _________________________________

1. [T][ ] read book
2. [D][X] submit assignment (by: 12 Aug 2026, 3 PM)
3. [E][ ] project meeting (from: 12 Aug 2026, 2 PM to: 12 Aug 2026, 4 PM)
_________________________________

> 
 Saving data...
 Successfully saved data
_________________________________

Bye. Hope to see you again soon! (wags tail)
_________________________________

```

Exit code: `0`

## 3. dump-tasks-on-exit - PASS

Aim: Verify that tasks created during a session are written to the data file when Hu9o exits.

### Command

```text
javac -d build/classes $(find src/main/java -name "*.java" -not -path "*/gui/*") && mkdir -p build/ui-tests/dump-tasks-on-exit/data && : > build/ui-tests/dump-tasks-on-exit/data/taskData.txt && ((cd build/ui-tests/dump-tasks-on-exit && java -cp ../../classes hu9o.Hu9o) && cat build/ui-tests/dump-tasks-on-exit/data/taskData.txt)
```

### Input sent to stdin

```text
todo read book
deadline submit assignment /by 12/08/26 3 PM
event project meeting /from 12/08/26 2 PM /to 12/08/26 4 PM
mark 2
bye
```

### Program output

```text
_________________________________
 _   _           ___ 
| | | | | | | | / _ \   ___  
| |_| | | | | || (_) | / _ \ 
|  _  | | |_| | \__,| | (_) |
|_| |_|  \___/   /_/   \___/ 
_________________________________

Give me a second....Loading tasks...
Successful! Use list to view the tasks.
Woof! I'm Hu9o!
What can I do for you?

> _________________________________

Got it. I've added this task:
	[T][ ] read book
Now you have 1 task in the list.
_________________________________

> _________________________________

Got it. I've added this task:
	[D][ ] submit assignment (by: 12 Aug 2026, 3 PM)
Now you have 2 tasks in the list.
_________________________________

> _________________________________

Got it. I've added this task:
	[E][ ] project meeting (from: 12 Aug 2026, 2 PM to: 12 Aug 2026, 4 PM)
Now you have 3 tasks in the list.
_________________________________

> _________________________________

Nice! I've marked this task as done:
	[D][X] submit assignment (by: 12 Aug 2026, 3 PM)
_________________________________

> 
 Saving data...
 Successfully saved data
_________________________________

Bye. Hope to see you again soon! (wags tail)
_________________________________

T| |read book|
D|X|submit assignment|12/08/26 3 PM|
E| |project meeting|12/08/26 2 PM|12/08/26 4 PM|
```

Exit code: `0`

## 4. task-lifecycle - PASS

Aim: Verify adding todo, deadline, and event tasks; listing tasks; marking and unmarking a task; deleting a task; and handling an unknown command.

### Command

```text
javac -d build/classes $(find src/main/java -name "*.java" -not -path "*/gui/*") && mkdir -p build/ui-tests/task-lifecycle/data && : > build/ui-tests/task-lifecycle/data/taskData.txt && (cd build/ui-tests/task-lifecycle && java -cp ../../classes hu9o.Hu9o)
```

### Input sent to stdin

```text
todo read book
deadline submit assignment /by 12/08/26 3 PM
event project meeting /from 12/08/26 2 PM /to 12/08/26 4 PM
list
mark 2
unmark 2
delete 2
list
unknown
bye
```

### Program output

```text
_________________________________
 _   _           ___ 
| | | | | | | | / _ \   ___  
| |_| | | | | || (_) | / _ \ 
|  _  | | |_| | \__,| | (_) |
|_| |_|  \___/   /_/   \___/ 
_________________________________

Give me a second....Loading tasks...
Successful! Use list to view the tasks.
Woof! I'm Hu9o!
What can I do for you?

> _________________________________

Got it. I've added this task:
	[T][ ] read book
Now you have 1 task in the list.
_________________________________

> _________________________________

Got it. I've added this task:
	[D][ ] submit assignment (by: 12 Aug 2026, 3 PM)
Now you have 2 tasks in the list.
_________________________________

> _________________________________

Got it. I've added this task:
	[E][ ] project meeting (from: 12 Aug 2026, 2 PM to: 12 Aug 2026, 4 PM)
Now you have 3 tasks in the list.
_________________________________

> _________________________________

1. [T][ ] read book
2. [D][ ] submit assignment (by: 12 Aug 2026, 3 PM)
3. [E][ ] project meeting (from: 12 Aug 2026, 2 PM to: 12 Aug 2026, 4 PM)
_________________________________

> _________________________________

Nice! I've marked this task as done:
	[D][X] submit assignment (by: 12 Aug 2026, 3 PM)
_________________________________

> _________________________________

Ok, I've marked this task as not done yet:
	[D][ ] submit assignment (by: 12 Aug 2026, 3 PM)
_________________________________

> _________________________________

Got it. Deleted the following task:
	[D][ ] submit assignment (by: 12 Aug 2026, 3 PM)
_________________________________

> _________________________________

1. [T][ ] read book
2. [E][ ] project meeting (from: 12 Aug 2026, 2 PM to: 12 Aug 2026, 4 PM)
_________________________________

> _________________________________

Unknown command, woof! Here's what I understand -- Tasks: todo, list, find, deadline, event, mark, unmark, delete, progress, bye. Contacts: person, people, findperson, deleteperson, link, connections, select, deselect.
_________________________________

> 
 Saving data...
 Successfully saved data
_________________________________

Bye. Hope to see you again soon! (wags tail)
_________________________________

```

Exit code: `0`

## 5. command-errors - PASS

Aim: Verify that each invalid command form is handled by its specific Hu9oException without ending the program.

### Command

```text
javac -d build/classes $(find src/main/java -name "*.java" -not -path "*/gui/*") && mkdir -p build/ui-tests/command-errors/data && : > build/ui-tests/command-errors/data/taskData.txt && (cd build/ui-tests/command-errors && java -cp ../../classes hu9o.Hu9o)
```

### Input sent to stdin

```text
todo
mark 0
unmark 2
delete 0
deadline report /at Friday
event study /from 2pm /until 4pm
unknown
bye
```

### Program output

```text
_________________________________
 _   _           ___ 
| | | | | | | | / _ \   ___  
| |_| | | | | || (_) | / _ \ 
|  _  | | |_| | \__,| | (_) |
|_| |_|  \___/   /_/   \___/ 
_________________________________

Give me a second....Loading tasks...
Successful! Use list to view the tasks.
Woof! I'm Hu9o!
What can I do for you?

> _________________________________

Ruff, I need a description for that todo! Try: todo DESCRIPTION
_________________________________

> _________________________________

I can't find that task! Check the list of items using "list"
_________________________________

> _________________________________

I can't find that task! Check the list of items using "list"
_________________________________

> _________________________________

I can't find that task to delete! Check the list of items using "list"
_________________________________

> _________________________________

That deadline's missing something! Try: deadline DESCRIPTION /by DATE
_________________________________

> _________________________________

That event's missing something! Try: event DESCRIPTION /from START /to END
_________________________________

> _________________________________

Unknown command, woof! Here's what I understand -- Tasks: todo, list, find, deadline, event, mark, unmark, delete, progress, bye. Contacts: person, people, findperson, deleteperson, link, connections, select, deselect.
_________________________________

> 
 Saving data...
 Successfully saved data
_________________________________

Bye. Hope to see you again soon! (wags tail)
_________________________________

```

Exit code: `0`

## 6. find-tasks - PASS

Aim: Verify that `find KEYWORD` lists the loaded tasks whose description contains the keyword (case-insensitively), reports when nothing matches, and rejects a find command with no keyword.

### Command

```text
javac -d build/classes $(find src/main/java -name "*.java" -not -path "*/gui/*") && mkdir -p build/ui-tests/find-tasks/data && printf 'T|X|read book|\nD|X|return book|06/06/26 3 PM|\nT| |wash car|\n' > build/ui-tests/find-tasks/data/taskData.txt && (cd build/ui-tests/find-tasks && java -cp ../../classes hu9o.Hu9o)
```

### Input sent to stdin

```text
find book
find plants
find
bye
```

### Program output

```text
_________________________________
 _   _           ___ 
| | | | | | | | / _ \   ___  
| |_| | | | | || (_) | / _ \ 
|  _  | | |_| | \__,| | (_) |
|_| |_|  \___/   /_/   \___/ 
_________________________________

Give me a second....Loading tasks...
Successful! Use list to view the tasks.
Woof! I'm Hu9o!
What can I do for you?

> _________________________________

Here are the matching tasks in your list:
1. [T][X] read book
2. [D][X] return book (by: 06 Jun 2026, 3 PM)
_________________________________

> _________________________________

No matching tasks found.
_________________________________

> _________________________________

What should I search for? Try: find KEYWORD
_________________________________

> 
 Saving data...
 Successfully saved data
_________________________________

Bye. Hope to see you again soon! (wags tail)
_________________________________

```

Exit code: `0`

## 7. person-lifecycle - PASS

Aim: Verify adding, listing, finding, linking, viewing connections, and deleting people in the contact network, and that an unrecognized command still produces the standard unknown-command error.

### Command

```text
javac -d build/classes $(find src/main/java -name "*.java" -not -path "*/gui/*") && mkdir -p build/ui-tests/person-lifecycle/data && : > build/ui-tests/person-lifecycle/data/taskData.txt && (cd build/ui-tests/person-lifecycle && java -cp ../../classes hu9o.Hu9o)
```

### Input sent to stdin

```text
person Alice Tan /phone 91112222 /email alice@example.com
person Bob Lee /phone 93334444 /email bob@example.com
people
findperson Bob
link Alice Tan /with Bob Lee
connections Alice Tan
deleteperson 2
people
frobnicate
bye
```

### Program output

```text
_________________________________
 _   _           ___ 
| | | | | | | | / _ \   ___  
| |_| | | | | || (_) | / _ \ 
|  _  | | |_| | \__,| | (_) |
|_| |_|  \___/   /_/   \___/ 
_________________________________

Give me a second....Loading tasks...
Successful! Use list to view the tasks.
Woof! I'm Hu9o!
What can I do for you?

> _________________________________

Got it. I've added this person:
	Alice Tan (Phone: 91112222, Email: alice@example.com)
Now you have 1 person in the network.
_________________________________

> _________________________________

Got it. I've added this person:
	Bob Lee (Phone: 93334444, Email: bob@example.com)
Now you have 2 people in the network.
_________________________________

> _________________________________

1. Alice Tan (Phone: 91112222, Email: alice@example.com)
2. Bob Lee (Phone: 93334444, Email: bob@example.com)
_________________________________

> _________________________________

Here are the matching people in your network:
1. Bob Lee (Phone: 93334444, Email: bob@example.com)
_________________________________

> _________________________________

Got it. Linked Alice Tan and Bob Lee.
_________________________________

> _________________________________

Alice Tan is connected to:
1. Bob Lee
_________________________________

> _________________________________

Got it. Deleted the following person:
	Bob Lee (Phone: 93334444, Email: bob@example.com)
_________________________________

> _________________________________

1. Alice Tan (Phone: 91112222, Email: alice@example.com)
_________________________________

> _________________________________

Unknown command, woof! Here's what I understand -- Tasks: todo, list, find, deadline, event, mark, unmark, delete, progress, bye. Contacts: person, people, findperson, deleteperson, link, connections, select, deselect.
_________________________________

> 
 Saving data...
 Successfully saved data
_________________________________

Bye. Hope to see you again soon! (wags tail)
_________________________________

```

Exit code: `0`

## 8. person-task-session - PASS

Aim: Verify that selecting a person redirects todo/deadline/event/list/mark/unmark/delete to their own task list, and that deselecting reverts to the global list, which stays untouched throughout.

### Command

```text
javac -d build/classes $(find src/main/java -name "*.java" -not -path "*/gui/*") && mkdir -p build/ui-tests/person-task-session/data && : > build/ui-tests/person-task-session/data/taskData.txt && (cd build/ui-tests/person-task-session && java -cp ../../classes hu9o.Hu9o)
```

### Input sent to stdin

```text
person Carol Ng /phone 95556666 /email carol@example.com
select Carol Ng
todo buy milk
deadline submit report /by 12/08/26 3 PM
event team meeting /from 12/08/26 2 PM /to 12/08/26 4 PM
list
mark 1
unmark 1
delete 2
list
deselect
list
bye
```

### Program output

```text
_________________________________
 _   _           ___ 
| | | | | | | | / _ \   ___  
| |_| | | | | || (_) | / _ \ 
|  _  | | |_| | \__,| | (_) |
|_| |_|  \___/   /_/   \___/ 
_________________________________

Give me a second....Loading tasks...
Successful! Use list to view the tasks.
Woof! I'm Hu9o!
What can I do for you?

> _________________________________

Got it. I've added this person:
	Carol Ng (Phone: 95556666, Email: carol@example.com)
Now you have 1 person in the network.
_________________________________

> _________________________________

Now viewing Carol Ng's list. todo/list/mark/... apply to them until you deselect.
_________________________________

> _________________________________

Got it. I've added this task:
	[T][ ] buy milk
Now you have 1 task in the list.
_________________________________

> _________________________________

Got it. I've added this task:
	[D][ ] submit report (by: 12 Aug 2026, 3 PM)
Now you have 2 tasks in the list.
_________________________________

> _________________________________

Got it. I've added this task:
	[E][ ] team meeting (from: 12 Aug 2026, 2 PM to: 12 Aug 2026, 4 PM)
Now you have 3 tasks in the list.
_________________________________

> _________________________________

1. [T][ ] buy milk
2. [D][ ] submit report (by: 12 Aug 2026, 3 PM)
3. [E][ ] team meeting (from: 12 Aug 2026, 2 PM to: 12 Aug 2026, 4 PM)
_________________________________

> _________________________________

Nice! I've marked this task as done:
	[T][X] buy milk
_________________________________

> _________________________________

Ok, I've marked this task as not done yet:
	[T][ ] buy milk
_________________________________

> _________________________________

Got it. Deleted the following task:
	[D][ ] submit report (by: 12 Aug 2026, 3 PM)
_________________________________

> _________________________________

1. [T][ ] buy milk
2. [E][ ] team meeting (from: 12 Aug 2026, 2 PM to: 12 Aug 2026, 4 PM)
_________________________________

> _________________________________

Back to your own list.
_________________________________

> _________________________________

_________________________________

> 
 Saving data...
 Successfully saved data
_________________________________

Bye. Hope to see you again soon! (wags tail)
_________________________________

```

Exit code: `0`

## 9. contact-save-format - PASS

Aim: Verify that people, a link between them, and a person's own task are written to data/contactData.txt, data/contactLinks.txt, and data/contacts/1.txt in the expected pipe-delimited format on exit.

### Command

```text
javac -d build/classes $(find src/main/java -name "*.java" -not -path "*/gui/*") && mkdir -p build/ui-tests/contact-save-format/data && : > build/ui-tests/contact-save-format/data/taskData.txt && ((cd build/ui-tests/contact-save-format && java -cp ../../classes hu9o.Hu9o) && cat build/ui-tests/contact-save-format/data/contactData.txt && cat build/ui-tests/contact-save-format/data/contactLinks.txt && cat build/ui-tests/contact-save-format/data/contacts/1.txt)
```

### Input sent to stdin

```text
person Dan Ho /phone 91230000 /email dan@example.com
person Eve Koh /phone 98760000 /email eve@example.com /dob 5/5/95 /notes vegetarian
link Dan Ho /with Eve Koh
select Dan Ho
todo call supplier
deselect
bye
```

### Program output

```text
_________________________________
 _   _           ___ 
| | | | | | | | / _ \   ___  
| |_| | | | | || (_) | / _ \ 
|  _  | | |_| | \__,| | (_) |
|_| |_|  \___/   /_/   \___/ 
_________________________________

Give me a second....Loading tasks...
Successful! Use list to view the tasks.
Woof! I'm Hu9o!
What can I do for you?

> _________________________________

Got it. I've added this person:
	Dan Ho (Phone: 91230000, Email: dan@example.com)
Now you have 1 person in the network.
_________________________________

> _________________________________

Got it. I've added this person:
	Eve Koh (Phone: 98760000, Email: eve@example.com, DOB: 5/5/95, Notes: vegetarian)
Now you have 2 people in the network.
_________________________________

> _________________________________

Got it. Linked Dan Ho and Eve Koh.
_________________________________

> _________________________________

Now viewing Dan Ho's list. todo/list/mark/... apply to them until you deselect.
_________________________________

> _________________________________

Got it. I've added this task:
	[T][ ] call supplier
Now you have 1 task in the list.
_________________________________

> _________________________________

Back to your own list.
_________________________________

> 
 Saving data...
 Successfully saved data
_________________________________

Bye. Hope to see you again soon! (wags tail)
_________________________________

Dan Ho|91230000|dan@example.com||
Eve Koh|98760000|eve@example.com|5/5/95|vegetarian
Dan Ho|Eve Koh
T| |call supplier|
```

Exit code: `0`

## 10. contact-load-from-seed - PASS

Aim: Verify that Hu9o loads pre-saved people (with and without biodata), a link between them, and one person's own saved tasks, all correctly on start-up.

### Command

```text
javac -d build/classes $(find src/main/java -name "*.java" -not -path "*/gui/*") && mkdir -p build/ui-tests/contact-load-from-seed/data/contacts && : > build/ui-tests/contact-load-from-seed/data/taskData.txt && printf 'Frank Sim|91110000|frank@example.com||\nGrace Yeo|92220000|grace@example.com|3/3/92|allergic to nuts\n' > build/ui-tests/contact-load-from-seed/data/contactData.txt && printf 'Frank Sim|Grace Yeo\n' > build/ui-tests/contact-load-from-seed/data/contactLinks.txt && printf 'T|X|call Grace|\nD| |renew passport|20/12/26 5 PM|\n' > build/ui-tests/contact-load-from-seed/data/contacts/1.txt && (cd build/ui-tests/contact-load-from-seed && java -cp ../../classes hu9o.Hu9o)
```

### Input sent to stdin

```text
people
select Frank Sim
list
deselect
connections Grace Yeo
bye
```

### Program output

```text
_________________________________
 _   _           ___ 
| | | | | | | | / _ \   ___  
| |_| | | | | || (_) | / _ \ 
|  _  | | |_| | \__,| | (_) |
|_| |_|  \___/   /_/   \___/ 
_________________________________

Give me a second....Loading tasks...
Successful! Use list to view the tasks.
Woof! I'm Hu9o!
What can I do for you?

> _________________________________

1. Frank Sim (Phone: 91110000, Email: frank@example.com)
2. Grace Yeo (Phone: 92220000, Email: grace@example.com, DOB: 3/3/92, Notes: allergic to nuts)
_________________________________

> _________________________________

Now viewing Frank Sim's list. todo/list/mark/... apply to them until you deselect.
_________________________________

> _________________________________

1. [T][X] call Grace
2. [D][ ] renew passport (by: 20 Dec 2026, 5 PM)
_________________________________

> _________________________________

Back to your own list.
_________________________________

> _________________________________

Grace Yeo is connected to:
1. Frank Sim
_________________________________

> 
 Saving data...
 Successfully saved data
_________________________________

Bye. Hope to see you again soon! (wags tail)
_________________________________

```

Exit code: `0`

## 11. contact-command-errors - PASS

Aim: Verify that each invalid contact command form is handled by its specific Hu9oException without ending the program: bad person format, a duplicate-free unknown select/link target, a self-link, an out-of-range deleteperson index, and empty findperson/connections keywords.

### Command

```text
javac -d build/classes $(find src/main/java -name "*.java" -not -path "*/gui/*") && mkdir -p build/ui-tests/contact-command-errors/data && : > build/ui-tests/contact-command-errors/data/taskData.txt && (cd build/ui-tests/contact-command-errors && java -cp ../../classes hu9o.Hu9o)
```

### Input sent to stdin

```text
person Henry Goh
person Henry Goh /phone 91112222 /email henry@example.com
select Nobody Here
link Henry Goh /with Nobody Here
link Henry Goh /with Henry Goh
deleteperson 0
findperson
connections
bye
```

### Program output

```text
_________________________________
 _   _           ___ 
| | | | | | | | / _ \   ___  
| |_| | | | | || (_) | / _ \ 
|  _  | | |_| | \__,| | (_) |
|_| |_|  \___/   /_/   \___/ 
_________________________________

Give me a second....Loading tasks...
Successful! Use list to view the tasks.
Woof! I'm Hu9o!
What can I do for you?

> _________________________________

That person's missing some details! Try: person NAME /phone PHONE /email EMAIL [/dob DOB] [/notes NOTES]
_________________________________

> _________________________________

Got it. I've added this person:
	Henry Goh (Phone: 91112222, Email: henry@example.com)
Now you have 1 person in the network.
_________________________________

> _________________________________

No person with that name! Check the list of people using "people"
_________________________________

> _________________________________

No person with that name! Check the list of people using "people"
_________________________________

> _________________________________

You can't link someone to themselves, silly!
_________________________________

> _________________________________

I can't find that person to delete! Check the list of people using "people"
_________________________________

> _________________________________

Who should I search for? Try: findperson KEYWORD
_________________________________

> _________________________________

Whose connections do you want to see? Try: connections NAME
_________________________________

> 
 Saving data...
 Successfully saved data
_________________________________

Bye. Hope to see you again soon! (wags tail)
_________________________________

```

Exit code: `0`

## 12. global-list-unaffected-by-contacts - PASS

Aim: Verify that adding a person does not change the behaviour or output of any existing task command: the rest of this input is byte-for-byte the same task-lifecycle sequence as the task-lifecycle test case, just preceded by one person command.

### Command

```text
javac -d build/classes $(find src/main/java -name "*.java" -not -path "*/gui/*") && mkdir -p build/ui-tests/global-list-unaffected-by-contacts/data && : > build/ui-tests/global-list-unaffected-by-contacts/data/taskData.txt && (cd build/ui-tests/global-list-unaffected-by-contacts && java -cp ../../classes hu9o.Hu9o)
```

### Input sent to stdin

```text
person Ivy Chua /phone 91112222 /email ivy@example.com
todo read book
deadline submit assignment /by 12/08/26 3 PM
event project meeting /from 12/08/26 2 PM /to 12/08/26 4 PM
list
mark 2
unmark 2
delete 2
list
unknown
bye
```

### Program output

```text
_________________________________
 _   _           ___ 
| | | | | | | | / _ \   ___  
| |_| | | | | || (_) | / _ \ 
|  _  | | |_| | \__,| | (_) |
|_| |_|  \___/   /_/   \___/ 
_________________________________

Give me a second....Loading tasks...
Successful! Use list to view the tasks.
Woof! I'm Hu9o!
What can I do for you?

> _________________________________

Got it. I've added this person:
	Ivy Chua (Phone: 91112222, Email: ivy@example.com)
Now you have 1 person in the network.
_________________________________

> _________________________________

Got it. I've added this task:
	[T][ ] read book
Now you have 1 task in the list.
_________________________________

> _________________________________

Got it. I've added this task:
	[D][ ] submit assignment (by: 12 Aug 2026, 3 PM)
Now you have 2 tasks in the list.
_________________________________

> _________________________________

Got it. I've added this task:
	[E][ ] project meeting (from: 12 Aug 2026, 2 PM to: 12 Aug 2026, 4 PM)
Now you have 3 tasks in the list.
_________________________________

> _________________________________

1. [T][ ] read book
2. [D][ ] submit assignment (by: 12 Aug 2026, 3 PM)
3. [E][ ] project meeting (from: 12 Aug 2026, 2 PM to: 12 Aug 2026, 4 PM)
_________________________________

> _________________________________

Nice! I've marked this task as done:
	[D][X] submit assignment (by: 12 Aug 2026, 3 PM)
_________________________________

> _________________________________

Ok, I've marked this task as not done yet:
	[D][ ] submit assignment (by: 12 Aug 2026, 3 PM)
_________________________________

> _________________________________

Got it. Deleted the following task:
	[D][ ] submit assignment (by: 12 Aug 2026, 3 PM)
_________________________________

> _________________________________

1. [T][ ] read book
2. [E][ ] project meeting (from: 12 Aug 2026, 2 PM to: 12 Aug 2026, 4 PM)
_________________________________

> _________________________________

Unknown command, woof! Here's what I understand -- Tasks: todo, list, find, deadline, event, mark, unmark, delete, progress, bye. Contacts: person, people, findperson, deleteperson, link, connections, select, deselect.
_________________________________

> 
 Saving data...
 Successfully saved data
_________________________________

Bye. Hope to see you again soon! (wags tail)
_________________________________

```

Exit code: `0`

## 13. progress-command - PASS

Aim: Verify the progress command on an empty list, a partially completed list, and a fully completed list.

### Command

```text
javac -d build/classes $(find src/main/java -name "*.java" -not -path "*/gui/*") && mkdir -p build/ui-tests/progress-command/data && : > build/ui-tests/progress-command/data/taskData.txt && (cd build/ui-tests/progress-command && java -cp ../../classes hu9o.Hu9o)
```

### Input sent to stdin

```text
progress
todo read book
todo write report
mark 1
progress
mark 2
progress
bye
```

### Program output

```text
_________________________________
 _   _           ___ 
| | | | | | | | / _ \   ___  
| |_| | | | | || (_) | / _ \ 
|  _  | | |_| | \__,| | (_) |
|_| |_|  \___/   /_/   \___/ 
_________________________________

Give me a second....Loading tasks...
Successful! Use list to view the tasks.
Woof! I'm Hu9o!
What can I do for you?

> _________________________________

No tasks yet -- nothing to track progress on!
_________________________________

> _________________________________

Got it. I've added this task:
	[T][ ] read book
Now you have 1 task in the list.
_________________________________

> _________________________________

Got it. I've added this task:
	[T][ ] write report
Now you have 2 tasks in the list.
_________________________________

> _________________________________

Nice! I've marked this task as done:
	[T][X] read book
_________________________________

> _________________________________

[██████████░░░░░░░░░░] 1/2 tasks completed (50%)
_________________________________

> _________________________________

Nice! I've marked this task as done:
	[T][X] write report
_________________________________

> _________________________________

[████████████████████] 2/2 tasks completed (100%)
_________________________________

> 
 Saving data...
 Successfully saved data
_________________________________

Bye. Hope to see you again soon! (wags tail)
_________________________________

```

Exit code: `0`

## 14. greeting-command - PASS

Aim: Verify that a greeting such as "hi" gets a friendly reply instead of an unknown-command error, and that it does not disturb the task list.

### Command

```text
javac -d build/classes $(find src/main/java -name "*.java" -not -path "*/gui/*") && mkdir -p build/ui-tests/greeting-command/data && : > build/ui-tests/greeting-command/data/taskData.txt && (cd build/ui-tests/greeting-command && java -cp ../../classes hu9o.Hu9o)
```

### Input sent to stdin

```text
hi
hello
todo read book
bye
```

### Program output

```text
_________________________________
 _   _           ___ 
| | | | | | | | / _ \   ___  
| |_| | | | | || (_) | / _ \ 
|  _  | | |_| | \__,| | (_) |
|_| |_|  \___/   /_/   \___/ 
_________________________________

Give me a second....Loading tasks...
Successful! Use list to view the tasks.
Woof! I'm Hu9o!
What can I do for you?

> _________________________________

Woof! Hello there! What can I do for you?
_________________________________

> _________________________________

Woof! Hello there! What can I do for you?
_________________________________

> _________________________________

Got it. I've added this task:
	[T][ ] read book
Now you have 1 task in the list.
_________________________________

> 
 Saving data...
 Successfully saved data
_________________________________

Bye. Hope to see you again soon! (wags tail)
_________________________________

```

Exit code: `0`

## 15. auto-create-missing-data-files - PASS

Aim: Verify that Storage and ContactStorage create an empty save file (and the data/ folder) as soon as loading finds one missing, rather than only after the first save. The program is given no input at all, so it crashes on EOF right after loading and before it can ever reach a save -- its own output is discarded, and only the resulting data/ folder contents are checked.

### Command

```text
javac -d build/classes $(find src/main/java -name "*.java" -not -path "*/gui/*") && rm -rf build/ui-tests/auto-create-missing-data-files && mkdir -p build/ui-tests/auto-create-missing-data-files && ((cd build/ui-tests/auto-create-missing-data-files && java -cp ../../classes hu9o.Hu9o > /dev/null 2>&1) ; true) && find build/ui-tests/auto-create-missing-data-files/data -type f | sort
```

### Input sent to stdin

```text

```

### Program output

```text
build/ui-tests/auto-create-missing-data-files/data/contactData.txt
build/ui-tests/auto-create-missing-data-files/data/contactLinks.txt
build/ui-tests/auto-create-missing-data-files/data/taskData.txt
```

Exit code: `0`

## 16. date-validation - PASS

Aim: Verify that a date with minutes (h[mm] a, e.g. 630 PM) and a lower-case pm are accepted; that a nonexistent date (31 Feb) is rejected instead of being moved to 28 Feb; that an event ending before it starts is rejected; and that the saved file keeps the minutes in the storage format.

### Command

```text
javac -d build/classes $(find src/main/java -name "*.java" -not -path "*/gui/*") && rm -rf build/ui-tests/date-validation && mkdir -p build/ui-tests/date-validation/data && : > build/ui-tests/date-validation/data/taskData.txt && ((cd build/ui-tests/date-validation && java -cp ../../classes hu9o.Hu9o) && cat build/ui-tests/date-validation/data/taskData.txt)
```

### Input sent to stdin

```text
deadline minute test /by 20/9/26 630 PM
deadline lower case /by 20/9/26 330 pm
deadline impossible /by 31/2/26 6 PM
event backwards /from 21/9/26 4 PM /to 21/9/26 2 PM
list
bye
```

### Program output

```text
_________________________________
 _   _           ___ 
| | | | | | | | / _ \   ___  
| |_| | | | | || (_) | / _ \ 
|  _  | | |_| | \__,| | (_) |
|_| |_|  \___/   /_/   \___/ 
_________________________________

Give me a second....Loading tasks...
Successful! Use list to view the tasks.
Woof! I'm Hu9o!
What can I do for you?

> _________________________________

Got it. I've added this task:
	[D][ ] minute test (by: 20 Sep 2026, 6:30 PM)
Now you have 1 task in the list.
_________________________________

> _________________________________

Got it. I've added this task:
	[D][ ] lower case (by: 20 Sep 2026, 3:30 PM)
Now you have 2 tasks in the list.
_________________________________

> _________________________________

Invalid Date...Use day/month/year 12 hr time
	i.e 12/8/26 330 pm
_________________________________

> _________________________________

That event ends before it starts! Make sure /to is not earlier than /from.
_________________________________

> _________________________________

1. [D][ ] minute test (by: 20 Sep 2026, 6:30 PM)
2. [D][ ] lower case (by: 20 Sep 2026, 3:30 PM)
_________________________________

> 
 Saving data...
 Successfully saved data
_________________________________

Bye. Hope to see you again soon! (wags tail)
_________________________________

D| |minute test|20/09/26 630 PM|
D| |lower case|20/09/26 330 PM|
```

Exit code: `0`

All 16 test case(s) passed.
