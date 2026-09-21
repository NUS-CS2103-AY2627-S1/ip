# Echo project template

This is a project template for a greenfield Java project. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
2. Open the project into Intellij as follows:
   1. Click `Open`.
   2. Select the project directory, and click `OK`.
   3. If there are any further prompts, accept the defaults.
3. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
4. After that, locate the `src/main/java/Echo.java` file, right-click it, and choose `Run Echo.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    _____     _
   | ____|___| |__   ___
   |  _| / __| '_ \ / _ \
   | |__| (__| | | | (_) |
   |_____\___|_| |_|\___/
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Image Credit
* DaEcho.png: https://picryl.com/media/cat-annoyed-mauzen-animals-717af2
* DaUser.png: https://www.pickpik.com/cat-animal-pet-closeup-macro-feline-8058

## AI Usage
### Codex:
* Suggestions on code improvements.
* Performing repetitive / manual tasks (e.g. ASCII banner generation).
* present-changes-visually skill: https://github.com/se-edu/skill-present-changes-visually.
* test-ui skill from ip Level-4 AI guidance section
* Code generation as directed by ip instructions under "AI Guidance" sections (usage ranges from AI-2 to AI-5).
### OpenCode:
~~Started to use OpenCode after Codex usage ran out.~~
* Suggestions on code improvements.
* Performing repetitive / manual tasks (e.g. ASCII banner generation).
* Code generation as guided by ip instructions under "AI Guidance" sections.(usage ranges from AI-4 to AI-5)
### Gemini:
* Search engine.
* Learn about concepts.