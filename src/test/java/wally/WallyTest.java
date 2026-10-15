package wally;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests response generation and persistence independently of the GUI. */
public class WallyTest {
    @TempDir
    private Path directory;

    @Test
    public void changeBackgroundColour_supportedColours_surviveRestart() throws Exception {
        Path file = directory.resolve("save.txt");
        Wally wally = new Wally(file);
        assertEquals("lightblue", wally.getBackgroundColour());
        wally.getResponse("todo read");
        for (String colour : new String[] {"black", "white", "light blue", "blue"}) {
            String expected = colour.contains("blue") ? "lightblue" : colour;
            assertEquals(expected, wally.changeBackgroundColour(colour));
            Wally restarted = new Wally(file);
            assertEquals(expected, restarted.getBackgroundColour());
            restarted.getResponse("list");
            assertEquals(expected, new Wally(file).getBackgroundColour());
            assertEquals("todo read\n", Files.readString(file));
        }
    }

    @Test
    public void changeBackgroundColour_invalidChoice_preservesSavedColour() throws Exception {
        Wally wally = new Wally(directory.resolve("save.txt"));
        wally.changeBackgroundColour("black");
        assertThrows(InvalidBackgroundColourException.class, () -> wally.changeBackgroundColour("red"));
        assertEquals("black", new Wally(directory.resolve("save.txt")).getBackgroundColour());
    }

    @Test
    public void background_corruptSetting_fallsBackAndCanBeReplaced() throws Exception {
        Path file = directory.resolve("save.txt");
        Files.writeString(directory.resolve("background.txt"), "invalid colour");
        Wally wally = new Wally(file);
        assertEquals("lightblue", wally.getBackgroundColour());
        wally.changeBackgroundColour("white");
        assertEquals("white", new Wally(file).getBackgroundColour());
    }

    @Test
    public void changeBackgroundColour_unwritableSetting_reportsFailure() throws Exception {
        Wally wally = new Wally(directory.resolve("save.txt"));
        Files.createDirectory(directory.resolve("background.txt"));
        assertThrows(java.io.IOException.class, () -> wally.changeBackgroundColour("black"));
    }

    @Test
    public void getResponse_commands_returnsResponsesAndPersistsChanges() throws Exception {
        Path file = directory.resolve("save.txt");
        Wally wally = new Wally(file);
        assertEquals("The following task has been added:\n[T][ ] read\nNow you have 1 tasks in the list.",
                wally.getResponse("todo read"));
        assertEquals("todo read\n", Files.readString(file));
        assertEquals("A task with this description already exists.", wally.getResponse("todo read"));
        assertEquals("todo read\n", Files.readString(file));
        assertEquals("TERMINATE_PROGRAM", wally.getResponse("bye"));
        assertEquals("Here are the tasks in your list:\n1. [T][ ] read", new Wally(file).getResponse("list"));
        assertEquals("The following task has been removed:\n[T][ ] read\nNow you have 0 tasks in the list.",
                wally.getResponse("delete 1"));
        assertEquals("", Files.readString(file));
    }

    @Test
    public void getStartupMessage_savedTasks_greetsAndSortsWithoutRewritingFile() throws Exception {
        Path file = directory.resolve("save.txt");
        String saved = "todo read\ndeadline submit /by 2026-09-14 12:00\n";
        Files.writeString(file, saved);
        Wally wally = new Wally(file);
        LocalDate before = LocalDate.now();
        String message = wally.getStartupMessage();
        LocalDate after = LocalDate.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
        assertTrue(message.startsWith("Welcome back, today is " + before.format(format) + "\n\n")
                || message.startsWith("Welcome back, today is " + after.format(format) + "\n\n"));
        assertTrue(message.endsWith("Here are the tasks in your schedule:"
                + "\n2. [D][ ] submit (by: 14 Sep 2026, 12:00)\n1. [T][ ] read"));
        assertEquals(saved, Files.readString(file));
    }
}
