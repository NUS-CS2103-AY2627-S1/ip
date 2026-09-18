# Invalid Encoding UI Test Plan

- Program command: `powershell -NoProfile -ExecutionPolicy Bypass -File test/invalid-encoding-and-run.ps1`
- Working directory: `.`
- Java requirement: Java 25
- Setup command:

  ```powershell
  $javaSources = Get-ChildItem -Path src/main/java -Recurse -Filter *.java `
      | Where-Object { $_.BaseName -notin @('DialogBox', 'Launcher', 'Main', 'MainWindow') } `
      | ForEach-Object { $_.FullName }
  javac -d out $javaSources
  ```
- Session log: `test/invalid-encoding-ui-test-session.log`

The fixture writes a saved task containing invalid UTF-8 bytes beneath a
disposable system-temp directory. Herta should report the read error instead of
silently replacing the invalid data or touching repository task data.

## Test case: Reject invalid UTF-8 data

- Aim: Verify that unreadable task data is reported as a startup error.

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
     Failed to load tasks: an I/O failure occurred.
```
