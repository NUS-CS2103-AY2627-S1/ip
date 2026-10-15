# Bubba

Bubba is a command-line task manager for todos, deadlines, and events.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/bubba/Bubba.java` file, right-click it, and choose `Run Bubba.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```text
   Hello! I'm Bubba.
   What can I do for you?
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.


## Building and running

Use Java 25. From the project root:

```bash
./gradlew test shadowJar
java -jar build/libs/bubba.jar
```

To distribute Bubba, attach `build/libs/bubba.jar` to a GitHub release.
Copy the JAR into a folder and run `java -jar bubba.jar` from that folder.
Tasks are stored in `data/bubba.txt`, relative to the folder you run from.
When upgrading from an earlier release, copy your previous task save file to
`data/bubba.txt` before starting Bubba; keep a backup of the original.
