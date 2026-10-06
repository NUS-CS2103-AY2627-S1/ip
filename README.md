# Duke project template

This is a project template for a greenfield Java project. It's named after the Java mascot _Duke_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Duke.java` file, right-click it, and choose `Run Duke.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    ____        _        
   |  _ \ _   _| | _____ 
   | | | | | | | |/ / _ \
   | |_| | |_| |   <  __/
   |____/ \__,_|_|\_\___|
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.


# Caesar - Personal Task Assistant

Caesar is a lightweight, command-line personal assistant chatbot designed to help you organize, track, and manage your daily tasks with ease.

---

## Features

### 1. Task Management
* **To-Do Tasks:** Add tasks without any specific date or time.
* **Deadlines:** Add tasks that need to be completed before a specific deadline.
* **Events:** Add tasks that start and end at specific times.

### 2. Task Operations
* **Mark / Unmark:** Mark tasks as done or revert them back to incomplete.
* **Delete:** Remove unwanted tasks from the list.
* **Reschedule:** Move a deadline to a new date, or an event to new start and end dates.
* **List:** View all currently tracked tasks.
* **Sort:** View tasks organized by status (pending tasks first).

---

## Quick Start

### Prerequisites
* **Java 25** or later installed on your system.

### Running the Application

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/luyao-111/ip.git](https://github.com/luyao-111/ip.git)
   cd ip
