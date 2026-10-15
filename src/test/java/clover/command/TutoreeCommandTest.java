package clover.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import clover.exception.CloverException;
import clover.storage.Storage;
import clover.task.TaskList;
import clover.tutoree.Tutoree;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/** Tests commands that manage Clover's tutoree directory. */
class TutoreeCommandTest {
    @TempDir
    Path tempDir;

    @Test
    void addTutoree_validDetails_tutoreeAddedAndSaved() throws CloverException {
        TutoreeList tutorees = new TutoreeList();

        new AddTutoreeCommand("Alice Tan /address 12 Example Road /fee 50")
                .execute(new TaskList(), tutorees, new Ui(), storage());

        Tutoree tutoree = tutorees.asList().getFirst();
        assertEquals("Alice Tan", tutoree.getName());
        assertEquals("12 Example Road", tutoree.getAddress());
        assertEquals("50", tutoree.getFee());
    }

    @Test
    void addTutoree_duplicateNameIgnoringCase_exceptionThrown() throws CloverException {
        TutoreeList tutorees = new TutoreeList();
        tutorees.add(new Tutoree("Alice Tan", "12 Example Road", "50"));

        CloverException exception = assertThrows(CloverException.class, () -> new AddTutoreeCommand(
                "alice tan /address 8 Sample Avenue /fee 55").execute(
                        new TaskList(), tutorees, new Ui(), storage()));

        assertEquals("A learning companion named \"alice tan\" is already in the grove.", exception.getMessage());
    }

    @Test
    void findTutoree_partialNameIgnoringCase_matchingTutoreeDisplayed() throws CloverException {
        TutoreeList tutorees = new TutoreeList();
        tutorees.add(new Tutoree("Alice Tan", "12 Example Road", "50"));

        String output = captureOutput(() -> new FindTutoreeCommand("ALI")
                .execute(new TaskList(), tutorees, new Ui(), storage()));

        assertEquals("The grove found these matching learning companions:" + System.lineSeparator()
                + "1. Alice Tan" + System.lineSeparator()
                + "   Address: 12 Example Road" + System.lineSeparator()
                + "   Fee: 50" + System.lineSeparator(), output);
    }

    @Test
    void addTutoree_missingFee_exceptionThrown() {
        CloverException exception = assertThrows(CloverException.class, () -> new AddTutoreeCommand(
                "Alice Tan /address 12 Example Road").execute(
                        new TaskList(), new TutoreeList(), new Ui(), storage()));

        assertEquals("To welcome a learning companion, use: add-tutoree NAME /address ADDRESS /fee FEE",
                exception.getMessage());
    }

    @Test
    void addTutoree_nameWithoutLetters_exceptionThrownWithoutAddingTutoree() {
        TutoreeList tutorees = new TutoreeList();

        CloverException exception = assertThrows(CloverException.class, () -> new AddTutoreeCommand(
                "123-!? /address 12 Example Road /fee 50").execute(
                        new TaskList(), tutorees, new Ui(), storage()));

        assertEquals("Enter a learning companion name containing at least one letter.", exception.getMessage());
        assertEquals(0, tutorees.size());
    }

    @Test
    void addTutoree_nonNumericFee_exceptionThrownWithoutAddingTutoree() {
        TutoreeList tutorees = new TutoreeList();

        CloverException exception = assertThrows(CloverException.class, () -> new AddTutoreeCommand(
                "Alice Tan /address 12 Example Road /fee $50/hour").execute(
                        new TaskList(), tutorees, new Ui(), storage()));

        assertEquals("Enter a positive fee amount, optionally followed by /hour, /session, /lesson, or /month; "
                + "for example 50 or 50/hour.", exception.getMessage());
        assertEquals(0, tutorees.size());
    }

    @Test
    void addTutoree_hourlyFee_tutoreeAdded() throws CloverException {
        TutoreeList tutorees = new TutoreeList();

        new AddTutoreeCommand("Alice Tan /address 12 Example Road /fee 50/hour")
                .execute(new TaskList(), tutorees, new Ui(), storage());

        assertEquals("50/hour", tutorees.asList().getFirst().getFee());
    }

    private Storage storage() {
        return new Storage(tempDir.resolve("data/clover.txt"), tempDir.resolve("data/tutorees.txt"));
    }

    /** Captures console output produced while running the supplied command action. */
    private String captureOutput(CommandAction action) throws CloverException {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            action.execute();
        } finally {
            System.setOut(originalOut);
        }
        return output.toString(StandardCharsets.UTF_8);
    }

    /** Represents a command action that can produce console output. */
    @FunctionalInterface
    private interface CommandAction {
        void execute() throws CloverException;
    }
}
