package sage.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import sage.command.AddCommand;
import sage.command.Command;
import sage.command.DeleteCommand;
import sage.command.ExitCommand;
import sage.command.FindCommand;
import sage.command.ListCommand;
import sage.command.MarkCommand;
import sage.command.UnmarkCommand;
import sage.exception.SageException;
import sage.storage.Storage;
import sage.task.Event;
import sage.task.TaskType;
import sage.ui.Ui;

class ParserTest {
    @TempDir
    Path tempDir;

    @Test
    void parse_invalidDateValues_rejectsEveryTimeField() {
        for (String value : List.of("12345", "25:00", "???", "0000-01-01", "25pm")) {
            for (String input : List.of("deadline report /by " + value,
                    "event meeting /from " + value + " /to Sunday",
                    "event meeting /from Sunday /to " + value)) {
                SageException error = assertThrows(SageException.class, () -> Parser.parse(input), input);
                assertTrue(error.getMessage().contains("That date or time is invalid."), input);
                assertInstanceOf(IllegalArgumentException.class, error.getCause());
            }
        }
    }

    @Test
    void parse_missingOrInvalidTaskNumbers_preservesErrorMessages() {
        for (String commandWord : List.of("mark", "unmark", "delete")) {
            SageException missing = assertThrows(SageException.class, () -> Parser.parse(commandWord + " "));
            assertEquals("The task number is missing. Try: " + commandWord + " <task number>",
                    missing.getMessage());
            for (String argument : List.of("abc", "2147483648")) {
                SageException invalid = assertThrows(SageException.class,
                        () -> Parser.parse(commandWord + " " + argument));
                assertEquals("The task number must be a valid integer. "
                        + "Try: mark <number>, unmark <number>, or delete <number>", invalid.getMessage());
            }
        }
    }

    @Test
    void parse_invalidDeadlineAndEventDelimiters_preservesFormatErrors() {
        for (String input : List.of("deadline", "deadline report")) {
            SageException error = assertThrows(SageException.class, () -> Parser.parse(input));
            assertEquals("The deadline format is invalid. Try: deadline <task> /by <time>", error.getMessage());
        }
        for (String input : List.of("event", "event meeting /from Monday", "event meeting /to Tuesday",
                "event meeting /to Tuesday /from Monday")) {
            SageException error = assertThrows(SageException.class, () -> Parser.parse(input));
            assertEquals("The event format is invalid. Try: event <task> /from <start> /to <end>",
                    error.getMessage());
        }
    }

    @Test
    void parse_unknownOrNullInput_rejectsCommand() {
        assertThrows(SageException.class, () -> Parser.parse(null));
        for (String input : List.of("   ", "unknown", "list extra", "bye extra")) {
            assertThrows(SageException.class, () -> Parser.parse(input), input);
        }
    }

