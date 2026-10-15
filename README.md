# Bubu

```text
/\___/\
(  >.<  )  Hello! I'm BUBU!
```

Bubu is a friendly pet-themed task management chatbot. It supports to-do tasks,
deadlines, events, task search, and reminders through both a command-line
interface and a JavaFX graphical user interface.

## User Guide

For installation instructions and the complete command reference, see the
[Bubu User Guide](https://xinyu-zxy.github.io/ip/).

## Setting up in IntelliJ

Prerequisites: JDK 25 and the most recent IntelliJ version.

1. Open IntelliJ. If you are not at the welcome screen, click `File` > `Close Project` first.
1. Open the project in IntelliJ:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25**, as explained in the
   [IntelliJ JDK documentation](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. Locate `src/main/java/bubu/Launcher.java`, right-click it, and choose
   `Run 'Launcher.main()'`. If the code editor shows compile errors, restart
   the IDE. If the setup is correct, the Bubu application starts.

**Warning:** Keep `src\main\java` as the root folder for Java files. Do not
rename those folders or move Java files outside that path, because tools such
as Gradle expect the default project layout.

## Credits

The initial project structure and build configuration were provided by the
NUS CS2103 project template. The Bubu application code and UI customisations
were developed for this project.
