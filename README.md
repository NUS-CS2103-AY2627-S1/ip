# Dobby project template

This is a project template for a greenfield Java project. It's named after the servant elf in Harry Potter _Dobby_. Given below are instructions on how to use it.

## Checking code style

Use JDK 25 and run `./gradlew checkstyleMain checkstyleTest` (`.\gradlew.bat checkstyleMain checkstyleTest`
in PowerShell) to check production and test code. `./gradlew check` runs these checks together with JUnit tests.
Style errors and warnings fail the checks; HTML reports are written to `build/reports/checkstyle/`.

The configuration in `config/checkstyle/` comes from
[AddressBook Level 3](https://github.com/se-edu/addressbook-level3/tree/master/config/checkstyle)
and follows the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
Keep using the coding-standard skill and code review for guidelines that automated checks cannot fully assess.

For optional IntelliJ feedback, follow the
[SE-EDU Checkstyle-IDEA setup guide](https://se-education.org/guides/tutorials/checkstyle.html#using-checkstyle-idea-plugin),
select Checkstyle **14.1.0**, load `config/checkstyle/checkstyle.xml`, and include test sources in the scan scope.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Dobby.java` file, right-click it, and choose `Run Dobby.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
          *       .       *       .       *       
      .      ____        _     _              .   
        *   |  _ \  ___ | |__ | |__  _   _     * 
      .     | | | |/ _ \| '_ \| '_ \| | | |   . 
        *   | |_| | (_) | |_) | |_) | |_| |     * 
      .     |____/ \___/|_.__/|_.__/ \__, |   . 
                                     |___/        
          *       .       *       .       *       
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Media credits

- `DobbyProfile.png` is adapted for circular display from
  [Dobby 2](https://commons.wikimedia.org/wiki/File:Dobby_2.jpg), a photograph by
  [NobbiP](https://commons.wikimedia.org/wiki/User:NobbiP), licensed under
  [CC BY-SA 3.0](https://creativecommons.org/licenses/by-sa/3.0/).
