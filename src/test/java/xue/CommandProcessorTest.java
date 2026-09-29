package xue;

import org.junit.jupiter.api.Test;
import xue.storage.Storage;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests command processing independently from console and GUI presentation. */
class CommandProcessorTest {
    private CommandProcessor createProcessor() throws Exception {
        Path directory = Files.createTempDirectory("xue-test");
        return new CommandProcessor(new Storage(directory.resolve("duke.txt")));
    }

    @Test
    void process_todo_returnsAddedTask() throws Exception {
        assertTrue(createProcessor().process("todo learn JavaFX").contains("learn JavaFX"));
    }

    @Test
    void process_deadlineAndEvent_returnsDateDetails() throws Exception {
        CommandProcessor processor = createProcessor();
        assertTrue(processor.process("deadline submit report /by Friday").contains("(by: Friday)"));
        assertTrue(processor.process("event meeting /from 2pm /to 3pm").contains("from: 2pm to: 3pm"));
    }

    @Test
    void process_listAndFind_returnsExpectedTasks() throws Exception {
        CommandProcessor processor = createProcessor();
        processor.process("todo read book");
        processor.process("todo buy milk");
        assertTrue(processor.process("list").contains("1.[T][ ] read book"));
        assertTrue(processor.process("find BOOK").contains("1.[T][ ] read book"));
    }

    @Test
    void process_markAndUnmark_updatesTaskStatus() throws Exception {
        CommandProcessor processor = createProcessor();
        processor.process("todo read book");
        assertTrue(processor.process("mark 1").contains("[X] read book"));
        assertTrue(processor.process("unmark 1").contains("[ ] read book"));
    }

    @Test
    void process_delete_removesTask() throws Exception {
        CommandProcessor processor = createProcessor();
        processor.process("todo remove me");
        assertTrue(processor.process("delete 1").contains("Now you have 0 tasks"));
        assertTrue(processor.process("list").endsWith(":\n"));
    }

    @Test
    void process_undoRestoresPreviousTaskState() throws Exception {
        CommandProcessor processor = createProcessor();
        processor.process("todo first");
        processor.process("todo second");

        assertTrue(processor.process("undo").contains("1.[T][ ] first"));
        assertTrue(processor.process("list").contains("1.[T][ ] first"));
        assertTrue(!processor.process("list").contains("second"));
    }

    @Test
    void process_undoAndRedo_restoreDeletedTaskAtOriginalPosition() throws Exception {
        CommandProcessor processor = createProcessor();
        processor.process("todo first");
        processor.process("todo second");
        processor.process("todo third");
        processor.process("delete 2");

        assertTrue(processor.process("undo").contains("2.[T][ ] second"));
        assertTrue(processor.process("redo").contains("2.[T][ ] third"));
    }

    @Test
    void process_undoWithoutHistory_returnsError() throws Exception {
        assertEquals("OOPS!!! Hey you need to DO before you can undo!", createProcessor().process("undo"));
    }

    @Test
    void process_newMutationClearsRedoHistory() throws Exception {
        CommandProcessor processor = createProcessor();
        processor.process("todo first");
        processor.process("undo");
        processor.process("todo second");

        assertEquals("OOPS!!! There is nothing to redo.", processor.process("redo"));
    }

    @Test
    void process_historyCommandsRejectArguments() throws Exception {
        CommandProcessor processor = createProcessor();

        assertEquals("OOPS!!! The undo command does not accept any arguments.", processor.process("undo 1"));
        assertEquals("OOPS!!! The redo command does not accept any arguments.", processor.process("redo 1"));
    }

    @Test
    void process_invalidCommand_returnsError() throws Exception {
        assertTrue(createProcessor().process("unknown").startsWith("OOPS!!!"));
    }

    @Test
    void processor_persistsTasksAcrossInstances() throws Exception {
        Path file = Files.createTempDirectory("xue-test").resolve("duke.txt");
        new CommandProcessor(new Storage(file)).process("todo saved task");
        CommandProcessor restored = new CommandProcessor(new Storage(file));
        assertTrue(restored.process("list").contains("saved task"));
        assertNull(restored.getLoadError());
    }

    @Test
    void processor_exposesStorageLoadError() throws Exception {
        Path directory = Files.createTempDirectory("xue-test");
        CommandProcessor processor = new CommandProcessor(new Storage(directory));
        assertEquals("I could not read your saved tasks.", processor.getLoadError());
    }

    @Test
    void process_invalidDates_preservesStorageAndRedoHistory() throws Exception {
        Path file = Files.createTempDirectory("xue-test").resolve("duke.txt");
        CommandProcessor processor = new CommandProcessor(new Storage(file));
        processor.process("todo retained");
        processor.process("todo redo me");
        processor.process("undo");
        String saved = Files.readString(file);
        String listed = processor.process("list");
        String[] commands = {"deadline impossible /by 2026-09-31 1200",
            "deadline malformed /by 2026/09/24 1200",
            "event impossible /from 2026-09-31 1400 /to 2026-10-01 1600",
            "event backwards /from 2026-09-26 1600 /to 2026-09-25 1400"};
        for (String command : commands) {
            assertTrue(processor.process(command).startsWith("OOPS!!!"), command);
            assertEquals(saved, Files.readString(file));
            assertEquals(listed, processor.process("list"));
        }
        assertTrue(processor.process("redo").contains("2.[T][ ] redo me"));
    }

    @Test
    void processor_invalidSavedDates_warnsAndRetainsValidTasksWithoutRewritingFile() throws Exception {
        Path file = Files.createTempDirectory("xue-test").resolve("duke.txt");
        String records = "T | 1 | retained\n"
                + "D | 0 | impossible | 2026-09-31 1200\n"
                + "D | 0 | malformed | 2026/09/24 1200\n"
                + "E | 0 | impossible event | 2026-09-31 1400 | 2026-10-01 1600\n"
                + "E | 0 | backwards | 2026-09-26 1600 | 2026-09-25 1400\n"
                + "D | 0 | leap day | 2028-02-29 0000\n"
                + "E | 1 | overnight | 2026-09-30 2359 | 2026-10-01 0000\n";
        Files.writeString(file, records);
        CommandProcessor processor = new CommandProcessor(new Storage(file));
        assertEquals("Some saved tasks were invalid and were skipped.", processor.getLoadError());
        assertEquals("Here are your tasks. Yes, I did all the work for you:\n"
                + "1.[T][X] retained\n2.[D][ ] leap day (by: Feb 29 2028 12:00 AM)\n"
                + "3.[E][X] overnight (from: Sep 30 2026 11:59 PM to: Oct 01 2026 12:00 AM)",
                processor.process("list"));
        assertEquals(records, Files.readString(file));
        processor.process("mark 2");
        CommandProcessor restored = new CommandProcessor(new Storage(file));
        assertNull(restored.getLoadError());
        assertEquals(processor.process("list"), restored.process("list"));
    }
}
