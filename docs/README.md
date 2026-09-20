# BonBon Task Manager

```text
 ______                ______               
 | ___ \               | ___ \              
 | |_/ / ___  _ __     | |_/ / ___  _ __    
 | ___ \/ _ \| '_ \    | ___ \/ _ \| '_ \   
 | |_/ / (_) | | | |   | |_/ / (_) | | | |  
 \____/ \___/|_| |_|   \____/ \___/|_| |_|  
```

> *Your lightweight desktop task-tracking assistant!*

---

## Quick Start

1. **Prerequisite:** Ensure you have **Java 17** or above installed on your system.
2. **Download:** Grab the latest `bonbon.jar` from the releases section.
3. **Run:** Open a terminal, navigate to your download directory, and execute:
   ```bash
   java -jar bonbon.jar
   ```
4. **Interact:** Type your commands into the chat box and press **Enter**.

---

## Features & Command Summary

> 💡 **Date Format Note:** All dates must strictly follow the `dd/MM/yyyy HHmm` format (e.g., `20/09/2026 1800` for 20th Sept 2026, 6:00 PM).

| Command | Syntax | Example |
| :--- | :--- | :--- |
| **ToDo** | `todo <description>` | `todo buy groceries` |
| **Deadline** | `deadline <description> /by <dd/MM/yyyy HHmm>` | `deadline submit assignment /by 20/09/2026 2359` |
| **Event** | `event <description> /from <start> /to <end> /at <location>` | `event team sync /from 21/09/2026 1000 /to 21/09/2026 1200 /at Room A` |
| **List** | `list` | `list` |
| **Mark** | `mark <index>` | `mark 1` |
| **Unmark** | `unmark <index>` | `unmark 1` |
| **Delete** | `delete <index>` | `delete 2` |
| **Find** | `find <keyword>` | `find assignment` |
| **Exit** | `bye` | `bye` |

---

## Usage Details

### Adding Tasks

* **Add a ToDo task:**  
  Adds a simple task without any date or time constraints.  
  `todo read software engineering book`

* **Add a Deadline task:**  
  Adds a task that must be completed before a set date and time.  
  `deadline return book /by 25/09/2026 1800`

* **Add an Event task:**  
  Adds an event with a designated start time, end time, and location.  
  `event orientation /from 20/09/2026 0900 /to 20/09/2026 1200 /at Sports Hall`

### Managing Tasks

* **View all tasks:**  
  Displays all current tasks with their completion status (`[X]` for done, `[ ]` for pending).  
  `list`

* **Mark a task as done:**  
  Marks the task at the specified 1-based index as completed.  
  `mark 2`

* **Unmark a task:**  
  Reverts a completed task at the specified 1-based index back to pending.  
  `unmark 2`

* **Delete a task:**  
  Removes the task at the specified 1-based index from your list.  
  `delete 1`

* **Find tasks by keyword:**  
  Searches and lists all tasks containing the search keyword in their description.  
  `find book`

### Exiting the Application
Type `bye` to exit. BonBon will display a farewell message and gracefully close after a short delay.

---

## Data Storage

All tasks are automatically saved to a text file located at `./src/main/java/data/bonbon.txt`.

* The file automatically updates whenever you add, mark, unmark, or delete a task.
* On launch, BonBon automatically loads your existing tasks from this file.
* **Caution:** Manual editing of `bonbon.txt` is not recommended. Corrupted lines will be skipped during load.

---

## FAQ

**Q: Why do I get a date parse error when adding deadlines or events?**  
**A:** Ensure your date string strictly follows `dd/MM/yyyy HHmm` using 24-hour time. For example, use `05/10/2026 0900` instead of `5/10/26 9am`.

**Q: Where can I find my saved tasks if I move the application?**  
**A:** BonBon creates a `data/` directory relative to the current working directory where the program is executed.
