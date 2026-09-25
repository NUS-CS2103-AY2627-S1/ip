# Goat task manager

Goat is a command-line task manager that supports todos, deadlines, events,
persistent storage, and keyword search.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
2. Open the project into Intellij as follows:
   1. Click `Open`.
   2. Select the project directory, and click `OK`.
   3. If there are any further prompts, accept the defaults.
3. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
4. After that, locate `src/main/java/goat/Goat.java`, right-click it, and choose
   `Run Goat.main()` (if the code editor shows compile errors, try restarting the IDE).
   If the setup is correct, you should see output like this:
   ```
     ____             _
    / ___| ___   __ _| |_
   | |  _ / _ \ / _` | __|
   | |_| | (_) | (_| | |_
    \____|\___/ \__,_|\__|
   Hello! I'm Goat.
   What can I do for you?
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building an executable JAR

Run `./gradlew shadowJar` from the project root. The executable JAR is created at
`build/libs/goat.jar`. You can copy that file into any folder and run it there with:

```text
java -jar goat.jar
```
