package panda.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Tests exception messages that form part of Panda's user-visible error contract.
 */
class PandaExceptionTest {

    @Test
    void emptyDescription_taskTypes_useGrammaticalArticles() {
        assertEquals("OOPS!!! This panda needs a todo description before it can get to work.",
                new EmptyDescriptionException("todo").getMessage());
        assertEquals("OOPS!!! This panda needs an event description before it can get to work.",
                new EmptyDescriptionException("event").getMessage());
    }

    @Test
    void invalidDate_contexts_useSpecificFormatGuidance() {
        assertEquals("OOPS!!! This panda needs a valid date and time in yyyy-MM-dd HH:mm format.",
                new InvalidDateException().getMessage());
        assertEquals("OOPS!!! This panda needs a valid list date in yyyy-MM-dd format.",
                InvalidDateException.createForListDate().getMessage());
    }

    @Test
    void invalidEventPeriod_usesOrderingGuidance() {
        assertEquals("OOPS!!! This panda needs an event to start no later than it ends.",
                new InvalidEventPeriodException().getMessage());
    }

    @Test
    void invalidTaskNumber_contexts_distinguishSyntaxAndRange() {
        assertEquals("OOPS!!! This panda needs a valid task number after delete.",
                new InvalidTaskNumberException("delete").getMessage());
        assertEquals("OOPS!!! This panda cannot find task 7 in the bamboo stack.",
                new InvalidTaskNumberException(7).getMessage());
    }

    @Test
    void invalidUpdate_contexts_useSpecificMessages() {
        assertEquals("OOPS!!! This panda needs a valid update. "
                        + "Try /name <new name>, /by <date and time>, "
                        + "or /from <start> /to <end>.",
                new InvalidUpdateException().getMessage());
        assertEquals("OOPS!!! This panda cannot apply that timing update to this task type.",
                InvalidUpdateException.createForUnsupportedTiming().getMessage());
    }

    @Test
    void storageExceptions_underlyingCauses_arePreserved() {
        IOException cause = new IOException("disk unavailable");
        DataLoadingException loadingException =
                new DataLoadingException("tasks.txt", cause);
        DataSavingException savingException = new DataSavingException(cause);

        assertSame(cause, loadingException.getCause());
        assertSame(cause, savingException.getCause());
        assertEquals("OOPS!!! This panda cannot read its bamboo archive at tasks.txt.",
                loadingException.getMessage());
        assertEquals("OOPS!!! This panda could not save its bamboo archive.",
                savingException.getMessage());
    }

    @Test
    void simpleExceptions_messages_matchUserContract() {
        assertEquals("OOPS!!! This panda needs a search keyword after find.",
                new EmptySearchTermException().getMessage());
        assertEquals("OOPS!!! I'm bamboo-zled; I don't know what that means :-(",
                new InvalidCommandException().getMessage());
        assertEquals("OOPS!!! This panda needs more timing details. Try: usage.",
                new MissingDateTimeException("usage.").getMessage());
        assertEquals("Line 4 has invalid data.",
                new DataLoadingException(4, "invalid data.").getMessage());
    }
}
