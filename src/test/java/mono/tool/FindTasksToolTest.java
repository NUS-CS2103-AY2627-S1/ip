package mono.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import mono.MonoBot;
import mono.exception.MonoException;
import mono.exception.WrongFormatException;

/**
 * Tests validation and delegation of task searches.
 */
public class FindTasksToolTest {
    @Test
    public void invoke_keyword_findsTasksAndContinues() throws MonoException {
        RecordingMonoBot bot = new RecordingMonoBot();

        ToolSignal signal = new FindTasksTool().invoke("  read book  ", bot);

        assertEquals(ToolSignal.CONTINUE, signal);
        assertEquals("read book", bot.searchedKeyword);
    }

    @Test
    public void invoke_emptyArguments_throwsWrongFormatException() {
        RecordingMonoBot bot = new RecordingMonoBot();

        WrongFormatException exception = assertThrows(WrongFormatException.class, () ->
                new FindTasksTool().invoke(" \t ", bot));

        assertEquals("Find format: find <keyword>", exception.getMessage());
        assertNull(bot.searchedKeyword);
    }

    /** Records searches without printing to the conversation UI. */
    private static class RecordingMonoBot extends MonoBot {
        private String searchedKeyword;

        @Override
        public void findTasks(String keyword) {
            this.searchedKeyword = keyword;
        }
    }
}
