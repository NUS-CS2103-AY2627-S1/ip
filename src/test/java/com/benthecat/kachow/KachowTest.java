package com.benthecat.kachow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.benthecat.kachow.ui.printer.Printer;

/** Tests command recovery and agreement between in-memory tasks and saved data. */
class KachowTest {
    @TempDir
    Path tempDirectory;

    @Test
    void handleUserInput_saveDenied_rollsBackEveryMutationAndAllowsRetry() throws IOException {
        assumeTrue(Files.getFileStore(tempDirectory).supportsFileAttributeView("posix"));
        Path dataFile = tempDirectory.resolve("kachow.txt");
        String original = "T | 0 | first\nT | 1 | second\n";
        Files.writeString(dataFile, original);
        RecordingPrinter printer = new RecordingPrinter();
        Kachow kachow = new Kachow(dataFile.toString(), printer);
        Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(dataFile);
        try {
            Files.setPosixFilePermissions(dataFile, PosixFilePermissions.fromString("r--r--r--"));
            assumeTrue(!Files.isWritable(dataFile), "Requires an account subject to file permissions.");
            for (String command : List.of("todo new", "mark 1", "unmark 2",
                    "edit 1 /description changed", "delete 1")) {
                printer.lines.clear();
                assertTrue(kachow.handleUserInput(command));
                assertTrue(printer.lines.getFirst().contains("No changes were applied."), command);
                assertEquals(original, Files.readString(dataFile));
                assertOriginalTasks(kachow, printer);
            }
        } finally {
            Files.setPosixFilePermissions(dataFile, permissions);
        }
        kachow.handleUserInput("mark 1");
        assertEquals("T | 1 | first\nT | 1 | second\n", Files.readString(dataFile));
    }

    @Test
    void handleUserInput_loadDenied_doesNotOverwriteUnreadData() throws IOException {
        assumeTrue(Files.getFileStore(tempDirectory).supportsFileAttributeView("posix"));
        Path dataFile = tempDirectory.resolve("kachow.txt");
        String original = "T | 0 | original\n";
        Files.writeString(dataFile, original);
        Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(dataFile);
        try {
            Files.setPosixFilePermissions(dataFile, PosixFilePermissions.fromString("---------"));
            assumeTrue(!Files.isReadable(dataFile), "Requires an account subject to file permissions.");
            RecordingPrinter printer = new RecordingPrinter();
            Kachow kachow = new Kachow(dataFile.toString(), printer);
            Files.setPosixFilePermissions(dataFile, permissions);
            kachow.handleUserInput("todo replacement");
            assertTrue(printer.lines.getFirst().contains("Saving is disabled"));
            assertEquals(original, Files.readString(dataFile));
        } finally {
            Files.setPosixFilePermissions(dataFile, permissions);
        }
    }

    @Test
    void handleUserInput_duplicateAddOrEdit_preservesDiskAndTaskStatus() throws IOException {
        Path dataFile = tempDirectory.resolve("kachow.txt");
        String original = "T | 0 | first\nT | 1 | second\n";
        Files.writeString(dataFile, original);
        RecordingPrinter printer = new RecordingPrinter();
        Kachow kachow = new Kachow(dataFile.toString(), printer);
        for (String command : List.of("todo FIRST", "edit 2 /description first")) {
            printer.lines.clear();
            kachow.handleUserInput(command);
            assertTrue(printer.lines.getFirst().contains("already on the grid as task 1"));
            assertEquals(original, Files.readString(dataFile));
            assertOriginalTasks(kachow, printer);
        }
    }

    @Test
    void handleUserInput_successfulLifecycle_persistsEveryMutationAndRestoresOnRestart() throws IOException {
        Path dataFile = tempDirectory.resolve("data/kachow.txt");
        RecordingPrinter printer = new RecordingPrinter();
        Kachow kachow = new Kachow(dataFile.toString(), printer);
        assertLifecycleMutationsPersist(kachow, dataFile);
        assertRestartRestoresTasks(dataFile, printer);
    }

