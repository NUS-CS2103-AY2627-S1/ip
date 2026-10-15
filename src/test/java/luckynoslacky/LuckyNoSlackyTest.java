package luckynoslacky;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import luckynoslacky.luckyexception.LuckyNoConfigurationException;
import luckynoslacky.luckyexception.LuckyNoStorageException;
import luckynoslacky.luckyparser.DateTimeParser;
import luckynoslacky.luckyresponse.LuckyNoQuips;
import luckynoslacky.luckyresponse.LuckyNoTaskResponses;
import luckynoslacky.luckystorage.CsvSaver;
import luckynoslacky.luckytask.Task;
import luckynoslacky.luckytask.TaskList;
import luckynoslacky.luckyui.LuckyNoCli;

/**
 * Tests the public chatbot facade used by the graphical interface.
 */
class LuckyNoSlackyTest {
    private static final String FIXED_NOW_PROPERTY =
            "luckynoslacky.fixedNow";

    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream capturedOutput;
    private String originalFixedNow;

    /** Redirects standard input and output before each CLI integration test. */
    @BeforeEach
    void setUp() {
        originalInput = System.in;
        originalOutput = System.out;
        originalFixedNow = System.getProperty(FIXED_NOW_PROPERTY);
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(
                capturedOutput, true, StandardCharsets.UTF_8));
    }

    /** Restores standard input and output after each CLI integration test. */
    @AfterEach
    void tearDown() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
        if (originalFixedNow == null) {
            System.clearProperty(FIXED_NOW_PROPERTY);
        } else {
            System.setProperty(FIXED_NOW_PROPERTY, originalFixedNow);
        }
    }

    /** Verifies that invalid GUI input is returned as a chatbot reply. */
    @Test
    void getResponse_unknownCommand_returnsUnknownCommandMessage() {
        LuckyNoSlacky chatbot = new LuckyNoSlacky();
        CommandResult response = chatbot.getResponse("unknown command");

        assertEquals(
                LuckyNoQuips.unknownCommandMessage(),
                response.message());
        assertFalse(response.shouldExit());
        assertEquals(ResponseTone.WARNING, response.tone());
        assertInstanceOf(TextContent.class, response.content());
    }

    /** Verifies that malformed input returns the exact parser error response. */
    @Test
    void getResponse_missingTaskDescription_returnsExactInputError() {
        LuckyNoSlacky chatbot = new LuckyNoSlacky();

        CommandResult response = chatbot.getResponse("todo");

        assertEquals("You don't tell me what to do how I know what to do???",
                response.message());
        assertFalse(response.shouldExit());
    }

    /** Verifies that the bye command returns an exit signal to the GUI. */
    @Test
    void getResponse_byeCommand_returnsGoodbyeAndExitStatus() {
        LuckyNoSlacky chatbot = new LuckyNoSlacky();

        CommandResult response = chatbot.getResponse("bye");

        assertEquals(LuckyNoQuips.goodbye(), response.message());
        assertTrue(response.shouldExit());
        assertEquals(ResponseTone.NEUTRAL, response.tone());
        assertInstanceOf(TextContent.class, response.content());
    }

    /** Verifies that the chatbot rejects a missing date/time parser. */
    @Test
    void construct_nullDateTimeParser_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new LuckyNoSlacky(null));
    }

    /** Verifies that the chatbot rejects a missing CSV saver dependency. */
    @Test
    void construct_nullCsvSaver_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new LuckyNoSlacky(new DateTimeParser(), null));
    }

    /** Verifies a valid fixed-now property creates the configured clock. */
    @Test
    void createDateTimeParser_validFixedNow_usesConfiguredTime() {
        System.setProperty(
                FIXED_NOW_PROPERTY, "2026-08-25T10:00:00Z");

        assertEquals(
                LocalDateTime.of(2026, 8, 25, 10, 0),
                LuckyNoSlacky.createDateTimeParser().now());
    }

    /** Verifies a blank fixed-now property falls back to the system clock. */
    @Test
    void createDateTimeParser_blankFixedNow_usesSystemClock() {
        System.setProperty(FIXED_NOW_PROPERTY, " ");

        assertNotNull(LuckyNoSlacky.createDateTimeParser());
    }

    /** Verifies an invalid fixed-now property becomes a configuration error. */
    @Test
    void createDateTimeParser_invalidFixedNow_throwsConfigurationException() {
        System.setProperty(FIXED_NOW_PROPERTY, "not-a-time");

        LuckyNoConfigurationException exception = assertThrows(
                LuckyNoConfigurationException.class,
                LuckyNoSlacky::createDateTimeParser);

        assertEquals(
                "Fixed application time must be an ISO-8601 instant.",
                exception.getMessage());
        assertInstanceOf(DateTimeParseException.class, exception.getCause());
    }

    /** Verifies invalid startup configuration is reported before returning. */
    @Test
    void main_invalidFixedNow_reportsErrorAndReturns() {
        System.setProperty(FIXED_NOW_PROPERTY, "not-a-time");

        LuckyNoSlacky.main(new String[0]);

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains(LuckyNoQuips.greeting()));
        assertTrue(output.contains(
                LuckyNoQuips.configurationErrorMessage()));
        assertFalse(output.contains(LuckyNoQuips.goodbye()));
    }

    /** Verifies that empty input exits with one goodbye message. */
    @Test
    void run_emptyInput_displaysGreetingAndSingleGoodbye() {
        setInput("");
        LuckyNoSlacky chatbot = new LuckyNoSlacky(
                new DateTimeParser(), new EmptySaver());

        LuckyNoSlacky.run(chatbot, new LuckyNoCli());

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        int greetingIndex = output.indexOf(LuckyNoQuips.greeting());
        int goodbyeIndex = output.indexOf(LuckyNoQuips.goodbye());
        assertTrue(greetingIndex >= 0);
        assertTrue(goodbyeIndex > greetingIndex);
        assertEquals(1, countOccurrences(output, LuckyNoQuips.goodbye()));
    }

    /** Verifies that commands before EOF are processed before goodbye. */
    @Test
    void run_commandThenEndOfFile_processesCommandAndDisplaysSingleGoodbye() {
        setInput("todo task before eof\n");
        LuckyNoSlacky chatbot = new LuckyNoSlacky(
                new DateTimeParser(), new EmptySaver());

        LuckyNoSlacky.run(chatbot, new LuckyNoCli());

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        int taskIndex = output.indexOf("task before eof");
        int goodbyeIndex = output.indexOf(LuckyNoQuips.goodbye());
        assertTrue(taskIndex >= 0);
        assertTrue(goodbyeIndex > taskIndex);
        assertEquals(1, countOccurrences(output, LuckyNoQuips.goodbye()));
    }

    /** Verifies that an invalid command before EOF still exits cleanly. */
    @Test
    void run_invalidCommandThenEndOfFile_displaysErrorAndSingleGoodbye() {
        setInput("unknown command\n");
        LuckyNoSlacky chatbot = new LuckyNoSlacky(
                new DateTimeParser(), new EmptySaver());

        LuckyNoSlacky.run(chatbot, new LuckyNoCli());

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        int errorIndex = output.indexOf(
                LuckyNoQuips.unknownCommandMessage());
        int goodbyeIndex = output.indexOf(LuckyNoQuips.goodbye());
        assertTrue(errorIndex >= 0);
        assertTrue(goodbyeIndex > errorIndex);
        assertEquals(1, countOccurrences(output, LuckyNoQuips.goodbye()));
    }

    /** Verifies that a load warning precedes goodbye when input reaches EOF. */
    @Test
    void run_loadFailureThenEndOfFile_displaysWarningAndSingleGoodbye() {
        setInput("");
        LuckyNoSlacky chatbot = new LuckyNoSlacky(
                new DateTimeParser(), new LoadFailingSaver());

        LuckyNoSlacky.run(chatbot, new LuckyNoCli());

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        int warningIndex = output.indexOf(LuckyNoQuips.loadErrorMessage());
        int goodbyeIndex = output.indexOf(LuckyNoQuips.goodbye());
        assertTrue(warningIndex >= 0);
        assertTrue(goodbyeIndex > warningIndex);
        assertEquals(1, countOccurrences(output, LuckyNoQuips.goodbye()));
    }

    /** Verifies that explicit bye produces one goodbye message. */
    @Test
    void run_explicitBye_displaysSingleGoodbye() {
        setInput("bye\n");
        LuckyNoSlacky chatbot = new LuckyNoSlacky(
                new DateTimeParser(), new EmptySaver());

        LuckyNoSlacky.run(chatbot, new LuckyNoCli());

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        assertEquals(1, countOccurrences(output, LuckyNoQuips.goodbye()));
    }

    /** Verifies that a load failure leaves the chatbot in degraded mode. */
    @Test
    void construct_loadFailure_setsLoadErrorState() {
        LuckyNoSlacky chatbot = new LuckyNoSlacky(
                new DateTimeParser(), new LoadFailingSaver());

        assertTrue(chatbot.hasLoadError());
        assertEquals(
                LuckyNoTaskResponses.emptyTaskListMessage(),
                chatbot.getResponse("list").message());
    }

    /** Verifies that the CLI continues and saves after a load failure. */
    @Test
    void run_loadFailure_processesCommandsAndSavesTasks() {
        System.setIn(new java.io.ByteArrayInputStream(
                "list\ntodo buy milk\nbye\n".getBytes(StandardCharsets.UTF_8)));
        LoadFailingSaver saver = new LoadFailingSaver();
        LuckyNoSlacky chatbot = new LuckyNoSlacky(
                new DateTimeParser(), saver);

        LuckyNoSlacky.run(chatbot, new LuckyNoCli());

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        int loadErrorIndex = output.indexOf(LuckyNoQuips.loadErrorMessage());
        int emptyListIndex = output.indexOf(
                LuckyNoTaskResponses.emptyTaskListMessage());
        assertTrue(loadErrorIndex >= 0);
        assertTrue(emptyListIndex > loadErrorIndex);
        assertEquals(
                loadErrorIndex,
                output.lastIndexOf(LuckyNoQuips.loadErrorMessage()));
        assertTrue(output.contains(LuckyNoQuips.goodbye()));
        assertTrue(saver.wasSaveCalled());
    }

    /** Verifies every task-creation command receives the capacity message. */
    @Test
    void getResponse_fullTaskList_rejectsAllCreationCommands() {
        List<String> commands = List.of(
                "todo overflow",
                "deadline overflow /by 26 Aug 2099",
                "event overflow /from 26 Aug 2099 /to 27 Aug 2099");

        for (String command : commands) {
            LoadFailingSaver saver = new LoadFailingSaver();
            LuckyNoSlacky chatbot = new LuckyNoSlacky(
                    new DateTimeParser(), saver);

            for (int taskNumber = 0; taskNumber < 100; taskNumber++) {
                chatbot.getResponse("todo task " + taskNumber);
            }

            CommandResult response = chatbot.getResponse(command);

            assertEquals(
                    LuckyNoQuips.taskLimitMessage(), response.message());
            assertFalse(response.shouldExit());
            assertEquals(100, saver.getSaveCount());
        }
    }

    /** Verifies that the CLI remains active after a capacity error. */
    @Test
    void run_fullTaskList_continuesAfterCapacityError() {
        StringBuilder input = new StringBuilder();
        for (int taskNumber = 0; taskNumber < 100; taskNumber++) {
            input.append("todo task ").append(taskNumber).append('\n');
        }
        input.append("todo overflow\nbye\n");
        System.setIn(new java.io.ByteArrayInputStream(
                input.toString().getBytes(StandardCharsets.UTF_8)));
        LoadFailingSaver saver = new LoadFailingSaver();
        LuckyNoSlacky chatbot = new LuckyNoSlacky(
                new DateTimeParser(), saver);

        LuckyNoSlacky.run(chatbot, new LuckyNoCli());

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains(LuckyNoQuips.taskLimitMessage()));
        assertTrue(output.contains(LuckyNoQuips.goodbye()));
        assertEquals(100, saver.getSaveCount());
    }

    /** Supplies deterministic standard input to a CLI integration test. */
    private void setInput(String input) {
        System.setIn(new ByteArrayInputStream(
                input.getBytes(StandardCharsets.UTF_8)));
    }

    /** Counts non-overlapping occurrences of a message in captured output. */
    private static int countOccurrences(String text, String message) {
        int count = 0;
        int searchStart = 0;
        int messageIndex;
        while ((messageIndex = text.indexOf(message, searchStart)) >= 0) {
            count++;
            searchStart = messageIndex + message.length();
        }
        return count;
    }

    /** Simulates an empty task store without accessing the repository data file. */
    private static final class EmptySaver extends CsvSaver {
        /** Returns an empty task list for isolated CLI tests. */
        @Override
        public List<Task> load() {
            return List.of();
        }

        /** Ignores saves because these tests focus on CLI termination. */
        @Override
        public void save(TaskList taskList) {
        }
    }

    /** Simulates a storage source that cannot load but can save new tasks. */
    private static final class LoadFailingSaver extends CsvSaver {
        private boolean saveCalled;
        private int saveCount;

        @Override
        public List<Task> load() {
            throw new LuckyNoStorageException("Simulated load failure.");
        }

        @Override
        public void save(TaskList taskList) {
            saveCalled = true;
            saveCount++;
        }

        /** Returns whether a task mutation attempted to save. */
        boolean wasSaveCalled() {
            return saveCalled;
        }

        /** Returns the number of save attempts made by the chatbot. */
        int getSaveCount() {
            return saveCount;
        }
    }
}
