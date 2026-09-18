package phin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Checks command boundaries, argument validation, and task construction.
 */
class ParserTest {
    /**
     * Checks phrase trimming while retaining case and internal spaces.
     */
    @Test
    void parseFindKeyword_validPhrase_preservesSearchText() throws PhinException {
        assertEquals("find", Parser.parseCommandWord("find"));
        assertEquals("find", Parser.parseCommandWord("find book"));
        assertEquals("Read  book", Parser.parseFindKeyword("find   Read  book  "));
    }

    /**
     * Checks blank phrases and command boundaries without accepting other commands.
     */
    @Test
    void parseFindKeyword_missingOrWrongCommand_rejected() {
        for (String command : new String[] {"find", "find   ", "find \t"}) {
            PhinException exception = assertThrows(PhinException.class,
                    () -> Parser.parseFindKeyword(command));
            assertEquals("Tell me what to find. Try: find KEYWORD", exception.getMessage());
        }
        for (String command : new String[] {"findbook", "Find book", "list", "todo book"}) {
            assertThrows(PhinException.class, () -> Parser.parseFindKeyword(command));
        }
    }

    @Test
    void parseCommandWord_supportedCommands_recognized() throws PhinException {
        for (String word : new String[] {
            "help", "list", "bye", "todo", "deadline", "event", "mark", "unmark", "delete", "update"
        }) {
            assertEquals(word, Parser.parseCommandWord(word));
        }
        for (String word : new String[] {"todo", "deadline", "event", "mark", "unmark", "delete", "update"}) {
            assertEquals(word, Parser.parseCommandWord(word + " argument"));
            assertEquals(word, Parser.parseCommandWord("  " + word + "\targument  "));
        }
    }

    @Test
    void parseCommandWord_wrongBoundaries_rejected() {
        for (String command : new String[] {"", " ", "TODO book", "todoist book",
                "list extra", "bye extra", "marking 1", "unknown"}) {
            assertThrows(PhinException.class, () -> Parser.parseCommandWord(command), command);
        }
    }

    @Test
    void parseTask_validTodo_trimsDescription() throws PhinException {
        Todo task = assertInstanceOf(Todo.class, Parser.parseTask("todo   read a book  "));
        assertEquals("read a book", task.description);
        assertEquals("[T][ ] read a book", task.toString());
    }

    @Test
    void parseTask_validDeadline_preservesDescriptionAndDate() throws PhinException {
        Deadline task = assertInstanceOf(Deadline.class,
                Parser.parseTask("deadline  return book   /by   2024-02-29  "));
        assertEquals("return book", task.description);
        assertEquals(LocalDate.of(2024, 2, 29), task.by);
    }

    @Test
    void parseTask_validEvent_preservesBothEndpoints() throws PhinException {
        Event task = assertInstanceOf(Event.class,
                Parser.parseTask("event  holiday  /from  2024-12-31  /to  2025-01-01 "));
        assertEquals("holiday", task.description);
        assertEquals(LocalDate.of(2024, 12, 31), task.from);
        assertEquals(LocalDate.of(2025, 1, 1), task.to);
        Event sameDay = assertInstanceOf(Event.class,
                Parser.parseTask("event meeting /from 2024-03-01 /to 2024-03-01"));
        assertEquals(sameDay.from, sameDay.to);
    }

    @Test
    void parseTask_missingFieldsOrWrongCommand_rejected() {
        for (String command : new String[] {"todo", "todo   ", "deadline book", "deadline /by 2024-03-01",
                "deadline book /by", "event meeting", "event /from 2024-03-01 /to 2024-03-02",
                "event meeting /from /to 2024-03-02", "event meeting /from 2024-03-01 /to",
                "event meeting /from 2024-03-01", "list", "bye", "mark 1"}) {
            assertThrows(PhinException.class, () -> Parser.parseTask(command), command);
        }
    }

    @Test
    void parseTask_invalidDatesOrReversedRange_rejected() {
        for (String command : new String[] {"deadline book /by Sunday", "deadline book /by 2023-02-29",
                "event meeting /from 2024-02-30 /to 2024-03-01",
                "event meeting /from 2024-03-01 /to 2024-13-01",
                "event meeting /from 2024-03-02 /to 2024-03-01"}) {
            assertThrows(IllegalArgumentException.class, () -> Parser.parseTask(command), command);
        }
    }

    @Test
    void parseTaskIndex_validBoundaries_convertsToZeroBased() throws PhinException {
        for (String word : new String[] {"mark", "unmark", "delete"}) {
            assertEquals(0, Parser.parseTaskIndex(word + " 1", word, 3));
            assertEquals(2, Parser.parseTaskIndex(word + "   3  ", word, 3));
        }
    }

