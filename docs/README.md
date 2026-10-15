# Jiji User Guide

**Jiji** is a cozy, comforting personal assistant chatbot (featuring both a modern JavaFX GUI and a fast CLI) that helps you organize and manage tasks (ToDos, Deadlines, and Events) with warm feline charm ₍^ ᵕ ᵕ ^₎ฅ.

<p align="center">
  <img src="Ui.png" alt="Jiji GUI Screenshot" width="450">
</p>

---

## Table of Contents
* [Quick Start](#quick-start)
* [Command Syntax Conventions](#command-syntax-conventions)
* [Features Summary](#features-summary)
* [Command Details](#command-details)
  * [1. Adding a ToDo Task: `todo`](#1-adding-a-todo-task-todo)
  * [2. Adding a Deadline Task: `deadline`](#2-adding-a-deadline-task-deadline)
  * [3. Adding an Event Task: `event`](#3-adding-an-event-task-event)
  * [4. Listing Tasks: `list`](#4-listing-tasks-list)
  * [5. Marking a Task as Completed: `mark`](#5-marking-a-task-as-completed-mark)
  * [6. Re-opening a Task: `unmark`](#6-re-opening-a-task-unmark)
  * [7. Deleting a Task: `delete`](#7-deleting-a-task-delete)
  * [8. Finding Tasks by Keyword: `find`](#8-finding-tasks-by-keyword-find)
  * [9. Viewing Schedules by Date: `schedule`](#9-viewing-schedules-by-date-schedule)
  * [10. Getting Help: `help`](#10-getting-help-help)
  * [11. Viewing Task Statistics: `stats`](#11-viewing-task-statistics-stats)
  * [12. Exiting the Application: `bye`](#12-exiting-the-application-bye)
* [Cozy Feline Personality & Statement Variety](#cozy-feline-personality--statement-variety)
* [Error Handling & Feline Feedback](#error-handling--feline-feedback)
* [Data Persistence](#data-persistence)
* [Graphical User Interface (JavaFX GUI)](#graphical-user-interface-javafx-gui)
* [Building and Running with Gradle](#building-and-running-with-gradle)
* [Standalone Executable JAR](#standalone-executable-jar)
* [Frequently Asked Questions (FAQ)](#frequently-asked-questions-faq)
* [Acknowledgments](#acknowledgments)

---

## Quick Start

1. **Prerequisites**: Ensure you have **Java 25** (or later) installed on your computer.
   * Verify your version by opening a terminal and running:
     ```bash
     java -version
     ```
2. **Download**: Grab the latest `jiji.jar` from the [Releases](https://github.com/Kimame04/ip/releases) page.
3. **Setup**: Copy `jiji.jar` into an empty folder where you want your tasks saved.
4. **Launch Jiji**:
   * **GUI Mode** (recommended): Open a terminal in that folder and run:
     ```bash
     java -jar jiji.jar
     ```
     *(Or simply double-click `jiji.jar` if your operating system associates JAR files with Java).*
   * **CLI Mode** (fast terminal text mode): Run:
     ```bash
     java -jar jiji.jar --cli
     ```
5. **Try your first commands** in the input dock:
   * Type `help` and press **Enter** to see all available commands.
   * Type `todo read chapter 4 of CS2103T textbook` and press **Enter** to add your first task.
   * Type `deadline submit project proposal /by 2026-09-20 2359` to schedule a deadline.
   * Type `list` to view your cozy basket of tasks.
   * Type `mark 1` to mark the first task completed.
   * Type `stats` to view your progress metrics and completion percentage.
   * Type `bye` to exit.

---

## Command Syntax Conventions

To help you use Jiji seamlessly, commands adhere to the following simple notation conventions:

* **Words in lowercase** (e.g. `todo`, `deadline`, `list`) are command keywords.
* **Words enclosed in angle brackets `<...>`** represent mandatory parameters that you must supply.
  * *Example*: In `todo <description>`, `<description>` is required (e.g. `todo read book`).
* **Words enclosed in square brackets `[...]`** represent optional parameters.
  * *Example*: In `list [pending|done]`, you can run `list` alone or optionally provide a filter (`list pending` or `list done`).
  * *Example*: In `help [command]`, you can run `help` alone or ask about a specific command (e.g. `help deadline`).
* **Case-Insensitive Keywords and Parameters**:
  * Commands such as `LIST`, `List`, and `list` are all recognized identically.
  * Parameter tags such as `/BY`, `/By`, and `/by` are also recognized identically.
* **Flexible Parameter Ordering**:
  * In `event <description> /from <start> /to <end>`, the `/from` and `/to` tags can be supplied in any relative order (e.g. `/to <end> /from <start>`).
* **Storage Delimiter Protection**:
  * Task descriptions, dates, and search keywords must not contain the pipe character `|` as it is reserved for data persistence.

---

## Features Summary

| Command | Syntax | Description |
| :--- | :--- | :--- |
| **`todo`** | `todo <description>` | Adds a to-do task. |
| **`deadline`** | `deadline <description> /by <time>` | Adds a task with a deadline. |
| **`event`** | `event <description> /from <start> /to <end>` | Adds an event with start and end times. |
| **`list`** | `list [pending|done]` | Lists all tasks, or filters by pending/done status while preserving indices. |
| **`schedule`** | `schedule <date|today>` | Views tasks scheduled on a specific date while preserving indices. |
| **`mark`** | `mark <task_number>` | Marks a task as completed (`[X]`). |
| **`unmark`** | `unmark <task_number>` | Marks a task as not completed (`[ ]`). |
| **`delete`** | `delete <task_number>` | Removes a task from the list and re-indexes remaining tasks. |
| **`find`** | `find <keyword>` | Finds tasks matching a search keyword. |
| **`stats`** | `stats` | Displays overall task progress, completion rate, and type breakdown. |
| **`help`** | `help [command]` | Displays general guidance or details on a specific command. |
| **`bye`** | `bye` | Exits the Jiji application. |

---

## Command Details

### 1. Adding a ToDo Task: `todo`
Adds a task without any date or time constraints.

* **Format**: `todo <description>`
* **Example**:
  ```text
  todo read book
  ```
* **Expected Output**:
  ```text
      ____________________________________________________________
       Tucked away safely! I've nestled this task into your list:
         [T][ ] read book
       That makes 1 tasks in our cozy bundle. ₍^. .^₎
      ____________________________________________________________
  ```

---

### 2. Adding a Deadline Task: `deadline`
Adds a task that must be completed by a specific date or time. Jiji understands standard date and time formats (e.g. `yyyy-MM-dd`, `d/M/yyyy HHmm`) and displays them in a friendly format (`MMM dd yyyy, h:mma`).

* **Format**: `deadline <description> /by <date/time>`
* **Accepted Formats**:
  * Date: `yyyy-MM-dd` (e.g. `2026-08-30`), `d/M/yyyy` (e.g. `2/12/2026`)
  * Date-Time: `yyyy-MM-dd HHmm` (e.g. `2026-08-30 1800`), `d/M/yyyy HHmm` (e.g. `2/12/2026 1800`)
  * Text descriptions (e.g. `Sunday`) are also supported.
* **Example (Date)**:
  ```text
  deadline return book /by 2026-08-30
  ```
* **Expected Output**:
  ```text
      ____________________________________________________________
       Tucked away safely! I've nestled this task into your list:
         [D][ ] return book (by: Aug 30 2026)
       That makes 2 tasks in our cozy bundle. ₍^. .^₎
      ____________________________________________________________
  ```
* **Example (Date and Time)**:
  ```text
  deadline submit project /by 2/12/2026 1800
  ```
* **Expected Output**:
  ```text
      ____________________________________________________________
       Tucked away safely! I've nestled this task into your list:
         [D][ ] submit project (by: Dec 02 2026, 6:00PM)
       That makes 3 tasks in our cozy bundle. ₍^. .^₎
      ____________________________________________________________
  ```

---

### 3. Adding an Event Task: `event`
Adds an event that occurs over a specific time interval. Both dates and times are formatted for clarity.

* **Format**: `event <description> /from <start> /to <end>`
* **Example**:
  ```text
  event orientation camp /from 2026-09-01 0900 /to 2026-09-03 1700
  ```
* **Expected Output**:
  ```text
      ____________________________________________________________
       Tucked away safely! I've nestled this task into your list:
         [E][ ] orientation camp (from: Sep 01 2026, 9:00AM to: Sep 03 2026, 5:00PM)
       That makes 4 tasks in our cozy bundle. ₍^. .^₎
      ____________________________________________________________
  ```

---

### 4. Listing Tasks: `list`
Displays current tasks along with their 1-based index, type tag (`[T]`, `[D]`, `[E]`), completion status (`[ ]` or `[X]`), and any associated times. You can view all tasks, or filter by pending or completed tasks to keep your view uncluttered. Filtered views preserve original task indices so you can directly mark, unmark, or delete them.

* **Format**: `list [pending|done]`
* **Example (All Tasks)**:
  ```text
  list
  ```
* **Expected Output**:
  ```text
      ____________________________________________________________
       Here are the tasks in your list:
       1.[T][ ] read book
       2.[D][ ] return book (by: Sunday)
       3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
      ____________________________________________________________
  ```
* **Example (Filter Pending Tasks)**:
  ```text
  list pending
  ```
* **Expected Output**:
  ```text
      ____________________________________________________________
       Here are the pending tasks in your list:
       1.[T][ ] read book
       2.[D][ ] return book (by: Sunday)
       3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
      ____________________________________________________________
  ```
* **Example (Filter Completed Tasks)**:
  ```text
  list done
  ```
* **Expected Output**:
  ```text
      ____________________________________________________________
       Here are the completed tasks in your list:
       (or "You have no completed tasks yet.")
      ____________________________________________________________
  ```
---

### 5. Marking a Task as Done: `mark`
Marks the task at the specified 1-based index as completed.

* **Format**: `mark <task_number>`
* **Example**:
  ```text
  mark 2
  ```
* **Expected Output**:
  ```text
      ____________________________________________________________
       Paws up! Marked this task as done:
         [D][X] return book (by: Sunday)
       Wonderful job! Time for a gentle stretch. ₍^ ᵕ ᵕ ^₎ฅ
      ____________________________________________________________
  ```

---

### 6. Marking a Task as Not Done: `unmark`
Marks a previously completed task back as incomplete.

* **Format**: `unmark <task_number>`
* **Example**:
  ```text
  unmark 2
  ```
* **Expected Output**:
  ```text
      ____________________________________________________________
       No hurry at all! I've marked this task as pending again:
         [D][ ] return book (by: Sunday)
       We'll get back to it when you're ready. ₍^. .^₎
      ____________________________________________________________
  ```

---

### 7. Deleting a Task: `delete`
Removes a task from the list at the specified 1-based index and automatically shifts the indices of subsequent tasks.

* **Format**: `delete <task_number>`
* **Example**:
  ```text
  delete 2
  ```
* **Expected Output**:
  ```text
      ____________________________________________________________
       Gently cleared away! I've removed this task:
         [D][ ] return book (by: Sunday)
       That leaves 2 cozy tasks in your list. ₍^. .^₎
      ____________________________________________________________
  ```

---

### 8. Finding Tasks by Keyword: `find`
Searches for tasks whose descriptions contain the given keyword (case-insensitive) and lists all matches.

* **Format**: `find <keyword>`
* **Example**:
  ```text
  find book
  ```
* **Expected Output**:
  ```text
      ____________________________________________________________
       Here are the matching tasks in your list:
       1.[T][ ] read book
       2.[D][ ] return book (by: Sunday)
      ____________________________________________________________
  ```

---

### 9. Viewing Schedules by Date: `schedule`
Displays tasks (deadlines and events) occurring on or due by a specified date. You can provide any standard date format (e.g. `yyyy-MM-dd`, `d/M/yyyy`) or use the keyword `today`. Master 1-based task indices are preserved in the output so you can immediately mark, unmark, or delete scheduled tasks.

* **Format**: `schedule <date|today>`
* **Accepted Formats**: `yyyy-MM-dd` (e.g. `2026-08-30`), `d/M/yyyy` (e.g. `30/8/2026`), or `today`
* **Example (Scheduled Tasks Found)**:
  ```text
  schedule 2026-08-30
  ```
* **Expected Output**:
  ```text
      ____________________________________________________________
       Schedule for Aug 30 2026:
       2.[D][ ] return book (by: Aug 30 2026)
       3.[E][ ] hackathon (from: Aug 30 2026, 10:00AM to: Aug 30 2026, 8:00PM)
      ____________________________________________________________
  ```
* **Example (No Tasks Scheduled)**:
  ```text
  schedule 2026-12-25
  ```
* **Expected Output**:
  ```text
      ____________________________________________________________
       No tasks scheduled for Dec 25 2026. A purr-fectly peaceful day to rest! ₍^ ᵕ ᵕ ^₎
      ____________________________________________________________
  ```

---

### 10. Getting Help: `help`
Displays a guide with all available commands, or detailed syntax and examples for a specified command.

* **Format**: `help [command]`
* **Example (General Help)**:
  ```text
  help
  ```
* **Expected Output**:
  ```text
      ____________________________________________________________
       Available commands in Jiji:

       [Add Tasks]
       • todo <description>
       • deadline <description> /by <time>
       • event <desc> /from <start> /to <end>

       [Manage Tasks]
       • list [filter] - View tasks (pending/done)
       • schedule <date> - View schedule for date
       • mark <index> - Mark as done
       • unmark <index> - Mark as not done
       • delete <index> - Delete a task
       • find <keyword> - Search by keyword

       [General]
       • stats - View task statistics
       • help [command] - View command guide
       • bye - Exit Jiji

       Tip: Type 'help <command>' (e.g. 'help deadline') for details!
      ____________________________________________________________
  ```
* **Example (Command-Specific Help)**:
  ```text
  help deadline
  ```
* **Expected Output**:
  ```text
      ____________________________________________________________
       Command: deadline
       Syntax: deadline <desc> /by <time>
       Description: Adds a task due by a specific date/time.
       Formats: yyyy-MM-dd, d/M/yyyy, HHmm
       Example: deadline submit report /by 2026-08-30 1800
      ____________________________________________________________
  ```

---

### 11. Viewing Task Statistics: `stats`
Displays overall task progress, completion rate percentage, and category breakdown across ToDos, Deadlines, and Events, including overdue deadline detection.

* **Format**: `stats` (or `statistics`)
* **Example**:
  ```text
  stats
  ```
* **Expected Output**:
  ```text
      ____________________________________________________________
       Task Statistics & Insights:

       [Overall Progress]
       • Total tasks: 3
       • Completed: 1 (33.3%)
       • Pending: 2

       [Breakdown by Type]
       • ToDos: 1 (0 completed)
       • Deadlines: 1 (1 completed, 0 overdue)
       • Events: 1 (0 completed)

       Tip: Use 'list pending' to view only incomplete tasks!
      ____________________________________________________________
  ```

---

### 12. Exiting the Application: `bye`
Exits Jiji with a farewell message.

* **Format**: `bye`
* **Expected Output**:
  ```text
      ____________________________________________________________
       Purrs and gentle head-bumps! Rest well and see you soon! ₍^ ᵕ ᵕ ^₎ฅ
      ____________________________________________________________
  ```

---

## Cozy Feline Personality & Statement Variety

Jiji is crafted to be a warm, comforting feline companion throughout your busy day. Inspired by Jiji from *Kiki's Delivery Service*, the chatbot greets you warmly, encourages gentle breaks, and keeps your tasks organized without pressure:

* **Dynamic Statement Bank**: In GUI mode, Jiji draws from an expressive bank of diverse, randomized responses for greetings, task confirmations, completions, deferrals, removals, and farewells. Every interaction feels fresh, caring, and lively!
* **Calm & Stress-Free Tone**: Tasks are framed as a "cozy bundle" or "basket", completed tasks are celebrated with gentle stretches and head-bumps, and unmarked tasks are re-opened with "no hurry at all".
* **Deterministic CLI Mode**: When executing automated test suites or running via CLI, Jiji automatically switches to a canonical statement set to guarantee 100% test reproducibility.

---

## Error Handling & Feline Feedback

Jiji validates your input and provides helpful, friendly error messages using custom feline emoticons:

* **Missing Task Description**:
  ```text
  todo
  ```
  ```text
      ____________________________________________________________
       OOPS! ₍^._.^₎ 𐒡 The description of a todo cannot be empty.
      ____________________________________________________________
  ```

* **Missing Deadline/Event Parameter**:
  ```text
  deadline return book
  ```
  ```text
      ____________________________________________________________
       OOPS! ^๑_๑^ ੭ A deadline task requires a description and a '/by' time.
      ____________________________________________________________
  ```

* **Invalid or Out-of-Bounds Task Number**:
  ```text
  mark 10
  ```
  ```text
      ____________________________________________________________
       OOPS! ₍^› ꘍ ‹ ^₎⟆ Please provide a valid task number.
      ____________________________________________________________
  ```

* **Unrecognized Command**:
  ```text
  blah
  ```
  ```text
      ____________________________________________________________
       OOPS! ₍^› ꘍ ‹ ^₎⟆ I'm sorry, but I don't know what that means.
      ____________________________________________________________
  ```

* **Duplicate Task Detection**:
  Attempting to add a task identical to an existing entry in your list is prevented:
  ```text
  todo read book
  ```
  ```text
      ____________________________________________________________
       OOPS! ₍^. .^₎ This task is already in your list:
         [T][ ] read book
      ____________________________________________________________
  ```

* **Event Chronological Order Validation**:
  Event end dates or times cannot precede start dates or times:
  ```text
  event camp /from 2026-09-03 /to 2026-09-01
  ```
  ```text
      ____________________________________________________________
       OOPS! ^๑_๑^ ੭ Event end date/time cannot be earlier than start date/time.
      ____________________________________________________________
  ```

* **Non-Existent Calendar Dates**:
  Calendar dates with impossible days or months (such as February 30 or April 31) are strictly rejected:
  ```text
  deadline report /by 2026-02-30
  ```
  ```text
      ____________________________________________________________
       OOPS! ₍^› ꘍ ‹ ^₎⟆ That date does not exist on the calendar (e.g. Feb 30). Please provide a valid date.
      ____________________________________________________________
  ```

* **Duplicate Parameter Rejection**:
  Commands with duplicate parameter tokens (such as multiple `/by`, `/from`, or `/to` tags) are rejected with clear guidance:
  ```text
  deadline book /by tomorrow /by Sunday
  ```
  ```text
      ____________________________________________________________
       OOPS! ^๑_๑^ ੭ The parameter '/by' cannot be specified multiple times.
      ____________________________________________________________
  ```

* **Storage Delimiter Protection**:
  The pipe character `|` is reserved for task file persistence and cannot be used in descriptions or parameters:
  ```text
  todo read | book
  ```
  ```text
      ____________________________________________________________
       OOPS! ₍^› ꘍ ‹ ^₎⟆ Task descriptions and parameters cannot contain the '|' character.
      ____________________________________________________________
  ```

* **Zero-Argument Command Enforcement**:
  Supplying unexpected arguments to commands that take none (e.g. `bye` or `stats`) is flagged immediately:
  ```text
  bye now
  ```
  ```text
      ____________________________________________________________
       OOPS! ₍^› ꘍ ‹ ^₎⟆ The 'bye' command does not take any arguments.
      ____________________________________________________________
  ```

---

## Data Persistence

Jiji automatically persists your task list so you never lose track of your items:

* **Automatic Saving**: Every time you add, delete, mark, or unmark a task, Jiji immediately updates the storage file on disk.
* **Storage Location**: Tasks are stored in `data/jiji.txt` relative to the application's root directory. The directory and file are created automatically if they do not exist.
* **Automatic Loading**: When Jiji starts up, it automatically reads `data/jiji.txt` and populates your task list.
* **Storage Format**: Pipe-separated text format:
  ```text
  T | 1 | read book
  D | 0 | return book | Sunday
  E | 0 | project meeting | Mon 2pm | 4pm
  ```

---

## Graphical User Interface (JavaFX GUI)

Jiji features a modern, responsive Graphical User Interface built with JavaFX and FXML:
* **Asymmetric Conversation Design**: User inputs and Jiji's replies are styled distinctly. User commands appear on the right in compact dark slate bubbles, while Jiji's responses appear on the left in clean white card bubbles with warm rose accents.
* **Visual Error Highlighting**: When an unrecognized command or malformed argument is submitted, Jiji's response is styled with a distinct soft rose-red background and crimson border, immediately drawing attention to the issue.
* **Responsive Layout & Text Wrapping**: Resizing the application window dynamically scales the chat container and automatically wraps text bubbles without clipping or awkward overflow.
* **Optimized Avatars & Spacing**: Compact 42×42 px circular avatars eliminate vertical dead space on short commands, keeping chat history dense and readable.
* **Streamlined Input Dock**: Floating pill-shaped text input with active focus retention and hover feedback allows fluid, continuous command entry without re-clicking.
* **Auto-Scrolling**: Automatically scrolls down to the newest message upon receiving input or responses.
* **Dual Execution Mode**: Supports both GUI and text-based CLI seamlessly.

To launch the JavaFX GUI application:
```bash
./gradlew run
```

---

## Building and Running with Gradle

Jiji uses Gradle for build automation, testing, and packaging:

* **Run Jiji in GUI mode (with assertions enabled)**:
  ```bash
  ./gradlew run
  ```
* **Run Jiji in CLI mode (with assertions enabled)**:
  ```bash
  ./gradlew run --args="--cli" --console=plain --quiet
  ```
* **Run automated unit tests (with assertions enabled)**:
  ```bash
  ./gradlew test
  ```
* **Run Checkstyle code style analysis**:
  ```bash
  ./gradlew checkstyleMain checkstyleTest
  ```
* **Build project & assemble distribution**:
  ```bash
  ./gradlew build
  ```

---

## Standalone Executable JAR

You can package and distribute Jiji as a standalone executable fat JAR:

### 1. Generating the JAR File
Run the following Gradle task:
```bash
./gradlew shadowJar
```
The generated executable JAR will be located at:
```text
build/libs/jiji.jar
```

### 2. Running the JAR File
You can run the JAR file on any system with Java 25 installed:
1. Copy `jiji.jar` into your desired working directory.
2. Open a terminal in that folder and run:
   ```bash
   java -jar "jiji.jar"
   ```
3. Jiji will automatically create and persist tasks in `data/jiji.txt` in that folder.

---

## Frequently Asked Questions (FAQ)

**Q: Where are my tasks saved?**  
**A:** Jiji automatically persists your task list in `data/jiji.txt` located in the directory where `jiji.jar` was launched. Both the `data/` directory and `jiji.txt` are created automatically if they do not exist.

**Q: Can I edit `data/jiji.txt` manually?**  
**A:** Yes, but exercise caution! Jiji uses a strict pipe-separated format (e.g. `T | 0 | read book`). If a line is malformed, Jiji will gracefully skip that line rather than crash. For best reliability, manage tasks directly through Jiji.

**Q: Why does Jiji reject task descriptions containing the `|` character?**  
**A:** Because `|` is the internal storage delimiter in `data/jiji.txt`. Disallowing `|` prevents task data from corrupting your persistence file or breaking field alignment.

**Q: What date formats does Jiji accept?**  
**A:** Jiji recognizes `yyyy-MM-dd` (e.g. `2026-08-30`), `d/M/yyyy` (e.g. `30/8/2026`), and date-times like `yyyy-MM-dd HHmm` (e.g. `2026-08-30 1800`) or `d/M/yyyy HHmm` (e.g. `2/12/2026 1800`). Natural relative dates like `today` are also supported in the `schedule` command. Freeform text dates (like `Sunday` or `next week`) are also safely preserved.

**Q: Why was my date rejected with "That date does not exist on the calendar"?**  
**A:** Jiji strictly validates calendar dates to prevent rollover bugs and typos (e.g. `2026-02-30`, `31/04/2026`, or month `13`). Please provide a valid calendar date.

**Q: How do I transfer my tasks to another computer?**  
**A:** Simply copy the `data/` folder (or just `data/jiji.txt`) to the directory containing `jiji.jar` on your other computer. When Jiji starts up, it will immediately load your tasks.

**Q: How do I run Jiji in terminal CLI mode instead of GUI mode?**  
**A:** Pass the `--cli` argument when executing the JAR:
```bash
java -jar jiji.jar --cli
```

---

## Acknowledgments

* **SE-EDU Initiative**: The foundational JavaFX architecture, FXML controllers, and dialog layout were adapted and extended from the [SE-EDU JavaFX 4 Tutorial](https://se-education.org/guides/tutorials/javaFxPart4.html).
* **CS2103/T Teaching Team**: For the starter codebase template, software engineering guidelines, and automated testing framework.
* **Studio Ghibli**: Inspiration for Jiji's cozy feline personality, character charm, and warmth from *Kiki's Delivery Service*.