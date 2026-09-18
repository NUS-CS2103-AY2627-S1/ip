# Mochi

Mochi is a desktop task assistant that helps you take your tasks one bite at a time.
Read the [User Guide](https://pinxinfang.github.io/ip/) or download the
[latest release](https://github.com/pinxinfang/ip/releases/latest).

![Mochi desktop interface](docs/Ui.png)

## Optional increments

| Increment | Implementation |
| --- | --- |
| A-BetterGui | Responsive message widths, distinct user/reply layouts, labelled errors, keyboard focus, resize support. |
| A-Personality | Mochi's calm, food-inspired wording and green/pink visual theme. |
| A-MoreErrorHandling | Strict markers and dates, safe event fields, protected corrupt files, unchanged state after save failures. |
| A-MoreTesting | JUnit coverage for commands, parsing, task models, console output, storage, and complete task workflows. |
| A-UserGuide | JAR quick start, examples for every command, persistence details, and troubleshooting. |

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/mochi/Mochi.java` file, right-click it, and choose `Run Mochi.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   ____________________________________________________________
   Hello! I'm Mochi, your little pocket of calm.
   Let's take your tasks one bite at a time.
   ____________________________________________________________
   todo borrow book
   ____________________________________________________________
   Tucked away! I've added this task:
     [T][ ] borrow book
   Now you have 1 task in the list.
   ____________________________________________________________
   deadline return book /by 2026-08-30
   ____________________________________________________________
   Tucked away! I've added this task:
     [D][ ] return book (by: Aug 30 2026)
   Now you have 2 tasks in the list.
   ____________________________________________________________
   event project meeting /from Mon 2pm /to 4pm
   ____________________________________________________________
   Tucked away! I've added this task:
     [E][ ] project meeting (from: Mon 2pm to: 4pm)
   Now you have 3 tasks in the list.
   ____________________________________________________________
   mark 2
   ____________________________________________________________
   Sweet! One bite done. I've marked this task as done:
     [D][X] return book (by: Aug 30 2026)
   ____________________________________________________________
   unmark 2
   ____________________________________________________________
   No rush. Back on the plate, marked as not done yet:
     [D][ ] return book (by: Aug 30 2026)
   ____________________________________________________________
   list
   ____________________________________________________________
   Here are the tasks in your list:
   1.[T][ ] borrow book
   2.[D][ ] return book (by: Aug 30 2026)
   3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
   ____________________________________________________________
   delete 3
   ____________________________________________________________
   A little less on your plate. I've removed this task:
     [E][ ] project meeting (from: Mon 2pm to: 4pm)
   Now you have 2 tasks in the list.
   ____________________________________________________________
   todo
   ____________________________________________________________
   Oops! A todo needs a description.
   ____________________________________________________________
   blah
   ____________________________________________________________
   Oops! I don't know that command yet. Try todo, deadline, event, list, sort, find, mark, unmark, delete, or bye.
   ____________________________________________________________
   bye
   ____________________________________________________________
   Rest well! Mochi will be here for your next little step.
   ____________________________________________________________
   ```

## Data storage

Mochi saves task data automatically in `data/mochi.txt` and reloads it the next time the application starts.

## Gradle

Use `./gradlew build` to build the project and `./gradlew run` to open Mochi's JavaFX interface.
Use `./gradlew test` to run the JUnit test suite and
`./gradlew checkstyleMain checkstyleTest` to verify the Java coding standard.
Use `./gradlew clean shadowJar` to create the executable `build/libs/mochi.jar`.
Copy it to a folder of your choice and run `java -jar mochi.jar` from that folder.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Acknowledgements

ChatGPT in Codex mode was used to review and refine the Level-0 through Level-8 implementations and documentation,
including the A-Classes, A-Inheritance, A-Exceptions, A-Collections, A-Enums, A-MoreOOP, A-Packages,
A-Gradle, A-JUnit, A-Jar, A-JavaDoc, A-CodingStandard, Level-9, A-CheckStyle, Level-10,
and A-Varargs enhancements. Codex also assisted with A-BetterGui, A-Personality,
A-MoreErrorHandling, A-MoreTesting, and the User Guide.

The project uses the [course starter template](https://github.com/NUS-CS2103-AY2627-S1/ip),
based on [SE-EDU Duke](https://github.com/se-edu/duke), and the course JavaFX tutorial.
The current theme uses standard JavaFX controls without external artwork.
