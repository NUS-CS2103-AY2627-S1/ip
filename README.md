# FlorkOfCows project

AI-assisted development note: This project was developed with the assistance of GitHub Copilot. The tool was used throughout implementation, debugging, and test creation to help draft and refine Java code, parser validation, and automated tests.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/FlorkOfCows.java` file, right-click it, and choose `Run FlorkOfCows.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
           ______ _            _     ____   __  _____                  
          |  ____| |          | |   / __ \ / _|/ ____|                 
          | |__  | | ___  _ __| | _| |  | | |_| |     _____      _____ 
          |  __| | |/ _ \| '__| |/ / |  | |  _| |    / _ \ \ /\ / / __|
          | |    | | (_) | |  |   <| |__| | | | |___| (_) \ V  V /\__ \"
          |_|    |_|\___/|_|  |_|\_\\____/|_|  \_____\___/ \_/\_/ |___/
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Image Credits
User image: "https://in.pinterest.com/pin/florkofcows-icon--5136987067416450/"
FlorkOfCows image: "https://merch.kawaentertainment.com/en-ca/collections/florkofcows?srsltid=AU7gw4VWBu3-Ch_7nMbdG9oau7dqLR15S2lrJeTsE6VYOk67CIuC5E1u"