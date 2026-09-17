# Unit Test Plan

This document records the unit test cases for Panda's core business logic components.

## Testing Strategy & Coverage Target
- **Target Coverage**: 50% method coverage.
- **Selection Criteria**: Prioritizes the top ~50% highest-value methods containing core, complex business logic (e.g. command parsing, date/time manipulation & validation, file storage serialization/deserialization, and task date overlap filtering).
- **Execution Command**: `./gradlew test`

---

## 1. `panda.parser.Parser`

### Method: `public static Command parse(String input) throws ParseException`

| Test Case | Test Input | Expected Output |
| :--- | :--- | :--- |
| Valid `bye` command | `"bye"` | Returns [`ByeCommand`](file:///home/zhu_j/ip/src/main/java/panda/command/ByeCommand.java) |
| `bye` with extraneous argument | `"bye now"` | Throws [`InvalidArgumentException`](file:///home/zhu_j/ip/src/main/java/panda/exception/parser/InvalidArgumentException.java) |
| Valid `list` command | `"list"` | Returns [`ListTasksCommand`](file:///home/zhu_j/ip/src/main/java/panda/command/ListTasksCommand.java) |
| `list` with extraneous argument | `"list all"` | Throws [`InvalidArgumentException`](file:///home/zhu_j/ip/src/main/java/panda/exception/parser/InvalidArgumentException.java) |
| Valid `todo` command | `"todo read book"` | Returns [`AddTodoCommand`](file:///home/zhu_j/ip/src/main/java/panda/command/AddTodoCommand.java) |
| `todo` with missing description | `"todo"`, `"todo   "` | Throws [`InvalidArgumentException`](file:///home/zhu_j/ip/src/main/java/panda/exception/parser/InvalidArgumentException.java) |
| Valid `deadline` command | `"deadline return book /by 15/10/2026 18:00"` | Returns [`AddDeadlineCommand`](file:///home/zhu_j/ip/src/main/java/panda/command/AddDeadlineCommand.java) |
| `deadline` missing `/by` delimiter | `"deadline return book"` | Throws [`InvalidArgumentException`](file:///home/zhu_j/ip/src/main/java/panda/exception/parser/InvalidArgumentException.java) |
| `deadline` missing description | `"deadline /by 15/10/2026"` | Throws [`InvalidArgumentException`](file:///home/zhu_j/ip/src/main/java/panda/exception/parser/InvalidArgumentException.java) |
| `deadline` invalid date format | `"deadline return book /by not-a-date"` | Throws `IllegalDateTimeException` |
| Valid `event` command | `"event meeting /from 15/10/2026 14:00 /to 15/10/2026 16:00"` | Returns [`AddEventCommand`](file:///home/zhu_j/ip/src/main/java/panda/command/AddEventCommand.java) |
| `event` missing `/to` delimiter | `"event meeting /from 15/10/2026 14:00"` | Throws [`InvalidArgumentException`](file:///home/zhu_j/ip/src/main/java/panda/exception/parser/InvalidArgumentException.java) |
| `event` missing description | `"event /from 15/10/2026 14:00 /to 15/10/2026 16:00"` | Throws [`InvalidArgumentException`](file:///home/zhu_j/ip/src/main/java/panda/exception/parser/InvalidArgumentException.java) |
| `event` invalid date format | `"event meeting /from invalid-date /to 15/10/2026 16:00"` | Throws `IllegalDateTimeException` |
| Valid `mark` command | `"mark 2"` | Returns [`MarkTaskCommand`](file:///home/zhu_j/ip/src/main/java/panda/command/MarkTaskCommand.java) |
| `mark` missing/non-numeric index | `"mark"`, `"mark abc"` | Throws [`InvalidArgumentException`](file:///home/zhu_j/ip/src/main/java/panda/exception/parser/InvalidArgumentException.java) |
| Valid `unmark` command | `"unmark 2"` | Returns [`UnmarkTaskCommand`](file:///home/zhu_j/ip/src/main/java/panda/command/UnmarkTaskCommand.java) |
| Valid `delete` command | `"delete 3"` | Returns [`DeleteTaskCommand`](file:///home/zhu_j/ip/src/main/java/panda/command/DeleteTaskCommand.java) |
| Valid `today` command | `"today"` | Returns [`TodayCommand`](file:///home/zhu_j/ip/src/main/java/panda/command/TodayCommand.java) |
| `today` with extraneous argument | `"today tomorrow"` | Throws [`InvalidArgumentException`](file:///home/zhu_j/ip/src/main/java/panda/exception/parser/InvalidArgumentException.java) |
| Valid `display /date` command | `"display /date 15/10/2026"` | Returns [`DisplayDateCommand`](file:///home/zhu_j/ip/src/main/java/panda/command/DisplayDateCommand.java) |
| `display` missing arguments | `"display"`, `"display /date"` | Throws [`InvalidArgumentException`](file:///home/zhu_j/ip/src/main/java/panda/exception/parser/InvalidArgumentException.java) |
| `display` invalid date format | `"display /date invalid-date"` | Throws `IllegalDateTimeException` |
| Valid `find` command | `"find book"` | Returns [`FindCommand`](file:///home/zhu_j/ip/src/main/java/panda/command/FindCommand.java) |
| `find` with missing keyword | `"find"`, `"find   "` | Throws [`InvalidArgumentException`](file:///home/zhu_j/ip/src/main/java/panda/exception/parser/InvalidArgumentException.java) |
| Empty / whitespace input | `""`, `"   "` | Throws [`NoCommandFoundException`](file:///home/zhu_j/ip/src/main/java/panda/exception/parser/NoCommandFoundException.java) |
| Unknown command | `"hello world"`, `"foobar"` | Throws [`NoCommandFoundException`](file:///home/zhu_j/ip/src/main/java/panda/exception/parser/NoCommandFoundException.java) |
| Input with surrounding whitespace | `"   list   "` | Returns [`ListTasksCommand`](file:///home/zhu_j/ip/src/main/java/panda/command/ListTasksCommand.java) |

---

## 2. `panda.util.datetime.DateTimeHelper`

### Method: `public static Temporal parseInput(String dateTime)`

| Test Case | Test Input | Expected Output |
| :--- | :--- | :--- |
| Valid date-time string | `"15/10/2026 18:00"` | `LocalDateTime.of(2026, 10, 15, 18, 0)` |
| Valid date-only string | `"15/10/2026"` | `LocalDate.of(2026, 10, 15)` |
| Invalid, null, or blank string | `"invalid-date"`, `null`, `"   "` | Throws `IllegalDateTimeException` |

### Method: `public static String formatForDisplay(Temporal temporal)`

| Test Case | Test Input | Expected Output |
| :--- | :--- | :--- |
| Format `LocalDateTime` | `LocalDateTime.of(2019, 12, 2, 18, 0)` | `"2 Dec 2019 18:00"` |
| Format `LocalDate` | `LocalDate.of(2019, 12, 2)` | `"2 Dec 2019"` |
| Null temporal | `null` | Throws `IllegalArgumentException` |

### Method: `public static String formatForStorage(Temporal temporal)`

| Test Case | Test Input | Expected Output |
| :--- | :--- | :--- |
| Save `LocalDateTime` | `LocalDateTime.of(2019, 12, 2, 18, 0)` | `"2019-12-02 18:00"` |
| Save `LocalDate` | `LocalDate.of(2019, 12, 2)` | `"2019-12-02"` |

### Method: `public static Temporal parseStorage(String dateTime)`

| Test Case | Test Input | Expected Output |
| :--- | :--- | :--- |
| Load saved date-time | `"2019-12-02 18:00"` | `LocalDateTime.of(2019, 12, 2, 18, 0)` |
| Load saved date-only | `"2019-12-02"` | `LocalDate.of(2019, 12, 2)` |
| Fallback to user input format | `"2/12/2019"` | `LocalDate.of(2019, 12, 2)` |
| Invalid, null, or blank string | `"invalid-date"`, `null`, `"   "` | Throws `IllegalDateTimeException` |

---

## 3. `panda.storage.TaskCodec`

### Round-Trip Serialization: `TaskCodec.encode(TaskCodec.decode(String line))`

| Test Case | Test Input (Original Line) | Expected Decoded Task | Expected Re-Encoded Output |
| :--- | :--- | :--- | :--- |
| Unmarked `Todo` round-trip | `"T \| 0 \| read book"` | Unmarked [`Todo`](file:///home/zhu_j/ip/src/main/java/panda/task/Todo.java) (`"read book"`) | `"T \| 0 \| read book"` |
| Marked `Todo` round-trip | `"T \| 1 \| read book"` | Marked [`Todo`](file:///home/zhu_j/ip/src/main/java/panda/task/Todo.java) (`"read book"`) | `"T \| 1 \| read book"` |
| Unmarked `Deadline` round-trip (with time) | `"D \| 0 \| return book \| 2019-06-06 18:00"` | Unmarked [`Deadline`](file:///home/zhu_j/ip/src/main/java/panda/task/Deadline.java) (`"return book"`, `2019-06-06 18:00`) | `"D \| 0 \| return book \| 2019-06-06 18:00"` |
| Marked `Deadline` round-trip (date-only) | `"D \| 1 \| return book \| 2019-06-06"` | Marked [`Deadline`](file:///home/zhu_j/ip/src/main/java/panda/task/Deadline.java) (`"return book"`, `2019-06-06`) | `"D \| 1 \| return book \| 2019-06-06"` |
| Marked `Event` round-trip (with time) | `"E \| 1 \| project meeting \| 2019-08-06 14:00 \| 2019-08-06 16:00"` | Marked [`Event`](file:///home/zhu_j/ip/src/main/java/panda/task/Event.java) (`"project meeting"`, `14:00` to `16:00`) | `"E \| 1 \| project meeting \| 2019-08-06 14:00 \| 2019-08-06 16:00"` |
| Unmarked `Event` round-trip (date-only) | `"E \| 0 \| orientation camp \| 2019-08-06 \| 2019-08-08"` | Unmarked [`Event`](file:///home/zhu_j/ip/src/main/java/panda/task/Event.java) (`"orientation camp"`, `06` to `08`) | `"E \| 0 \| orientation camp \| 2019-08-06 \| 2019-08-08"` |

### Error Handling: `public static Task decode(String line) throws FileCorruptedException`

| Test Case | Test Input | Expected Output |
| :--- | :--- | :--- |
| Corrupted task record line | `"corrupted task record"` | Throws [`FileCorruptedException`](file:///home/zhu_j/ip/src/main/java/panda/exception/storage/FileCorruptedException.java) |

---

## 4. `panda.task.Task` (and Subclasses)

### Method: `public boolean hasKeyword(String keyword)`

| Class Being Tested | Test Case | Test Input | Expected Output |
| :--- | :--- | :--- | :--- |
| [`Todo`](file:///home/zhu_j/ip/src/main/java/panda/task/Todo.java) | Matching lowercase keyword | Description: `"read a library book"`; keyword: `"library"` or `"book"` | `true` |
| [`Todo`](file:///home/zhu_j/ip/src/main/java/panda/task/Todo.java) | Matching uppercase keyword | Description: `"read a library book"`; keyword: `"LIBRARY"` | `true` |
| [`Todo`](file:///home/zhu_j/ip/src/main/java/panda/task/Todo.java) | Partial keyword | Description: `"read a library book"`; keyword: `"lib"` | `false` |
| [`Todo`](file:///home/zhu_j/ip/src/main/java/panda/task/Todo.java) | Non-matching keyword | Description: `"read a library book"`; keyword: `"write"` | `false` |

### Method: `public boolean checkDate(Temporal date)`

| Class Being Tested | Test Case | Test Input | Expected Output |
| :--- | :--- | :--- | :--- |
| [`Todo`](file:///home/zhu_j/ip/src/main/java/panda/task/Todo.java) | Check date on `Todo` | Any date / `null` | `false` |
| [`Deadline`](file:///home/zhu_j/ip/src/main/java/panda/task/Deadline.java) | Target matches due date | `LocalDate.of(2026, 10, 15)` (due date: `2026-10-15 18:00`) | `true` |
| [`Deadline`](file:///home/zhu_j/ip/src/main/java/panda/task/Deadline.java) | Target does not match due date | `LocalDate.of(2026, 10, 16)` | `false` |
| [`Deadline`](file:///home/zhu_j/ip/src/main/java/panda/task/Deadline.java) | Null target date | `null` | `false` |
| [`Event`](file:///home/zhu_j/ip/src/main/java/panda/task/Event.java) | Target on start date | `LocalDate.of(2026, 10, 15)` (from `15/10` to `17/10`) | `true` |
| [`Event`](file:///home/zhu_j/ip/src/main/java/panda/task/Event.java) | Target between start & end | `LocalDate.of(2026, 10, 16)` (from `15/10` to `17/10`) | `true` |
| [`Event`](file:///home/zhu_j/ip/src/main/java/panda/task/Event.java) | Target on end date | `LocalDate.of(2026, 10, 17)` (from `15/10` to `17/10`) | `true` |
| [`Event`](file:///home/zhu_j/ip/src/main/java/panda/task/Event.java) | Target outside date range | `LocalDate.of(2026, 10, 14)`, `LocalDate.of(2026, 10, 18)` | `false` |
| [`Event`](file:///home/zhu_j/ip/src/main/java/panda/task/Event.java) | Null target date | `null` | `false` |

### Event interval validation and date-only boundaries

| Test Case | Expected Output |
| :--- | :--- |
| Date-only event end | Includes the whole end date and excludes the following date |
| Date-time end at midnight | Does not occur on the date beginning at that midnight |
| End equals start | Throws `InvalidArgumentException` |
| End precedes start | Throws `InvalidArgumentException` |

---

## 5. `panda.task.EventScheduleIndex`

### Method: `List<Event> findOverlappingEvents(Event event)`

| Test Case | Expected Output |
| :--- | :--- |
| Event starts when indexed event ends | Empty list |
| Event overlaps multiple indexed events | Every conflict in chronological order |
| Event overlaps date-only event on its end date | Date-only event returned |
| Indexed event is removed before query | Empty list |
| Overlapping event is added directly to the index | Throws `IllegalArgumentException` |

---

## 6. Event-clash integration

| Test Case | Expected Output |
| :--- | :--- |
| Proposed event overlaps marked and unmarked events | Addition rejected and all conflicts listed chronologically |
| Event is rejected | Task-list size remains unchanged |
| Existing event is deleted | Its interval becomes available |
| Stored events overlap | `FileCorruptedException` |
| Stored events are adjacent | Both records load successfully |
| Stored event has an invalid range | `FileCorruptedException` |
