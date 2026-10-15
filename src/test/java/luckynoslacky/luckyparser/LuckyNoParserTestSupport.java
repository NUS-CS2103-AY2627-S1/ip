package luckynoslacky.luckyparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import luckynoslacky.luckyexception.LuckyNoInputException;
import luckynoslacky.luckystorage.CsvSaver;
import luckynoslacky.luckytask.TaskMaster;

/** Shared deterministic parser fixtures and assertions. */
abstract class LuckyNoParserTestSupport {
    protected static final Clock TEST_CLOCK = Clock.fixed(
            Instant.parse("2026-08-25T10:00:00Z"), ZoneId.of("UTC"));

    @TempDir
    protected Path tempDir;

    protected DateTimeParser parser;
    protected TaskMaster taskMaster;
    protected LuckyNoParser scanner;

    /** Creates deterministic parser and task-list dependencies for each test. */
    @BeforeEach
    protected void setUp() {
        parser = new DateTimeParser(TEST_CLOCK);
        taskMaster = new TaskMaster(
                100,
                new CsvSaver(tempDir.resolve("tasks.csv")));
        scanner = new LuckyNoParser(parser, taskMaster);
    }

    /** Asserts that parsing an input returns the expected user-facing error. */
    protected void assertInputError(String message, String userInput, int taskCount) {
        LuckyNoInputException error = assertThrows(
                LuckyNoInputException.class, () -> scanner.parseCommand(
                        userInput, taskCount));
        assertEquals(message, error.getMessage());
    }
}
