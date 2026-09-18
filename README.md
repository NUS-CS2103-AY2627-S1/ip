# Lune project template

This is a project template for a greenfield Java project. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/lune/Lune.java` file, right-click it, and choose `Run Lune.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    _                     
   | |   _   _ _ __   ___ 
   | |  | | | | '_ \ / _ \
   | |__| |_| | | | |  __/
   |_____\__,_|_| |_|\___|
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Checkstyle

This project enforces the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html) via Checkstyle. Run it from the command line with:

```
./gradlew checkstyleMain checkstyleTest
```

To get the same checks live in the editor, install the **Checkstyle-IDEA** plugin (`File` > `Settings` > `Plugins` > `Marketplace`, search "Checkstyle-IDEA", install, then restart Intellij), then under `File` > `Settings` > `Tools` > `Checkstyle`, add a configuration pointing to `config/checkstyle/checkstyle.xml` and mark it active. See the [Checkstyle tutorial](https://se-education.org/guides/tutorials/checkstyle.html) for the full walkthrough with screenshots.
