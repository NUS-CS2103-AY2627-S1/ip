package bot67.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import bot67.storage.Storage;
import bot67.task.TaskList;
import bot67.ui.Ui;

class ExitCommandTest {
    @Test
    void exitCommand_reportsExitAndGoodbye() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(output));
            ExitCommand command = new ExitCommand("art");
            command.execute(new TaskList(List.of()), new Ui(), new Storage());
            assertTrue(command.isExit());
            assertTrue(output.toString().contains("Bye. Hope to see you again soon."));
            assertTrue(output.toString().contains("art"));
        } finally {
            System.setOut(original);
        }
    }
}
