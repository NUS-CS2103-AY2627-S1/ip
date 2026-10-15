package tasklist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TasklistTest {

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void constructor_default_success() {
        Tasklist tasklist = new Tasklist();
        assertNotNull(tasklist);
        assertEquals(0, tasklist.getSize());
    }

    @Test
    public void constructor_scannerWithValidData_populatesTasks() {
        String fileData = "T | read book\n"
                + "D | submit assignment | 2026-08-28 2359\n"
                + "E | team meeting | 2026-08-28 1400 | 2026-08-28 1600\n";
        Scanner scanner = new Scanner(fileData);
        Tasklist tasklist = new Tasklist(scanner);

        assertEquals(3, tasklist.getSize());
    }

    @Test
    public void constructor_scannerWithMalformedLines_skipsInvalidLines() {
        String fileData = "T | read book\n"
                + "INVALID | corrupted line\n"
                + "D | missing date\n"
                + "E | meeting | invalid-date | invalid-date\n";
        Scanner scanner = new Scanner(fileData);
        Tasklist tasklist = new Tasklist(scanner);

        assertEquals(1, tasklist.getSize());
    }

    @Test
    public void addItem_singleTask_increasesSize() {
        Tasklist tasklist = new Tasklist();
        tasklist.addItem(new Task("read book"));

        assertEquals(1, tasklist.getSize());
    }

    @Test
    public void deleteItem_validIndex_decreasesSize() {
        Tasklist tasklist = new Tasklist();
        tasklist.addItem(new Task("read book"));
        tasklist.deleteItem(0);

        assertEquals(0, tasklist.getSize());
    }

    @Test
    public void markItem_validIndex_marksTaskDone() {
        Tasklist tasklist = new Tasklist();
        tasklist.addItem(new Task("read book"));
        tasklist.markItem(0);

        assertEquals(1, tasklist.getSize());
    }

    @Test
    public void unmarkItem_validIndex_unmarksTask() {
        Tasklist tasklist = new Tasklist();
        tasklist.addItem(new Task("read book"));
        tasklist.markItem(0);
        tasklist.unmarkItem(0);

        assertEquals(1, tasklist.getSize());
    }

    @Test
    public void toString_populatedTasklist_formattedList() {
        Tasklist tasklist = new Tasklist();
        tasklist.addItem(new Task("read book"));
        tasklist.addItem(new Task("write code"));

        String expected = "1. [T] [ ] read book\n"
                + "2. [T] [ ] write code\n";
        assertEquals(expected, tasklist.toString());
    }

    @Test
    public void fileFormat_populatedTasklist_formattedFileString() {
        Tasklist tasklist = new Tasklist();
        tasklist.addItem(new Task("read book"));
        tasklist.addItem(new Task("write code"));

        String expected = "T | read book\n"
                + "T | write code\n";
        assertEquals(expected, tasklist.fileFormat());
    }
}
