# Xuan

Xuan is a simple task management chatbot built with Java and JavaFX.

It helps users manage todos, deadlines, and events through simple text commands. Xuan also provides a graphical user interface and stores tasks locally so that they can be loaded again when the application is restarted.

## Features

Xuan supports the following features:

- Add todos
- Add deadlines
- Add events
- View all tasks
- Mark tasks as done
- Mark tasks as not done
- Delete tasks
- Find tasks by keyword
- Find deadlines by date
- Display help information
- Handle invalid commands and common input errors
- Save and load tasks from local storage

## Setting up in IntelliJ

### Prerequisites

- JDK 25
- A recent version of IntelliJ IDEA

### Steps

1. Open IntelliJ IDEA.
2. Click `Open`.
3. Select the Xuan project directory.
4. Configure the project to use **JDK 25**.
5. Set the **Project language level** to `SDK default`.
6. Locate:

   `src/main/java/xuan/Launcher.java`

7. Right-click `Launcher.java`.
8. Choose `Run Launcher.main()`.

The Xuan GUI should open if the project is configured correctly.

## Running with Gradle

You can also run Xuan from the project directory using:

```bash
./gradlew run