    @Test
    void parseTaskIndex_missingOrNonnumericArguments_explainsError() {
        for (String word : new String[] {"mark", "unmark", "delete"}) {
            PhinException missing = assertThrows(PhinException.class,
                    () -> Parser.parseTaskIndex(word + "  ", word, 3));
            assertEquals("Tell me which task to " + word + ". Try: " + word + " NUMBER", missing.getMessage());
            for (String argument : new String[] {"two", "1.5", "1 2", "2147483648", "-2147483649"}) {
                PhinException invalid = assertThrows(PhinException.class,
                        () -> Parser.parseTaskIndex(word + " " + argument, word, 3), argument);
                assertEquals("Task numbers are, inconveniently, numbers. Try: " + word + " NUMBER",
                        invalid.getMessage());
            }
        }
    }

    @Test
    void parseTaskIndex_outsideListOrEmptyList_rejected() {
        for (int number : new int[] {-1, 0, 4, Integer.MAX_VALUE}) {
            assertThrows(PhinException.class, () -> Parser.parseTaskIndex("delete " + number, "delete", 3));
        }
        assertThrows(PhinException.class, () -> Parser.parseTaskIndex("mark 1", "mark", 0));
    }

    @Test
    void expandShortcut_allShortcuts_expandsOnlyCommandWord() {
        assertEquals("help", Parser.expandShortcut("h"));
        assertEquals("list", Parser.expandShortcut(" l "));
        assertEquals("todo  read book", Parser.expandShortcut("t  read book"));
        assertEquals("deadline submit /by 2024-03-01", Parser.expandShortcut("dl submit /by 2024-03-01"));
        assertEquals("event meeting /from 2024-03-01 /to 2024-03-02",
                Parser.expandShortcut("e meeting /from 2024-03-01 /to 2024-03-02"));
        assertEquals("mark 1", Parser.expandShortcut("m 1"));
        assertEquals("unmark 1", Parser.expandShortcut("um 1"));
        assertEquals("delete 1", Parser.expandShortcut("del 1"));
        assertEquals("update 1 /description revised", Parser.expandShortcut("u 1 /description revised"));
        assertEquals("find book", Parser.expandShortcut("f book"));
        assertEquals("bye", Parser.expandShortcut("q"));
        assertEquals("todoist unchanged", Parser.expandShortcut("todoist unchanged"));
    }

    @Test
    void parseTask_surroundingWhitespaceAndTabs_accepted() throws PhinException {
        Todo todo = assertInstanceOf(Todo.class, Parser.parseTask("\t todo\twrite tests  \t"));
        assertEquals("write tests", todo.description);
        assertEquals(0, Parser.parseTaskIndex("  mark\t1  ", "mark", 1));
        assertEquals("book", Parser.parseFindKeyword("\tfind\tbook\t"));
    }

    @Test
    void parseUpdatedTask_validFields_preservesTypeStatusAndUnspecifiedFields() throws PhinException {
        Event original = new Event("meeting", "2024-03-01", "2024-03-02");
        original.markAsDone();

        assertEquals(0, Parser.parseUpdateIndex("update 1 /to 2024-03-03", 1));
        Event updated = assertInstanceOf(Event.class,
                Parser.parseUpdatedTask("update 1 /description project meeting /to 2024-03-03", original));
        assertEquals("project meeting", updated.description);
        assertEquals(LocalDate.of(2024, 3, 1), updated.from);
        assertEquals(LocalDate.of(2024, 3, 3), updated.to);
        assertEquals("X", updated.getStatusIcon());
    }

    @Test
    void parseUpdatedTask_invalidFields_rejectedWithoutChangingOriginal() {
        Deadline original = new Deadline("submit report", "2024-03-02");
        for (String command : new String[] {"update 1", "update 1 /description", "update 1 /from 2024-03-01",
                "update 1 /by 2024-03-03 /by 2024-03-04", "update 1 /when 2024-03-03"}) {
            assertThrows(PhinException.class, () -> Parser.parseUpdatedTask(command, original), command);
        }
        assertEquals("submit report", original.description);
        assertEquals(LocalDate.of(2024, 3, 2), original.by);
    }

    @Test
    void parseUpdatedTask_invalidDateOrRange_rejectedWithoutChangingOriginal() {
        Event original = new Event("meeting", "2024-03-01", "2024-03-03");
        for (String command : new String[] {"update 1 /to 2024-02-30", "update 1 /from 2024-03-04"}) {
            assertThrows(IllegalArgumentException.class, () -> Parser.parseUpdatedTask(command, original), command);
        }
        assertEquals(LocalDate.of(2024, 3, 1), original.from);
        assertEquals(LocalDate.of(2024, 3, 3), original.to);
    }
}
