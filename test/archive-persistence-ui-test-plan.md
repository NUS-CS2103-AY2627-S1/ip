# Archive Persistence UI Test Plan

- Program command: `powershell -NoProfile -ExecutionPolicy Bypass -File test/archive-persistence-and-run.ps1`
- Working directory: `.`
- Java requirement: Java 25
- Setup command: `$javaSources = Get-ChildItem -Path src/main/java -Recurse -Filter *.java | Where-Object { $_.BaseName -notin @('DialogBox', 'Launcher', 'Main', 'MainWindow') } | ForEach-Object { $_.FullName }; javac -d out $javaSources`
- Session log: `test/archive-persistence-ui-test-session.log`

The program command launches one Herta process to archive a completed task,
then launches a second fresh process using the same temporary data directory.
The test inputs are sent to the second process.

## Test case: Archive persists across a restart

- Aim: Verify that an archive written by one process is loaded by the next process.

### Inputs

```text
archived
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
     There. I've added it:
       [T][ ] persisted task
     That makes 1 active task. Try to keep up.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Done. It's marked complete:
       [T][X] persisted task
     ____________________________________________________________
Your command?      ____________________________________________________________
     There. I've archived 1 completed task:
       [T][X] persisted task
     The active list is down to 0 tasks. Much tidier.
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
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
     The archive, as requested:
     1. [T][X] persisted task
     ____________________________________________________________
Your command?      ____________________________________________________________
     Let's see what you've managed to pile up:
     ____________________________________________________________
Your command?      ____________________________________________________________
     Leaving already? Goodbye.
     ____________________________________________________________
```
