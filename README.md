# Fox project template

This is a project template for a greenfield Java project. The chatbot is named _Fox_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/fox/Fox.java` file, right-click it, and choose `Run Fox.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    /\_/\
   ( •ᴗ• )   Hi! I'm Fox, your little companion. 🦊
    > ^ <    I may be small, but I've got plenty of tricks up my sleeve.

             What can I do for you?

   ____________________________________________________________
  read book
   ____________________________________________________________
    added: read book
   ____________________________________________________________
  return book
   ____________________________________________________________
    added: return book
   ____________________________________________________________
  buy bread
   ____________________________________________________________
    added: buy bread
   ____________________________________________________________
  list
   ____________________________________________________________
    Here are the tasks in your list:
    1.[ ] read book
    2.[ ] return book
    3.[ ] buy bread
   ____________________________________________________________
  mark 2
   ____________________________________________________________
    Nice! I've marked this task as done:
      [X] return book
   ____________________________________________________________
  list
   ____________________________________________________________
    Here are the tasks in your list:
    1.[ ] read book
    2.[X] return book
    3.[ ] buy bread
   ____________________________________________________________
  unmark 2
   ____________________________________________________________
    OK, I've marked this task as not done yet:
      [ ] return book
   ____________________________________________________________
  list
   ____________________________________________________________
    Here are the tasks in your list:
    1.[ ] read book
    2.[ ] return book
    3.[ ] buy bread
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

Fox's Java classes are organized under the `fox` package. Application startup is
`fox.Fox`; task models are in `fox.task`, command parsing is in `fox.parser`,
file persistence is in `fox.storage`, and console presentation is in `fox.ui`.

## Acknowledgements

Parts of Fox's JavaFX GUI were adapted from the
[SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html):

* [Part 1](https://se-education.org/guides/tutorials/javaFxPart1.html) guided the JavaFX launcher and
  cross-platform Gradle dependency setup.
* [Part 4](https://se-education.org/guides/tutorials/javaFxPart4.html) guided the FXML controller and dialog-box
  interaction structure.
* [Part 5](https://se-education.org/guides/tutorials/javaFxPart5.html) provided the basis for the initial CSS styling.

These parts were subsequently customized for Fox.
