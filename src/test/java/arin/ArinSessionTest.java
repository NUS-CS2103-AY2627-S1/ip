package arin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import arin.storage.Storage;
import arin.task.Task;
import arin.task.Todo;

/**
 * Exercises the same session API used by both the GUI and the console.
 */
class ArinSessionTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_restart_restoresEveryTaskTypeAndEdits() {
        Path taskFile = temporaryDirectory.resolve("data/arin.txt");
        Arin firstSession = new Arin(new Storage(taskFile));
        assertTrue(firstSession.getResponse("  todo   Read book  ").contains("added"));
        firstSession.getResponse("deadline return book /by 2026-09-18");
        firstSession.getResponse("event meeting /from Sep 16 2pm /to Sep 16 4pm");
        firstSession.getResponse("mark 1");

        Arin secondSession = new Arin(new Storage(taskFile));
        assertEquals(firstSession.getResponse("list"), secondSession.getResponse("list"));
        assertTrue(secondSession.getWelcome().contains("Loaded 3 tasks"));
        assertTrue(secondSession.getResponse("unmark 1").contains("not done yet"));
        assertTrue(secondSession.getResponse("delete 2").contains("2 tasks"));
        assertEquals(secondSession.getResponse("list"), new Arin(new Storage(taskFile)).getResponse("list"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"todo lost task", "mark 1", "unmark 2", "delete 1", "sort name", "sort deadline"})
    void getResponse_saveFailure_keepsOriginalMemoryAndDisk(String command) throws Exception {
        Path taskFile = temporaryDirectory.resolve("arin.txt");
        Task doneTask = new Todo("apple");
        doneTask.markTask();
        new Storage(taskFile).saveTasks(List.of(new Todo("zebra"), doneTask));
        String savedText = Files.readString(taskFile);
        Storage failingStorage = new Storage(taskFile) {
            @Override
            public void saveTasks(List<Task> tasks) throws IOException {
                throw new IOException("Simulated disk failure");
            }
        };
        Arin arin = new Arin(failingStorage);
        String originalList = arin.getResponse("list");

        String response = arin.getResponse(command);

        assertTrue(response.startsWith("Oops! Could not save tasks"));
        assertEquals(originalList, arin.getResponse("list"));
        assertEquals(savedText, Files.readString(taskFile));
    }

    @Test
    void getResponse_corruptFile_blocksChangesUntilRestartWithoutOverwritingData() throws Exception {
        Path taskFile = temporaryDirectory.resolve("arin.txt");
        String content = "T | X | keep this\ninvalid\n";
        Files.writeString(taskFile, content);
        Arin arin = new Arin(new Storage(taskFile));

        assertTrue(arin.getWelcome().contains("line 2"));
        for (String command : List.of("todo new", "mark 1", "unmark 1", "delete 1", "sort name", "list", "find keep")) {
            assertTrue(arin.getResponse(command).contains("Your file is unchanged"));
        }
        assertTrue(arin.getResponse("help").contains("todo <description>"));
        assertEquals(content, Files.readString(taskFile));
        assertTrue(arin.getResponse("bye").contains("Bye"));
        assertTrue(arin.isExit());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "mark", "mark 0", "mark -1", "mark 2147483648", "delete 2", "unmark abc",
        "find ", "sort date", "list extra", "bye extra", "help extra", "todo invalid | task",
        "todo two\nlines", "event meeting /from 2pm /to 4pm /to 5pm", "deadline x /by 2026-02-30"})
    void getResponse_invalidInput_returnsErrorAndKeepsSessionUsable(String command) {
        Arin arin = new Arin(new Storage(temporaryDirectory.resolve("arin.txt")));
        arin.getResponse("todo keep this");
        String originalList = arin.getResponse("list");

        assertTrue(arin.getResponse(command).startsWith("Oops!"));
        assertEquals(originalList, arin.getResponse("list"));
        assertFalse(arin.isExit());
    }

    @Test
    void getResponse_goodbye_preventsFurtherCommands() {
        Arin arin = new Arin(new Storage(temporaryDirectory.resolve("arin.txt")));
        assertFalse(arin.isExit());
        assertTrue(arin.getResponse("bye").contains("Bye"));
        assertTrue(arin.isExit());
        assertTrue(arin.getResponse("todo too late").contains("session has ended"));
    }
}
