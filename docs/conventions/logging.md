# Java logging conventions

Source: <https://se-education.org/guides/conventions/java/logging.html>

## Which library

Use the **`java.util.logging`** package. It ships with the JDK, so it adds no
dependency to `build.gradle`.

```java
import java.util.logging.Level;
import java.util.logging.Logger;

private static final Logger logger = Logger.getLogger(TaskList.class.getName());
```

## Logging levels

Pick the level from what the reader of the log is trying to do, not from how
alarming the message feels.

| Level | Use it for |
| --- | --- |
| `SEVERE` | A critical problem detected which may cause the termination of the application. |
| `WARNING` | Can continue, but with caution. |
| `INFO` | Information showing the noteworthy actions by the App. |
| `FINE` | Details that is not usually noteworthy but may be useful in debugging, e.g. print the actual list instead of just its size. |

The guide defines these four. `CONFIG`, `FINER` and `FINEST` exist in
`java.util.logging` but have no assigned meaning here, so do not use them.

## Applying this in Nori

Nori has no logging today; every diagnostic reaches the user through `Ui`. That
is a deliberate choice for a console app of this size, not an oversight. When
logging is added, these are the boundaries to hold:

- **Keep logging out of the `Ui` layer.** `Ui` writes to the user; a logger
  writes to the developer. A message that belongs in both goes through both, and
  is worded separately for each.
- **`SEVERE` is for a failure Nori cannot recover from**, such as a save that
  fails after a task list has already been mutated.
- **`WARNING` is for a recovered fault**, such as a corrupt line skipped while
  loading the save file.
- **`INFO` marks the noteworthy actions** — session start, session end, save
  file loaded and how many tasks it held.
- **`FINE` carries the detail** — the parsed command, the full task list.
- **Never log user task descriptions above `FINE`.** They are the content of
  the save file and belong in a debugging trace, not a routine log.
- **Do not log an exception and rethrow it.** Do one or the other, or the same
  fault appears twice with no indication it is one event.
