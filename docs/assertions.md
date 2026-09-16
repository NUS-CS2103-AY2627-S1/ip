# Assertions

Java `assert condition : message;` documents an assumption about correct program execution.
When assertions are enabled, a false condition throws `AssertionError` with the diagnostic message.
These checks identify programming mistakes. User input validation and file I/O error handling still use
ordinary exceptions and remain active when assertions are disabled.

## Justification for each assertion

| Location | Assumption | Why it belongs here |
| --- | --- | --- |
| `Parser.parseEvent` | The selected `/to` marker starts at or after the end of `/from`. | The search deliberately begins after `/from`, and missing markers have already been rejected. The assertion documents the ordered substring bounds and catches a regression in marker selection, such as selecting `/to` from the description. Adjacent markers still reach the existing missing-date validation. |
| `TaskList.toIndex` | A validated task number converts to an index from zero through `size - 1`. | Invalid user numbers have already raised `AnacondaException`. A failure here identifies a bug in validation or the one-based to zero-based conversion before the index is used by `mark` or `delete`. |
| `TaskList.mark` | The task's completion state equals the requested state after the update. | The caller is about to save and report success. This postcondition catches a task subtype that fails to honor `markAsDone` or `markAsUndone`. |
| `Storage.formatTask` | After handling deadlines and events, the remaining task is a `ToDo`. | The parser and loader create only these three task types. If another type is introduced without updating storage, the fallback would silently save it as a todo and lose its type-specific data. The assertion exposes this development mistake before the data file is written. It does not validate file contents. |

Every assertion is free of side effects: parsing, task updates, and persistence occur outside assertion
expressions, so disabling assertions does not skip required work. An `AssertionError` is intentionally
not caught and presented as an ordinary user error.

## Enabling and testing

Use Java 25. Gradle explicitly enables assertions for `test` and `run`:

```powershell
.\gradlew.bat test
.\gradlew.bat run
```

For a direct Java launch, pass `-ea` before the class name or `-jar`, for example:

```powershell
java -ea -jar build/libs/Anaconda.jar
```

The JAR must first be built with `.\gradlew.bat shadowJar`. For an IDE launch, add `-ea` to its VM options.
Direct Java launches leave assertions disabled by default; `-da` explicitly disables them.

JUnit tests exercise normal parser, task-list, and storage behavior with assertions enabled. Parser tests
cover an earlier `/to` in the description and adjacent markers; task-number tests cover first/last indices,
empty lists, and invalid integer boundaries. Deliberately faulty task subclasses trigger the completion
assertion in both directions. Saving an unsupported task triggers the storage assertion and preserves the
existing file. The application suite also launches a child JVM without `-ea` to exercise normal startup,
task creation, and persistence with assertions disabled.
