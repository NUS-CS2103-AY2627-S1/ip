# Nova UI Test Plan

## Greeting and farewell
Aim: Verify that the chatbot starts and exits cleanly.
Inputs:
    bye
Expected output:
    ____________________________________________________________
     _   _    ___    _   _    _
    | \ | |  / _ \  | | | |  / \
    |  \| | | | | | | | | | / _ \
    | |\  | | |_| |  \ V / / ___ \
    |_| \_|  \___/    \_/ /_/   \_\
    Hello! I'm Nova.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________

## Reject duplicate tasks
Aim: Verify that adding a task with the same type and details is rejected.
Inputs:
    todo read book
    todo read book
    bye
Expected output:
    ____________________________________________________________
     _   _    ___    _   _    _
    | \ | |  / _ \  | | | |  / \
    |  \| | | | | | | | | | / _ \
    | |\  | | |_| |  \ V / / ___ \
    |_| \_|  \___/    \_/ /_/   \_\
    Hello! I'm Nova.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     A task with the same details already exists.
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________

## Reject invalid event data
Aim: Verify that nonexistent, equal, and reversed event date-times and storage delimiters are rejected.
Inputs:
    event invalid date /from 2026-02-30 09:00 /to 2026-03-01 10:00
    event reversed /from 2026-08-24 10:00 /to 2026-08-24 09:00
    event equal /from 2026-08-24 09:00 /to 2026-08-24 09:00
    todo buy | milk
    bye
Expected output:
    ____________________________________________________________
     _   _    ___    _   _    _
    | \ | |  / _ \  | | | |  / \
    |  \| | | | | | | | | | / _ \
    | |\  | | |_| |  \ V / / ___ \
    |_| \_|  \___/    \_/ /_/   \_\
    Hello! I'm Nova.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Please use event date-times in yyyy-MM-dd HH:mm format.
    ____________________________________________________________
    ____________________________________________________________
     Event start must be before its end.
    ____________________________________________________________
    ____________________________________________________________
     Event start must be before its end.
    ____________________________________________________________
    ____________________________________________________________
     The character '|' is not allowed in task descriptions.
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________

## Tag and untag tasks
Aim: Verify that tasks accept tags, ignore duplicate tags, remove existing tags, and reject malformed or missing tags.
Inputs:
    todo read book
    tag 1 #fun
    tag 1 #fun
    tag 1 fun
    untag 1 #fun
    untag 1 #fun
    bye
Expected output:
    ____________________________________________________________
     _   _    ___    _   _    _
    | \ | |  / _ \  | | | |  / \
    |  \| | | | | | | | | | / _ \
    | |\  | | |_| |  \ V / / ___ \
    |_| \_|  \___/    \_/ /_/   \_\
    Hello! I'm Nova.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Noted. I've added a tag to this task:
       [T][ ] read book #fun
    ____________________________________________________________
    ____________________________________________________________
     Noted. I've added a tag to this task:
       [T][ ] read book #fun
    ____________________________________________________________
    ____________________________________________________________
     Please provide a valid tag in the format #label.
    ____________________________________________________________
    ____________________________________________________________
     Noted. I've removed the tag from this task:
       [T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     Task does not have the tag #fun.
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________

## Find matching tasks
Aim: Verify that find searches task descriptions case-insensitively and numbers only matching results.
Inputs:
    todo read book
    deadline return book /by 2026-06-06
    todo wash dishes
    find BOOK
    bye
Expected output:
    ____________________________________________________________
     _   _    ___    _   _    _
    | \ | |  / _ \  | | | |  / \
    |  \| | | | | | | | | | / _ \
    | |\  | | |_| |  \ V / / ___ \
    |_| \_|  \___/    \_/ /_/   \_\
    Hello! I'm Nova.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [D][ ] return book (by: Jun 6 2026)
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [T][ ] wash dishes
     Now you have 3 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the matching tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Jun 6 2026)
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________

## Handle find edge cases
Aim: Verify that an empty keyword is rejected and a valid keyword with no matches reports no results.
Inputs:
    find
    find nonexistent
    bye
