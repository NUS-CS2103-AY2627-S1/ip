package megatron.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests user-facing messages and causes for Megatron exceptions.
 */
class ExceptionTest {
    @Test
    void simpleExceptions_haveExpectedMessages() {
        assertEquals("Please enter a command.", new EmptyCommandException().getMessage());
        assertEquals("A todo description cannot be empty.", new EmptyDescriptionException().getMessage());
        assertEquals("Please provide a valid task number.", new InvalidTaskNumberException().getMessage());
        assertEquals("That task number does not exist.", new TaskNotFoundException().getMessage());
        assertEquals("The task list is full. Remove a task before adding another.",
                new TaskListFullException().getMessage());
    }

    @Test
    void invalidTaskFormatException_includesUsage() {
        assertEquals("Use: todo <description>.",
                new InvalidTaskFormatException("todo <description>.").getMessage());
    }

    @Test
    void unknownCommandException_oneCommand_usesSingleCommandFormat() {
        assertEquals("I do not recognise that command. Try todo.",
                new UnknownCommandException(List.of("todo")).getMessage());
    }

    @Test
    void unknownCommandException_multipleCommands_usesReadableListFormat() {
        assertEquals("I do not recognise that command. Try todo, list, or bye.",
                new UnknownCommandException(List.of("todo", "list", "bye")).getMessage());
    }

    @Test
    void storageException_saveAndLoad_preserveCausesAndMessages() {
        Throwable saveCause = new IllegalStateException("save failed");
        Throwable loadCause = new IllegalStateException("load failed");

        StorageException saveException = new StorageException(saveCause);
        StorageException loadException = StorageException.createForLoad(loadCause);

        assertEquals("Could not save tasks. Check that the data file is writable.",
                saveException.getMessage());
        assertEquals("Could not load tasks. Check that the data file is readable.",
                loadException.getMessage());
        assertSame(saveCause, saveException.getCause());
        assertSame(loadCause, loadException.getCause());
    }

    @Test
    void megatronException_messageAndCause_areRetained() {
        Throwable cause = new IllegalArgumentException("bad input");

        MegatronException exception = new MegatronException("message", cause);

        assertEquals("message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