    /** Checks the exact saved state after each operation in a successful task lifecycle. */
    private void assertLifecycleMutationsPersist(Kachow kachow, Path dataFile) throws IOException {
        String todo = "T | 0 | read book\n";
        String deadline = "D | 0 | submit report | 2026-09-11T18:00\n";
        String markedDeadline = "D | 1 | submit report | 2026-09-11T18:00\n";
        String event = "E | 0 | planning | 2026-09-11T10:00 | 2026-09-11T11:00\n";
        String editedEvent = "E | 0 | meeting | 2026-09-11T10:00 | 2026-09-11T12:00\n";
        assertCommandPersists(kachow, dataFile, "todo read book", todo);
        assertCommandPersists(kachow, dataFile, "deadline submit report /by 2026-09-11 1800", todo + deadline);
        assertCommandPersists(kachow, dataFile, "event planning /from 2026-09-11 1000 /to 1100",
                todo + deadline + event);
        assertCommandPersists(kachow, dataFile, "mark 2", todo + markedDeadline + event);
        assertCommandPersists(kachow, dataFile, "unmark 2", todo + deadline + event);
        assertCommandPersists(kachow, dataFile, "edit 3 /description meeting /to 1200", todo + deadline + editedEvent);
        assertCommandPersists(kachow, dataFile, "delete 1", deadline + editedEvent);
    }

    /** Executes a command and checks its complete saved representation. */
    private void assertCommandPersists(Kachow kachow, Path dataFile, String command, String expected)
            throws IOException {
        assertTrue(kachow.handleUserInput(command), command);
        assertEquals(expected, Files.readString(dataFile), command);
    }

    /** Checks restored list output, deletion of every task, and restart with an empty file. */
    private void assertRestartRestoresTasks(Path dataFile, RecordingPrinter printer) throws IOException {
        printer.lines.clear();
        Kachow restarted = new Kachow(dataFile.toString(), printer);
        restarted.handleUserInput("list");
        assertEquals(List.of(
                "Crew chief's clipboard! Here are all your tasks, from first lap to finish line:",
                "1.[D][ ] submit report (by: Sep 11 2026, 6:00 PM)",
                "2.[E][ ] meeting (from: Sep 11 2026, 10:00 AM to: Sep 11 2026, 12:00 PM)"), printer.lines);
        restarted.handleUserInput("delete 2");
        restarted.handleUserInput("delete 1");
        assertEquals("", Files.readString(dataFile));
        printer.lines.clear();
        new Kachow(dataFile.toString(), printer).handleUserInput("list");
        assertEquals(List.of("Quiet as Radiator Springs before sunrise! Add a task with todo, deadline, or event."),
                printer.lines);
    }

    @Test
    void handleUserInput_searchAndDateLookup_keepOriginalNumbersWithoutWriting() throws IOException {
        Path dataFile = tempDirectory.resolve("kachow.txt");
        String original = "T | 1 | read book\nD | 0 | submit report | 2026-09-11\n"
                + "E | 0 | book club | 2026-09-11T10:00 | 2026-09-11T11:00\n";
        Files.writeString(dataFile, original);
        RecordingPrinter printer = new RecordingPrinter();
        Kachow kachow = new Kachow(dataFile.toString(), printer);
        kachow.handleUserInput("find BOOK");
        assertEquals(List.of("Mater found 'em! Here are the tasks that match your search:",
                "1.[T][X] read book",
                "3.[E][ ] book club (from: Sep 11 2026, 10:00 AM to: Sep 11 2026, 11:00 AM)"), printer.lines);
        printer.lines.clear();
        kachow.handleUserInput("on 2026-09-11");
        assertEquals(List.of("Sally's road map! Here are the deadlines and events on Sep 11 2026:",
                "2.[D][ ] submit report (by: Sep 11 2026)",
                "3.[E][ ] book club (from: Sep 11 2026, 10:00 AM to: Sep 11 2026, 11:00 AM)"), printer.lines);
        assertEquals(original, Files.readString(dataFile));
    }

