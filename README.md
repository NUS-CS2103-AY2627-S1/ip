# YourHand

YourHand is a command-line task manager for tracking to-dos, deadlines, and events during one program session.

## Features

- Add to-dos, deadlines, and events.
- List tasks with their type and completion status.
- Mark and unmark tasks as done.
- Delete tasks by their displayed number.
- Report invalid commands without losing the current task list.

## Requirements

- Java Development Kit (JDK) 25
- IntelliJ IDEA (optional)

## Run the application

### IntelliJ IDEA

1. Open this project directory in IntelliJ IDEA.
2. Set the project SDK and language level to JDK 25.
3. Open `src/main/java/yourhand/YourHand.java` and run `yourhand.YourHand.main()`.

### Terminal

From the project root, compile and run the application:

```powershell
javac -d _temp/classes src/main/java/yourhand/YourHand.java src/main/java/yourhand/tasks/*.java src/main/java/yourhand/exceptions/*.java src/main/java/yourhand/storage/*.java src/main/java/yourhand/ui/*.java src/main/java/yourhand/commands/*.java
java -cp _temp/classes yourhand.YourHand
```

## Build and run the fat JAR

The Shadow plugin packages the application and its runtime dependencies into one runnable JAR.

1. From the project root, create the JAR:

   ```powershell
   .\gradlew.bat shadowJar
   ```

2. Find the generated file at `build\libs\yourhand.jar`.
3. Run it with JDK 25:

   ```powershell
   java -jar build\libs\yourhand.jar
   ```

The JAR reads commands from the same terminal in which it is started. Run `bye` to exit normally.

## Test the console interface

The repeatable console test plan is in `test/ui-test-plan.md`. Run it from the project root:

```powershell
python .codex/skills/test-ui/scripts/run-ui-tests.py test/ui-test-plan.md
```

The session transcript and results are written to `test/ui-test-record.md`.

## User guide

See [the YourHand User Guide](docs/README.md) for commands, examples, and error-handling notes.
