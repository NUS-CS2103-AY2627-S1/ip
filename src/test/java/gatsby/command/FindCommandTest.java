package gatsby.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import gatsby.exception.EmptyPayloadException;
import gatsby.model.Deadline;
import gatsby.model.Event;
import gatsby.model.TaskList;
import gatsby.model.Todo;
import gatsby.testutil.RecordingUi;

/** Tests searching task descriptions and reporting the matching results. */
class FindCommandTest extends AbstractCommandTest {

    /** Verifies that matching descriptions use the original task-list numbers. */
    @Test
    void execute_matchingDescriptions_printsNumberedMatches() throws Exception {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("return report", LocalDateTime.of(2019, 12, 2, 18, 0)),
                new Event("book club", LocalDateTime.of(2019, 12, 3, 14, 0),
                        LocalDateTime.of(2019, 12, 3, 16, 0))));
        RecordingUi ui = recordingUi();

        new FindCommand("book").execute(tasks, ui);

        assertEquals(List.of(" Here are the matching tasks in your list (numbers refer to your full task list):",
                " 1. [T][ ] read book",
                " 3. [E][ ] book club (from: Dec 03 2019 14:00:00 to: Dec 03 2019 16:00:00)"),
                ui.messages());
    }

    /** Verifies case-insensitive, prefix, and internal substring matching. */
    @Test
    void execute_caseInsensitivePartialWords_printsMatchingTasks() throws Exception {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("notebook shopping")));
        RecordingUi ui = recordingUi();

        new FindCommand("OOK").execute(tasks, ui);

        assertEquals(List.of(" Here are the matching tasks in your list (numbers refer to your full task list):",
                " 1. [T][ ] read book",
                " 2. [T][ ] notebook shopping"), ui.messages());
    }

    /** Verifies that all query words are required and may appear in any order. */
    @Test
    void execute_multipleWordsInAnyOrder_requiresEveryWord() throws Exception {
        TaskList tasks = new TaskList(List.of(
                new Todo("book club"),
                new Todo("club meeting"),
                new Todo("book meeting")));
        RecordingUi ui = recordingUi();

        new FindCommand("meeting book").execute(tasks, ui);

        assertEquals(List.of(" Here are the matching tasks in your list (numbers refer to your full task list):",
                " 3. [T][ ] book meeting"), ui.messages());
    }

    /** Verifies that exact word matches are displayed before partial matches. */
    @Test
    void execute_exactMatchesBeforePartialMatches_preservesOrderWithinGroups() throws Exception {
        TaskList tasks = new TaskList(List.of(
                new Todo("notebook shopping"),
                new Todo("buy book"),
                new Todo("book club"),
                new Todo("book")));
        RecordingUi ui = recordingUi();

        new FindCommand("book").execute(tasks, ui);

        assertEquals(List.of(" Here are the matching tasks in your list (numbers refer to your full task list):",
                " 2. [T][ ] buy book",
                " 3. [T][ ] book club",
                " 4. [T][ ] book",
                " 1. [T][ ] notebook shopping"), ui.messages());
    }

    /** Verifies that dates and other display details are not searched as descriptions. */
    @Test
    void execute_keywordOnlyInDate_reportsNoMatches() throws Exception {
        TaskList tasks = new TaskList(List.of(
                new Deadline("return report", LocalDateTime.of(2019, 12, 2, 18, 0))));
        RecordingUi ui = recordingUi();

        new FindCommand("2019").execute(tasks, ui);

        assertEquals(List.of(" No matching tasks found :("), ui.messages());
    }

    /** Verifies that a blank search keyword is rejected without producing results. */
    @Test
    void execute_blankKeyword_throwsException() {
        RecordingUi ui = recordingUi();

        EmptyPayloadException exception = assertThrows(EmptyPayloadException.class, () ->
                new FindCommand("   ").execute(new TaskList(), ui));

        assertEquals("OOPS! How do I even find nothing??", exception.getMessage());
        assertEquals(List.of(), ui.messages());
    }
}
