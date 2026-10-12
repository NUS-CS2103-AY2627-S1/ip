# E.C.H.O. project

This is a project template for a greenfield Java project for the E.C.H.O. chatbot. E.C.H.O. stands for _Everyday Conversational and Helpful Operator_. Given below are instructions on how to use it.

## Setting up in IntelliJ

Prerequisites: JDK 25 and the latest version of IntelliJ.

1. Open IntelliJ. If another project is open, click `File` > `Close Project` first.
1. Open the project in IntelliJ:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** as explained in the [IntelliJ documentation](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. Locate `src/main/java/echo/Echo.java`, right-click it, and choose `Run Echo.main()`.
   E.C.H.O. should start and display its welcome message.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Commands

Run `help` in E.C.H.O. to display the available commands. The supported formats are:

```text
help
list
todo <description>
deadline <description> /by <dd-mm-yyyy> [HH:MM]
event <description> /from <dd-mm-yyyy> [HH:MM] /to <dd-mm-yyyy> [HH:MM]
mark <task number>
unmark <task number>
delete <task number>
bye
```

Task numbers are the one-based numbers shown by `list`. Invalid commands and
task formats are reported with an explanation and an example of how to fix them.

## Acknowledgements

### Use of Generative AI Tools

This project made extensive use of generative AI assistance throughout development, in accordance with the module's policy on AI assistance:

* **Tools Used**:
  * **OpenAI Codex / ChatGPT**: Used as an intelligent code-completion and pair-programming tool during initial feature development and refactoring.
  * **Google Antigravity**: Used for codebase analysis, test suite generation, coding standard verification, and project workflow assistance.
* **Extent of Use**:
  * **Architecture & Refactoring**: Refining OOP class structures (Command pattern, Task hierarchy, TaskManager/TaskList separation) and adhering to the Single Level of Abstraction Principle (SLAP).
  * **Automated Unit Testing**: Writing and expanding JUnit 5 test suites for `DateTimeParser`, `Ui`, `TaskList`, `Task`, `Storage`, and command executions, achieving comprehensive branch and instruction coverage.
  * **Error Handling & Edge Cases**: Designing defensive checks for corrupted storage files, chronological event validations, and duplicate task detection.
  * **Coding Standards & Documentation**: Ensuring strict compliance with SE-EDU Checkstyle configurations, generating standard-compliant Javadocs, drafting commit messages following SE-EDU Git conventions, and structuring the manual testing guide.