Expected output:
    ____________________________________________________________
     _   _    ___    _   _    _
    | \ | |  / _ \  | | | |  / \
    |  \| | | | | | | | | | / _ \
    | |\  | | |_| |  \ V / / ___ \
    |_| \_|  \___/    \_/ /_/   \_\
    Hello! I'm Nova.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Please add a keyword after 'find'.
    ____________________________________________________________
    ____________________________________________________________
     No matching tasks found.
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________

## Handle invalid commands
Aim: Verify that an empty todo and an unrecognized command produce errors without adding tasks.
Inputs:
    todo
    blah
    list
    bye
Expected output:
    ____________________________________________________________
     _   _    ___    _   _    _
    | \ | |  / _ \  | | | |  / \
    |  \| | | | | | | | | | / _ \
    | |\  | | |_| |  \ V / / ___ \
    |_| \_|  \___/    \_/ /_/   \_\
    Hello! I'm Nova.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Please add a description after 'todo'.
    ____________________________________________________________
    ____________________________________________________________
     I don't recognize that command.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________

## Mark and unmark tasks
Aim: Verify that task completion status can be set and reversed.
Inputs:
    todo read book
    todo return book
    mark 1
    mark 2
    unmark 2
    list
    bye
Expected output:
    ____________________________________________________________
     _   _    ___    _   _    _
    | \ | |  / _ \  | | | |  / \
    |  \| | | | | | | | | | / _ \
    | |\  | | |_| |  \ V / / ___ \
    |_| \_|  \___/    \_/ /_/   \_\
    Hello! I'm Nova.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [T][ ] return book
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] return book
    ____________________________________________________________
    ____________________________________________________________
     OK, I've marked this task as not done yet:
       [T][ ] return book
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[T][ ] return book
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________

## Delete tasks
Aim: Verify that a task can be removed by its one-based position and that invalid task numbers produce an error without changing the list.
Inputs:
    todo read book
    todo return book
    delete 1
    delete 3
    list
    bye
Expected output:
    ____________________________________________________________
     _   _    ___    _   _    _
    | \ | |  / _ \  | | | |  / \
    |  \| | | | | | | | | | / _ \
    | |\  | | |_| |  \ V / / ___ \
    |_| \_|  \___/    \_/ /_/   \_\
    Hello! I'm Nova.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [T][ ] return book
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Noted. I've removed this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] return book
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________

## Handle malformed task and date commands
Aim: Verify that malformed task numbers and deadline or event formats display errors and do not add or change tasks.
Inputs:
    todo read book
    mark 0
    mark 3
    unmark abc
    unmark 0
    delete
    delete -1
    delete abc
    deadline read book
    deadline /by Friday
    deadline submit assignment /by 2019-02-30
    deadline submit assignment /by 15-10-2019
    on 2019/10/15
    event meeting /from Monday
    event meeting /to Tuesday
    list
    bye
Expected output:
    ____________________________________________________________
     _   _    ___    _   _    _
    | \ | |  / _ \  | | | |  / \
    |  \| | | | | | | | | | / _ \
    | |\  | | |_| |  \ V / / ___ \
    |_| \_|  \___/    \_/ /_/   \_\
    Hello! I'm Nova.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     Please provide a valid task number.
    ____________________________________________________________
    ____________________________________________________________
     Please use: deadline DESCRIPTION /by DATE.
    ____________________________________________________________
    ____________________________________________________________
     Please use: deadline DESCRIPTION /by DATE.
    ____________________________________________________________
    ____________________________________________________________
     Please use a valid date in yyyy-MM-dd format.
    ____________________________________________________________
    ____________________________________________________________
     Please use a valid date in yyyy-MM-dd format.
    ____________________________________________________________
    ____________________________________________________________
     Please use a valid date in yyyy-MM-dd format.
    ____________________________________________________________
    ____________________________________________________________
     Please use: event DESCRIPTION /from START /to END.
    ____________________________________________________________
    ____________________________________________________________
     Please use: event DESCRIPTION /from START /to END.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________

## Maintain task positions across task types
Aim: Verify that deleting a middle task keeps the remaining task order and positions correct across todo, deadline, and event tasks.
Inputs:
    todo read book
    deadline submit assignment /by 2020-01-01
    event project meeting /from 2026-08-24 14:00 /to 2026-08-24 16:00
    delete 2
    mark 2
    list
    bye