    @Test
    void parse_exactCommandAndWhitespaceSyntax_preservesArguments() throws SageException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("prefix.txt").toString());
        Ui ui = new Ui();

        Parser.parse("  todo   read book  ").execute(tasks, ui, storage);
        Parser.parse("todo\twrite notes").execute(tasks, ui, storage);
        Parser.parse("mark\t1").execute(tasks, ui, storage);
        for (String malformed : List.of("todowrite notes", "mark1", "finding book", "delete2", "eventual")) {
            assertThrows(SageException.class, () -> Parser.parse(malformed), malformed);
        }

        assertEquals("read book", tasks.get(0).getDescription());
        assertEquals("write notes", tasks.get(1).getDescription());
        assertEquals("X", tasks.get(0).getStatusIcon());
    }

    @Test
    void parse_eventWithFreeTextTimes_preservesAcceptedValues() throws SageException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("events.txt").toString());
        Ui ui = new Ui();

        Parser.parse("event  meeting  /from Monday /to Tuesday").execute(tasks, ui, storage);

        Event meeting = assertInstanceOf(Event.class, tasks.get(0));
        assertEquals("meeting", meeting.getDescription());
        assertEquals("Monday", meeting.getFromText());
        assertEquals("Tuesday", meeting.getToText());
        assertThrows(SageException.class,
                () -> Parser.parse("event reminder /from 2025-01-02 /to 2025-01-02"));
    }

    @Test
    void parse_validCommandsReturnExpectedCommandTypes() throws SageException {
        Command bye = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, bye);

        Command list = Parser.parse("list");
        assertInstanceOf(ListCommand.class, list);

        Command find = Parser.parse("find book");
        assertInstanceOf(FindCommand.class, find);

        Command todo = Parser.parse("todo read book");
        assertInstanceOf(AddCommand.class, todo);

        Command deadline = Parser.parse("deadline write report /by 2025-01-02");
        assertInstanceOf(AddCommand.class, deadline);

        Command event = Parser.parse("event meeting /from 2025-01-02 /to 2025-01-03");
        assertInstanceOf(AddCommand.class, event);

        Command mark = Parser.parse("mark 2");
        assertInstanceOf(MarkCommand.class, mark);

        Command unmark = Parser.parse("unmark 2");
        assertInstanceOf(UnmarkCommand.class, unmark);

        Command delete = Parser.parse("delete 2");
        assertInstanceOf(DeleteCommand.class, delete);
    }

    @Test
    void parse_invalidCommandsThrowSageException() {
        assertThrows(SageException.class, () -> Parser.parse(""));
        assertThrows(SageException.class, () -> Parser.parse("find "));
        assertThrows(SageException.class, () -> Parser.parse("todo "));
        assertThrows(SageException.class, () -> Parser.parse("deadline finish /by "));
        assertThrows(SageException.class, () -> Parser.parse("event meeting /from 2025-01-03 /to 2025-01-02"));
        assertThrows(SageException.class, () -> Parser.parse("mark"));
    }

    @Test
    void parse_malformedOrRepeatedFields_rejectsInput() {
        for (String input : List.of("deadline /by Sunday", "deadline task /by", "deadline task /by Sunday /by Monday",
                "deadline task /from Sunday /by Monday", "event /from Monday /to Tuesday",
                "event meeting /from /to Tuesday", "event meeting /from Monday /to",
                "event meeting /from Monday /from Tuesday /to Wednesday",
                "event meeting /from Monday /to Tuesday /to Wednesday", "todo first\nsecond",
                "note first\rsecond", "todo bad\u0000text")) {
            assertThrows(SageException.class, () -> Parser.parse(input), input);
        }
    }

    @Test
    void parse_whitespaceAroundTimeFields_acceptsTabsAndRepeatedSpaces() throws SageException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("whitespace.txt").toString());
        Parser.parse(" deadline\t report\t /by\t 2026-09-18  ").execute(tasks, new Ui(), storage);
        Parser.parse("event meeting  /from\tMon 2pm\t/to   4pm").execute(tasks, new Ui(), storage);

        assertEquals("report", tasks.get(0).getDescription());
        Event event = assertInstanceOf(Event.class, tasks.get(1));
        assertEquals("Mon 2pm", event.getFromText());
        assertEquals("4pm", event.getToText());
    }

    @Test
    void parse_whitespaceWithinDateTimes_preservesNumericTimes() throws SageException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("date-whitespace.txt").toString());
        Parser.parse("event meeting /from 2026-09-18   1400 /to 18/9/2026\t15:00")
                .execute(tasks, new Ui(), storage);

        Event event = assertInstanceOf(Event.class, tasks.get(0));
        assertEquals(14, event.getFrom().getHour());
        assertEquals(15, event.getTo().getHour());
    }

    @Test
    void parse_mixedNumericAndNaturalLanguageTimes_retainsUncomparableEnd() throws SageException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("mixed-times.txt").toString());
        Parser.parse("event meeting /from 2026-09-18 1400 /to tomorrow")
                .execute(tasks, new Ui(), storage);

        Event event = assertInstanceOf(Event.class, tasks.get(0));
        assertEquals(14, event.getFrom().getHour());
        assertEquals("tomorrow", event.getToText());
    }

    @Test
    void parse_impossibleDateOrTime_rejectsWithoutNormalization() {
        for (String time : List.of("2026-02-30", "29/2/2025", "2026-13-01", "2026-09-18 24:00",
                "18/9/2026 2460", "2026-09-18T25:00:00", "2026-9-18", "2026.09.18")) {
            assertThrows(SageException.class, () -> Parser.parse("deadline report /by " + time), time);
            assertThrows(SageException.class,
                    () -> Parser.parse("event report /from " + time + " /to Sunday"), time);
        }
    }

    @Test
    void parse_executesCommandOnTaskList() throws SageException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());
        Ui ui = new Ui();

        Parser.parse("todo read book").execute(tasks, ui, storage);
        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
        assertEquals(TaskType.TODO, tasks.get(0).getType());

        Parser.parse("deadline submit /by 2025-01-02").execute(tasks, ui, storage);
        assertEquals(2, tasks.size());
        assertTrue(tasks.get(1).toString().contains("submit"));
    }
}
