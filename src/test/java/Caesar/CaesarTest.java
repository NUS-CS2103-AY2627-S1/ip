package caesar;

import caesar.exception.CaesarException;
import caesar.gui.GuiUi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests Caesar command processing independently from the JavaFX window. */
public class CaesarTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies that a command is executed and its response is sent to the GUI user interface. */
    @Test
    public void processCommandAddsTaskAndReturnsResponse() throws Exception {
        GuiUi guiUi = new GuiUi();
        Caesar caesar = createCaesar(guiUi);

        assertFalse(caesar.processCommand("todo Read a book"));

        assertTrue(guiUi.consumeResponse().contains("Read a book"));
    }

    /** Verifies that a task-added response stores the dynamic comment separately. */
    @Test
    public void processCommandStoresDynamicCommentAsFollowUpResponse() throws Exception {
        GuiUi guiUi = new GuiUi();
        Caesar caesar = createCaesar(guiUi);
        LocalDate today = LocalDate.now();

        caesar.processCommand("deadline Submit report /by " + today.plusDays(1));

        assertFalse(guiUi.consumeResponse().contains("You have 1 task due within the next 3 days."));
        assertEquals("You have 1 task due within the next 3 days. "
                + "Take it one step at a time—you've got this.",
                guiUi.consumeFollowUpResponse());
    }

    /** Verifies that the list command returns the persisted task to the GUI user interface. */
    @Test
    public void processCommandListsTasks() throws Exception {
        GuiUi guiUi = new GuiUi();
        Caesar caesar = createCaesar(guiUi);
        caesar.processCommand("todo Read a book");
        guiUi.clearResponse();

        caesar.processCommand("list");

        assertTrue(guiUi.consumeResponse().contains("1. [T][ ] Read a book"));
    }

    /** Verifies that find returns matching tasks through the GUI response collector. */
    @Test
    public void processCommandFindsTasks() throws Exception {
        GuiUi guiUi = new GuiUi();
        Caesar caesar = createCaesar(guiUi);
        caesar.processCommand("todo Read a book");
        caesar.processCommand("todo Buy groceries");
        guiUi.clearResponse();

        caesar.processCommand("find BOOK");

        assertTrue(guiUi.consumeResponse().contains("1. [T][ ] Read a book"));
    }

    /** Verifies that the reminder command returns separate missed and upcoming sections. */
    @Test
    public void processCommandShowsReminders() throws Exception {
        GuiUi guiUi = new GuiUi();
        Caesar caesar = createCaesar(guiUi);
        LocalDate today = LocalDate.now();

        caesar.processCommand("deadline Missed report /by " + today.minusDays(1));
        caesar.processCommand("event Upcoming meeting /from " + today.plusDays(1)
                + " /to " + today.plusDays(2));
        guiUi.clearResponse();

        caesar.processCommand("reminder");

        String response = guiUi.consumeResponse();
        assertTrue(response.contains("A few loose ends from earlier:"));
        assertTrue(response.contains("On the horizon (next 3 days):"));
        assertTrue(response.contains("Missed report"));
        assertTrue(response.contains("Upcoming meeting"));
        assertTrue(guiUi.consumeFollowUpResponse().contains(
                "You have 1 task due within the next 3 days."));
    }

    /** Verifies that the clear command removes missed tasks and persists the remaining tasks. */
    @Test
    public void processCommandClearsMissedTasks() throws Exception {
        GuiUi guiUi = new GuiUi();
        Caesar caesar = createCaesar(guiUi);
        LocalDate today = LocalDate.now();

        caesar.processCommand("deadline Missed report /by " + today.minusDays(1));
        caesar.processCommand("deadline Upcoming report /by " + today.plusDays(1));
        guiUi.clearResponse();

        caesar.processCommand("clear missed");

        assertEquals("Cleared 1 missed task.", guiUi.consumeResponse());
        String savedTasks = java.nio.file.Files.readString(temporaryDirectory.resolve("tasks.txt"));
        assertTrue(savedTasks.contains("Upcoming report"));
        assertFalse(savedTasks.contains("Missed report"));
    }

    /** Verifies that typing help produces the command list in the GUI response. */
    @Test
    public void processCommandShowsHelp() throws Exception {
        GuiUi guiUi = new GuiUi();
        Caesar caesar = createCaesar(guiUi);

        caesar.processCommand("HELP");

        String response = guiUi.consumeResponse();
        assertTrue(response.contains("find <keyword>"));
        assertTrue(response.contains("reschedule <number> <date> [<end date>]"));
    }

    /** Verifies that rescheduling a deadline changes its saved due date. */
    @Test
    public void processCommandReschedulesDeadline() throws Exception {
        GuiUi guiUi = new GuiUi();
        Caesar caesar = createCaesar(guiUi);

        caesar.processCommand("deadline Submit report /by 2024-06-01");
        guiUi.clearResponse();

        caesar.processCommand("reschedule 1 05/06/2024");

        assertTrue(guiUi.consumeResponse().contains("Jun 5 2024"));
        assertEquals("D | 0 | Submit report | Jun 5 2024",
                java.nio.file.Files.readString(temporaryDirectory.resolve("tasks.txt")).trim());
    }

    /** Verifies that rescheduling an event changes both its saved dates. */
    @Test
    public void processCommandReschedulesEvent() throws Exception {
        GuiUi guiUi = new GuiUi();
        Caesar caesar = createCaesar(guiUi);

        caesar.processCommand("event Project meeting /from 2024-06-01 /to 2024-06-02");
        guiUi.clearResponse();

        caesar.processCommand("reschedule 1 05/05/2026 06/05/2026");

        assertTrue(guiUi.consumeResponse().contains("May 5 2026"));
        assertEquals("E | 0 | Project meeting | May 5 2026 | May 6 2026",
                java.nio.file.Files.readString(temporaryDirectory.resolve("tasks.txt")).trim());
    }

    /** Verifies that an undated to-do task cannot be rescheduled. */
    @Test
    public void processCommandRejectsReschedulingTodo() throws Exception {
        GuiUi guiUi = new GuiUi();
        Caesar caesar = createCaesar(guiUi);
        caesar.processCommand("todo Read a book");

        CaesarException exception = org.junit.jupiter.api.Assertions.assertThrows(
                CaesarException.class,
                () -> caesar.processCommand("reschedule 1 05/06/2024")
        );

        assertTrue(exception.getMessage().contains("Only deadline and event tasks can be rescheduled"));
    }

    /** Verifies that the bye command signals the JavaFX window to stop accepting input. */
    @Test
    public void processCommandRecognizesExit() throws Exception {
        GuiUi guiUi = new GuiUi();
        Caesar caesar = createCaesar(guiUi);

        assertTrue(caesar.processCommand("bye"));
        assertTrue(guiUi.consumeResponse().contains("Until next time"));
    }

    /** Verifies that the interactive loop executes commands through the shared command path. */
    @Test
    public void runProcessesCommandsAndStopsAtExit() {
        InputStream originalInput = System.in;
        System.setIn(new ByteArrayInputStream(
                "todo Read a book\nbye\n".getBytes(StandardCharsets.UTF_8)));

        try {
            GuiUi guiUi = new GuiUi();
            Caesar caesar = createCaesar(guiUi);
            caesar.run();

            String response = guiUi.consumeResponse();
            assertTrue(response.contains("Read a book"));
            assertTrue(response.contains("Until next time"));
        } finally {
            System.setIn(originalInput);
        }
    }

    /** Creates a Caesar instance backed by an isolated temporary task file. */
    private Caesar createCaesar(GuiUi guiUi) {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        return new Caesar(taskFile.toString(), guiUi);
    }
}
