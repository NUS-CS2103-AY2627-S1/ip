# UI Test Plan

This file is the source of truth for console UI test cases run with the `test-ui` skill.

## Test session configuration

- Launch command: `java -Dstdout.encoding=UTF-8 -cp out/production/ip longfrog.Longfrog` (compile current sources and run with Java 25).
- Start a fresh session for each case, run cases in order, and stop after the first failure.
- Before each case, preserve any existing `data/longfrog.txt`, use an empty save file unless the case defines a
  fixture, and restore the original file after the session.
- Compare the output literally, including blank lines and each 50-character `─` separator. User input is not console output.

## Shared console output

Each session starts without printing a greeting or other startup output unless the test case defines an explicit
startup warning.

After every input, the corresponding expected response below is inserted literally into this block. If the response has multiple lines, it replaces `RESPONSE` in full.

```text
──────────────────────────────────────────────────
RESPONSE

──────────────────────────────────────────────────

```

The complete expected output is the startup output plus one response block per input, in order. No normalization is allowed.

## Test cases

### TC-001: List an empty task list

- Aim: Verify the empty-list response and ordinary exit.

  | Input | Expected output |
  | --- | --- |
  | `list` | `Tasks currently on the lily pads:`<br>`The pond is clear—no tasks waiting.` |
  | `bye` | `Pond secured. Rest well—ribbit.` |

### TC-002: Add every supported task type and list them

- Aim: Verify valid `todo`, `deadline`, and `event` commands.

  | Input | Expected output |
  | --- | --- |
  | `todo read book` | `Task secured on the lily pad: [T][ ] read book` |
  | `deadline return book /by 2/12/2019 1800` | `Task secured on the lily pad: [D][ ] return book (by: Dec 02 2019, 6:00 pm)` |
  | `event team meeting /from 2/12/2019 1400 /to 2/12/2019 1600` | `Task secured on the lily pad: [E][ ] team meeting (from: Dec 02 2019, 2:00 pm to: Dec 02 2019, 4:00 pm)` |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [T][ ] read book`<br>`2: [D][ ] return book (by: Dec 02 2019, 6:00 pm)`<br>`3: [E][ ] team meeting (from: Dec 02 2019, 2:00 pm to: Dec 02 2019, 4:00 pm)` |
  | `bye` | `Pond secured. Rest well—ribbit.` |

### TC-003: Mark and unmark an existing task

- Aim: Verify valid state changes are shown by `list`.

  | Input | Expected output |
  | --- | --- |
  | `todo submit assignment` | `Task secured on the lily pad: [T][ ] submit assignment` |
  | `mark 1` | `Caught it. Marked done: submit assignment` |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [T][X] submit assignment` |
  | `unmark 1` | `Back into the pond: submit assignment` |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [T][ ] submit assignment` |
  | `bye` | `Pond secured. Rest well—ribbit.` |

### TC-004: Reject missing task numbers without changing the list

- Aim: Interleave valid and invalid task-number commands to verify failed operations preserve state.

  | Input | Expected output |
  | --- | --- |
  | `todo read book` | `Task secured on the lily pad: [T][ ] read book` |
  | `mark 2` | `There is no task 2. Choose a task number from 1 to 1.` |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [T][ ] read book` |
  | `mark 1` | `Caught it. Marked done: read book` |
  | `unmark 0` | `Index underflow: task numbers start at 1.` |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [T][X] read book` |
  | `bye` | `Pond secured. Rest well—ribbit.` |

### TC-005: Reject empty and unknown commands without ending the session

- Aim: Verify invalid commands are handled and later commands still use the existing task list.

  | Input | Expected output |
  | --- | --- |
  | `todo read book` | `Task secured on the lily pad: [T][ ] read book` |
  | `todo` | `Syntax error. Expected: todo TASK` |
  | _empty line_ | `Input buffer is empty. Please enter a command, ribbit.` |
  | `dance` | `Unknown command token. My parser cannot compute that, ribbit.` |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [T][ ] read book` |
  | `bye` | `Pond secured. Rest well—ribbit.` |

### TC-006: Reject incomplete deadline and event inputs without adding tasks

