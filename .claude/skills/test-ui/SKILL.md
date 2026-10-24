---
name: test-ui
description: Compile and run this iP project's Echo chatbot (src/main/java/echo/**/*.java, entry point echo.Echo) through a scripted stdin regression suite covering every supported command, to verify a code change hasn't altered its user-visible behavior. Use this whenever asked to "test the UI", "run /test-ui", verify no regressions after a refactor, or check that a change to Echo/Task/Storage/etc. still behaves correctly end-to-end. This project has no JUnit/Gradle test suite yet, so this scripted CLI walkthrough is the only regression check available — always use it after any change that could affect runtime behavior (not just internal structure).
---

# Test UI

Behavior-level regression test for the Echo chatbot CLI (this repo, classes
under `src/main/java/echo/` in the `echo` package tree, no build tool yet —
plain `javac`/`java`). Confirms user-visible behavior is unchanged after a
code change (e.g. a refactor), since there's no automated test suite in
this project.

## Why this exists

Echo's classes get refactored across the course's levels (Ui/Storage/Parser/
TaskList extraction, date handling, etc.), but the CLI's actual
input/output behavior must stay identical unless a change is explicitly
meant to alter it. Without JUnit (that arrives in a later increment,
A-JUnit), the only way to catch a regression is to actually run the
compiled program and inspect its transcript.

## Steps

1. **Compile fresh.** Always compile from a clean temp output directory so
   stale `.class` files can't mask a compile error:
   ```bash
   rm -rf /tmp/echo-test-ui && mkdir -p /tmp/echo-test-ui
   javac -d /tmp/echo-test-ui $(find src/main/java -name "*.java")
   ```
   (Classes live under packages now, e.g. `src/main/java/echo/task/Task.java`
   — a flat `src/main/java/*.java` glob will silently miss them, so always
   use `find`.)
   If this fails, stop and report the compile error — don't proceed to a
   behavioral run.

2. **Run the regression script in an isolated working directory** (so
   `data/echo.txt` doesn't collide with the student's real save file),
   piping every currently-supported command through stdin in one session,
   then a fresh session to confirm persistence:
   ```bash
   rm -rf /tmp/echo-test-ui-run && mkdir -p /tmp/echo-test-ui-run && cd /tmp/echo-test-ui-run
   printf 'todo read book\ndeadline return book /by 2019-06-06\nevent trip /from 2019-08-01 /to 2019-08-03\nlist\nmark 1\nunmark 1\nmark 1\non 2019-08-02\non 2099-01-01\ndelete 2\nlist\nbadcommand\ntodo\ndeadline nodate\nevent nofrom\nbye\n' | java -cp /tmp/echo-test-ui echo.Echo
   ```
   Then a second, separate run with just `list` and `bye` to confirm the
   save file reloaded correctly:
   ```bash
   printf 'list\nbye\n' | java -cp /tmp/echo-test-ui echo.Echo
   ```
   Note the fully-qualified class name `echo.Echo` — since A-Packages, the
   entry point lives in the `echo` package, so `java -cp ... Echo` (no
   package prefix) will fail with a `ClassNotFoundException`.
   Adjust/extend this command list if the project has grown new commands
   since this was written — the goal is full coverage of every command
   `Echo` (or `Parser`) currently understands, not just this fixed list.
   Check the current dispatch logic first if unsure what's supported.

3. **Check the transcript for:**
   - No stack traces / uncaught exceptions.
   - Every valid command produces the expected acknowledgement (e.g. "Got
     it. I've added this task", correct task rendering with type tag and
     date formatting).
   - Every invalid command produces a friendly `OOPS!!!`-prefixed message,
     not a crash.
   - The second run's `list` shows the same tasks/mark-state as the end of
     the first run (persistence round-trip).

4. **Report pass/fail plainly**: if everything matches expected behavior,
   say so briefly (don't paste the full transcript unless something's
   wrong). If something regressed, quote the specific line(s) that differ
   from expected and explain what changed.

5. **Clean up** the temp directories when done
   (`rm -rf /tmp/echo-test-ui /tmp/echo-test-ui-run`).

## Notes

- This is a manual/scripted smoke test, not unit tests — it exercises the
  whole program through its real CLI interface, which is the right level
  for checking a refactor didn't change behavior (internal class structure
  changing is fine; output changing is not, unless that's the point of the
  change).
- If a change is *expected* to alter output (e.g. a new command, a changed
  error message), update step 2's expectations accordingly rather than
  treating the difference as a failure.
