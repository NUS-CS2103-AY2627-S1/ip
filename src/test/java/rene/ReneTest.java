package rene;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ReneTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_addThenList_returnsPersistedTask() {
        Rene rene = new Rene(temporaryDirectory.resolve("rene.txt"));

        String addResponse = rene.getResponse("todo read book");
        String listResponse = rene.getResponse("list");

        assertTrue(addResponse.contains("[T][ ] read book"));
        assertEquals(" Here is your current action-item backlog:\n 1.[T][ ] read book", listResponse);
    }

    @Test
    public void getResponse_invalidCommand_returnsFriendlyError() {
        Rene rene = new Rene(temporaryDirectory.resolve("rene.txt"));

        assertEquals(
                " Apologies — That falls outside my scope of operations. Standard procedures are: todo, "
                        + "deadline, event, list, mark, unmark, delete, find, help, or bye.",
                rene.getResponse("unknown"));
    }

    @Test
    public void getResponse_help_returnsEveryCommandSyntax() {
        Rene rene = new Rene(temporaryDirectory.resolve("rene.txt"));

        assertEquals(
                " Per your request, here is the standard operating procedure for working with Rene:\n"
                        + " todo DESCRIPTION\n"
                        + " deadline DESCRIPTION /by yyyy-MM-dd\n"
                        + " event DESCRIPTION /from START /to END\n"
                        + " list\n"
                        + " mark NUMBER\n"
                        + " unmark NUMBER\n"
                        + " delete NUMBER\n"
                        + " find KEYWORD\n"
                        + " help\n"
                        + " bye",
                rene.getResponse("help"));
    }

    @Test
    public void getResponse_bye_returnsFarewell() {
        Rene rene = new Rene(temporaryDirectory.resolve("rene.txt"));

        assertEquals("Thank you for your time. Standing by — let's touch base again soon.", rene.getResponse("bye"));
    }

    @Test
    public void getResponse_addCannotBeSaved_doesNotChangeInMemoryTasks() throws IOException {
        Path dataFile = temporaryDirectory.resolve("rene.txt");
        Rene rene = new Rene(dataFile);
        Files.delete(dataFile);
        Files.createDirectory(dataFile);

        String response = rene.getResponse("todo unsaved task");

        assertTrue(response.contains("is a folder, not a file"));
        assertEquals(" Here is your current action-item backlog:", rene.getResponse("list"));
    }

    @Test
    public void getResponse_existingTaskCannotBeSaved_preservesOriginalState() throws IOException {
        Path dataFile = temporaryDirectory.resolve("rene.txt");
        Rene rene = new Rene(dataFile);
        rene.getResponse("todo keep this task");
        Files.delete(dataFile);
        Files.createDirectory(dataFile);

        assertTrue(rene.getResponse("mark 1").contains("is a folder, not a file"));
        assertTrue(rene.getResponse("delete 1").contains("is a folder, not a file"));

        assertEquals(" Here is your current action-item backlog:\n 1.[T][ ] keep this task",
                rene.getResponse("list"));
    }

    @Test
    public void startup_corruptData_keepsValidTasksAndBlocksDestructiveChanges() throws IOException {
        Path dataFile = temporaryDirectory.resolve("rene.txt");
        String originalData = "T | 0 | keep first\nmalformed line\nT | 1 | keep last\n";
        Files.writeString(dataFile, originalData, StandardCharsets.UTF_8);

        Rene rene = new Rene(dataFile);

        assertTrue(rene.getWelcomeMessage().contains("I couldn't understand line 2"));
        assertTrue(rene.getWelcomeMessage().contains("Changes are disabled to protect the original data file"));
        assertEquals(" Here is your current action-item backlog:\n"
                        + " 1.[T][ ] keep first\n"
                        + " 2.[T][X] keep last",
                rene.getResponse("list"));
        assertTrue(rene.getResponse("todo do not save").contains("Changes are disabled"));
        assertEquals(originalData, Files.readString(dataFile, StandardCharsets.UTF_8));
    }
}