- Aim: Verify malformed `deadline` and `event` commands do not add partial tasks.

  | Input | Expected output |
  | --- | --- |
  | `deadline submit report /by 2/12/2019 1800` | `Task secured on the lily pad: [D][ ] submit report (by: Dec 02 2019, 6:00 pm)` |
  | `deadline missing date` | `Syntax error. Expected: deadline TASK /by d/M/yyyy HHmm` |
  | `event tutorial /from 2/12/2019 1400 /to 2/12/2019 1600` | `Task secured on the lily pad: [E][ ] tutorial (from: Dec 02 2019, 2:00 pm to: Dec 02 2019, 4:00 pm)` |
  | `event missing end /from 2/12/2019 1400` | `Syntax error. Expected: event TASK /from d/M/yyyy HHmm /to d/M/yyyy HHmm` |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [D][ ] submit report (by: Dec 02 2019, 6:00 pm)`<br>`2: [E][ ] tutorial (from: Dec 02 2019, 2:00 pm to: Dec 02 2019, 4:00 pm)` |
  | `bye` | `Pond secured. Rest well—ribbit.` |

### TC-007: Reject malformed task-number inputs without changing completion state

- Aim: Verify non-integer and missing task numbers leave the task unmarked.

  | Input | Expected output |
  | --- | --- |
  | `todo revise notes` | `Task secured on the lily pad: [T][ ] revise notes` |
  | `mark first` | `Type mismatch: task number must be an integer.` |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [T][ ] revise notes` |
  | `unmark` | `Index argument missing. Try: unmark 1` |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [T][ ] revise notes` |
  | `bye` | `Pond secured. Rest well—ribbit.` |

### TC-008: Accept command keywords regardless of letter case

- Aim: Verify keyword case normalization still works after validation.

  | Input | Expected output |
  | --- | --- |
  | `TODO Read Book` | `Task secured on the lily pad: [T][ ] Read Book` |
  | `LIST` | `Tasks currently on the lily pads:`<br>`1: [T][ ] Read Book` |
  | `BYE` | `Pond secured. Rest well—ribbit.` |

### TC-009: Delete a task and preserve the remaining task order

- Aim: Verify that `delete INDEX` removes the selected task, reindexes later tasks, and leaves the list unchanged after an invalid deletion.

  | Input | Expected output |
  | --- | --- |
  | `todo first task` | `Task secured on the lily pad: [T][ ] first task` |
  | `todo second task` | `Task secured on the lily pad: [T][ ] second task` |
  | `todo third task` | `Task secured on the lily pad: [T][ ] third task` |
  | `delete 4` | `There is no task 4. Choose a task number from 1 to 3.` |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [T][ ] first task`<br>`2: [T][ ] second task`<br>`3: [T][ ] third task` |
  | `delete 2` | `Released from the pond: second task` |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [T][ ] first task`<br>`2: [T][ ] third task` |
  | `delete` | `Index argument missing. Try: delete 1` |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [T][ ] first task`<br>`2: [T][ ] third task` |
  | `bye` | `Pond secured. Rest well—ribbit.` |

### TC-010: Find tasks by a case-insensitive description keyword

- Aim: Verify `find KEYWORD` returns matching tasks in their original order and excludes non-matching tasks.

  | Input | Expected output |
  | --- | --- |
  | `todo read book` | `Task secured on the lily pad: [T][ ] read book` |
  | `todo go running` | `Task secured on the lily pad: [T][ ] go running` |
  | `deadline return book /by 2/12/2019 1800` | `Task secured on the lily pad: [D][ ] return book (by: Dec 02 2019, 6:00 pm)` |
  | `find BOOK` | `Found 2 matching ripples:`<br>`1: [T][ ] read book`<br>`2: [D][ ] return book (by: Dec 02 2019, 6:00 pm)` |
  | `find holiday` | `No ripples for “holiday”.` |
  | `bye` | `Pond secured. Rest well—ribbit.` |

### TC-011: Use the clearer date command

- Aim: Verify `date d/M/yyyy` lists matching date-based tasks.

  | Input | Expected output |
  | --- | --- |
  | `deadline return book /by 2/12/2019 1800` | `Task secured on the lily pad: [D][ ] return book (by: Dec 02 2019, 6:00 pm)` |
  | `date 2/12/2019` | `Temporal query complete for 2/12/2019:`<br>`1: [D][ ] return book (by: Dec 02 2019, 6:00 pm)` |
  | `date 3/12/2019` | `Temporal query returned zero tasks for 3/12/2019.` |
  | `bye` | `Pond secured. Rest well—ribbit.` |

### TC-012: Reject a duplicate task without changing the list

- Aim: Verify automatic duplicate detection ignores description case and repeated whitespace while preserving the
  original task.

  | Input | Expected output |
  | --- | --- |
  | `todo Read Book` | `Task secured on the lily pad: [T][ ] Read Book` |
  | `todo read   book` | `Duplicate detected; task already exists at position 1: [T][ ] Read Book` |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [T][ ] Read Book` |
  | `bye` | `Pond secured. Rest well—ribbit.` |

### TC-013: Warn about duplicate tasks loaded from storage

- Aim: Verify saved duplicates are preserved and reported once before the first command.
- Save-file fixture for `data/longfrog.txt`:

  ```text
  T | 0 | Read Book
  T | 1 | read   book
  D | 0 | submit report | 2/12/2019 1800
  D | 1 | SUBMIT REPORT | 2/12/2019 1800
  ```

- Before the first normal response block, expect this startup output exactly once:

  ```text
  Warning: 2 duplicate task entries detected in saved data. Existing entries were preserved.
  ```

  | Input | Expected output |
  | --- | --- |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [T][ ] Read Book`<br>`2: [T][X] read   book`<br>`3: [D][ ] submit report (by: Dec 02 2019, 6:00 pm)`<br>`4: [D][X] SUBMIT REPORT (by: Dec 02 2019, 6:00 pm)` |
  | `bye` | `Pond secured. Rest well—ribbit.` |

