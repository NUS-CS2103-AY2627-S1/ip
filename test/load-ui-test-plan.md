# Persistence UI Test Plan

- Program command: `powershell -NoProfile -ExecutionPolicy Bypass -File test/load-data-and-run.ps1`
- Working directory: `.`
- Java requirement: Java 25
- Setup command:

  ```powershell
  $javaSources = Get-ChildItem -Path src/main/java -Recurse -Filter *.java `
      | Where-Object { $_.BaseName -notin @('DialogBox', 'Launcher', 'Main', 'MainWindow') } `
      | ForEach-Object { $_.FullName }
  javac -d out $javaSources
  ```
- Session log: `test/load-ui-test-session.log`

The fixture writes valid saved tasks beneath a disposable system-temp directory
before starting Herta. This verifies that the chatbot reconstructs todo,
deadline, and event objects, including their completion statuses, when it starts
without touching repository task data.

## Test case: Load saved tasks at startup

- Aim: Verify that tasks stored on disk are loaded into the local task list.

### Inputs

```text
list
bye
```

### Expected output

```text
     ____________________________________________________________
      _   _           _
     | | | | ___ _ __| |_ __ _
     | |_| |/ _ \ '__| __/ _` |
     |  _  |  __/ |  | || (_| |
     |_| |_|\___|_|   \__\__,_|
     Oh, you're here. I'm Herta.
     Well? What do you want?
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     1. [T][X] loaded todo
     2. [D][ ] loaded deadline (by: Oct 15 2019)
     3. [E][ ] loaded event (from: Oct 15 2019 to: Oct 16 2019)
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```
