# UI Test Session

## PASS: Reject clashing and invalid event ranges

Aim: Verify half-open event boundaries, chronological reporting of every conflict, marked-event blocking, and invalid
range handling.

Java version: 25.0.4

Console input:

```text
event laboratory /from 10/9/2026 11:00 /to 10/9/2026 12:00
mark 1
todo buy lunch
event lecture /from 10/9/2026 09:00 /to 10/9/2026 10:00
event adjacent /from 10/9/2026 10:00 /to 10/9/2026 11:00
event workshop /from 10/9/2026 09:30 /to 10/9/2026 11:30
event reversed /from 10/9/2026 15:00 /to 10/9/2026 14:00
list
bye
```

Observed results:

- The adjacent event was added.
- The workshop was rejected with tasks 3, 4, and 1 listed in chronological order.
- The marked laboratory remained a conflict.
- The reversed event was rejected with the expected invalid-range message.
- The final list contained only the four successfully added tasks.

Exit code: 0

## PASS: Termination function

Aim: Verify that Panda starts, accepts `bye`, displays a farewell, and exits normally.

Command:
~~~sh
javac -d out/ui-test $(find src/main/java -name '*.java') && java -cp out/ui-test panda.Panda
~~~

Console input:
~~~text
bye
~~~

Console output:
~~~text
____________________________________________________________
                                            _______                
