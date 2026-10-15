package gnaix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import gnaix.task.Task;
import gnaix.task.Todo;

class ParsedCommandTest {

    @Test
    void of_listCommand_defaultsReturned() {
        ParsedCommand command = ParsedCommand.of(Command.LIST);

        assertFalse(command.hasError());
        assertEquals(Command.LIST, command.getCommand());
        assertNull(command.getTask());
        assertEquals(-1, command.getIndex());
        assertNull(command.getDate());
        assertNull(command.getKeyword());
        assertTrue(command.getTags().isEmpty());
    }

    @Test
    void forTask_todoCommand_taskReturned() {
        Task task = new Todo("buy milk");

        ParsedCommand command = ParsedCommand.forTask(Command.TODO, task);

        assertFalse(command.hasError());
        assertEquals(Command.TODO, command.getCommand());
        assertSame(task, command.getTask());
    }

    @Test
    void forIndex_markCommand_indexReturned() {
        ParsedCommand command = ParsedCommand.forIndex(Command.MARK, 2);

        assertFalse(command.hasError());
        assertEquals(Command.MARK, command.getCommand());
        assertEquals(2, command.getIndex());
    }

    @Test
    void forDate_validDate_dateCommandReturned() {
        LocalDate date = LocalDate.of(2026, 9, 1);

        ParsedCommand command = ParsedCommand.forDate(date);

        assertFalse(command.hasError());
        assertEquals(Command.DATE, command.getCommand());
        assertEquals(date, command.getDate());
    }

    @Test
    void forKeyword_keyword_keywordReturned() {
        ParsedCommand command = ParsedCommand.forKeyword("book");

        assertFalse(command.hasError());
        assertEquals(Command.FIND, command.getCommand());
        assertEquals("book", command.getKeyword());
    }

    @Test
    void forTags_mutableInput_tagsDefensivelyCopied() {
        Set<String> tags = new LinkedHashSet<>();
        tags.add("school");

        ParsedCommand command = ParsedCommand.forTags(tags);
        tags.add("urgent");

        assertEquals(Set.of("school"), command.getTags());
    }

    @Test
    void getTags_modifyReturnedSet_exceptionThrown() {
        ParsedCommand command = ParsedCommand.forTags(Set.of("school"));

        assertThrows(UnsupportedOperationException.class, () -> command.getTags().add("urgent"));
    }

    @Test
    void error_message_errorReturned() {
        ParsedCommand command = ParsedCommand.error("No.");

        assertTrue(command.hasError());
        assertEquals(Command.UNKNOWN, command.getCommand());
        assertEquals("No.", command.getError());
        assertTrue(command.getTags().isEmpty());
    }
}
