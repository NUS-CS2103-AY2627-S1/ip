# Verity

This is a greenfield Java project. It's named after the eponymous minecraft mod, where a yellow talking ball follows you around and only chats the truth. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Verity.java` file, right-click it, and choose `Run Verity.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    V   V  EEEEE  RRRR   IIIII  TTTTT  Y   Y
    V   V  E      R   R    I      T     Y Y
     V V   EEEE   RRRR     I      T      Y
      V    E      R R      I      T      Y
      V    EEEEE  R  RR  IIIII    T      Y
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
## Testing

This project uses JUnit 5. Test classes are located under
`src/test/java` and mirror the packages under `src/main/java`.

Run all tests with:

```bash
./gradlew test
```

## Client management

Verity can store client contact information and associate clients with tasks.
Client IDs are generated automatically in the form `C001`, `C002`, and so on.

```text
client add /name Alice Tan /phone +65 9123 4567 /email alice@example.com
client list
client view C001
client find alice
client edit C001 /phone +65 9876 5432
client edit C001 /clear phone
client associate C001 /task 1
client dissociate C001 /task 1
client delete C001
client delete C001 confirm
```

Client assignment is optional when creating a task:

```text
todo Prepare invoice /client C001
deadline Submit proposal /by 2026-09-30 /client C001
event Client meeting /from 2026-09-20 /to 2026-09-20 /client C001
```

See the [user guide](docs/README.md) for field validation, command details,
and deletion behavior.

## AI Declaration
I declare that I used Chatgpt 5.6 Luna XHigh on Codex. I used AI whenever the
project tasks gave the option to use AI to perform the tasks. Specifically,
I followed the steps outlined in the AI Guidance boxes in the Projects page.
