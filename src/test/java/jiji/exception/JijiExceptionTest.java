package jiji.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for custom exception hierarchy in {@link jiji.exception}.
 */
public class JijiExceptionTest {

    @Test
    public void jijiException_storesMessageAndIsException() {
        JijiException ex = new JijiException("test error");
        assertEquals("test error", ex.getMessage());
        assertTrue(ex instanceof Exception);
    }

    @Test
    public void jijiInvalidIndexException_factories_returnExpectedMessages() {
        JijiInvalidIndexException invalidNum = JijiInvalidIndexException.forInvalidNumber();
        assertTrue(invalidNum.getMessage().contains("Please provide a valid task number"));

        JijiInvalidIndexException missingIndex = JijiInvalidIndexException.forMissingIndex("mark");
        assertTrue(missingIndex.getMessage().contains("Please specify a task number to mark"));
    }

    @Test
    public void jijiMissingArgumentException_factories_returnExpectedMessages() {
        JijiMissingArgumentException emptyTodo = JijiMissingArgumentException.forEmptyTodo();
        assertTrue(emptyTodo.getMessage().contains("The description of a todo cannot be empty"));

        JijiMissingArgumentException missingDeadline = JijiMissingArgumentException.forMissingDeadline();
        assertTrue(missingDeadline.getMessage().contains("A deadline task requires a description"));

        JijiMissingArgumentException missingEvent = JijiMissingArgumentException.forMissingEvent();
        assertTrue(missingEvent.getMessage().contains("An event task requires a description"));

        JijiMissingArgumentException emptyFind = JijiMissingArgumentException.forEmptyFind();
        assertTrue(emptyFind.getMessage().contains("Please specify a keyword to search for"));

        JijiMissingArgumentException emptySchedule = JijiMissingArgumentException.forEmptySchedule();
        assertTrue(emptySchedule.getMessage().contains("Please specify a date for the schedule"));
    }

    @Test
    public void jijiStorageException_storesMessage() {
        JijiStorageException storageEx = new JijiStorageException("Disk full");
        assertEquals("Disk full", storageEx.getMessage());
    }

    @Test
    public void jijiUnknownCommandException_hasDefaultMessage() {
        JijiUnknownCommandException unknown = new JijiUnknownCommandException();
        assertTrue(unknown.getMessage().contains("I'm sorry, but I don't know what that means"));
    }
}
