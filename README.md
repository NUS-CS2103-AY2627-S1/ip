# Assistant BERT

This is based on "Duke", a project template for a greenfield Java project. It's named `B E R T`. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/kpei/Launcher.java` file, right-click it, and choose `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    ____     ___  ____  ______     ____     ___  ____  ______     ____     ___  ____  ______ 
   |    \   /  _]|    \|      |   |    \   /  _]|    \|      |   |    \   /  _]|    \|      |
   |  o  ) /  [_ |  D  )      |   |  o  ) /  [_ |  D  )      |   |  o  ) /  [_ |  D  )      |
   |     ||    _]|    /|_|  |_|   |     ||    _]|    /|_|  |_|   |     ||    _]|    /|_|  |_|
   |  O  ||   [_ |    \  |  |     |  O  ||   [_ |    \  |  |     |  O  ||   [_ |    \  |  |  
   |     ||     ||  .  \ |  |     |     ||     ||  .  \ |  |     |     ||     ||  .  \ |  |  
   |_____||_____||__|\_| |__|     |_____||_____||__|\_| |__|     |_____||_____||__|\_| |__|  
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## AI Declaration

Generative AI was used heavily in writing code for this project.

Tools used:
- Google Antigravity
- OpenAI Codex

Where it was used:
- Most of the GUI - JavaFX and JavaFXML
- Most of the Tests
- Storage Config classes
- Exceptions

Where it was used LESS:
- Re-written command classes: flags and user commands
- Datatypes

Basically much of the codebase is AI-generated, but with much of my oversight and heavily refactored.

## Image attribution

artsyolive_. (n.d.). *[bart simpson gummybear tattoo]* [Pinterest Pin]. Pinterest. Retrieved September 18, 2026, from https://fr.pinterest.com/pin/572449802640664140/
