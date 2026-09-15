# LuigiBot

LuigiBot is a Luigi-themed task manager created for the CS2103 Individual
Project. It supports todos, deadlines, events, task searching, and persistent
storage through both a command-line interface and a JavaFX GUI.

## Setting up in IntelliJ IDEA

Prerequisites: JDK 25 and a recent version of IntelliJ IDEA.

1. Open IntelliJ IDEA.
1. Select **Open**, choose this project directory, and accept the default
   import settings.
1. Configure the project to use **JDK 25**, following the
   [IntelliJ IDEA SDK guide](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).
1. Set the project language level to **SDK default**.
1. Open the Gradle tool window and run the `run` task, or run `gradlew run`
   from the project root.

Keep `src/main/java` as the Java source root. Gradle and other project tools
expect Java source files to remain under this directory.

## Building and testing

Run the following command from the project root:

```shell
gradlew clean build
```

This compiles LuigiBot and runs its JUnit and Checkstyle checks.

## Acknowledgements

- The JavaFX GUI structure was adapted from the
  [SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFxPart1.html).
- The Checkstyle configuration was adapted from
  [AddressBook Level 3](https://github.com/se-edu/addressbook-level3/tree/master/config/checkstyle).
- Luigi and Mario are characters owned by Nintendo. Their images are used in
  this project for educational purposes.
