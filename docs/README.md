# Kiki

![Kiki running with a todo and a deadline](Ui.png)

Kiki is a desktop task assistant for keeping track of todos, deadlines, and events. Add tasks in the command field, then search, review, complete, reopen, prioritize, remove, or sort them without leaving the chat window.

## Getting started

Download `kiki.jar` from the [latest GitHub Release](https://github.com/eatenquek/ip/releases/latest) and run it with Java 25:

```bash
java -jar kiki.jar
```

Kiki saves tasks in `data/kiki.txt` in the directory from which it is launched. Add commands in the field at the bottom of the window and press Enter or select **Send**. Type `bye` to close Kiki.

## Commands

| To | Command | Example |
| --- | --- | --- |
| Add a todo | `todo DESCRIPTION` | `todo read chapter 4` |
| Add a deadline | `deadline DESCRIPTION /by yyyy-MM-dd HHmm` | `deadline submit report /by 2026-09-21 1700` |
| Add an event | `event DESCRIPTION /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm` | `event project meeting /from 2026-09-22 1400 /to 2026-09-22 1500` |
| Show all tasks | `list` | `list` |
| Find tasks by description | `find KEYWORD` | `find report` |
| Show a day's schedule | `check day DATE` | `check day 21 September 2026` |
| Show a week's schedule | `check week DATE` | `check week 21 September 2026` |
| Sort tasks by date and time | `sort` | `sort` |
| Mark a task complete | `mark NUMBER` | `mark 1` |
| Reopen a task | `unmark NUMBER` | `unmark 1` |
| Remove a task | `delete NUMBER` | `delete 2` |
| Set task priority | `priority NUMBER low\|normal\|high` | `priority 1 high` |
| Ask AI for command help | `@ai QUESTION` | `@ai how do I add a deadline?` |
| Close Kiki | `bye` | `bye` |

Use the number shown by `list` with `mark`, `unmark`, or `delete`. Dates for `check day` and `check week` use `d MMMM` or `d MMMM uuuu`; omitting the year uses the current year. Event end times must be later than start times. Kiki highlights invalid commands and malformed dates so they can be corrected.

## Task status

`[T]` is a todo, `[D]` a deadline, and `[E]` an event. `[ ]` means not completed and `[X]` means completed. `[L]`, `[N]`, and `[H]` represent low, normal, and high priority. Tasks are saved automatically after changes.

### AI help setup

The optional `@ai` command answers read-only questions about Kiki's commands. It requires an API key for an OpenAI-compatible provider. Set the key in your environment before starting Kiki:

```bash
export LLM_API_KEY=your_api_key_here
```

Groq users can optionally set `LLM_BASE_URL` and `LLM_MODEL`; Kiki defaults to the Groq OpenAI-compatible endpoint and `llama-3.3-70b-versatile`. Never put the API key in the repository or share it with others. Without a key, Kiki will explain how to configure AI help and the rest of the application continues to work.

## Build from source

Install Java 25, then run:

```bash
./gradlew clean shadowJar
java -jar build/libs/kiki.jar
```
