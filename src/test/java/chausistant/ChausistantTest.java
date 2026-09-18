package chausistant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests user-facing responses produced by the chatbot command bridge. */
class ChausistantTest {
    private static final DateTimeFormatter SAVE_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HHmm", Locale.ROOT);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d uuuu HHmm", Locale.ENGLISH);

    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_todoThenList_returnsCommandResponses() {
        Chausistant chausistant = new Chausistant(temporaryDirectory.resolve("chausistant.txt"));

        String addResponse = chausistant.getResponse("todo read book");
        String listResponse = chausistant.getResponse("list");

        assertEquals("yay! i've added this little mission:\n[T][ ] read book\n"
                + "don't give up! you have 1 task ahead of you...", addResponse);
        assertEquals("here's your tiny adventure list:\n1.[T][ ] read book", listResponse);
    }

    @Test
    void getResponse_invalidCommand_returnsErrorMessage() {
        Chausistant chausistant = new Chausistant(temporaryDirectory.resolve("chausistant.txt"));

        String response = chausistant.getResponse("dance");

        assertEquals("oopsie! Unknown command: dance", response);
    }

    @Test
    void getChatResponse_invalidCommand_marksResponseAsError() {
        Chausistant chausistant = new Chausistant(temporaryDirectory.resolve("chausistant.txt"));

        ChatResponse response = chausistant.getChatResponse("dance");

        assertTrue(response.isError());
        assertEquals("oopsie! Unknown command: dance", response.text());
    }

    @Test
    void getChatResponse_missingCommand_returnsError() {
        Chausistant chausistant = new Chausistant(temporaryDirectory.resolve("chausistant.txt"));

        ChatResponse response = chausistant.getChatResponse(null);

        assertTrue(response.isError());
        assertEquals("oopsie! Please enter a command.", response.text());
    }

    @Test
    void getTaskSummaries_completedTask_containsCurrentTaskState() {
        Chausistant chausistant = new Chausistant(temporaryDirectory.resolve("chausistant.txt"));
        chausistant.getResponse("todo read book");
        chausistant.getResponse("mark 1");

        TaskSummary taskSummary = chausistant.getTaskSummaries().getFirst();

        assertTrue(taskSummary.isCompleted());
        assertEquals("[T][X] read book", taskSummary.text());
    }

    @Test
    void getChatResponse_successfulCommand_isNotAnError() {
        Chausistant chausistant = new Chausistant(temporaryDirectory.resolve("chausistant.txt"));

        ChatResponse response = chausistant.getChatResponse("list");

        assertFalse(response.isError());
    }

    @Test
    void getResponse_remindListsUpcomingDeadline() throws IOException {
        LocalDateTime deadline = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);
        Path saveFile = temporaryDirectory.resolve("chausistant.txt");
        Files.writeString(saveFile, "D | 0 | submit report | " + deadline.format(SAVE_DATE_TIME_FORMATTER),
                StandardCharsets.UTF_8);
        Chausistant chausistant = new Chausistant(saveFile);

        String response = chausistant.getResponse("remind");

        assertEquals("peek-a-boo! here are your upcoming deadlines:\n[D][ ] submit report (by: "
                + deadline.format(DISPLAY_DATE_TIME_FORMATTER) + ")", response);
    }

    @Test
    void getResponse_blankCommand_returnsEmptyResponse() {
        Chausistant chausistant = new Chausistant(temporaryDirectory.resolve("chausistant.txt"));

        assertEquals("", chausistant.getResponse("   "));
    }

    @Test
    void getResponse_savedTask_isAvailableToNewChatbotInstance() {
        Path saveFile = temporaryDirectory.resolve("chausistant.txt");
        Chausistant firstChausistant = new Chausistant(saveFile);

        firstChausistant.getResponse("todo read book");
        String response = new Chausistant(saveFile).getResponse("list");

        assertEquals("here's your tiny adventure list:" + System.lineSeparator()
                + "1.[T][ ] read book", response);
    }

    @Test
    void getChatResponse_loadWarning_marksResponseAsErrorWhileKeepingValidTasks() throws IOException {
        Path saveFile = temporaryDirectory.resolve("chausistant.txt");
        Files.write(saveFile, List.of("T | 0 | read book", "T | bad | skip this task"),
                StandardCharsets.UTF_8);

        ChatResponse response = new Chausistant(saveFile).getChatResponse("list");

        assertTrue(response.isError());
        assertTrue(response.text().contains("Ignoring malformed task on line 2"));
        assertTrue(response.text().endsWith("1.[T][ ] read book"));
    }

    @Test
    void getChatResponse_saveFailure_returnsErrorResponse() throws IOException {
        Path saveDirectory = temporaryDirectory.resolve("chausistant.txt");
        Files.createDirectory(saveDirectory);

        ChatResponse response = new Chausistant(saveDirectory).getChatResponse("todo read book");

        assertTrue(response.isError());
        assertTrue(response.text().contains("I could not load your tasks"));
        assertTrue(response.text().contains("I could not save your tasks"));
    }

    @Test
    void getTaskSummaries_loadsPersistedTasksBeforeReturningDashboardData() throws IOException {
        Path saveFile = temporaryDirectory.resolve("chausistant.txt");
        Files.writeString(saveFile, "T | 1 | read book", StandardCharsets.UTF_8);

        List<TaskSummary> taskSummaries = new Chausistant(saveFile).getTaskSummaries();

        assertEquals(List.of(new TaskSummary("[T][X] read book", true)), taskSummaries);
    }
}
