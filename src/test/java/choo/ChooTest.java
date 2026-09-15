package choo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import choo.exception.ChooException;
import choo.storage.Storage;
import choo.task.Task;
import choo.ui.Ui;

/**
 * Checks the user-visible behavior of the CHOO command-line interface.
 */
public class ChooTest {
    @Test
    void getResponse_sequentialGuiCommands_preservesStateAndFormatsReplies() throws Exception {
        Path dataFile = Files.createTempDirectory("choo-gui-response-test-")
                .resolve("data").resolve("choo.txt");
        Choo choo = new Choo(new Storage(dataFile), new Ui());
        String lineSeparator = System.lineSeparator();

        assertEquals("Ticket issued. I've added this task:" + lineSeparator
                + "  [T][ ] read book" + lineSeparator
                + "Your itinerary now has 1 task.", choo.getResponse("todo read book"));
        assertEquals("On track! I've marked this task as done:" + lineSeparator
                + "  [T][X] read book", choo.getResponse("mark 1"));
        assertEquals("Here is your task itinerary:" + lineSeparator
                + "1.[T][X] read book", choo.getResponse("list"));
        assertEquals("OOPS!!! Signal problem: Task number 2 is outside the list.",
                choo.getResponse("delete 2"));
        assertEquals("End of the line for now. Safe travels!", choo.getResponse("bye"));
    }

    @Test
    void getResponse_sortCommand_reordersAndSavesTasks() throws Exception {
        Path dataFile = Files.createTempDirectory("choo-sort-test-")
                .resolve("data").resolve("choo.txt");
        Choo choo = new Choo(new Storage(dataFile), new Ui());
        String lineSeparator = System.lineSeparator();
        choo.getResponse("todo undated");
        choo.getResponse("deadline later /by 2026-12-31");
        choo.getResponse("deadline earlier /by 2026-01-15");

        String response = choo.getResponse("sort");

        assertEquals("Timetable sorted by deadline:" + lineSeparator
                + "1.[D][ ] earlier (by: Jan 15 2026)" + lineSeparator
                + "2.[D][ ] later (by: Dec 31 2026)" + lineSeparator
                + "3.[T][ ] undated", response);
        assertEquals(List.of(
                "D | 0 | earlier | 2026-01-15",
                "D | 0 | later | 2026-12-31",
                "T | 0 | undated"), Files.readAllLines(dataFile));
    }

    @Test
    void getResponse_findUnmarkAndDeleteCommands_updateExpectedState() throws Exception {
        Path dataFile = Files.createTempDirectory("choo-command-coverage-test-")
                .resolve("data").resolve("choo.txt");
        Choo choo = new Choo(new Storage(dataFile), new Ui());
        String lineSeparator = System.lineSeparator();
        choo.getResponse("todo read book");
        choo.getResponse("todo write notes");
        choo.getResponse("mark 1");

        assertEquals("Back on the route. I've marked this task as not done:"
                + lineSeparator + "  [T][ ] read book", choo.getResponse("unmark 1"));
        assertEquals("Here are the matching stops:" + lineSeparator
                + "1.[T][ ] read book", choo.getResponse("find book"));
        assertEquals("Route updated. I've removed this task:" + lineSeparator
                + "  [T][ ] write notes" + lineSeparator
                + "Your itinerary now has 1 task.", choo.getResponse("delete 2"));
        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(dataFile));
    }

    @Test
    void run_loadFails_showsErrorAndStops() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(
                new ByteArrayInputStream("todo ignored\n".getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));
        Storage failingStorage = new Storage(Path.of("unused-test-data.txt")) {
            @Override
            public List<Task> load() throws ChooException {
                throw new ChooException("I couldn't read the task data file.");
            }
        };

        new Choo(failingStorage, ui).run();

        String actualOutput = output.toString(StandardCharsets.UTF_8);
        assertTrue(actualOutput.contains(
                "OOPS!!! Signal problem: I couldn't read the task data file."));
        assertFalse(actualOutput.contains("Ticket issued"));
    }

    @Test
    void run_mixedValidAndInvalidCommands_preservesCorrectTaskState() throws Exception {
        String input = "todo\n"
                + "mystery command\n"
                + "todo keep this\n"
                + "deadline remove this /by 2019-12-06\n"
                + "event keep event /from Monday /to Tuesday\n"
                + "delete 2\n"
                + "list\nbye\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        java.io.InputStream originalInput = System.in;
        Path dataFile = Files.createTempDirectory("choo-command-test-")
                .resolve("data").resolve("choo.txt");

        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            Choo.run(new Storage(dataFile));
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String actualOutput = output.toString(StandardCharsets.UTF_8);
        String lineSeparator = System.lineSeparator();
        assertTrue(actualOutput.contains("OOPS!!! Signal problem: A todo needs a description."));
        assertTrue(actualOutput.contains("OOPS!!! Signal problem: I don't recognize that command."));
        assertTrue(actualOutput.contains("Route updated. I've removed this task:"));
        assertTrue(actualOutput.contains("[D][ ] remove this (by: Dec 6 2019)"));
        assertTrue(actualOutput.contains("Your itinerary now has 2 tasks."));
        assertTrue(actualOutput.contains("Here is your task itinerary:" + lineSeparator
                + "1.[T][ ] keep this" + lineSeparator
                + "2.[E][ ] keep event (from: Monday to: Tuesday)" + lineSeparator
                + "____________________________________________________________"));
        assertFalse(actualOutput.contains("[T][ ] mystery command"));
        assertTrue(actualOutput.contains("End of the line for now. Safe travels!"));
    }
}