### TC-014: Reject invalid event time ranges

- Aim: Verify an event must have a start strictly before its end while allowing a range that crosses midnight.

  | Input | Expected output |
  | --- | --- |
  | `event overnight /from 2/12/2019 2300 /to 3/12/2019 0100` | `Task secured on the lily pad: [E][ ] overnight (from: Dec 02 2019, 11:00 pm to: Dec 03 2019, 1:00 am)` |
  | `event reverse /from 2/12/2019 1800 /to 2/12/2019 1400` | `This event must end after it begins. Check the /from and /to times.` |
  | `event zero /from 2/12/2019 1800 /to 2/12/2019 1800` | `This event must end after it begins. Check the /from and /to times.` |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [E][ ] overnight (from: Dec 02 2019, 11:00 pm to: Dec 03 2019, 1:00 am)` |
  | `bye` | `Pond secured. Rest well—ribbit.` |

### TC-015: Reject impossible calendar dates

- Aim: Verify strict date parsing accepts a valid leap day but rejects impossible dates without adding tasks.

  | Input | Expected output |
  | --- | --- |
  | `deadline leap-day task /by 29/2/2024 1800` | `Task secured on the lily pad: [D][ ] leap-day task (by: Feb 29 2024, 6:00 pm)` |
  | `deadline impossible /by 29/2/2025 1800` | `Temporal parsing failed. Expected: d/M/yyyy HHmm (e.g., 2/12/2019 1800)` |
  | `date 31/4/2025` | `Temporal parsing failed. Expected: d/M/yyyy (e.g., 2/12/2019)` |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [D][ ] leap-day task (by: Feb 29 2024, 6:00 pm)` |
  | `bye` | `Pond secured. Rest well—ribbit.` |

### TC-016: Reject the save-file delimiter in task descriptions

- Aim: Verify task descriptions cannot contain the reserved `|` delimiter or corrupt persisted task fields.

  | Input | Expected output |
  | --- | --- |
  | `todo compare A | B` | `Task descriptions cannot contain “|” because it is reserved for storage.` |
  | `deadline compare A | B /by 2/12/2019 1800` | `Task descriptions cannot contain “|” because it is reserved for storage.` |
  | `event compare A | B /from 2/12/2019 1400 /to 2/12/2019 1600` | `Task descriptions cannot contain “|” because it is reserved for storage.` |
  | `list` | `Tasks currently on the lily pads:`<br>`The pond is clear—no tasks waiting.` |
  | `bye` | `Pond secured. Rest well—ribbit.` |

### TC-017: Reject unexpected command arguments

- Aim: Verify no-argument and task-number commands reject trailing text without exiting or changing tasks.

  | Input | Expected output |
  | --- | --- |
  | `todo read book` | `Task secured on the lily pad: [T][ ] read book` |
  | `list extra` | `Syntax error. Expected: list` |
  | `bye later` | `Syntax error. Expected: bye` |
  | `mark 1 extra` | `Syntax error. Expected: mark INDEX` |
  | `unmark 1 extra` | `Syntax error. Expected: unmark INDEX` |
  | `delete 1 extra` | `Syntax error. Expected: delete INDEX` |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [T][ ] read book` |
  | `bye` | `Pond secured. Rest well—ribbit.` |

### TC-018: Reject redundant task-state changes

- Aim: Verify completed tasks cannot be marked again and unfinished tasks cannot be unmarked.

  | Input | Expected output |
  | --- | --- |
  | `todo swim` | `Task secured on the lily pad: [T][ ] swim` |
  | `unmark 1` | `That task is already unfinished: swim` |
  | `mark 1` | `Caught it. Marked done: swim` |
  | `mark 1` | `That task is already marked done: swim` |
  | `unmark 1` | `Back into the pond: swim` |
  | `unmark 1` | `That task is already unfinished: swim` |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [T][ ] swim` |
  | `bye` | `Pond secured. Rest well—ribbit.` |

### TC-019: Report contextual task-number bounds

- Aim: Verify invalid task numbers report empty-list context or the current valid range for every indexed command.

  | Input | Expected output |
  | --- | --- |
  | `mark 1` | `The pond is clear—there are no tasks to mark.` |
  | `unmark 1` | `The pond is clear—there are no tasks to unmark.` |
  | `delete 1` | `The pond is clear—there are no tasks to delete.` |
  | `todo swim` | `Task secured on the lily pad: [T][ ] swim` |
  | `mark 8` | `There is no task 8. Choose a task number from 1 to 1.` |
  | `unmark 8` | `There is no task 8. Choose a task number from 1 to 1.` |
  | `delete 8` | `There is no task 8. Choose a task number from 1 to 1.` |
  | `list` | `Tasks currently on the lily pads:`<br>`1: [T][ ] swim` |
  | `bye` | `Pond secured. Rest well—ribbit.` |
