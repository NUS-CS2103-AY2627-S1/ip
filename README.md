## Introduction

- GRAVE WARNING: **All** software engineers may get replaced by *AI*.
- PSA: Are ***you*** an aspiring software engineer? Beating AI at software engineering seems more and more like a ***MYTH*** every day.

### URGENT ACTION YOU MUST DO THIS: EMBRACE THE MYTH

- Stare directly into ***THE MYTH***. Walk into it. Embrace it. Let it envelop you.
- **Start with THE MYTH** chatbot (this product btw).

## Set up

1. Ensure you have JDK 25. **Skip to step 6** if you already have JDK 25.
2. **Download JDK 25**, which includes the Java Virtual Machine (JVM), from the [Java downloads page](https://www.oracle.com/java/technologies/downloads/#java25). Choose the package for your operating system and processor architecture.
3. **Install JDK 25** using the [installation instructions](https://docs.oracle.com/en/java/javase/25/install/) for Windows, macOS, or Linux.
4. **Set** `JAVA_HOME` to the JDK installation directory, excluding its `bin` folder.
5. **Update** `PATH` to place `%JAVA_HOME%\bin` (Windows) or `$JAVA_HOME/bin` (macOS/Linux) before any existing Java entries.
6. **Verify the setup:** Open a new terminal or command prompt and run `java -version`. Confirm that the reported version starts with `25`.
7. Download The Myth from its [sparkling palace](https://github.com/fireinyu/ip/releases/tag/stable-latest).
8. **Summon The Myth** by executing `java -jar themyth.jar` in the directory containing *themyth.jar*.

## Commands

| Command | Description |
| --- | --- |
| [quiz](#cmd-quiz) | View the software engineering quiz question |
| [answer](#cmd-answer) | Answer the quiz question |
| [todo](#cmd-todo) | Create a [todo task](#glossary) |
| [deadline](#cmd-deadline) | Create a [deadline task](#glossary) due by a [Datetime](#argument-formats) |
| [event](#cmd-event) | Create an [event task](#glossary) between start and end [Datetime](#argument-formats)s |
| [list](#cmd-list) | List all [task](#glossary)s |
| [find](#cmd-find) | List [task](#glossary)s matching a keyword |
| [due](#cmd-due) | List [deadline task](#glossary)s due by a [Datetime](#argument-formats) |
| [at](#cmd-at) | List [event task](#glossary)s happening at a [Datetime](#argument-formats) |
| [mark](#cmd-mark) | Mark a [task](#glossary) as [completed](#glossary) |
| [unmark](#cmd-unmark) | Mark a [task](#glossary) as [pending](#glossary) |
| [delete](#cmd-delete) | Delete a [task](#glossary) |
| [bye](#cmd-bye) | Exit the app |

### Take a quiz

| Command | Description |
| --- | --- |
| [quiz](#cmd-quiz) | View the software engineering quiz question |
| [answer](#cmd-answer) | Answer the quiz question |

<a id="cmd-quiz"></a>

#### quiz

- View the software engineering quiz question, along with the answer options.
- The first question is randomly selected.
- **Example:** `quiz` displays the current quiz question and its answer options.

<a id="cmd-answer"></a>

#### answer \<idx:[Integer](#argument-formats)>

- Attempt the quiz question by selecting an answer option.
- Answering correctly marks the *[Quiz Task](#glossary)* as complete.
- A random new question is selected after each attempt, regardless of whether it is right or wrong.
- \<idx>: index of option to use as answer
- **Example:** After viewing a question with `quiz`, enter `answer 1` to submit option 1. A new question is selected after the attempt.

### Create [task](#glossary)s

| Command | Description |
| --- | --- |
| [todo](#cmd-todo) | Create a [todo task](#glossary) |
| [deadline](#cmd-deadline) | Create a [deadline task](#glossary) due by a [Datetime](#argument-formats) |
| [event](#cmd-event) | Create an [event task](#glossary) between start and end [Datetime](#argument-formats)s |
|  |  |

<a id="cmd-todo"></a>

#### todo \<desc:[String](#argument-formats)>

- Create a new [todo task](#glossary).
- \<desc>: description of the [task](#glossary)
- **Example:** `todo read_textbook` creates a todo task with the description `read_textbook`.

<a id="cmd-deadline"></a>

#### deadline \<desc:[String](#argument-formats)> \</by:[Datetime](#argument-formats)>

- Create a [deadline task](#glossary) due by a [Datetime](#argument-formats).
- \<desc>: description of the [task](#glossary)
- \<by>: [task](#glossary) due [Datetime](#argument-formats)
- **Example:** `deadline submit_assignment /by 2026-09-25-23-59` creates a deadline task due on 25 September 2026 at 23:59.

<a id="cmd-event"></a>

#### event \<desc:[String](#argument-formats)> \</from:[Datetime](#argument-formats)> \</to:[Datetime](#argument-formats)>

- Create an [event task](#glossary) between start and end [Datetime](#argument-formats)s.
- \<desc>: description of the [task](#glossary)
- \<from>: event start [Datetime](#argument-formats)
- \<to>: event end [Datetime](#argument-formats)
- **Example:** `event study_group /from 2026-09-20-14-00 /to 2026-09-20-16-00` creates an event on 20 September 2026 from 14:00 to 16:00.

### List [task](#glossary)s

| Command | Description |
| --- | --- |
| [list](#cmd-list) | List all [task](#glossary)s |
| [find](#cmd-find) | List [task](#glossary)s matching a keyword |
| [due](#cmd-due) | List [deadline task](#glossary)s due by a [Datetime](#argument-formats) |
| [at](#cmd-at) | List [event task](#glossary)s happening at a [Datetime](#argument-formats) |

<a id="cmd-list"></a>

#### list \[/sort:[Order](#argument-formats)]

- List all [task](#glossary)s, optionally sorting it first.
- The [index](#glossary) of each [task](#glossary) is set according to its numbering in this list.
- Sorting the [task](#glossary)s may change their numbering in the displayed list. The [index](#glossary) of each [task](#glossary) **will be updated accordingly**.
- \[/sort]: [Order](#argument-formats) for sorting the list of [task](#glossary)s
  - defaults to "modified"
- **Example:** `list /sort name` lists all tasks in alphabetical order of their descriptions and updates their indices to match that order.

<a id="cmd-find"></a>

#### find \<keyword:[String](#argument-formats)> \[/sort:[Order](#argument-formats)]

- List [task](#glossary)s matching a keyword, optionally in a sorted order.
- The numbering of [task](#glossary)s in this list **does not represent** the [index](#glossary) of each [task](#glossary).
- \[/sort]: [Order](#argument-formats) for sorting the list of [task](#glossary)s
  - defaults to "modified"
- **Example:** `find read /sort name` lists tasks matching `read`, sorted alphabetically by description.

<a id="cmd-due"></a>

#### due \<dt:[Datetime](#argument-formats)> \[/sort:[Order](#argument-formats)]

- List [deadline task](#glossary)s due by a [Datetime](#argument-formats), optionally in a sorted order.
- The numbering of [task](#glossary)s in this list **does not represent** the [index](#glossary) of each [task](#glossary).
- \[/sort]: [Order](#argument-formats) for sorting the list of [task](#glossary)s
  - defaults to "modified"
- **Example:** `due 2026-09-26 /sort created` lists deadline tasks due by the start of 26 September 2026, with the most recently created tasks first.

<a id="cmd-at"></a>

#### at \<dt:[Datetime](#argument-formats)> \[/sort:[Order](#argument-formats)]

- List [event task](#glossary)s happening at a [Datetime](#argument-formats), optionally in a sorted order.
- The numbering of [task](#glossary)s in this list **does not represent** the [index](#glossary) of each [task](#glossary).
- \[/sort]: [Order](#argument-formats) for sorting the list of [task](#glossary)s
  - defaults to "modified"
- **Example:** `at 2026-09-20-15-00 /sort name` lists events happening on 20 September 2026 at 15:00, sorted alphabetically by description.

### Edit [task](#glossary)s

| Command | Description |
| --- | --- |
| mark | Mark a [task](#glossary) as completed |
| unmark | Mark a [task](#glossary) as pending/ incomplete |
| delete | Delete a [task](#glossary) |

<a id="cmd-mark"></a>

#### mark \<idx:[Integer](#argument-formats)>

- Mark a [task](#glossary) as [completed](#glossary).
- Marking the [Quiz Task](#glossary) as [completed](#glossary) is equivalent to [quiz](#cmd-quiz); if the *Quiz [task](#glossary)* was [pending](#glossary), it remains [pending](#glossary).
- \<idx>: [index](#glossary) of [task](#glossary) to mark as [completed](#glossary)
  - check the [index](#glossary) of each [task](#glossary) using [list](#cmd-list)
- **Example:** If `list` shows a regular task at index `2`, `mark 2` marks that task as completed.

<a id="cmd-unmark"></a>

#### unmark \<idx:[Integer](#argument-formats)>

- Mark a [task](#glossary) as [pending](#glossary).
- \<idx>: [index](#glossary) of [task](#glossary) to mark as [pending](#glossary)
  - check the [index](#glossary) of each [task](#glossary) using [list](#cmd-list)
- **Example:** If `list` shows a completed task at index `2`, `unmark 2` changes its status to pending.

<a id="cmd-delete"></a>

#### delete \<idx:[Integer](#argument-formats)>

- Delete a [task](#glossary).
- Marking the [Quiz Task](#glossary) is not allowed; doing so will cause an *exception* and the [Quiz Task](#glossary) will remain.
- \<idx>: [index](#glossary) of [task](#glossary) to mark as [pending](#glossary)
  - check the [index](#glossary) of each [task](#glossary) using [list](#cmd-list)
- **Example:** If `list` shows a regular task at index `2`, `delete 2` removes that task from the list. Choose a task other than the Quiz Task.

### Exit the app

#### bye

- Exit the app and save the current [task](#glossary) list to disk.
- **Important**: if you don't use this command (e.g. exit by closing the window), any changes you made **will be lost**.
- **Example:** `bye` saves the current task list to disk and exits the app.

### Argument Formats

| Type     | Format/s                                                                     | Notes                                                                                                                                                                                                                                                                   | Examples                                |
| -------- | ---------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------- |
| String   | {characters}                                                                 | Use "..." if you need to use spaces.<br>Use \\" instead of \" for double-quotation literals.                                                                                                                                                                            | software<br>"the myth"<br>"do \\"it\\"" |
| Integer  | {digits}                                                                     | No negative integers.                                                                                                                                                                                                                                                   | 9<br>67                                 |
| Datetime | YYYY<br>YYYY-MM<br>YYYY-MM-DD<br>YYYY-MM-DD-HH<br>YYYY-MM-DD-HH-mm           | Set to the start of the time period.<br>Time is specified in 24-hour format.                                                                                                                                                                                            | 2026-03<br>2026-03-15-21-42             |
| Order    | Choice of:<br>- name<br>- created<br>- modified<br>- pending<br><br><br><br> | Sorting order for [task](#glossary)s.<br>**name**: ascending alphabetical order of descriptions.<br>**created**: descending order of creation<br>**modified**: descending order of modification (such as marking)<br>**pending**: unmarked [task](#glossary)s first<br> | name<br>modified                        |

## Glossary

| Term | Definition |
| --- | --- |
| **task** | A tracked activity or work item. Each task comprises a description, a status (completed or pending), creation/modification timestamps. |
| **task index** | A 1-based positive integer corresponding to the position of a task in the list shown by the `list` command. |
| **Todo task** | A simple task containing only a text description without any associated date or time constraints. |
| **Deadline task** | A task that must be completed before a specified due date and time. |
| **Event task** | A task that occurs across a specific time interval defined by start and end datetimes. |
| **Quiz task** | A persistent built-in task for revising software engineering. It cannot be deleted and is marked as complete upon submitting the correct answer to the quiz via the `answer` command. |
| **Completed** | The status of a task indicating that it has been completed (displayed with `[X]`). |
| **Pending** | The status of a task indicating that it has not been completed (displayed with `[ ]`). |

## AI Use Disclaimer

| Tool used       | purpose                                                          | scope(time)   | scope(code)                 |
| --------------- | ---------------------------------------------------------------- | ------------- | --------------------------- |
| ChatGPT         | Generate chatbot profile image variations                        | ~Week 5       | main/java/resources/images  |
| Antigravity     | Heavily used to reimagine JavaFX GUI                             | A-BetterGUI   | main/java.../ui/gui         |
| Antigravity     | Greatly improve test coverage                                    | A-MoreTesting | test/java                   |
| Antigravity     | Fill in missing Javadoc                                          | ~Week 4-6     | ~50% of total javadoc       |
| ChatGPT         | Make responses more quirky with a flamboyant personality         | A-Personality | main/java.../chatmodes      |
| ChatGPT         | Assist in final refactoring and clean-up to improve code quality | Week 6        | main/java<br>test/java      |
| Gemini Notebook | Generate software engineering quizzes from se-edu textbook       | ~Week 4       | main/resources/data/quizzes |