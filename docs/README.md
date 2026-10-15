# Jarvis Documentation

Jarvis is a *fast and intuitive task scheduler that helps you to keep track of your tasks and deadlines. So much so that you will be feeling like Tony Stark below.


*When completed

![Screenshot of product](https://john-dai-88.github.io/ip/Ui.png)

---

## Setting up and Running Jarvis
1. Download the latest java executable (.jar file) from [here](https://github.com/John-Dai-88/ip/releases)
2. Place it in your desired directory
3. Open a command prompt terminal and navigate to the directory the java executable file is located in
4. Run the command `java -jar jarvis.jar`
* Alternatively, double-click the java executable to run the program

If successful, Jarvis will open a desktop window similar to the image above.

Jarvis stores tasks in`data/jarvis.txt` which is located within the same directory as the JAR executable

---

## Available Commands

### Command Appendix

| Command Type                | Command Format                                                             | Example                                                          | Function                                                         |
|-----------------------------|----------------------------------------------------------------------------|------------------------------------------------------------------|------------------------------------------------------------------|
| Getting help                | `help`                                                                     | `help`                                                           | Displays list of commands available and their respective formats |
| To-do task                  | `todo <TASK_DESCRIPTION>`                                                  | `todo Buy a book`                                                | Creates a to-do task                                             |
| Deadline task (Date only)   | `deadline <TASK_DESCRIPTION> /by <yyyy-mm-dd>`                             | `deadline Return book /by 2026-07-20`                            | Creates a date-only deadline task                                |
| Deadline task (Date & Time) | `deadline <TASK_DESCRIPTION> /by <yyyy-mm-dd HH:MM>`                       | `deadline Return book /by 2026-07-20 15:30`                      | Creates a date-and-time deadline task                            |
| Event task (Date only)      | `event <TASK_DESCRIPTION> /from <yyyy-mm-dd> /to <yyyy-mm-dd>`             | `event Book sale /from 2026-07-05 /to 2026-07-10`                | Creates a date-only event task                                   |
| Event task (Date & Time)    | `event <TASK_DESCRIPTION> /from <yyyy-mm-dd HH:MM> /to <yyyy-mm-dd HH:MM>` | `event Book seminar /from 2026-07-15 10:00 /to 2026-07-15 14:00` | Creates a date-and-time event task                               |
| Listing of tasks            | `list`                                                                     | `list`                                                           | Lists all stored tasks                                           |
| Marking task                | `mark <VALID_TASK_NUMBER>`                                                 | `mark 3`                                                         | Marks a task as done                                             |
| Unmarking task              | `unmark <VALID_TASK_NUMBER>`                                               | `unmark 5`                                                       | Marks a task as undone                                           |
| Deletion of task            | `delete <VALID_TASK_NUMBER>`                                               | `delete 8`                                                       | Deletes a task from Jarvis's list of stored tasks                |
| Finding tasks               | `find <KEY_WORD>` (Minimum 2 characters long)                              | `find book`                                                      | Finds all tasks that fully or partially match the keyword        |
| Exiting the program         | `bye`                                                                      | `bye`                                                            | Quits Jarvis                                                     |




### Understanding the parameters
**⚠ All commands are in lowercase, parameters are case-insensitive**   

**⚠ Follow the format EXACTLY as seen in the table or the program might not recognize the command**

- **For all task command** : The `<TASK_DESCRIPTION>` parameter refers to an arbitrary task name

- **For deadline and event command** : Both date-only and date-and-time formats are supported, allowing users to choose whichever format best suits their needs.

- **For deadline and event command** : For the `<yyyy-mm-dd HH:MM>` parameter
    - yyyy refers to year
    - mm refers to month
    - dd refers to day
    - HH refers to hour (in 24-hour format)
    - MM refers to minutes (in 24-hour format)
  
- **For mark, unmark and delete command** : The `<VALID_TASK_NUMBER>` parameter is referring to the task index numbers from the output of `list` (Refer below for examples)

  ![Sample photo of task index numbers](https://john-dai-88.github.io/ip/ValidTaskNumberParamReference.png)
  - To mark/unmark/delete `[T][X] Buy a book`, the task number to insert into <ValidTaskNumber> would be 1
  - Likewise for `[D][] Submit assignment (by: 09 14 2026 23:59)`, the task number to use would be 3
  
- **For find command** : The `<KEY_WORD>` parameter has to be of minimally 2 characters long

---

## Data Storage and Management

### Data Saving
Jarvis automatically saves the data to `data/jarvis.txt` relative to the folder `jarvis.jar` is ran from

###  Data Migration
Retain `jarvis.txt` and store it within the `data` folder relative to the folder of the new Jarvis release

### Importing Data
Users can directly edit `jarvis.txt` and manually update it with data

Please also ensure that data is stored in one of the following formats
- Todo task : `[T][] <TASK DESCRIPTION>` or `[T][X] <TASK DESCRIPTION>`
- Deadline task (Date only) : `[D][] <TASK DESCRIPTION> /by <yyyy-mm-dd>` or `[D][X] <TASK DESCRIPTION> /by <yyyy-mm-dd>`
- Deadline task (Date & Time) : `[D][] <TASK DESCRIPTION> /by <yyyy-mm-dd HH:MM>` or `[D][X] <TASK DESCRIPTION> /by <yyyy-mm-dd HH:MM>`
- Event task (Date only) : `[E][] <TASK_DESCRIPTION> /from <yyyy-mm-dd> /to <yyyy-mm-dd>` or `[E][X] <TASK_DESCRIPTION> /from <yyyy-mm-dd> /to <yyyy-mm-dd>`
- Event task (Date & Time) : `[E][] <TASK_DESCRIPTION> /from <yyyy-mm-dd HH:MM> /to <yyyy-mm-dd HH:MM>` or `[E][X] <TASK_DESCRIPTION> /from <yyyy-mm-dd HH:MM> /to <yyyy-mm-dd HH:MM>`

---

## Troubleshooting
### Jarvis is unable to start up
Cause : Java version is most likely missing from your local machine

Remedy : Run `java -version` in your command terminal and confirm installed java version is 25

If java is not installed, follow this [java installation guide](https://nus-cs2103-ay2627-s1.github.io/website/admin/programmingLanguages.html)

### Missing tasks from stored tasks
Cause : Jarvis executable is most likely not in the same folder as `data/jarvis.txt`

Remedy : Verify that the Jarvis jar executable file is located in the same folder you ran from previously

As Jarvis reads tasks from `data/jarvis.txt` relative to the folder it is located in

### Command is not executed properly
Cause : Command format inputted by user does not match the program's format

Remedy : Please follow the command format specified in the [*Command Appendix*](https://john-dai-88.github.io/ip/#command-appendix) table **EXACTLY**

### Imported task is not read properly
Cause : Imported task data format is malformed or missing certain parameters

Remedy : Please follow the task data follows the format specified in the [*Importing Data*](https://john-dai-88.github.io/ip/#importing-data) section **EXACTLY**
, else the program will skip reading that task

---

### Credits
Jarvis's documentation was done with reference to [SE-EDU AB3 user guide](https://se-education.org/addressbook-level3/UserGuide.html#features)

Week 6's [increments](https://nus-cs2103-ay2627-s1.github.io/website/schedule/week6/project.html) were developed with the support of ChatGPT