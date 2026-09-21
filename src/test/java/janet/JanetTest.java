package janet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JanetTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_validAndInvalidCommands_returnsCleanResponsesAndPreservesState() {
        Janet janet = new Janet(temporaryDirectory.resolve("data/janet.txt").toString());

        String addResponse = janet.getResponse("todo read book");
        String invalidResponse = janet.getResponse("mark 2");
        String listResponse = janet.getResponse("list");

        assertEquals("""
                Done and done! I added this task:
                [T][ ] read book
                You now have 1 task.""", addResponse);
        assertEquals("Sorry, that task number does not exist.", invalidResponse);
        assertTrue(listResponse.contains("1.[T][ ] read book"));
    }

    @Test
    void getResponse_commonInputMistakes_returnsSpecificErrorsAndPreservesState() {
        Janet janet = new Janet(temporaryDirectory.resolve("data/janet.txt").toString());

        assertEquals("Please enter a command.", janet.getResponse("   "));
        assertEquals("Sorry, list does not take any arguments.", janet.getResponse("list extra"));
        assertEquals("Sorry, bye does not take any arguments.", janet.getResponse("bye now"));
        assertEquals("Sorry, task details cannot contain tabs or line breaks.",
                janet.getResponse("todo read\tbook"));

        String addResponse = janet.getResponse("  todo   read book  ");
        String listResponse = janet.getResponse("  list  ");

        assertTrue(addResponse.contains("[T][ ] read book"));
        assertTrue(listResponse.contains("1.[T][ ] read book"));
    }

    @Test
    void getResponse_duplicateTask_rejectsTaskAndPreservesState() {
        Janet janet = new Janet(temporaryDirectory.resolve("data/janet.txt").toString());

        janet.getResponse("event meeting /from 2099-01-01 10:00 /to 2099-01-01 11:00");
        String duplicateResponse =
                janet.getResponse("event meeting /from 2099-01-01 10:00 /to 2099-01-01 11:00");
        String listResponse = janet.getResponse("list");

        assertEquals("Sorry, that exact task is already in your list.", duplicateResponse);
        assertEquals(1, listResponse.lines().filter(line -> line.contains("[E][ ] meeting")).count());
    }

    @Test
    void getCommandResult_pastDateWarnsWithoutClassifyingResponseAsError() {
        Janet janet = new Janet(temporaryDirectory.resolve("data/janet.txt").toString());

        CommandResult result = janet.getCommandResult("deadline archived task /by 2000-01-01");

        assertTrue(result.message().contains("Warning: this task contains a date or time that has already passed."));
        assertFalse(result.isError());
        assertTrue(janet.getResponse("list").contains("archived task"));
    }

    @Test
    void getResponse_saveFails_reversesAllTaskChanges() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data/janet.txt");
        Janet janet = new Janet(dataFile.toString());
        janet.getResponse("todo first");
        janet.getResponse("todo second");

        Files.delete(dataFile);
        Files.createDirectory(dataFile);
        Files.writeString(dataFile.resolve("blocking-file.txt"), "keep directory non-empty");

        String markResponse = janet.getResponse("mark 1");
        String deleteResponse = janet.getResponse("delete 2");
        String addResponse = janet.getResponse("todo third");
        String listResponse = janet.getResponse("list");

        assertTrue(markResponse.contains("that change was reversed"));
        assertTrue(deleteResponse.contains("that change was reversed"));
        assertTrue(addResponse.contains("that change was reversed"));
        assertTrue(listResponse.contains("1.[T][ ] first"));
        assertTrue(listResponse.contains("2.[T][ ] second"));
        assertFalse(listResponse.contains("third"));
    }

    @Test
    void getResponse_malformedSavedData_warnsAndProtectsOriginalFile() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data/janet.txt");
        Files.createDirectories(dataFile.getParent());
        String originalData = "T\t0\tvalid task\ninvalid row\n";
        Files.writeString(dataFile, originalData);

        Janet janet = new Janet(dataFile.toString());
        String addResponse = janet.getResponse("todo another task");
        String listResponse = janet.getResponse("list");

        assertEquals(1, janet.getStartupWarnings().size());
        assertTrue(janet.getStartupWarnings().get(0).contains("line 2"));
        assertTrue(addResponse.contains("saved-data warning is unresolved"));
        assertTrue(listResponse.contains("1.[T][ ] valid task"));
        assertFalse(listResponse.contains("another task"));
        assertEquals(originalData, Files.readString(dataFile));
    }

    @Test
    void getCommandResult_normalErrorAndExitCommands_classifiesResponses() {
        Janet janet = new Janet(temporaryDirectory.resolve("data/janet.txt").toString());

        CommandResult normalResult = janet.getCommandResult("list");
        CommandResult errorResult = janet.getCommandResult("unknown");
        CommandResult exitResult = janet.getCommandResult("  bye  ");

        assertFalse(normalResult.isError());
        assertFalse(normalResult.isExit());
        assertTrue(errorResult.isError());
        assertFalse(errorResult.isExit());
        assertFalse(exitResult.isError());
        assertTrue(exitResult.isExit());
    }

    @Test
    void getResponse_completeCommandWorkflow_updatesStateAndSavedData() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data/janet.txt");
        Janet janet = new Janet(dataFile.toString());

        janet.getResponse("todo read book");
        janet.getResponse("deadline return book /by 2099-12-02");
        janet.getResponse("event project meeting /from 14:00 /to 16:00");
        String markResponse = janet.getResponse("mark 2");
        String unmarkResponse = janet.getResponse("unmark 2");
        String findResponse = janet.getResponse("find book");
        String deleteResponse = janet.getResponse("delete 3 1");
        String listResponse = janet.getResponse("list");

        assertTrue(markResponse.contains("[D][X] return book"));
        assertTrue(unmarkResponse.contains("[D][ ] return book"));
        assertTrue(findResponse.contains("read book"));
        assertTrue(findResponse.contains("return book"));
        assertTrue(deleteResponse.contains("project meeting"));
        assertTrue(deleteResponse.contains("read book"));
        assertFalse(listResponse.contains("read book"));
        assertFalse(listResponse.contains("project meeting"));
        assertTrue(listResponse.contains("1.[D][ ] return book"));
        assertEquals("D\t0\treturn book\t2099-12-02\n", Files.readString(dataFile));
    }
}
