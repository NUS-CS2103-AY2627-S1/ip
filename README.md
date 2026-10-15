# Noms project

This is a project template for a greenfield Java project. The chatbot is named _Noms_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Noms.java` file, right-click it, and choose `Run Noms.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    _   _  ___  __  __  ____
   | \ | |/ _ \|  \/  |/ ___|
   |  \| | | | | |\/| |\___ \
   | |\  | |_| | |  | | ___) |
   |_| \_|\___/|_|  |_||____/
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

---
### AI Usage Declaration
I used Claude Code (Sonnet 5, Opus 4.8) and Codex (GPT5.6 Sol, GPT5.6 Luna and GPT6 Astra) as co-development tools throughout this project. They supported tasks such as code generation, test writing, debugging, refactoring, git support, and documentation drafting.
I remain responsible for the project requirements, review of code generated and decisions made. 
