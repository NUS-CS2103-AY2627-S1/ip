package milo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests statistics through the same response API used by the graphical interface. */
public class StatisticsTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_emptyList_reportsZerosWithoutCreatingStorage() {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Milo milo = new Milo(taskFile.toString());

        assertEquals(summary(0, 0, 0, "0.0", 0, 0, 0), milo.getResponse("  stats  "));
        assertFalse(Files.exists(taskFile));
    }

    @Test
    public void getResponse_mixedTasks_reportsCurrentStateAndPreservesStorage() throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Milo milo = new Milo(taskFile.toString());
        milo.getResponse("todo read book");
        milo.getResponse("deadline return book /by 2026-09-15");
        milo.getResponse("event meeting /from 2026-09-16 /to 2026-09-17");
        milo.getResponse("mark 1");
        String savedTasks = Files.readString(taskFile);

        assertEquals(summary(3, 1, 2, "33.3", 1, 1, 1), milo.getResponse("stats"));
        assertEquals(savedTasks, Files.readString(taskFile));
        Milo restartedMilo = new Milo(taskFile.toString());
        assertEquals(summary(3, 1, 2, "33.3", 1, 1, 1), restartedMilo.getResponse("stats"));

        milo.getResponse("mark 1");
        assertEquals(summary(3, 1, 2, "33.3", 1, 1, 1), milo.getResponse("stats"));
        milo.getResponse("mark 2");
        milo.getResponse("mark 3");
        assertEquals(summary(3, 3, 0, "100.0", 1, 1, 1), milo.getResponse("stats"));
        milo.getResponse("unmark 1");
        assertEquals(summary(3, 2, 1, "66.7", 1, 1, 1), milo.getResponse("stats"));
        milo.getResponse("delete 2");
        milo.getResponse("find missing");
        assertEquals(summary(2, 1, 1, "50.0", 1, 0, 1), milo.getResponse("stats"));
    }

    @Test
    public void getResponse_invalidStatisticsCommand_reportsError() {
        Milo milo = new Milo(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("OOPS!!! Use stats without additional arguments.", milo.getResponse("stats week"));
        assertEquals("OOPS!!! Use stats without additional arguments.", milo.getResponse("stats\t1"));
        assertEquals("OOPS!!! I don't recognize that command :-(", milo.getResponse("statsmore"));
        assertEquals("OOPS!!! I don't recognize that command :-(", milo.getResponse("STATS"));
    }

    /** Builds the specified display with an explicit expected percentage. */
    private String summary(int total, int completed, int pending, String percentage,
            int todos, int deadlines, int events) {
        return "Here are your task statistics:\n"
                + "Total: " + total + "\n"
                + "Completed: " + completed + "\n"
                + "Pending: " + pending + "\n"
                + "Completion: " + percentage + "%\n"
                + "Todos: " + todos + "\n"
                + "Deadlines: " + deadlines + "\n"
                + "Events: " + events;
    }
}
