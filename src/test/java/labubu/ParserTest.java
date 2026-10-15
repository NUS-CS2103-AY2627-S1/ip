package labubu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests parser command handling and validation. */
public class ParserTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void processesTaskCommands() {
        TaskList tasks = new TaskList();
        Parser parser = parserFor(
                "todo read book\n"
                        + "deadline return book /by 06/07/2026\n"
                        + "event team meeting /from 06/07/2026 14:00 /to 06/07/2026 15:30\n"
                        + "mark 1\n"
                        + "unmark 1\n", tasks);

        runCommands(parser, 5);

        assertEquals(3, tasks.getTaskListSize());
        assertEquals(Task.Status.INCOMPLETE, tasks.getTask(0).getStatus());
        assertEquals("return book (by: 06 July 2026 [11:59pm])", tasks.getTask(1).getTaskDescription());
        assertEquals("E", tasks.getTask(2).getMarker());
    }

    @Test
    public void reportsInvalidCommandsAndArguments() {
        Parser parser = parserFor("\nunknown\ntodo\nfind\nmark\n");

        String output = captureOutput(() -> runCommands(parser, 5));

        assertTrue(output.contains("Oops! Invalid task details."));
        assertTrue(output.contains("Oops! Invalid task index."));
        assertTrue(output.contains("Hmm, I don't recognise that command."));
    }

    @Test
    public void reportsInvalidDateAndEventRange() {
        TaskList tasks = new TaskList();
        Parser parser = parserFor(
                "deadline return book /by 31/02/2026\n"
                        + "event meeting /from 07/07/2026 /to 06/07/2026\n");

        String output = captureOutput(() -> runCommands(parser, 2));

        assertTrue(output.contains("Oops! Invalid date/time."));
        assertTrue(output.contains("Oops! An event must start before it ends."));
        assertEquals(0, tasks.getTaskListSize());
    }

    @Test
    public void savesAndReportsTerminationForExitCommand() {
        TaskList tasks = new TaskList();
        tasks.addTask(new ToDo("read book"));
        Path savePath = temporaryDirectory.resolve("tasks.txt");
        Parser parser = new Parser(new Scanner("bye\n"), new Storage(savePath.toString()), tasks);

        boolean isTerminated = parser.parse();

        assertTrue(isTerminated);
        assertTrue(savePath.toFile().exists());
    }

    @Test
    public void continuesAfterNonTerminatingCommand() {
        Parser parser = parserFor("list\n");

        String output = captureOutput(parser::parse);

        assertFalse(output.isEmpty());
        assertTrue(output.contains("Hmm, your task list is empty."));
    }

    @Test
    public void displaysListFindAndHelpResponses() {
        TaskList tasks = new TaskList();
        tasks.addTask(new ToDo("read book"));
        Parser parser = parserFor("list\nfind book\nhelp\n", tasks);

        String output = captureOutput(() -> runCommands(parser, 3));

        assertTrue(output.contains("1. [T][ ] read book"));
        assertTrue(output.contains("Hmm, these are the matching tasks:"));
        assertTrue(output.contains("Here are the commands you can use:"));
    }

    private Parser parserFor(String input) {
        return parserFor(input, new TaskList());
    }

    private Parser parserFor(String input, TaskList tasks) {
        return new Parser(new Scanner(input),
                new Storage(temporaryDirectory.resolve("tasks.txt").toString()), tasks);
    }

    private void runCommands(Parser parser, int commandCount) {
        for (int i = 0; i < commandCount; i++) {
            parser.parse();
        }
    }

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            action.run();
        } finally {
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
