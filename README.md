# Odysseus

Odysseus is a personal-assistant chatbot. Given below are instructions on how to use the project.

## AI use

AI assistance was used to set up Beryl's repository workflow, consolidate the
existing project guidance into Beryl's canonical agent files, and add this
acknowledgement. AI has implemented application behavior for this project.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
2. Open the project into Intellij as follows:

   1. Click `Open`.
   2. Select the project directory, and click `OK`.
   3. If there are any further prompts, accept the defaults.
3. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).
   In the same dialog, set the **Project language level** field to the `SDK default` option.
4. After that, locate the `src/main/java/Odysseus.java` file, right-click it, and choose `Run Odysseus.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see:

   ```
     ___    ____  __   __  ____   ____  _____  _   _  ____
    / _ \  |  _ \ \ \ / / / ___| / ___|| ____|| | | |/ ___|
   | | | | | | | | \ V /  \___ \ \___ \|  _|  | | | |\___ \
   | |_| | | |_| |  | |    ___) | ___) | |___ | |_| | ___) |
    \___/  |____/   |_|   |____/ |____/|_____| \___/ |____/

   Ahoy, traveler! I am Odysseus, long tested by sea and fate.
   What course shall we chart together?
   ____________________________________________________________

   todo read book
   ____________________________________________________________
   Well charted. I've added this task:
     [T][ ] read book
   Our voyage now holds 1 task.
   ____________________________________________________________

   deadline return home /by 2026-12-25
   ____________________________________________________________
   Well charted. I've added this task:
     [D][ ] return book (by: Sunday)
   Our voyage now holds 2 tasks.
   ____________________________________________________________

   event project meeting /from Mon 2pm /to 4pm
   ____________________________________________________________
   Well charted. I've added this task:
     [E][ ] project meeting (from: Mon 2pm to: 4pm)
   Our voyage now holds 3 tasks.
   ____________________________________________________________

   list
   ____________________________________________________________
   Here are the tasks on our voyage, traveler:
   1. [T][ ] read book
   2. [D][ ] return book (by: Sunday)
   3. [E][ ] project meeting (from: Mon 2pm to: 4pm)
   ____________________________________________________________

   mark 2
   ____________________________________________________________
   Well sailed! I've marked this task as done:
     [D][X] return book (by: Sunday)
   ____________________________________________________________

   unmark 2
   ____________________________________________________________
   This task awaits its hour again:
     [D][ ] return book (by: Sunday)
   ____________________________________________________________

   delete 3
   ____________________________________________________________
   The waves have carried this task from our log:
     [E][ ] project meeting (from: Mon 2pm to: 4pm)
   Our voyage now holds 2 tasks.
   ____________________________________________________________

   bye
   ____________________________________________________________
   Farewell, traveler. May Athena guide your voyage until we meet again.
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Correcting a command

Odysseus keeps sailing after a mistake and explains how to correct it. For
example, enter `todo borrow book` rather than `todo`, and use `mark 2` rather
than `mark` when marking the second task complete.

## Studying the Odyssey with Athena's Archive

Odysseus can retain Odyssey-related vocabulary and revision questions between
conversations. Create a card with `learn`, then use `review` to study it:

```text
learn nostos /answer homecoming /topic Greek terms
review Greek terms
answer homecoming
```

Use `cards [topic]` to view the archive and its mastery levels. During an
active review, `reveal` shows the expected answer; follow it with `mastered`
when you recalled it or `again` when it needs another voyage. Use
`forget <card number>` to remove a card.

## Launching the JavaFX GUI

Make sure JDK 25 is configured, then run the following command from the project
root:

```powershell
.\gradlew.bat run
```

Alternatively, in IntelliJ, open `src/main/java/odysseus/Launcher.java` and run
`Launcher.main()`. This opens the Odysseus JavaFX conversation window.

## Building the JAR

With JDK 25 installed, run this from the project root:

```powershell
.\gradlew.bat shadowJar
```

Retrieve the bundled application JAR from `build\libs\odysseus.jar`.
