# Jeff

Jeff is a desktop task-management chatbot that helps users manage todos,
deadlines, and events through simple text commands.

Jeff supports adding, listing, marking, deleting, finding, and updating tasks.
Tasks are saved automatically and restored when the application is reopened.

![Jeff GUI](docs/Ui.png)

## Features

Jeff allows users to:

- add todos, deadlines, and events;
- view all saved tasks;
- mark tasks as completed or incomplete;
- delete tasks;
- search for tasks by keyword;
- update individual task details;
- preserve task completion status when updating tasks; and
- save and restore tasks automatically.

For complete command instructions, see the
[Jeff User Guide](docs/README.md).

## Requirements

- Java Development Kit (JDK) 25
- Windows, macOS, or Linux

Check your Java version using:

```bash
java -version
```

## Running the application

### Running the JAR file

1. Download `Jeff.jar`.
2. Place it inside an empty folder.
3. Open a terminal in that folder.
4. Run:

```bash
java -jar Jeff.jar
```

Jeff will create a `data` folder in the current directory to store tasks.

### Running from the source code

Clone this repository:

```bash
git clone https://github.com/patientotter/ip.git
cd ip
```

On Windows PowerShell:

```powershell
.\gradlew clean run
```

On macOS or Linux:

```bash
./gradlew clean run
```

## Building the application

On Windows PowerShell:

```powershell
.\gradlew clean shadowJar
```

On macOS or Linux:

```bash
./gradlew clean shadowJar
```

The generated JAR file can be found at:

```text
build/libs/Jeff.jar
```

## Testing

Run the automated tests with:

### Windows PowerShell

```powershell
.\gradlew clean test
```

### macOS or Linux

```bash
./gradlew clean test
```

## Code-quality checks

Run Checkstyle with:

### Windows PowerShell

```powershell
.\gradlew checkstyleMain checkstyleTest
```

### macOS or Linux

```bash
./gradlew checkstyleMain checkstyleTest
```

The generated Checkstyle reports can be found in:

```text
build/reports/checkstyle/
```

## Generating JavaDoc

On Windows PowerShell:

```powershell
.\gradlew javadoc
```

On macOS or Linux:

```bash
./gradlew javadoc
```

The generated JavaDoc can be found in:

```text
build/docs/javadoc/
```

## Full verification

Before committing changes, run:

### Windows PowerShell

```powershell
.\gradlew clean test checkstyleMain checkstyleTest javadoc shadowJar
```

### macOS or Linux

```bash
./gradlew clean test checkstyleMain checkstyleTest javadoc shadowJar
```

All tasks should finish with:

```text
BUILD SUCCESSFUL
```

## User guide

Refer to the [Jeff User Guide](docs/README.md) for:

- the complete command summary;
- command formats and examples;
- task symbols;
- update-field compatibility;
- common input errors; and
- information about saved data.

## Acknowledgements

Jeff was developed as an individual project for the NUS CS2103 course.

The JavaFX graphical interface is based on the
[SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html).
