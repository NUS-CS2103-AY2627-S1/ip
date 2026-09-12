package pixel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the response adapter used by the graphical user interface. */
class PixelTest {

    @Test
    void getResponse_todoCommand_returnsAddedTaskMessage(@TempDir Path tempDir) {
        Pixel pixel = new Pixel(tempDir.resolve("pixel.txt"));

        String response = pixel.getResponse("todo read book");

        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(response.contains("Now you have 1 tasks in the list."));
    }

    @Test
    void getResponse_listCommand_reusesTaskList(@TempDir Path tempDir) {
        Pixel pixel = new Pixel(tempDir.resolve("pixel.txt"));
        pixel.getResponse("todo read book");

        String response = pixel.getResponse("list");

        assertEquals("Here are the tasks in your list:" + System.lineSeparator()
                + "1.[T][ ] read book", response);
    }

    @Test
    void getResponse_byeCommand_returnsGoodbyeMessage(@TempDir Path tempDir) {
        Pixel pixel = new Pixel(tempDir.resolve("pixel.txt"));

        String response = pixel.getResponse("bye");

        assertEquals("Bye. Hope to see you again soon!", response);
        assertFalse(pixel.isLastResponseError());
    }

    @Test
    void isLastResponseError_invalidThenValidCommand_tracksLatestResponse(@TempDir Path tempDir) {
        Pixel pixel = new Pixel(tempDir.resolve("pixel.txt"));

        pixel.getResponse("blah");
        assertTrue(pixel.isLastResponseError());

        pixel.getResponse("todo");
        assertTrue(pixel.isLastResponseError());

        pixel.getResponse("list");
        assertFalse(pixel.isLastResponseError());
    }

    @Test
    void getResponse_updateCommands_changeOnlySelectedDetails(@TempDir Path tempDir) {
        Pixel pixel = new Pixel(tempDir.resolve("pixel.txt"));
        pixel.getResponse("todo read book");
        pixel.getResponse("deadline submit report /by 2026-09-05");
        pixel.getResponse("event meeting /from 2pm /to 4pm");
        pixel.getResponse("mark 3");

        pixel.getResponse("update 1 /description read novel");
        pixel.getResponse("update 2 /by 2026-09-10");
        pixel.getResponse("update 3 /to 5pm");

        assertEquals("Here are the tasks in your list:" + System.lineSeparator()
                + "1.[T][ ] read novel" + System.lineSeparator()
                + "2.[D][ ] submit report (by: Sep 10 2026)" + System.lineSeparator()
                + "3.[E][X] meeting (from: 2pm to: 5pm)", pixel.getResponse("list"));
    }

    @Test
    void getResponse_inapplicableOrInvalidUpdate_preservesTask(@TempDir Path tempDir) {
        Pixel pixel = new Pixel(tempDir.resolve("pixel.txt"));
        pixel.getResponse("todo read book");
        pixel.getResponse("deadline submit report /by 2026-09-05");

        assertEquals("Only events have a /to time.", pixel.getResponse("update 1 /to 5pm"));
        assertEquals("Oops! Please enter the deadline date in YYYY-MM-DD format.",
                pixel.getResponse("update 2 /by tomorrow"));
        assertEquals("Here are the tasks in your list:" + System.lineSeparator()
                + "1.[T][ ] read book" + System.lineSeparator()
                + "2.[D][ ] submit report (by: Sep 05 2026)", pixel.getResponse("list"));
    }

    @Test
    void getResponse_duplicateAddAndUpdate_preservesUniqueTasks(@TempDir Path tempDir) {
        Pixel pixel = new Pixel(tempDir.resolve("pixel.txt"));
        pixel.getResponse("todo alpha");
        pixel.getResponse("todo beta");

        assertEquals("This task is already in your list.", pixel.getResponse("todo alpha"));
        assertTrue(pixel.isLastResponseError());
        assertEquals("That update would duplicate another task in your list.",
                pixel.getResponse("update 2 /description alpha"));
        assertEquals("Here are the tasks in your list:" + System.lineSeparator()
                + "1.[T][ ] alpha" + System.lineSeparator()
                + "2.[T][ ] beta", pixel.getResponse("list"));
    }
}