_________   _...._                  _..._   \  ___ `'.             
\        |.'      '-.             .'     '.  ' |--.\  \            
 \        .'```'.    '.          .   .-.   . | |    \  '           
  \      |       \     \   __    |  '   '  | | |     |  '    __    
   |     |        |    |.:--.'.  |  |   |  | | |     |  | .:--.'.  
   |      \      /    ./ |   \ | |  |   |  | | |     ' .'/ |   \ | 
   |     |\`'-.-'   .' `" __ | | |  |   |  | | |___.' /' `" __ | | 
   |     | '-....-'`    .'.''| | |  |   |  |/_______.'/   .'.''| | 
  .'     '.            / /   | |_|  |   |  |\_______|/   / /   | |_
'-----------'          \ \._,\ '/|  |   |  |             \ \._,\ '/
                        `--'  `" '--'   '--'              `--'  `" 

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
~~~

Exit code: 0

## PASS: Add to-do, event, and deadline tasks

Aim: Verify that the add-task command parsers create to-do, deadline, and event tasks from valid commands.

Command:
~~~sh
javac -d out/ui-test $(find src/main/java -name '*.java') && java -cp out/ui-test panda.Panda
~~~

Console input:
~~~text
todo borrow book
deadline return book /by 2/12/2019 18:00
event project meeting /from 2/12/2019 14:00 /to 2/12/2019 16:00
list
bye
~~~

Console output:
~~~text
____________________________________________________________
                                            _______                
_________   _...._                  _..._   \  ___ `'.             
\        |.'      '-.             .'     '.  ' |--.\  \            
 \        .'```'.    '.          .   .-.   . | |    \  '           
  \      |       \     \   __    |  '   '  | | |     |  '    __    
   |     |        |    |.:--.'.  |  |   |  | | |     |  | .:--.'.  
   |      \      /    ./ |   \ | |  |   |  | | |     ' .'/ |   \ | 
   |     |\`'-.-'   .' `" __ | | |  |   |  | | |___.' /' `" __ | | 
   |     | '-....-'`    .'.''| | |  |   |  |/_______.'/   .'.''| | 
  .'     '.            / /   | |_|  |   |  |\_______|/   / /   | |_
'-----------'          \ \._,\ '/|  |   |  |             \ \._,\ '/
                        `--'  `" '--'   '--'              `--'  `" 

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: 2 Dec 2019 18:00)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: 2 Dec 2019 18:00)
3.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
~~~

Exit code: 0

## PASS: Mark and unmark an event

Aim: Verify task-list logic: an event can be marked as done, unmarked, and displayed as unmarked afterwards.

Command:
~~~sh
javac -d out/ui-test $(find src/main/java -name '*.java') && java -cp out/ui-test panda.Panda
~~~

Console input:
~~~text
event project meeting /from 2/12/2019 14:00 /to 2/12/2019 16:00
mark 1
unmark 1
list
bye
~~~

Console output:
~~~text
____________________________________________________________
                                            _______                
_________   _...._                  _..._   \  ___ `'.             
\        |.'      '-.             .'     '.  ' |--.\  \            
 \        .'```'.    '.          .   .-.   . | |    \  '           
  \      |       \     \   __    |  '   '  | | |     |  '    __    
   |     |        |    |.:--.'.  |  |   |  | | |     |  | .:--.'.  
   |      \      /    ./ |   \ | |  |   |  | | |     ' .'/ |   \ | 
   |     |\`'-.-'   .' `" __ | | |  |   |  | | |___.' /' `" __ | | 
   |     | '-....-'`    .'.''| | |  |   |  |/_______.'/   .'.''| | 
  .'     '.            / /   | |_|  |   |  |\_______|/   / /   | |_
'-----------'          \ \._,\ '/|  |   |  |             \ \._,\ '/
                        `--'  `" '--'   '--'              `--'  `" 

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [E][X] project meeting (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [E][ ] project meeting (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[E][ ] project meeting (from: 2 Dec 2019 14:00 to: 2 Dec 2019 16:00)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
~~~

Exit code: 0

## PASS: Handle parser, task-list, and task-state errors

Aim: Verify that the global exception handler displays messages for unknown commands, invalid mark usage, invalid task indexes, and repeated task-state changes.

Command:
~~~sh
javac -d out/ui-test $(find src/main/java -name '*.java') && java -cp out/ui-test panda.Panda
~~~

Console input:
~~~text
unknown command
todo read book
mark
mark abc
mark 1 2
mark 2
unmark 2
mark 1
mark 1
unmark 1
unmark 1
bye
~~~

Console output:
~~~text
____________________________________________________________
                                            _______                
_________   _...._                  _..._   \  ___ `'.             
\        |.'      '-.             .'     '.  ' |--.\  \            
 \        .'```'.    '.          .   .-.   . | |    \  '           
  \      |       \     \   __    |  '   '  | | |     |  '    __    
   |     |        |    |.:--.'.  |  |   |  | | |     |  | .:--.'.  
   |      \      /    ./ |   \ | |  |   |  | | |     ' .'/ |   \ | 
   |     |\`'-.-'   .' `" __ | | |  |   |  | | |___.' /' `" __ | | 
   |     | '-....-'`    .'.''| | |  |   |  |/_______.'/   .'.''| | 
  .'     '.            / /   | |_|  |   |  |\_______|/   / /   | |_
'-----------'          \ \._,\ '/|  |   |  |             \ \._,\ '/
                        `--'  `" '--'   '--'              `--'  `" 

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS! Panda does not know the "unknown command" command yet. :<
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the mark command written like this: "mark TASK_NUMBER" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the mark command written like this: "mark TASK_NUMBER" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the mark command written like this: "mark TASK_NUMBER" 
____________________________________________________________
____________________________________________________________
OOPS! Panda cannot find task number 2; there are only 1 task(s) in the list. 
____________________________________________________________
____________________________________________________________
OOPS! Panda cannot find task number 2; there are only 1 task(s) in the list. 
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
OOPS! Panda has already marked this task as done:
  [T][X] read book
No extra tick needed. :>
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
OOPS! Panda has already marked this task as not done:
  [T][ ] read book
No extra un-tick needed. :>
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
~~~

Exit code: 0

## PASS: Reject malformed command values

Aim: Verify that every command parser rejects blank, unknown, and malformed command values with an informative usage error while Panda continues running.

Command:
~~~sh
javac -d out/ui-test $(find src/main/java -name '*.java') && java -cp out/ui-test panda.Panda
~~~

Console input:
~~~text
   
unknown command
bye later
list all
todo
deadline return book
deadline /by Sunday
deadline return book /by
event meeting
event meeting /from Monday
event /from Monday /to Tuesday
event meeting /from Monday /to
mark
mark one
mark 1 2
unmark
unmark one
unmark 1 2
delete
delete one
delete 1 2
delete 2
delete 0
bye
~~~

Console output:
~~~text
____________________________________________________________
                                            _______                
_________   _...._                  _..._   \  ___ `'.             
\        |.'      '-.             .'     '.  ' |--.\  \            
 \        .'```'.    '.          .   .-.   . | |    \  '           
  \      |       \     \   __    |  '   '  | | |     |  '    __    
   |     |        |    |.:--.'.  |  |   |  | | |     |  | .:--.'.  
   |      \      /    ./ |   \ | |  |   |  | | |     ' .'/ |   \ | 
   |     |\`'-.-'   .' `" __ | | |  |   |  | | |___.' /' `" __ | | 
   |     | '-....-'`    .'.''| | |  |   |  |/_______.'/   .'.''| | 
  .'     '.            / /   | |_|  |   |  |\_______|/   / /   | |_
'-----------'          \ \._,\ '/|  |   |  |             \ \._,\ '/
                        `--'  `" '--'   '--'              `--'  `" 

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS! Panda needs a command before it can help. :>
____________________________________________________________
____________________________________________________________
OOPS! Panda does not know the "unknown command" command yet. :<
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the bye command written like this: "bye" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the list command written like this: "list" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the todo command written like this: "todo DESCRIPTION" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the deadline command written like this: "deadline DESCRIPTION /by DATE" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the deadline command written like this: "deadline DESCRIPTION /by DATE" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the deadline command written like this: "deadline DESCRIPTION /by DATE" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the event command written like this: "event DESCRIPTION /from START /to END" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the event command written like this: "event DESCRIPTION /from START /to END" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the event command written like this: "event DESCRIPTION /from START /to END" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the event command written like this: "event DESCRIPTION /from START /to END" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the mark command written like this: "mark TASK_NUMBER" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the mark command written like this: "mark TASK_NUMBER" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the mark command written like this: "mark TASK_NUMBER" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the unmark command written like this: "unmark TASK_NUMBER" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the unmark command written like this: "unmark TASK_NUMBER" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the unmark command written like this: "unmark TASK_NUMBER" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the delete command written like this: "delete TASK_NUMBER" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the delete command written like this: "delete TASK_NUMBER" 
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the delete command written like this: "delete TASK_NUMBER" 
____________________________________________________________
____________________________________________________________
OOPS! Panda cannot find task number 2; there are only 0 task(s) in the list. 
____________________________________________________________
____________________________________________________________
OOPS! I think you made a mistake, task number cannot be 0
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
~~~

Exit code: 0

## PASS: Delete a task

Aim: Verify that delete removes the requested task, preserves the remaining task order, and reports the updated task count.

Command:
~~~sh
javac -d out/ui-test $(find src/main/java -name '*.java') && java -cp out/ui-test panda.Panda
~~~

Console input:
~~~text
todo read book
deadline return book /by 6/6/2019 18:00
event project meeting /from 6/8/2019 14:00 /to 6/8/2019 16:00
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
list
delete 3
list
bye
~~~

Console output:
~~~text
____________________________________________________________
                                            _______                
_________   _...._                  _..._   \  ___ `'.             
\        |.'      '-.             .'     '.  ' |--.\  \            
 \        .'```'.    '.          .   .-.   . | |    \  '           
  \      |       \     \   __    |  '   '  | | |     |  '    __    
   |     |        |    |.:--.'.  |  |   |  | | |     |  | .:--.'.  
   |      \      /    ./ |   \ | |  |   |  | | |     ' .'/ |   \ | 
   |     |\`'-.-'   .' `" __ | | |  |   |  | | |___.' /' `" __ | | 
   |     | '-....-'`    .'.''| | |  |   |  |/_______.'/   .'.''| | 
  .'     '.            / /   | |_|  |   |  |\_______|/   / /   | |_
'-----------'          \ \._,\ '/|  |   |  |             \ \._,\ '/
                        `--'  `" '--'   '--'              `--'  `" 

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: 6 Jun 2019 18:00)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: 6 Aug 2019 14:00 to: 6 Aug 2019 16:00)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: 6 Jun 2019 18:00)
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: 6 Jun 2019 18:00)
3.[E][ ] project meeting (from: 6 Aug 2019 14:00 to: 6 Aug 2019 16:00)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [E][ ] project meeting (from: 6 Aug 2019 14:00 to: 6 Aug 2019 16:00)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: 6 Jun 2019 18:00)
3.[T][X] join sports club
4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
~~~

Exit code: 0

## PASS: Load and save persisted tasks

Aim: Verify that Panda loads all task types and marked states, then overwrites the save file with the updated task list on termination.

Setup:
~~~sh
mkdir -p data
cp test/fixtures/preloaded-tasks.txt data/tasks.txt
~~~

Command:
~~~sh
javac -d out/ui-test $(find src/main/java -name '*.java') && java -cp out/ui-test panda.Panda && diff -u test/fixtures/expected-saved-tasks.txt data/tasks.txt
~~~

Console input:
~~~text
list
todo new task
bye
~~~

Console output:
~~~text
____________________________________________________________
                                            _______                
_________   _...._                  _..._   \  ___ `'.             
\        |.'      '-.             .'     '.  ' |--.\  \            
 \        .'```'.    '.          .   .-.   . | |    \  '           
  \      |       \     \   __    |  '   '  | | |     |  '    __    
   |     |        |    |.:--.'.  |  |   |  | | |     |  | .:--.'.  
   |      \      /    ./ |   \ | |  |   |  | | |     ' .'/ |   \ | 
   |     |\`'-.-'   .' `" __ | | |  |   |  | | |___.' /' `" __ | | 
   |     | '-....-'`    .'.''| | |  |   |  |/_______.'/   .'.''| | 
  .'     '.            / /   | |_|  |   |  |\_______|/   / /   | |_
'-----------'          \ \._,\ '/|  |   |  |             \ \._,\ '/
                        `--'  `" '--'   '--'              `--'  `" 

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: 6 Jun 2019 18:00)
3.[E][ ] project meeting (from: 6 Aug 2019 14:00 to: 6 Aug 2019 16:00)
4.[T][X] join sports club
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] new task
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
~~~

Exit code: 0

## PASS: Recover from a corrupted save file

Aim: Verify that an invalid saved-task format initializes an empty task list instead of terminating Panda.

Setup:
~~~sh
mkdir -p data
cp test/fixtures/corrupted-tasks.txt data/tasks.txt
~~~

Command:
~~~sh
javac -d out/ui-test $(find src/main/java -name '*.java') && java -cp out/ui-test panda.Panda
~~~

Console input:
~~~text
list
bye
~~~

Console output:
~~~text
____________________________________________________________
                                            _______                
_________   _...._                  _..._   \  ___ `'.             
\        |.'      '-.             .'     '.  ' |--.\  \            
 \        .'```'.    '.          .   .-.   . | |    \  '           
  \      |       \     \   __    |  '   '  | | |     |  '    __    
   |     |        |    |.:--.'.  |  |   |  | | |     |  | .:--.'.  
   |      \      /    ./ |   \ | |  |   |  | | |     ' .'/ |   \ | 
   |     |\`'-.-'   .' `" __ | | |  |   |  | | |___.' /' `" __ | | 
   |     | '-....-'`    .'.''| | |  |   |  |/_______.'/   .'.''| | 
  .'     '.            / /   | |_|  |   |  |\_______|/   / /   | |_
'-----------'          \ \._,\ '/|  |   |  |             \ \._,\ '/
                        `--'  `" '--'   '--'              `--'  `" 

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
____________________________________________________________
The saved file is broken... I can only restart your task list.
Expected 4 fields but found 3
____________________________________________________________
____________________________________________________________
~~~ Empty List ~~~
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
~~~

Exit code: 0

## PASS: Find tasks by description

Aim: Verify that `find` matches whole keywords case-insensitively, does not match partial words, preserves original task
numbers, reports no matches, and rejects a missing keyword.

Setup:
~~~sh
mkdir -p data
cp test/fixtures/preloaded-tasks.txt data/tasks.txt
printf 'T | 0 | organize bookshelf\n' >> data/tasks.txt
~~~

Command:
~~~sh
javac -d out/ui-test $(find src/main/java -name '*.java') && java -cp out/ui-test panda.Panda
~~~

Console input:
~~~text
find BOOK
find homework
find
bye
~~~

Console output:
~~~text
____________________________________________________________
                                            _______                
_________   _...._                  _..._   \  ___ `'.             
\        |.'      '-.             .'     '.  ' |--.\  \            
 \        .'```'.    '.          .   .-.   . | |    \  '           
  \      |       \     \   __    |  '   '  | | |     |  '    __    
   |     |        |    |.:--.'.  |  |   |  | | |     |  | .:--.'.  
   |      \      /    ./ |   \ | |  |   |  | | |     ' .'/ |   \ | 
   |     |\`'-.-'   .' `" __ | | |  |   |  | | |___.' /' `" __ | | 
   |     | '-....-'`    .'.''| | |  |   |  |/_______.'/   .'.''| | 
  .'     '.            / /   | |_|  |   |  |\_______|/   / /   | |_
'-----------'          \ \._,\ '/|  |   |  |             \ \._,\ '/
                        `--'  `" '--'   '--'              `--'  `" 

Hello! I'm Panda.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: 6 Jun 2019 18:00)
____________________________________________________________
____________________________________________________________
~~~ Empty List ~~~
____________________________________________________________
____________________________________________________________
OOPS! Panda needs the find command written like this: "find KEYWORD" 
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
~~~

Exit code: 0
