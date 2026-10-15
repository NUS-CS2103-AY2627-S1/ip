# Shai project template

This is a project template for a greenfield Java project. It has been customized as the Shai chatbot. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Shai.java` file, right-click it, and choose `Run Shai.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
     SSSS  H  H   AAA   III
    S      H  H  A   A   I
     SSS   HHHH  AAAAA   I
        S  H  H  A   A   I
    SSSS   H  H  A   A  III
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Acknowledgements

The project author used OpenAI Codex with GPT-5.6 Luna for AI-assisted coding,
debugging, testing, and documentation throughout this project. Final design
decisions, code review, and validation were performed by the project author.
See the [official GPT-5.6 Luna documentation](https://developers.openai.com/api/docs/models/gpt-5.6-luna).
