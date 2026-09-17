# Crystal

Crystal is a console chatbot that helps users manage tasks, deadlines, and events.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/crystal/Crystal.java`, right-click it, and choose `Run Crystal.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see Crystal's banner:
   ```
     ____ ______   ______ _____  _    _
    / ___|  _ \ \ / / ___|_   _|/ \  | |
   | |   | |_) \ V /\___ \ | | / _ \ | |
   | |___|  _ < | |  ___) || |/ ___ \| |___
    \____|_| \_\|_| |____/ |_/_/   \_\_____|
   ```

Alternatively, run Crystal through Gradle from the project root:

```bash
./gradlew run --console=plain
```

## Testing

JUnit tests mirror Crystal's production packages beneath `src/test/java`. Run the complete unit test suite with:

```bash
./gradlew test
```

Whenever application code changes, update the corresponding JUnit tests with reasonable normal, boundary, and error cases before running the suite.

**Warning:** Keep `src/main/java` as the source root for Java files. Gradle and IntelliJ expect the package directories, such as `crystal/task`, beneath this folder.
