package lloyd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests help output, validation, and preservation of application state. */
public class LloydHelpTest {
    private static final String EXPECTED_HELP =
            """
                    Here's what I can do:

                    Add tasks:
                      todo DESCRIPTION
                      deadline DESCRIPTION /by DD/MM/YYYY
                      event DESCRIPTION /from DD/MM/YYYY HHMM /to DD/MM/YYYY HHMM

                    View tasks:
                      list
                      find KEYWORD
                      check DD/MM/YYYY
                      reminder

                    Manage tasks:
                      mark TASK_NUMBER
                      unmark TASK_NUMBER
                      delete TASK_NUMBER

                    Other commands:
                      help [COMMAND]
                      bye

                    Dates use DD/MM/YYYY.
                    Times use the 24-hour HHMM format; for example, 1430 means 2:30 PM.""";

    @TempDir
    private Path temporaryDirectory;

    /** Verifies that help returns the complete command summary with a confident mood. */
    @Test
    public void help_withoutArguments_returnsCommandSummary() throws IOException {
        Lloyd lloyd = createLloyd("");

        LloydResponse response = lloyd.getReply("help");

        assertEquals(EXPECTED_HELP, response.text());
        assertEquals(LloydMood.CONFIDENT, response.mood());
        assertFalse(lloyd.isExitRequested());
    }

    /** Verifies that help can be read without changing existing task data. */
    @Test
    public void help_withExistingTasks_preservesStorage() throws IOException {
        String storageContents = "T | 0 | inspect foundations";
        Path storagePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(storagePath, storageContents, StandardCharsets.UTF_8);
        Lloyd lloyd = new Lloyd(storagePath);

        lloyd.getResponse("help");

        assertEquals(storageContents, Files.readString(storagePath, StandardCharsets.UTF_8));
        assertEquals("Tasks:\n1. [T][ ] inspect foundations", lloyd.getResponse("list"));
    }

    /** Verifies detailed guidance for adding an undated task. */
    @Test
    public void help_todoTopic_returnsTodoGuide() throws IOException {
        Lloyd lloyd = createLloyd("");

        LloydResponse response = lloyd.getReply("help todo");

        assertEquals(
                """
                        todo - Add a task without a date or time.

                        Format:
                          todo DESCRIPTION

                        Example:
                          todo read book""",
                response.text());
        assertEquals(LloydMood.CONFIDENT, response.mood());
    }

    /** Verifies detailed guidance for adding an event. */
    @Test
    public void help_eventTopic_returnsEventGuide() throws IOException {
        Lloyd lloyd = createLloyd("");

        LloydResponse response = lloyd.getReply("help event");

        assertEquals(
                """
                        event - Add an event with a start and end date and time.

                        Format:
                          event DESCRIPTION /from DD/MM/YYYY HHMM /to DD/MM/YYYY HHMM

                        Example:
                          event project meeting /from 06/08/2026 1400 /to 06/08/2026 1600""",
                response.text());
        assertEquals(LloydMood.CONFIDENT, response.mood());
    }

    /** Verifies that every displayed command has a specific help topic. */
    @Test
    public void help_everySupportedTopic_returnsConfidentGuide() throws IOException {
        Lloyd lloyd = createLloyd("");
        List<String> helpTopics = List.of(
                "todo", "deadline", "event", "list", "find", "check",
                "reminder", "mark", "unmark", "delete", "help", "bye");

        for (String helpTopic : helpTopics) {
            LloydResponse response = lloyd.getReply("help " + helpTopic);

            assertEquals(LloydMood.CONFIDENT, response.mood());
            assertTrue(response.text().startsWith(helpTopic + " - "));
        }
    }

    /** Verifies that an unknown help topic produces useful guidance. */
    @Test
    public void help_unknownTopic_returnsAvailableTopics() throws IOException {
        Lloyd lloyd = createLloyd("");

        LloydResponse response = lloyd.getReply("help tasks");

        assertEquals(
                "No help is available for: tasks\n"
                        + "Available help topics: todo, deadline, event, list, find, check, reminder,"
                        + " mark, unmark, delete, help, bye",
                response.text());
        assertEquals(LloydMood.ANNOYED, response.mood());
    }

    /** Creates a Lloyd instance backed by the supplied storage contents. */
    private Lloyd createLloyd(String storageContents) throws IOException {
        Path storagePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(storagePath, storageContents, StandardCharsets.UTF_8);
        return new Lloyd(storagePath);
    }
}
