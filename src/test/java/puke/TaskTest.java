package puke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void todoIsOpenByDefault() {
        assertEquals("[T][ ] buy milk", new Task("buy milk").toString());
    }

    @Test
    void markedTaskShowsDoneStatus() {
        Task task = new Task("buy milk");
        task.mark();
        assertEquals("[T][X] buy milk", task.toString());
    }

    @Test
    void keywordSearchMatchesDescriptionCaseInsensitively() {
        assertTrue(new Task("Read Book").matchesKeyword("book"));
    }

    @Test
    void deadlineFormatsDate() {
        assertEquals("[D][ ] submit report (by: Aug 24 2026)",
                new Deadline("submit report", "2026-08-24").toString());
    }

    @Test
    void taskFactoryAcceptsIndividualStorageFields() {
        assertEquals("[T][ ] buy milk",
                TaskFactory.fromStorageFields("T", "0", "buy milk").toString());
    }

    @Test
    void responseProcessesTaskCommands() throws IOException {
        Path file = Files.createTempFile("puke", ".txt");
        try {
            Puke puke = new Puke(file.toString());
            assertEquals("> 1. [T][ ] buy milk", puke.getResponse("todo buy milk"));
            assertTrue(puke.getResponse("list").contains("[T][ ] buy milk"));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void duplicateTasksAreRejected() throws IOException {
        Path file = Files.createTempFile("puke", ".txt");
        try {
            Puke puke = new Puke(file.toString());
            assertEquals("> 1. [T][ ] buy milk", puke.getResponse("todo buy milk"));
            assertEquals("> puke already has this task: [T][ ] buy milk",
                    puke.getResponse("todo buy milk"));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void responseProcessesAllTaskCommands() throws IOException {
        Path file = Files.createTempFile("puke", ".txt");
        try {
            Puke puke = new Puke(file.toString());
            assertTrue(puke.getResponse("todo read book").contains("[T][ ] read book"));
            assertTrue(puke.getResponse("deadline return book /by 2026-08-24")
                    .contains("[D][ ] return book"));
            assertTrue(puke.getResponse("event project meeting /from 2pm /to 4pm")
                    .contains("[E][ ] project meeting"));
            assertTrue(puke.getResponse("mark 1").contains("[T][X] read book"));
            assertTrue(puke.getResponse("unmark 1").contains("[T][ ] read book"));
            assertTrue(puke.getResponse("find return").contains("return book"));
            assertTrue(puke.getResponse("delete 2").contains("return book"));
            assertFalse(puke.getResponse("list").contains("return book"));
            assertEquals("> Puke is logging off. Keep your tasks tidy!", puke.getResponse("bye"));
            assertTrue(puke.isExitRequested());
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void invalidCommandsReceiveAnErrorResponse() throws IOException {
        Path file = Files.createTempFile("puke", ".txt");
        try {
            Puke puke = new Puke(file.toString());
            String error = "> Puke needs a valid command and its required arguments.";
            assertEquals(error, puke.getResponse("todo"));
            assertEquals(error, puke.getResponse("deadline report"));
            assertEquals(error, puke.getResponse("event meeting /from 2pm"));
            assertEquals(error, puke.getResponse("list extra"));
            assertEquals(error, puke.getResponse("mark 1"));
            assertEquals(error, puke.getResponse("find"));
            assertEquals("> Puke is puzzled. Try a supported command.", puke.getResponse("unknown"));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void nullInputIsIgnored() throws IOException {
        Path file = Files.createTempFile("puke", ".txt");
        try {
            assertEquals("", new Puke(file.toString()).getResponse(null));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void saveFailureIsReportedToTheUser() throws IOException {
        Path directory = Files.createTempDirectory("puke");
        try {
            Puke puke = new Puke(directory.toString());
            assertEquals("> Puke could not save your tasks.",
                    puke.getResponse("todo read book"));
        } finally {
            Files.deleteIfExists(directory);
        }
    }
}
