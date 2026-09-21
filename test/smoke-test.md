# Gronk release smoke test

Please test the supplied `topaz.jar` on Java 25 and report your operating system,
CPU architecture, `java -version` output, and whether each check below passes.
Use a new empty folder so your existing task data stays untouched.

1. Put only `topaz.jar` in the empty folder. Open a terminal in that folder and run:

   ```text
   java -version
   java -jar topaz.jar
   ```

2. Confirm the window title is **Gronk**, the greeting is readable, and resizing
   keeps the input box and Send button usable.
3. Enter these commands one at a time. Use both Enter and the Send button:

   ```text
   todo buy milk
   deadline submit project /by 2026-09-18
   event study /from 18/9/2026 1400 /to 18/9/2026 1500
   duration revise notes /for 45m
   list
   mark 2
   find submit
   ```

   The list has four tasks. The deadline is marked `[X]`, and the search displays
   it as task **2**, retaining its number from the full list.

4. Check graceful errors:

   ```text
   mark 99
   deadline impossible /by 2026-02-30
   todo BUY MILK
   list
   ```

   The first three commands show error cards. The task list still has four tasks
   and task 2 remains done.

5. Check changes and closing:

   ```text
   unmark 2
   delete 1
   list
   bye
   ```

   Three incomplete tasks remain, numbered 1–3. `bye` shows a farewell and disables
   the input controls. Close the window.

6. Run `java -jar topaz.jar` again from the **same folder**, then enter `list`.
   The same three tasks should load. Confirm `data/Topaz.txt` exists beneath the
   launch folder. Close the test window afterward.

## Report

- Operating system and version:
- CPU architecture (for example x64 or ARM64):
- Java version/distribution:
- Startup and resize: pass/fail
- Add/list/find/mark/unmark/delete: pass/fail
- Invalid commands and duplicate rejection: pass/fail
- Save and reload: pass/fail
- Any failed command, exact terminal error, or screenshot:

The current package includes x64 JavaFX natives for Windows, Linux, and macOS.
Please report ARM/native-library failures rather than marking an untested
platform as passed. Compatibility/deprecation warnings alone are not failures
if the GUI and all checks work, but include them in your report.
