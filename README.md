# Rambo

Rambo is an encouraging mission commander for your daily tasks: accept new missions,
adjust priorities, and tackle your day one task at a time. Mission control uses charcoal backgrounds,
olive accents, and amber highlights.

Run the GUI with Java 25 using `./gradlew run`. Type `help` to see the commands.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/rambo/Rambo.java` file, right-click it, and choose `Run Rambo.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   Rambo reporting for duty! Ready when you are.
   Let's tackle today's missions, one task at a time.
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Running the automated tests

Make sure the project is using Java 25, then run:

```shell
./gradlew test
```

JUnit runs focused tests for individual classes and representative simulated conversations automatically. A successful
test is reported as `PASSED`; if a conversation test fails, its captured Rambo output is shown for comparison.

## Checking code style

Checkstyle automatically checks Java source and test files against the
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
The rules in `config/checkstyle/` come from
[AddressBook Level 3](https://github.com/se-edu/addressbook-level3/tree/master/config/checkstyle), following the
[course setup tutorial](https://se-education.org/guides/tutorials/checkstyle.html).
It complements coding guidance by detecting style violations consistently; it does not replace code review.

With Java 25 selected, run style checks manually:

```shell
./gradlew checkstyleMain checkstyleTest
```

Run both tests and style checks with `./gradlew check`. Style checks also run as part of `./gradlew build`.
On Windows, use `gradlew.bat` instead of `./gradlew`.
Any style error or warning fails the check. HTML reports are generated at
`build/reports/checkstyle/main.html` and `build/reports/checkstyle/test.html`.

For editor feedback in IntelliJ, install CheckStyle-IDEA, select version **14.1.0**, and activate
`config/checkstyle/checkstyle.xml` as a local configuration. Include test sources in the scan scope.
