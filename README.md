# Shannon

This is a greenfield Java project, started from a project template named after the Java mascot _Duke_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/shannon/Shannon.java` file, right-click it, and choose `Run Shannon.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
      oo_    \\  //       \\\  ///\\\  ///   .-.   \\\  ///
     /  _)-< (o)(o)   /)  ((O)(O))((O)(O)) c(O_O)c ((O)(O))
     \__ `.  ||  || (o)(O) | \ ||  | \ || ,'.---.`, | \ ||
        `. | |(__)|  //\\  ||\\||  ||\\||/ /|_|_|\ \||\\||
        _| | /.--.\ |(__)| || \ |  || \ || \_____/ ||| \ |
     ,-'   |-'    `-/,-. | ||  ||  ||  ||'. `---' .`||  ||
    (_..--'        -'   ''(_/  \_)(_/  \_) `-...-' (_/  \_)
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.


## Acknowledgements

### Third-party libraries
- [JavaFX](https://openjfx.io/): graphical user interface
- [JUnit 5](https://junit.org/junit5/): unit testing
- [Shadow Gradle plugin](https://gradleup.com/shadow/): packaging the runnable JAR
- [Checkstyle](https://checkstyle.org/): coding standard checks

The avatar images (`DaUser.png`, and `Shannon.png`, renamed from `DaDuke.png`) come from the
SE-EDU JavaFX tutorial.

### Use of AI tools
- **Tool:** [Claude Code](https://claude.com/claude-code), Anthropic's AI coding agent,
  running Claude Opus 5.
- **Used by:** Ravan ([@ravanc](https://github.com/ravanc))
- **Extent:** Claude Code wrote almost all of the code in this repository, including the
  task logic, parsing, storage, error handling and JavaFX GUI. It also wrote most commit
  messages and parts of the documentation, including sections of the User Guide. I decided
  what to build, prompted and directed each change, and reviewed the results before
  committing.
