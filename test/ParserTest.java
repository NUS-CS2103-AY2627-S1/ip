import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/** Dependency-free regression tests for command recognition and argument parsing. */
public class ParserTest {
    public static void main(String[] args) {
        testCommandRecognition();
        testTaskCreation();
        testInvalidTasks();
        testTaskNumbers();
        testDateQueries();
        System.out.println("All 5 parser test groups passed.");
    }

    /** Command boundaries, case, and whitespace keep their existing meaning. */
    private static void testCommandRecognition() {
        for (Parser.CommandType type : Parser.CommandType.values()) {
            check(Parser.parseCommandType(type.name().toLowerCase(Locale.ROOT)) == type,
                    "Failed to recognize command: " + type);
        }
        check(Parser.parseCommandType("todo   read book") == Parser.CommandType.TODO,
                "Arguments may have leading spaces after the command word.");
        for (String invalid : List.of("", "blah", "TODO read book", " todo read book", "todo\tread book",
                "todoLater read book", "deadlineX book", "eventually meeting", "onward", "marking 1",
                "unmarked 1", "deleted 1", "bye now", "hello ", "thanks extra", "help now", "list ")) {
            expectError(() -> Parser.parseCommandType(invalid), "Sir, I don't know what you are saying :-(");
        }
    }

    /** Parsing creates the correct task types and preserves descriptions and supported dates. */
    private static void testTaskCreation() {
        Task todo = Parser.parseTask("todo   read | café /by tomorrow  ");
        check(todo instanceof ToDo && todo.getDescription().equals("read | café /by tomorrow"),
                "Todo descriptions must be trimmed without interpreting their contents.");
        Task deadline = Parser.parseTask("deadline  return book  /by 2/12/2019 1800  ");
        check(deadline instanceof Deadline && deadline.getDescription().equals("return book")
                && ((Deadline) deadline).getBy().equals(TaskDateTime.parse("2019-12-02 18:00")),
                "Deadline fields must be parsed into a real date-time.");
        Task event = Parser.parseTask("event  meeting /from 2019-12-02 14:00 /to 2019-12-03 16:00");
        check(event instanceof Event && event.getDescription().equals("meeting")
                && ((Event) event).getFrom().equals(TaskDateTime.parse("2019-12-02 14:00"))
                && ((Event) event).getTo().equals(TaskDateTime.parse("2019-12-03 16:00")),
                "Both event endpoints must be preserved.");
        check(!todo.isDone() && !deadline.isDone() && !event.isDone(), "New tasks must start unmarked.");
    }

    /** Missing fields and invalid task dates report the same errors as the original command loop. */
    private static void testInvalidTasks() {
        for (String command : List.of("todo", "todo   ")) {
            expectError(() -> Parser.parseTask(command), "Sir, description of a todo cannot be empty.");
        }
        for (String command : List.of("deadline", "deadline book", "deadline /by 2019-12-02",
                "deadline book /by ", "deadline book/by 2019-12-02")) {
            expectError(() -> Parser.parseTask(command),
                    "Invalid deadline format. Use: deadline DESCRIPTION /by DEADLINE");
        }
        for (String command : List.of("event", "event meeting /from 2019-12-02",
                "event /from 2019-12-02 /to 2019-12-03", "event meeting /from /to 2019-12-03",
                "event meeting /from 2019-12-02 /to ", "event meeting /to 2019-12-03 /from 2019-12-02")) {
            expectError(() -> Parser.parseTask(command),
                    "Invalid event format. Use: event DESCRIPTION /from START /to END");
        }
        for (String command : List.of("deadline book /by 2019-02-29",
                "event meeting /from 2019-12-02 /to Sunday")) {
            expectError(() -> Parser.parseTask(command), "Invalid date/time. Use yyyy-MM-dd, yyyy-MM-dd HH:mm, "
                    + "or d/M/yyyy HHmm (e.g., 2/12/2019 1800).");
        }
        expectError(() -> Parser.parseTask("event meeting /from 2019-12-03 /to 2019-12-02"),
                "An event cannot end before it starts.");
        expectError(() -> Parser.parseTask("list"), "This command does not add a task.");
    }

    /** Number syntax is separate from task existence; the old -1 format-error behavior is retained. */
    private static void testTaskNumbers() {
        for (String word : List.of("mark", "unmark", "delete")) {
            for (String argument : List.of("", "   ", "abc", "1 2", "1.0", "2147483648", "-2147483649", "-1")) {
                expectError(() -> Parser.parseTaskNumber(word + " " + argument),
                        "Sir, Invalid " + word + " format. Use: " + word + " TASK_NUMBER");
            }
            expectError(() -> Parser.parseTaskNumber(word),
                    "Sir, Invalid " + word + " format. Use: " + word + " TASK_NUMBER");
            for (String argument : List.of("1", "+1", "01", "  1  ")) {
                check(Parser.parseTaskNumber(word + " " + argument) == 1,
                        "Valid integer spelling must remain accepted: " + argument);
            }
            for (int number : new int[]{Integer.MIN_VALUE, -2, 0, Integer.MAX_VALUE}) {
                check(Parser.parseTaskNumber(word + " " + number) == number,
                        "Task existence must be checked against TaskList, not by Parser.");
            }
        }
        expectError(() -> Parser.parseTaskNumber("list"), "This command does not select a task number.");
    }

    /** Date queries accept only an ISO calendar date and return a value without querying any task list. */
    private static void testDateQueries() {
        check(Parser.parseDate("on  2024-02-29  ").equals(LocalDate.of(2024, 2, 29)),
                "A valid leap day with surrounding argument spaces must parse.");
        for (String command : List.of("on", "on   ", "on Sunday", "on 2019-02-29", "on 2/12/2019",
                "on 2019-12-02 18:00", "on 2019-1-2")) {
            expectError(() -> Parser.parseDate(command),
                    "Invalid date. Use: on yyyy-MM-dd (e.g., on 2019-12-02).");
        }
    }

    /** Ensures validation failures retain the message expected by the console UI. */
    private static void expectError(Runnable operation, String message) {
        try {
            operation.run();
            throw new AssertionError("Expected validation error: " + message);
        } catch (IllegalArgumentException expected) {
            check(expected.getMessage().equals(message), "Unexpected error: " + expected.getMessage());
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
