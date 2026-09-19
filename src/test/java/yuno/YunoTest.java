package yuno;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import yuno.command.CommandResult;
import yuno.exception.FileStorageException;
import yuno.exception.InvalidTaskNumberException;
import yuno.parser.Parser;
import yuno.storage.Storage;
import yuno.task.TaskList;
import yuno.ui.Ui;

class YunoTest {
    @TempDir
    private Path tempDir;
    private final InputStream originalInput = System.in;
    private final PrintStream originalOutput = System.out;
    private ByteArrayOutputStream output;

    @BeforeEach
    void redirectStandardOutput() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreSystemStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    void run_invalidThenValidCommands_reportsErrorAndContinuesUntilBye()
            throws FileStorageException, InvalidTaskNumberException, IOException {
        System.setIn(new ByteArrayInputStream(
                "nonsense\ntodo read book\nbye\n".getBytes(StandardCharsets.UTF_8)));
        Ui ui = new Ui();
        TaskList taskList = new TaskList();
        Path filePath = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(filePath);
        Yuno yuno = new Yuno(ui, new Parser(), storage, taskList);

        yuno.run();

        String consoleOutput = output.toString(StandardCharsets.UTF_8);
        assertEquals(1, taskList.getCount());
        assertEquals("read book", taskList.getTask(1).getDescription());
        assertEquals("T |   | read book", Files.readString(filePath).stripTrailing());
        assertTrue(consoleOutput.contains(
                "Did you look at what you typed? That's just a random command."));
        assertTrue(consoleOutput.contains("Added:"));
        assertTrue(consoleOutput.contains("Finally! Bye. I'm leaving!"));
    }

    @Test
    void handleCommand_validTodo_addsTaskAndContinues()
            throws FileStorageException, InvalidTaskNumberException {
        Ui ui = new Ui();
        TaskList taskList = new TaskList();
        Storage storage = new Storage(tempDir.resolve("tasks.txt"));
        Yuno yuno = new Yuno(ui, new Parser(), storage, taskList);

        CommandResult commandResult = yuno.handleCommand("todo read book");

        assertEquals(CommandResult.CONTINUE, commandResult);
        assertEquals(1, taskList.getCount());
        assertEquals("read book", taskList.getTask(1).getDescription());
        assertEquals(
                "Added:\n[T][ ] read book\nJust another task you won't finish.",
                ui.getResponse());
    }

    @Test
    void handleCommand_unknownCommand_reportsErrorAndContinues()
            throws FileStorageException {
        Ui ui = new Ui();
        TaskList taskList = new TaskList();
        Storage storage = new Storage(tempDir.resolve("tasks.txt"));
        Yuno yuno = new Yuno(ui, new Parser(), storage, taskList);

        CommandResult commandResult = yuno.handleCommand("nonsense");

        assertEquals(CommandResult.CONTINUE, commandResult);
        assertEquals(0, taskList.getCount());
        assertEquals(
                "Did you look at what you typed? That's just a random command.",
                ui.getResponse());
    }

    @Test
    void handleCommand_invalidFormat_reportsErrorAndContinues()
            throws FileStorageException {
        Ui ui = new Ui();
        TaskList taskList = new TaskList();
        Storage storage = new Storage(tempDir.resolve("tasks.txt"));
        Yuno yuno = new Yuno(ui, new Parser(), storage, taskList);

        CommandResult commandResult = yuno.handleCommand("todo");

        assertEquals(CommandResult.CONTINUE, commandResult);
        assertEquals(0, taskList.getCount());
        assertEquals("If you have no task, please don't bother me.", ui.getResponse());
    }

    @Test
    void handleCommand_saveFailure_reportsErrorAndKeepsTasks()
            throws FileStorageException, InvalidTaskNumberException {
        Ui ui = new Ui();
        TaskList taskList = new TaskList();
        taskList.addTask("existing task");
        Storage storage = new Storage(tempDir.resolve("tasks.txt")) {
            @Override
            public void save(TaskList taskList) throws FileStorageException {
                throw new FileStorageException("Simulated save failure");
            }
        };
        Yuno yuno = new Yuno(ui, new Parser(), storage, taskList);

        CommandResult commandResult = yuno.handleCommand("todo new task");

        assertEquals(CommandResult.CONTINUE, commandResult);
        assertEquals(1, taskList.getCount());
        assertEquals("existing task", taskList.getTask(1).getDescription());
        assertEquals("Simulated save failure", ui.getResponse());
    }

    @Test
    void handleCommand_afterSaveFailure_acceptsNextCommand()
            throws FileStorageException, InvalidTaskNumberException {
        Ui ui = new Ui();
        TaskList taskList = new TaskList();
        Storage storage = new Storage(tempDir.resolve("tasks.txt")) {
            private boolean shouldFail = true;

            @Override
            public void save(TaskList taskList)
                    throws FileStorageException, InvalidTaskNumberException {
                if (shouldFail) {
                    shouldFail = false;
                    throw new FileStorageException("Simulated save failure");
                }
                super.save(taskList);
            }
        };
        Yuno yuno = new Yuno(ui, new Parser(), storage, taskList);

        yuno.handleCommand("todo failed task");
        CommandResult commandResult = yuno.handleCommand("todo saved task");

        assertEquals(CommandResult.CONTINUE, commandResult);
        assertEquals(1, taskList.getCount());
        assertEquals("saved task", taskList.getTask(1).getDescription());
    }

    @Test
    void handleCommand_bye_reportsFarewellAndStops() throws FileStorageException {
        Ui ui = new Ui();
        TaskList taskList = new TaskList();
        Storage storage = new Storage(tempDir.resolve("tasks.txt"));
        Yuno yuno = new Yuno(ui, new Parser(), storage, taskList);

        CommandResult commandResult = yuno.handleCommand("bye");

        assertEquals(CommandResult.EXIT, commandResult);
        assertEquals("Finally! Bye. I'm leaving!", ui.getResponse());
    }

    @Test
    void run_byeDoesNotReadAnotherCommand() throws FileStorageException {
        class CountingUi extends Ui {
            private int readCount;

            @Override
            public String readCommand() {
                readCount++;
                if (readCount > 1) {
                    throw new AssertionError("Yuno should stop reading after bye");
                }
                return "bye";
            }
        }

        CountingUi ui = new CountingUi();
        Yuno yuno = new Yuno(
                ui,
                new Parser(),
                new Storage(tempDir.resolve("tasks.txt")),
                new TaskList());

        yuno.run();

        assertEquals(1, ui.readCount);
    }
}