Expected output:
    ____________________________________________________________
     _   _    ___    _   _    _
    | \ | |  / _ \  | | | |  / \
    |  \| | | | | | | | | | / _ \
    | |\  | | |_| |  \ V / / ___ \
    |_| \_|  \___/    \_/ /_/   \_\
    Hello! I'm Nova.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [D][ ] submit assignment (by: Jan 1 2020)
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [E][ ] project meeting (from: 2026-08-24 14:00 to: 2026-08-24 16:00)
     Now you have 3 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Noted. I've removed this task:
       [D][ ] submit assignment (by: Jan 1 2020)
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Nice! I've marked this task as done:
       [E][X] project meeting (from: 2026-08-24 14:00 to: 2026-08-24 16:00)
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[E][X] project meeting (from: 2026-08-24 14:00 to: 2026-08-24 16:00)
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________

## Add and list todo tasks
Aim: Verify that todo tasks use the T prefix and report the task count when added.
Inputs:
    todo borrow book
    todo buy bread
    list
    bye
Expected output:
    ____________________________________________________________
     _   _    ___    _   _    _
    | \ | |  / _ \  | | | |  / \
    |  \| | | | | | | | | | / _ \
    | |\  | | |_| |  \ V / / ___ \
    |_| \_|  \___/    \_/ /_/   \_\
    Hello! I'm Nova.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [T][ ] borrow book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [T][ ] buy bread
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] borrow book
     2.[T][ ] buy bread
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________

## Add and list deadline tasks
Aim: Verify that deadline tasks accept yyyy-mm-dd dates, display them as MMM d yyyy, and use the D prefix.
Inputs:
    deadline return book /by 2019-10-15
    deadline submit assignment /by 2020-01-01
    list
    bye
Expected output:
    ____________________________________________________________
     _   _    ___    _   _    _
    | \ | |  / _ \  | | | |  / \
    |  \| | | | | | | | | | / _ \
    | |\  | | |_| |  \ V / / ___ \
    |_| \_|  \___/    \_/ /_/   \_\
    Hello! I'm Nova.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [D][ ] return book (by: Oct 15 2019)
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [D][ ] submit assignment (by: Jan 1 2020)
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[D][ ] return book (by: Oct 15 2019)
     2.[D][ ] submit assignment (by: Jan 1 2020)
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________

## Add and list event tasks
Aim: Verify that event tasks accept valid date-time ranges and display the E prefix.
Inputs:
    event project meeting /from 2026-08-24 14:00 /to 2026-08-24 16:00
    event workshop /from 2026-08-25 09:00 /to 2026-08-25 11:00
    list
    bye
Expected output:
    ____________________________________________________________
     _   _    ___    _   _    _
    | \ | |  / _ \  | | | |  / \
    |  \| | | | | | | | | | / _ \
    | |\  | | |_| |  \ V / / ___ \
    |_| \_|  \___/    \_/ /_/   \_\
    Hello! I'm Nova.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [E][ ] project meeting (from: 2026-08-24 14:00 to: 2026-08-24 16:00)
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [E][ ] workshop (from: 2026-08-25 09:00 to: 2026-08-25 11:00)
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[E][ ] project meeting (from: 2026-08-24 14:00 to: 2026-08-24 16:00)
     2.[E][ ] workshop (from: 2026-08-25 09:00 to: 2026-08-25 11:00)
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________

## Save tasks after changes
Aim: Verify that adding, completing, uncompleting, and deleting tasks follow the normal task-change flow that saves the task list.
Inputs:
    todo read book
    deadline return book /by 2019-10-15
    mark 1
    unmark 1
    delete 2
    bye
Expected output:
    ____________________________________________________________
     _   _    ___    _   _    _
    | \ | |  / _ \  | | | |  / \
    |  \| | | | | | | | | | / _ \
    | |\  | | |_| |  \ V / / ___ \
    |_| \_|  \___/    \_/ /_/   \_\
    Hello! I'm Nova.
    What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
      [D][ ] return book (by: Oct 15 2019)
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     OK, I've marked this task as not done yet:
       [T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     Noted. I've removed this task:
       [D][ ] return book (by: Oct 15 2019)
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
    Bye. Hope to see you again soon!
    ____________________________________________________________

