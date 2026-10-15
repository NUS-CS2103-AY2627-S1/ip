<div align="center">

<h1>✦ Nano user guide ✦</h1>

<h3>Your calm, command-line companion for getting things done.</h3>

<p><strong>Plan less. Finish more.</strong></p>

</div>

## Meet Nano

Nano is a lightweight task-management chatbot with a friendly JavaFX interface. Type natural commands into the message box to organise your tasks, deadlines, events, and priorities.

| What Nano does | Why it helps |
| --- | --- |
| Adds todos, deadlines, and events | Capture tasks quickly |
| Lists and searches tasks | Find what matters |
| Tracks completion and priority | Keep work organised |
| Saves tasks automatically | Continue where you left off |

## Start here

1. Install **JDK 25** if it is not already installed.
2. Download the provided `nano.jar` file.
3. Double-click the JAR file to launch Nano, or run it from a terminal:

```bash
java -jar build/libs/nano.jar
```

4. Type a command and click **Send** or press **Enter**.

Nano saves your tasks automatically in `data/nano.txt`.

<h2 style="color: #5865D9;">Features</h2>

## Add tasks

<details>
<summary><strong>To-do</strong> — a task without a date</summary>

<p>Use a to-do for a task that does not need a specific date or time. Nano stores the description and gives the task a number.</p>

<pre><code>todo read a book</code></pre>

<p>Example response:</p>

<pre><code>Got it. Adding [ ] read a book (priority: normal)
Now you have 1 tasks in the list.</code></pre>

<p>The description cannot be empty.</p>

</details>

<details>
<summary><strong>Deadline</strong> — a task due at a specific time</summary>

<p>Use <code>/by</code> to attach a due date and time. The required format is <code>yyyy-MM-dd HHmm</code>.</p>

<pre><code>deadline submit assignment /by 2026-10-01 1800</code></pre>

<p>Nano displays the deadline when you use <code>list</code>. A deadline must include a valid date and time.</p>

</details>

<details>
<summary><strong>Event</strong> — something with a start and end time</summary>

<p>Use <code>/from</code> and <code>/to</code> to define the start and end of an event. Both sections are required and must use the format <code>yyyy-MM-dd HHmm</code>.</p>

<pre><code>event project meeting /from 2026-10-02 1400 /to 2026-10-02 1530</code></pre>

<p>The <code>/from</code> section must come before <code>/to</code>, and the end time must be later than the start time.</p>

</details>

## View and search

See every saved task:

```text
list
```

Search descriptions with a case-insensitive keyword:

```text
find something
```

## Update your list

Every task receives a number when it is added. Use that number to update the task later.

| Action | Example |
| --- | --- |
| Complete a task | `mark 1` |
| Reopen a task | `unmark 1` |
| Set priority | `priority 1 high` |
| Remove a task | `delete 1` |

### Completion status

Use `mark` when you finish a task:

```text
mark 1
```

Nano changes the task's status to completed. If you need to work on it again, use:

```text
unmark 1
```

### Priority levels

Every new task starts with `normal` priority. Change it with:

```text
priority 1 high
```

Nano supports three priority levels:

| Priority | Use it for |
| --- | --- |
| `high` | Urgent or important tasks |
| `normal` | Regular tasks and activities |
| `low` | Tasks that can wait |

Priority is shown when tasks are listed and is saved between sessions. Only `high`, `normal`, and `low` are accepted.

### Removing tasks

Delete a task permanently by using its number:

```text
delete 1
```

Nano confirms the deleted task and updates the number of remaining tasks. Deleting a task cannot be undone, so check the task number before confirming the command.

## Command deck

| Command | Purpose |
| --- | --- |
| `todo <description>` | Add a to-do |
| `deadline <description> /by <date and time>` | Add a deadline |
| `event <description> /from <start> /to <end>` | Add an event |
| `list` | Show all tasks |
| `find <keyword>` | Search for matching tasks |
| `mark <task number>` | Mark a task complete |
| `unmark <task number>` | Mark a task unfinished |
| `priority <task number> <high\|normal\|low>` | Change priority |
| `delete <task number>` | Delete a task |
| `bye` | Show the goodbye banner and exit |

## Signing off

When your work is done, type:

```text
bye
```

Nano displays its goodbye banner and closes after a short delay.

## Quick fixes

- Use lowercase commands.
- Use the task number shown by `list` for `mark`, `unmark`, `priority`, and `delete`.
- Check the date format for deadlines and events.
- If Nano rejects a command, read the response and adjust the syntax.

<div align="center">

<h3>Built with Java · Designed for focus · Powered by Nano</h3>

</div>
