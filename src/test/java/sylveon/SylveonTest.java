package sylveon;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests Sylveon's command processing and sorted-task numbering. */
public class SylveonTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void sortDeadline_mixedTasks_displaysOnlyDeadlinesInDateOrder() {
        Sylveon sylveon = createSylveon();
        sylveon.getResponse("todo unrelated task");
        sylveon.getResponse("deadline later /by 2099-09-20");
        sylveon.getResponse("deadline earlier /by 2099-09-10");

        String response = sylveon.getResponse("sort deadline");

        assertEquals("Sorted deadlines by date (earliest first):\n"
                + "1. [D][ ] earlier (by: Sept 10 2099)\n"
                + "2. [D][ ] later (by: Sept 20 2099)", response);
    }

    @Test
    public void markAfterSortDeadline_marksTheDisplayedDeadline() {
        Sylveon sylveon = createSylveon();
        sylveon.getResponse("todo unrelated task");
        sylveon.getResponse("deadline later /by 2099-09-20");
        sylveon.getResponse("deadline earlier /by 2099-09-10");
        sylveon.getResponse("sort deadline");

        String response = sylveon.getResponse("mark 1");

        assertEquals("Great job! You finished a task 🎉\n"
                + "[D][X] earlier (by: Sept 10 2099)", response);
    }

    @Test
    public void eventWithNonIsoDate_returnsDateFormatError() {
        Sylveon sylveon = createSylveon();

        String response = sylveon.getResponse("event meeting /from tomorrow /to 2026-09-20");

        assertEquals("Error! Event dates must use the format yyyy-mm-dd :)", response);
    }

    @Test
    public void emptyCommand_returnsHelpfulError() {
        assertEquals("Please enter a command. Try typing \"list\" or \"help\" 🙂",
                createSylveon().getResponse("   "));
    }

    @Test
    public void eventEndingBeforeStart_returnsHelpfulError() {
        assertEquals("Oops! An event cannot end before it starts 😅",
                createSylveon().getResponse("event meeting /from 2099-09-20 /to 2099-09-10"));
    }

    @Test
    public void deadlineInPast_returnsHelpfulError() {
        assertEquals("Error! A deadline cannot be in the past :)",
                createSylveon().getResponse("deadline overdue /by 2000-01-01"));
    }

    @Test
    public void eventStartingInPast_returnsHelpfulError() {
        assertEquals("Error! An event cannot start in the past :)",
                createSylveon().getResponse("event meeting /from 2000-01-01 /to 2000-01-02"));
    }

    private Sylveon createSylveon() {
        return new Sylveon(temporaryDirectory.resolve("tasks.txt").toString());
    }
}