    @Test
    void handleUserInput_invalidCommands_reportsErrorAndContinuesWithoutSaving() throws IOException {
        Path dataFile = tempDirectory.resolve("kachow.txt");
        RecordingPrinter printer = new RecordingPrinter();
        Kachow kachow = new Kachow(dataFile.toString(), printer);
        for (String command : List.of("", "dance", "bye extra", "list extra", "todo", "mark 1",
                "unmark 1", "delete 1", "edit 1 /description new", "find", "on tomorrow")) {
            printer.lines.clear();
            int previousFlushes = printer.flushCount;
            assertTrue(kachow.handleUserInput(command), command);
            assertEquals(previousFlushes + 1, printer.flushCount);
            assertEquals(1, printer.lines.size());
            assertTrue(printer.lines.getFirst().startsWith("Pit stop, buddy! Let's get you rolling."), command);
            assertTrue(Files.notExists(dataFile));
        }
        printer.lines.clear();
        assertFalse(kachow.handleUserInput("bye"));
        assertEquals(List.of("Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!",
                "____________________________________________________________"), printer.lines);
    }

    @Test
    void sendWelcomeMessage_invalidStoredData_reportsRecoveryAndFlushes() throws IOException {
        Path dataFile = tempDirectory.resolve("kachow.txt");
        Files.writeString(dataFile, "bad record\n");
        RecordingPrinter printer = new RecordingPrinter();
        Kachow kachow = new Kachow(dataFile.toString(), printer);
        kachow.sendWelcomeMessage();
        assertEquals(1, printer.flushCount);
        assertTrue(printer.lines.get(1).startsWith("Ka-chow! I'm Kachow"));
        assertEquals("Pit stop, buddy! Let's get you rolling. Task data on line 1 of "
                + dataFile + " is invalid.", printer.lines.get(3));
        assertEquals("Saving is disabled. Repair the task file or restore a backup, then restart Kachow.",
                printer.lines.get(4));
        kachow.handleUserInput("todo replacement");
        assertEquals("bad record\n", Files.readString(dataFile));
    }

    @Test
    void handleUserInput_externalFileChange_rejectsMutationAndRetainsLoadedTasks() throws IOException {
        Path dataFile = tempDirectory.resolve("kachow.txt");
        Files.writeString(dataFile, "T | 0 | first\nT | 1 | second\n");
        RecordingPrinter printer = new RecordingPrinter();
        Kachow kachow = new Kachow(dataFile.toString(), printer);
        Files.writeString(dataFile, "T | 0 | external\n");
        kachow.handleUserInput("mark 1");
        assertEquals(List.of("Pit stop, buddy! Let's get you rolling. The task file changed outside Kachow. "
                + "No changes were applied. Restart Kachow to load the latest tasks."), printer.lines);
        assertOriginalTasks(kachow, printer);
        assertEquals("T | 0 | external\n", Files.readString(dataFile));
    }

    /** Checks the complete list reply after a rejected mutation. */
    private void assertOriginalTasks(Kachow kachow, RecordingPrinter printer) {
        printer.lines.clear();
        kachow.handleUserInput("list");
        assertEquals(List.of(
                "Crew chief's clipboard! Here are all your tasks, from first lap to finish line:",
                "1.[T][ ] first", "2.[T][X] second"), printer.lines);
    }

    /** Captures shared application replies without a console or JavaFX dependency. */
    private static class RecordingPrinter implements Printer {
        private final List<String> lines = new ArrayList<>();
        private int flushCount;

        @Override
        public void addData(String message) {
            lines.add(message);
        }

        @Override
        public void outputData() {
            flushCount++;
            // Replies remain available until the test clears them.
        }
    }
}
