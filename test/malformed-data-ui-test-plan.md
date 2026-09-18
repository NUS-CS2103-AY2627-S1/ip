# Malformed Data UI Test Plan

- Program command: `powershell -NoProfile -ExecutionPolicy Bypass -File test/malformed-data-and-run.ps1`
- Working directory: `.`
- Java requirement: Java 25
- Setup command:

  ```powershell
  $javaSources = Get-ChildItem -Path src/main/java -Recurse -Filter *.java `
      | Where-Object { $_.BaseName -notin @('DialogBox', 'Launcher', 'Main', 'MainWindow') } `
      | ForEach-Object { $_.FullName }
  javac -d out $javaSources
  ```
- Session log: `test/malformed-data-ui-test-session.log`

The fixture supplies a malformed saved task with an invalid completion status
beneath a disposable system-temp directory. Herta should report the problem and
stop before accepting commands, protecting repository task data from accidental
overwriting.

## Test case: Reject malformed saved data

- Aim: Verify that an invalid saved record produces a clear startup error instead of crashing or replacing the data.

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
     Failed to load tasks at line 1: Invalid saved task: completion status must be 0 or 1.
```
