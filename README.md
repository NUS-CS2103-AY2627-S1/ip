# Bro project template

This is a project template for a greenfield Java project. It is named _Bro_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
2. Open the project into Intellij as follows:
   1. Click `Open`.
   2. Select the project directory, and click `OK`.
   3. If there are any further prompts, accept the defaults.
3. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
4. After that, locate the `src/main/java/Bro.java` file, right-click it, and choose `Run Bro.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:

   ```
       ____   ____  ____ 
      / __ ) / __ \/ __ \
     / __  |/ /_/ / / / /
    / /_/ // _, _/ /_/ /
   /_____//_/ |_|\____/
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

---

## Acknowledgements & AI Assistance

Generative AI tools were used responsibly throughout the development of this project. The specific AI models utilized were:

- **Gemini 3.7 Flash**
- **Gemini 3.8 Flash**
- **GPT-5.6 Luna**

Specific areas where AI assistance was utilized include:

- **Implementation Planning & Design**: Brainstorming implementation approaches and creating structured plans for new features and structural enhancements.
- **Feature Increments**: Assisting in the implementation of specific increments, such as GUI improvements (speech bubbles, avatar positioning, responsive window layout) and advanced error handling and syntax validation.
- **Code Refactoring & Coding Standards**: Refactoring code for readability and ensuring strict adherence to the SE-EDU Java Coding Standard (resolving Checkstyle issues, member ordering, and naming conventions).
- **Automated Testing**: Assisting in drafting and structuring JUnit 5 unit and integration tests across parser, domain models, and chatbot workflows.
- **Documentation & Git**: Drafting the User Guide (`docs/README.md`) and composing detailed Git commit messages and Pull Request descriptions.
