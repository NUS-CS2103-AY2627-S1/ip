# Verity

Verity is a desktop chatbot for tracking todos, deadlines, and events, usable via both a
command-line interface and a JavaFX GUI. See the [user guide](docs/README.md) for full
usage instructions.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/verity/gui/Launcher.java` file, right-click it, and choose `Run Launcher.main()` to start the GUI (if the code editor is showing compile errors, try restarting the IDE).
1. To run Verity via the command line instead, locate `src/main/java/verity/Verity.java` and choose `Run Verity.main()`. If the setup is correct, you should see something like the below as the output:
   ```
   __     __            _  _          
   \ \   / / ___  _ __ (_)| |_  _   _ 
    \ \ / / / _ \| '__|| || __|| | | |
     \ V / |  __/| |   | || |_ | |_| |
      \_/   \___||_|   |_| \__| \__, | 
                                |___/ 
   ```

## Acknowledgements

This project uses the [Monocraft](https://github.com/IdreesInc/Monocraft) font (Copyright
(c) 2022, Idrees Hassan), licensed under the [SIL Open Font License 1.1](src/main/resources/fonts/Monocraft-OFL-LICENSE.txt),
for the GUI's pixel-art aesthetic.

### AI usage

This project makes pervasive use of AI assistance (Claude Code) for design discussion 
before implementing a feature, writing the implementation itself, and keeping
the JUnit test suite and the console UI test plan (`test/ui-test-plan.md`) in sync with
behavioural changes across increments, including `A-Personality` and `A-BetterGui`. 
Every AI-assisted change was reviewed, verified (via the build, JUnit, and the console UI test suite),
and committed by the project owner.
