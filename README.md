# Zen

Zen is a calm, desktop task-management chatbot that helps you keep track of to-dos, deadlines, and events. Add priorities, mark tasks as complete, search your list, and view tasks scheduled for a particular date; Zen saves your task list between sessions.

Use the command box to chat with Zen. For example, enter `todo buy milk`, `deadline submit report /by 2026-10-10 10:30:00`, or `list`.

For complete setup and command instructions, see the [Zen User Guide](docs/README.md).

## Setting up in IntelliJ

Prerequisites: JDK 25, update IntelliJ to the most recent version.

1. Open IntelliJ (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
2. Open the project into IntelliJ as follows:
   1. Click `Open`.
   2. Select the project directory, and click `OK`.
   3. If there are any further prompts, accept the defaults.
3. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
4. After that, locate the `src/main/java/zen/Zen.java` file, right-click it, and choose `Run Zen.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   ____________________________________________________________
    ______              
   |__  /___  _ __      
     / // _ \| '_ \     
    / /|  __/| | | |    
   /____\___||_| |_|
   Hello! I'm Zen, your calm task companion.
   What can I do for you?
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Acknowledgements

### Use of AI

- **Tool**: ChatGPT Codex
- **Extent of Use**: 
  - **Levels 1 to 8**: Primarily used to generate test cases using the `test-ui`.
  - **Following increments**: Used sample prompts provided by the course to explore and apply AI-assisted software engineering techniques.
  - **Specific Increments**: As [encouraged by the course schedule](https://nus-cs2103-ay2627-s1.github.io/website/schedule/week6/project.html#:~:text=you%20should%20use%20AI%20heavily%20to%20do%20them), AI was heavily used to implement the following increments:
    - `A-BetterGui`: Reduced the size of the user and Zen icons and made the dialog bubbles size responsive to window width.
    - `A-Personality`: Standardized application responses to align with the Zen theme.
    - `A-MoreErrorHandling`: Added improved error handling for `find` and `bye` commands as well as for malformed data in the input file.
    - `A-MoreTesting`: Generated more test cases for areas with insufficient coverage.
- **Quality Assurance:** All AI-generated code was manually reviewed, tested, and refactored to ensure strict adherence to the software engineering principles required by the course.
