package eva;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class EvaTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void getResponse_addTodo_returnsConfirmation() {
        Eva eva = new Eva(tempDirectory.resolve("eva.txt").toString());

        String response = eva.getResponse("todo read book");

        assertEquals(
                "All set! I've added this task:\n"
                        + "  [T][ ] read book\n"
                        + "You have 1 task on your list.",
                response.replace(System.lineSeparator(), "\n"));
    }

    @Test
    void getResponse_unknownCommand_returnsError() {
        Eva eva = new Eva(tempDirectory.resolve("eva.txt").toString());

        assertEquals(
                "OOPS!!! I'm sorry, but I don't know what that means. :(",
                eva.getResponse("hello"));
    }

    @Test
    void getResponse_sort_ordersDeadlinesChronologically() {
        Eva eva = new Eva(tempDirectory.resolve("eva.txt").toString());
        eva.getResponse("deadline submit report /by 2026-09-30");
        eva.getResponse("deadline attend meeting /by 2026-09-15");

        String response = eva.getResponse("sort");

        assertEquals(
                "Here's your task list:\n"
                        + "1.[D][ ] attend meeting (by: Sep 15 2026)\n"
                        + "2.[D][ ] submit report (by: Sep 30 2026)",
                response.replace(System.lineSeparator(), "\n"));
    }

    @Test
    void getResponse_corruptDataDoesNotOverwriteFile() throws IOException {
        Path dataFile = tempDirectory.resolve("eva.txt");
        Files.writeString(dataFile, "not a task");
        Eva eva = new Eva(dataFile.toString());

        assertEquals(
                "OOPS!!! Saved tasks could not be loaded. "
                        + "Fix the data file before continuing. :(",
                eva.getResponse("todo read book"));
        assertEquals("See you soon. One task at a time!",
                eva.getResponse("bye"));
        assertEquals("not a task", Files.readString(dataFile));
    }

    @Test
    void getResponse_addDeadlineAndEvent_listsBothTasks() {
        Eva eva = new Eva(tempDirectory.resolve("eva.txt").toString());

        String deadline = eva.getResponse(
                "deadline submit report /by 2026-09-30");
        String event = eva.getResponse(
                "event team meeting /from 09:00 /to 10:00");
        String list = eva.getResponse("list");

        assertTrue(deadline.contains("[D][ ] submit report"));
        assertTrue(event.contains("[E][ ] team meeting"));
        assertTrue(list.contains("1.[D][ ] submit report"));
        assertTrue(list.contains("2.[E][ ] team meeting"));
    }

    @Test
    void getResponse_markUnmarkDelete_persistsChanges() {
        Path dataFile = tempDirectory.resolve("eva.txt");
        Eva eva = new Eva(dataFile.toString());
        eva.getResponse("todo read book");

        assertTrue(eva.getResponse("mark 1")
                .contains("[T][X] read book"));
        assertTrue(eva.getResponse("unmark 1")
                .contains("[T][ ] read book"));
        assertTrue(new Eva(dataFile.toString()).getResponse("list")
                .contains("[T][ ] read book"));

        assertTrue(eva.getResponse("delete 1")
                .contains("I've removed this task"));
        assertEquals("Here's your task list:",
                new Eva(dataFile.toString()).getResponse("list"));
    }

    @Test
    void getResponse_find_matchesDescriptionsWithoutCase() {
        Eva eva = new Eva(tempDirectory.resolve("eva.txt").toString());
        eva.getResponse("todo submit report");
        eva.getResponse("todo read book");

        String matches = eva.getResponse("find REPORT");

        assertTrue(matches.contains("submit report"));
        assertFalse(matches.contains("read book"));
    }

    @Test
    void getResponse_invalidNumberAndDate_returnsErrors() {
        Eva eva = new Eva(tempDirectory.resolve("eva.txt").toString());

        assertTrue(eva.getResponse("mark 1").startsWith("OOPS!!!"));
        assertTrue(eva.getResponse("deadline submit /by 2026-02-30")
                .startsWith("OOPS!!!"));
        assertEquals("Here's your task list:", eva.getResponse("list"));
    }

    @Test
    void getResponse_bye_savesTaskFile() {
        Path dataFile = tempDirectory.resolve("eva.txt");
        Eva eva = new Eva(dataFile.toString());

        assertEquals("See you soon. One task at a time!",
                eva.getResponse("bye"));
        assertTrue(Files.exists(dataFile));
    }

    @Test
    void getResponse_aiQuestion_returnsAiAnswer() {
        AiHelper aiHelper = new AiHelper("") {
            @Override
            public String getAiResponse(
                    String systemPrompt, String userPrompt) {
                assertTrue(systemPrompt.contains("deadline DESCRIPTION"));
                assertEquals("how do I add a deadline?", userPrompt);
                return "Use deadline DESCRIPTION /by YYYY-MM-DD.";
            }
        };
        Eva eva = new Eva(
                tempDirectory.resolve("eva.txt").toString(), aiHelper);

        assertEquals("Use deadline DESCRIPTION /by YYYY-MM-DD.",
                eva.getResponse("@ai how do I add a deadline?"));
    }

    @Test
    void getResponse_aiWithoutKey_returnsSetupInstructions() {
        Eva eva = new Eva(
                tempDirectory.resolve("eva.txt").toString(),
                new AiHelper(""));

        assertEquals(
                "OOPS!!! AI help needs an LLM_API_KEY. "
                        + "See the User Guide for setup instructions. :(",
                eva.getResponse("@ai what can Eva do?"));
    }
}
