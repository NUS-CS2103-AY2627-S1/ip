package braun.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import braun.task.Task;
import braun.task.Todo;

/**
 * Unit tests for {@link Ui} persona remark matching logic and task presentation.
 */
public class UiTest {

    @Test
    public void getRemarkForTask_loreKeywordGhost_anomalyRemarkReturned() {
        Ui ui = new Ui();
        String remark = ui.getRemarkForTask("explore ghost story");
        assertTrue(remark.contains("A new anomaly! Daydream Inc. will want this documented."));
    }

    @Test
    public void getRemarkForTask_loreKeywordWork_workRemarkReturned() {
        Ui ui = new Ui();
        String remark = ui.getRemarkForTask("finish monthly work report");
        assertTrue(remark.contains("Even trapped in a ghost story, we still gotta work, don't we?"));
    }

    @Test
    public void getRemarkForTask_loreKeywordRabbit_rabbitRemarkReturned() {
        Ui ui = new Ui();
        String remark = ui.getRemarkForTask("find pink rabbit doll");
        assertTrue(remark.contains("Reminds me of a certain charming pink rabbit doll, doesn't it?"));
    }

    @Test
    public void getRemarkForTask_unrecognizedDescription_generalRemarkReturned() {
        Ui ui = new Ui();
        String remark = ui.getRemarkForTask("arbitrary generic task 12345");
        assertNotNull(remark);
        assertFalse(remark.isEmpty());
    }

    @Test
    public void showMatchingTasks_matchingTasksProvided_printsMatchingHeaderAndTasks() {
        Ui ui = new Ui();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(outContent));
            ArrayList<Task> matches = new ArrayList<>();
            matches.add(new Todo("read book"));
            matches.add(new Todo("return book"));

            ui.showMatchingTasks("book", matches);

            String output = outContent.toString();
            assertTrue(output.contains("Here are the matching tasks in your list:"));
            assertTrue(output.contains("1.[T][ ] read book"));
            assertTrue(output.contains("2.[T][ ] return book"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void showMatchingTasks_emptyListProvided_printsNoMatchingTasksMessage() {
        Ui ui = new Ui();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(outContent));
            ui.showMatchingTasks("mystery", new ArrayList<>());

            String output = outContent.toString();
            assertTrue(output.contains("*static* No matching broadcast tasks found for: mystery"));
        } finally {
            System.setOut(originalOut);
        }
    }
}
