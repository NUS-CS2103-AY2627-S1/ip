# Kia project template

This is a project template for a greenfield Java project. The chatbot is named _Kia_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Kia.java` file, right-click it, and choose `Run Kia.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, Kia will support to-dos, deadlines, events, and task completion in memory, and exit when you enter `bye`:
   ```
   ____________________________________________________________
   ██╗  ██╗██╗ █████╗
   ██║ ██╔╝██║██╔══██╗
   █████╔╝ ██║███████║
   ██╔═██╗ ██║██╔══██║
   ██║  ██╗██║██║  ██║
   ╚═╝  ╚═╝╚═╝╚═╝  ╚═╝
   Hello! I'm Kia.
   What can I do for you?
   ____________________________________________________________
   ```

   For example, entering `todo borrow book`, `deadline return book /by Sunday`, and `event project meeting /from Mon 2pm /to 4pm` produces:
   ```
   ____________________________________________________________
   Got it. I've added this task:
     [T][ ] borrow book
   Now you have 1 task in the list.
   ____________________________________________________________
   ____________________________________________________________
   Got it. I've added this task:
     [D][ ] return book (by: Sunday)
   Now you have 2 tasks in the list.
   ____________________________________________________________
   ____________________________________________________________
   Got it. I've added this task:
     [E][ ] project meeting (from: Mon 2pm to: 4pm)
   Now you have 3 tasks in the list.
   ____________________________________________________________
   ____________________________________________________________
   ```

   Running `list` displays:
   ```
   ____________________________________________________________
   Here are the tasks in your list:
   1.[T][ ] borrow book
   2.[D][ ] return book (by: Sunday)
   3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
   ____________________________________________________________
   ```

   Invalid commands are reported without stopping Kia. For example:
   ```
   ____________________________________________________________
   OOPS!!! The description of a todo cannot be empty.
   ____________________________________________________________
   ____________________________________________________________
   OOPS!!! I'm sorry, but I don't know what that means :-(
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Running the JavaFX GUI with Gradle

Use JDK 25 and run:

```powershell
.\gradlew.bat run
```

Gradle supplies JavaFX's platform libraries and the native-access flags needed by
JavaFX 17 on JDK 25. The application uses the tutorial's non-modular classpath
layout, so JavaFX may still print its informational `unnamed module` warning;
it does not prevent the GUI from starting. Removing that warning entirely would
require migrating the project to a named Java module.

If you launch `kia.Launcher` directly from IntelliJ, add these VM options to the
run configuration as Gradle cannot apply them to an IDE-created configuration:

```text
--enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=allow
```
