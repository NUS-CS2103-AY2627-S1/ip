# Woofer — Your tail-wagging task buddy

Woofer is a cheerful pet-dog chatbot that helps you keep track of tasks. It fetches your list, sniffs out matching tasks, and celebrates each completed task with a high paw. Its GUI pairs a paw badge with biscuit-yellow and warm brown colours. Commands and date formats stay the same.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Woofer.java` file, right-click it, and choose `Run Woofer.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
____________________________________________________________________________________________________
__        __   ____   ____   _____   _____   ____
\ \      / /  / __ \ / __ \ |  ___| | ____| |  _ \
 \ \ /\ / /  | |  | | |  | || |_    |  _|   | |_) |
  \ V  V /   | |__| | |__| ||  _|   | |___  |  _ <
   \_/\_/     \____/ \____/ |_|     |_____| |_| \_\
Woof! I'm Woofer, your tail-wagging task buddy.
Let's tackle your tasks, one paw at a time!
____________________________________________________________________________________________________
Woof woof! Time for a nap. See you on our next adventure!
____________________________________________________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Input errors and file recovery

- Leading/trailing spaces and repeated spaces or tabs are accepted and normalized to single spaces.
- Commands take one line. Task descriptions cannot contain `|`, which separates fields in the save file.
- `list`, `undo`, and `bye` do not accept extra arguments. Task numbers must be positive whole numbers from `list`.
- Dates must be real calendar dates in `yyyy-MM-dd` format. Events must end after their start date; same-day events are rejected.
- Deadline/event date markers must appear exactly once in the required order. Invalid commands leave tasks and undo history unchanged.
- Identical tasks remain allowed, since repeated chores can be intentional.
- A missing `data/woofer.txt` is normal on first use. An unreadable or malformed file produces a startup warning and disables saving for that session, protecting the original file. Check the file's records and permissions, repair it, then restart Woofer. Tasks entered during this session are only in memory.
- Saves use a temporary file and atomic replacement. If writing or atomic replacement fails, Woofer warns that changes are only in memory. Resolve the file/folder problem and make another task change to retry saving before closing the app.

## Testing

Run `./gradlew test checkstyleMain checkstyleTest` with Java 25. See [the testing guide](docs/Testing.md) for the automated test scope, isolated test-data setup, and a manual GUI/platform checklist.
