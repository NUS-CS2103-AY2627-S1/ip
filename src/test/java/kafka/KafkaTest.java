package kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import kafka.exception.KafkaException;
import kafka.storage.TaskStorage;
import kafka.task.TaskList;

/**
 * Tests complete Kafka sessions across the user interface and application layers.
 */
class KafkaTest {
    private static final List<String> INPUT_ERROR_MESSAGES = List.of(
            "toodaloo. todo needs to hv a description alpha",
            "im deaddd. The deadline description cannot be empty.",
            "A deadline must include /by. Do you hate me?",
            "The deadline date or time cannot be empty alpha.",
            "are u event-ing new ways to tease me? The event description cannot be empty.",
            "An event must include /from followed by /to. Do you hate me?",
            "The event start or end cannot be empty my forbidden alpha~",
            "please gimme just a whole numberrr",
            "The task number must be at least 1 meow.",
            "There is no task with that number",
            "Task details cannot contain | sorryyy");

    @TempDir
    Path temporaryDirectory;

    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream capturedOutput;

    @BeforeEach
    void setUpConsole() {
        originalInput = System.in;
        originalOutput = System.out;
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreConsole() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    void mainAddsAndListsTypedTasks() {
        String output = runKafka("todo borrow book\n"
                + "deadline return book /by Sunday\n"
                + "event project meeting /from Mon 2pm /to 4pm\n"
                + "list\nbye\n");

        assertTrue(output.contains("1.[T][ ]  borrow book"));
        assertTrue(output.contains("2.[D][ ]  return book  (by: Sunday)"));
        assertTrue(output.contains(
                "3.[E][ ]  project meeting  (from: Mon 2pm to: 1600)"));
        assertTrue(output.contains("Now you have 3 tasks in the list."));
    }

    @Test
    void mainMarksAndUnmarksTypedTask() {
        String output = runKafka("todo read book\nmark 1\nlist\nunmark 1\nlist\nbye\n");

        int markedPosition = output.indexOf("1.[T][X]  read book");
        int unmarkedPosition = output.indexOf("1.[T][ ]  read book", markedPosition);
        assertTrue(markedPosition >= 0, "The task should be marked");
        assertTrue(unmarkedPosition > markedPosition,
                "The task should later be unmarked");
    }

    @Test
    void mainDeletesTaskAndShiftsRemainingTasks() {
        String output = runKafka("todo first\n"
                + "deadline second /by Sunday\n"
                + "event third /from Monday /to Tuesday\n"
                + "delete 2\nlist\nbye\n");

        assertTrue(output.contains("Aight. I've yeeted this task:"));
        assertTrue(output.contains("[D][ ]  second  (by: Sunday)"));
        assertTrue(output.contains("1.[T][ ]  first"));
        assertTrue(output.contains("2.[E][ ]  third  (from: Monday to: Tuesday)"));
        assertFalse(output.contains("2.[D][ ]  second  (by: Sunday)"));
    }

    @Test
    void mainFindsAndPreservesOriginalTaskNumbers() {
        String output = runKafka("todo buy milk\n"
                + "todo read book\n"
                + "deadline return book /by June 6th\n"
                + "todo write essay\n"
                + "mark 2\nmark 3\nfind book\nbye\n");

        String normalizedOutput = output.replace("\r\n", "\n");
        assertTrue(normalizedOutput.contains("I worked hard to find the matching tasks in your list king:\n"
                + "2.[T][X]  read book\n"
                + "3.[D][X]  return book  (by: June 6th)\n"
                + "_".repeat(60)));
        assertFalse(output.contains("4.[T][ ]  write essay"));
    }

    @Test
    void invalidCommandsDoNotCorruptTaskState() {
        String output = runKafka("todo read book\n"
                + "deadline broken /by\n"
                + "event meeting /from Monday /to Tuesday\n"
                + "mark abc\n"
                + "mark 1\n"
                + "event broken /from /to Friday\n"
                + "unmark 3\n"
                + "deadline submit report /by Sunday\n"
                + "list\nbye\n");

        assertTrue(output.contains("1.[T][X]  read book"));
        assertTrue(output.contains(
                "2.[E][ ]  meeting  (from: Monday to: Tuesday)"));
        assertTrue(output.contains("3.[D][ ]  submit report  (by: Sunday)"));
        assertFalse(output.contains("4."));
        assertTrue(output.contains("deadline date or time cannot be empty"));
        assertTrue(output.contains("whole number"));
        assertTrue(output.contains("event start or end cannot be empty"));
        assertTrue(output.contains("There is no task with that number"));
    }

    @Test
    void unknownCommandDoesNotAddTask() {
        String output = runKafka("Event invalid /from Monday /to Tuesday\n"
                + "todo valid\nlist\nbye\n");

        assertTrue(output.contains("I don't know that command"));
        assertTrue(output.contains("1.[T][ ]  valid"));
        assertFalse(output.contains("2."));
    }

    @Test
    void getResponse_errorThenSuccess_returnsIndependentErrorFlags() {
        TaskStorage taskStorage = new TaskStorage(temporaryDirectory.resolve("tasks.txt"));
        Kafka kafka = new Kafka(taskStorage);

        KafkaResponse errorResponse = kafka.getResponse("todo");
        assertTrue(errorResponse.isError());

        KafkaResponse successResponse = kafka.getResponse("todo read book");
        assertFalse(successResponse.isError());
        assertTrue(errorResponse.isError());
    }

    @ParameterizedTest
    @ValueSource(strings = {"\t", "\u2003", "   "})
    void getResponse_whitespaceAroundCommandsAndArguments_preservesDescriptions(String whitespace) throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Kafka kafka = new Kafka(new TaskStorage(dataFile));
        assertFalse(kafka.getResponse(whitespace + "todo" + whitespace + "read  book" + whitespace).isError());
        assertTrue(kafka.getResponse(whitespace + "list" + whitespace).message()
                .contains("1.[T][ ] " + whitespace + "read  book" + whitespace));
        assertFalse(kafka.getResponse(whitespace + "rename" + whitespace + "1" + whitespace + "read  novel").isError());
        assertFalse(kafka.getResponse(whitespace + "mark" + whitespace + "1" + whitespace).isError());
        assertEquals(List.of("T | 1 | " + whitespace + "read  novel"), Files.readAllLines(dataFile));
        assertEquals(KafkaResponse.Action.EXIT, kafka.getResponse(whitespace + "bye" + whitespace).action());
    }

    @ParameterizedTest
    @ValueSource(strings = {"todo", "deadline", "event"})
    void getResponse_descriptionWhitespace_survivesEditsAndReload(String command) throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        TaskStorage storage = new TaskStorage(dataFile);
        Kafka kafka = new Kafka(storage);
        String description = "\t  read\t\tbook   \t";
        String timing = switch (command) {
            case "deadline" -> "/by\tFriday";
            case "event" -> "/from\tMonday\t/to\tTuesday";
            default -> "";
        };
        assertFalse(kafka.getResponse("  " + command + description + timing).isError());
        String originalRecord = Files.readAllLines(dataFile).getFirst();
        assertTrue(originalRecord.contains(" | " + description));
        assertEquals(originalRecord, storage.load().getTasks().getFirst().toDataString());
        for (String operation : List.of("mark 1", "unmark 1", "list", "find book")) {
            KafkaResponse response = kafka.getResponse(operation);
            assertFalse(response.isError());
            assertTrue(response.message().contains(description));
            assertTrue(new Kafka(storage).getResponse("list").message().contains(description));
        }
        String replacement = "\t renamed  book\t ";
        assertFalse(kafka.getResponse("rename\t1" + replacement).isError());
        assertTrue(new Kafka(storage).getResponse("list").message().contains(replacement));
    }

    @Test
    void getResponse_snoozeWithUnicodeWhitespace_updatesSchedule() {
        Kafka kafka = new Kafka(new TaskStorage(temporaryDirectory.resolve("tasks.txt")));
        kafka.getResponse("deadline report /by Sunday");

        assertFalse(kafka.getResponse("\u2003snooze\u20031\u2003/by\u2003Monday\u2003").isError());
        assertTrue(kafka.getResponse("list").message().contains("(by: Monday)"));
    }

    @Test
    void mainStopsImmediatelyOnBye() {
        String output = runKafka("bye\ntodo should not be added\n");

        assertTrue(output.contains("Bye babe~"));
        assertFalse(output.contains("I've added this task"));
    }

    @Test
    void tasksPersistAcrossSessions() {
        runKafka("todo remember me\nmark 1\nbye\n");

        String output = runKafka("list\nbye\n");

        assertTrue(output.contains("1.[T][X]  remember me"));
    }

    @Test
    void renameChangesTaskNameAndPersistsIt() {
        String renameOutput = runKafka("todo read book\nrename 1 read novel\nbye\n");

        assertTrue(renameOutput.contains("[T][ ]  read book"));
        assertTrue(renameOutput.contains("[T][ ]  read novel"));

        String listOutput = runKafka("list\nbye\n");
        assertTrue(listOutput.contains("1.[T][ ]  read novel"));
    }

    @Test
    void rejectingCorruptedFileOverwritePreservesFileAndShowsLocation() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "invalid saved task");

        String output = runKafka("no\n");

        assertEquals("invalid saved task", Files.readString(dataFile));
        assertTrue(output.contains("Your task data was not changed."));
        assertTrue(output.contains(dataFile.toAbsolutePath().toString()));
        assertFalse(output.contains("Starting with an empty list."));
    }

    @Test
    void approvingCorruptedFileOverwriteClearsFileAndContinues() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "invalid saved task");

        String output = runKafka("yes\nlist\nbye\n");

        assertEquals("", Files.readString(dataFile));
        assertTrue(output.contains("Starting with an empty list."));
        assertTrue(output.contains("You have no tasks lined up"));
        assertTrue(output.contains("Bye babe~"));
    }

    @Test
    void getResponse_corruptedFile_requestsRecoveryWithoutConsoleInput() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "invalid saved task");
        System.setIn(new InputStream() {
            @Override
            public int read() {
                throw new AssertionError("GUI responses must never read from the console");
            }
        });
        Kafka kafka = new Kafka(new TaskStorage(dataFile));

        KafkaResponse response = kafka.getResponse("list");

        assertTrue(response.isError());
        assertEquals(KafkaResponse.Action.CONFIRM_STORAGE_OVERWRITE, response.action());
        assertEquals("invalid saved task", Files.readString(dataFile));
        assertTrue(response.message().contains(dataFile.toAbsolutePath().toString()));
        assertEquals("", capturedOutput.toString(StandardCharsets.UTF_8));

        KafkaResponse recovery = kafka.recoverStorage();

        assertEquals("", Files.readString(dataFile));
        assertFalse(recovery.isError());
        assertTrue(kafka.getResponse("list").message().contains("You have no tasks lined up"));
    }

    @ParameterizedTest
    @CsvSource({
        "snooze 1 /by 2024-02-29 12pm, D | 1 |  report  | 29 Feb 2024 1200, "
                + "1.[D][X]  report  (by: 29 Feb 2024 1200)",
        "snooze 2 /from 9am, E | 1 |  meeting  | 0900 | Tuesday, 2.[E][X]  meeting  (from: 0900 to: Tuesday)",
        "snooze 2 /to 5pm, E | 1 |  meeting  | Monday | 1700, 2.[E][X]  meeting  (from: Monday to: 1700)",
        "snooze 2 /from 9am /to 5pm, E | 1 |  meeting  | 0900 | 1700, 2.[E][X]  meeting  (from: 0900 to: 1700)"
    })
    void getResponse_snooze_persistsScheduleAndCompletionAcrossSessions(String command,
            String expectedRecord, String expectedDisplay) throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Kafka kafka = new Kafka(new TaskStorage(dataFile));
        assertFalse(kafka.getResponse("deadline report /by Friday").isError());
        assertFalse(kafka.getResponse("event meeting /from Monday /to Tuesday").isError());
        assertFalse(kafka.getResponse("mark 1").isError());
        assertFalse(kafka.getResponse("mark 2").isError());

        KafkaResponse response = kafka.getResponse(command);

        assertFalse(response.isError());
        assertTrue(response.message().contains("I've rescheduled this task"));
        assertEquals(command.startsWith("snooze 1")
                ? List.of(expectedRecord, "E | 1 |  meeting  | Monday | Tuesday")
                : List.of("D | 1 |  report  | Friday", expectedRecord), Files.readAllLines(dataFile));
        KafkaResponse restored = new Kafka(new TaskStorage(dataFile)).getResponse("list");
        assertFalse(restored.isError());
        assertTrue(restored.message().contains(expectedDisplay));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "snooze 1 /by Sunday", "snooze 1 /from Monday", "snooze 1 /to Sunday", "snooze 2 /to Sunday",
        "snooze 3 /by Sunday", "snooze 4 /by Sunday", "snooze 2 /by",
        "snooze 3 /from Monday /to", "snooze 3 /to Tuesday /from Monday",
        "snooze 2 /by Sun | Mon", "rename 1", "rename 0 changed", "rename 4 changed",
        "rename 1 buy | cook", "find", "unknown"
    })
    void getResponse_invalidEditOrQuery_preservesMemoryAndSavedFile(String command) throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Kafka kafka = new Kafka(new TaskStorage(dataFile));
        assertFalse(kafka.getResponse("todo read book").isError());
        assertFalse(kafka.getResponse("deadline report /by Friday").isError());
        assertFalse(kafka.getResponse("event meeting /from Monday /to Tuesday").isError());
        String originalFile = Files.readString(dataFile);
        String originalList = kafka.getResponse("list").message();

        KafkaResponse response = kafka.getResponse(command);

        assertTrue(response.isError(), command);
        if (command.startsWith("snooze 1 ")) {
            assertTrue(response.message().contains(
                    "A todo can't be snoozed because it has no date or time to change"));
        }
        assertEquals(originalFile, Files.readString(dataFile));
        assertEquals(originalList, kafka.getResponse("list").message());
    }

    @Test
    void getResponse_unmarkAndDelete_persistAcrossSessions() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Kafka kafka = new Kafka(new TaskStorage(dataFile));
        kafka.getResponse("todo first");
        kafka.getResponse("todo second");
        kafka.getResponse("mark 2");

        assertFalse(kafka.getResponse("unmark 2").isError());
        assertFalse(kafka.getResponse("delete 1").isError());

        assertEquals(List.of("T | 0 |  second"), Files.readAllLines(dataFile));
        KafkaResponse response = new Kafka(new TaskStorage(dataFile)).getResponse("list");
        assertTrue(response.message().contains("1.[T][ ]  second"));
        assertFalse(response.message().contains("first"));
    }

    @Test
    void getResponse_readOnlyCommands_doNotRewriteStorage() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        String savedContents = "T|0|read book\n\nD|1|report|Sunday\n";
        Files.writeString(dataFile, savedContents);
        Kafka kafka = new Kafka(new TaskStorage(dataFile));

        assertFalse(kafka.getResponse("list").isError());
        assertFalse(kafka.getResponse("find book").isError());
        assertFalse(kafka.getResponse("find absent").isError());
        assertTrue(kafka.getResponse("unknown").isError());
        KafkaResponse farewell = kafka.getResponse("bye");
        assertFalse(farewell.isError());
        assertEquals(KafkaResponse.Action.EXIT, farewell.action());
        assertTrue(farewell.message().contains("Bye babe~"));
        assertEquals(savedContents, Files.readString(dataFile));
    }

    @Test
    void getResponse_unreadableStorage_returnsErrorWithoutRequestingOverwrite() {
        Kafka kafka = new Kafka(new TaskStorage(temporaryDirectory));

        KafkaResponse response = kafka.getResponse("list");

        assertTrue(response.isError());
        assertTrue(response.message().contains("Could not read tasks from"));
        assertTrue(response.message().contains(temporaryDirectory.toAbsolutePath().toString()));
        assertEquals(KafkaResponse.Action.NONE, response.action());
        assertFalse(capturedOutput.toString(StandardCharsets.UTF_8).contains("Overwrite it"));
    }

    @Test
    void getResponse_saveFailure_returnsErrorAndPreservesExistingFile() throws IOException {
        Path parentFile = temporaryDirectory.resolve("parent.txt");
        Files.writeString(parentFile, "keep me");
        Kafka kafka = new Kafka(new TaskStorage(parentFile.resolve("tasks.txt")));

        KafkaResponse response = kafka.getResponse("todo read book");

        assertTrue(response.isError());
        assertTrue(response.message().contains("Could not save tasks to"));
        assertEquals("keep me", Files.readString(parentFile));
        assertTrue(kafka.getResponse("list").message().contains("You have no tasks lined up"));
    }

    @Test
    void getResponse_deleteSearchResult_deletesTheDisplayedTask() {
        Kafka kafka = new Kafka(new TaskStorage(temporaryDirectory.resolve("tasks.txt")));
        kafka.getResponse("todo buy milk");
        kafka.getResponse("todo read book");
        kafka.getResponse("todo another book");

        String matches = kafka.getResponse("find book").message();
        assertTrue(matches.contains("2.[T][ ]  read book"));
        assertTrue(matches.contains("3.[T][ ]  another book"));
        assertTrue(kafka.getResponse("delete 2").message().contains("[T][ ]  read book"));
        String remaining = kafka.getResponse("list").message();
        assertTrue(remaining.contains("1.[T][ ]  buy milk"));
        assertFalse(remaining.contains("read book"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "todo added", "deadline added /by Sunday", "event added /from Monday /to Tuesday",
        "mark 1", "unmark 2", "unmark 3", "delete 1", "delete 2", "delete 3",
        "rename 1 changed", "rename 2 changed", "rename 3 changed",
        "snooze 2 /by Sunday", "snooze 3 /from Wednesday", "snooze 3 /to Thursday",
        "snooze 3 /from Wednesday /to Thursday"
    })
    void getResponse_failedSave_preservesStateAndAllowsRetry(String command) throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        FailingStorage storage = new FailingStorage(dataFile);
        Kafka kafka = new Kafka(storage);
        kafka.getResponse("todo read book");
        kafka.getResponse("deadline report /by Friday");
        kafka.getResponse("event meeting /from Monday /to Tuesday");
        kafka.getResponse("mark 2");
        kafka.getResponse("mark 3");
        String originalFile = Files.readString(dataFile);
        String originalList = kafka.getResponse("list").message();

        Path expectedFile = temporaryDirectory.resolve("expected.txt");
        Files.writeString(expectedFile, originalFile);
        Kafka expected = new Kafka(new TaskStorage(expectedFile));
        assertFalse(expected.getResponse(command).isError());
        storage.failNextSave();

        assertTrue(kafka.getResponse(command).isError());
        assertEquals(originalList, kafka.getResponse("list").message());
        assertEquals(originalFile, Files.readString(dataFile));
        assertFalse(kafka.getResponse(command).isError());
        assertEquals(Files.readString(expectedFile), Files.readString(dataFile));
        assertEquals(expected.getResponse("list").message(), kafka.getResponse("list").message());
    }

    @Test
    void recoverStorage_failedSave_preservesCorruptionAndAllowsRetry() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "invalid saved task");
        FailingStorage storage = new FailingStorage(dataFile);
        Kafka kafka = new Kafka(storage);
        assertEquals(KafkaResponse.Action.CONFIRM_STORAGE_OVERWRITE, kafka.getResponse("list").action());
        storage.failNextSave();

        assertTrue(kafka.recoverStorage().isError());
        assertEquals("invalid saved task", Files.readString(dataFile));
        assertFalse(kafka.recoverStorage().isError());
        assertEquals("", Files.readString(dataFile));
    }

    @Test
    void recoverStorage_withoutPendingRecovery_preservesTasks() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Kafka kafka = new Kafka(new TaskStorage(dataFile));
        kafka.getResponse("todo keep me");
        String savedFile = Files.readString(dataFile);

        assertTrue(kafka.recoverStorage().isError());
        assertEquals(savedFile, Files.readString(dataFile));
    }

    @Test
    void getResponse_repairedFile_clearsPendingRecovery() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "invalid saved task");
        Kafka kafka = new Kafka(new TaskStorage(dataFile));
        assertEquals(KafkaResponse.Action.CONFIRM_STORAGE_OVERWRITE, kafka.getResponse("list").action());
        Files.writeString(dataFile, "T | 0 | repaired\n");

        assertFalse(kafka.getResponse("list").isError());
        assertTrue(kafka.recoverStorage().isError());
        assertEquals("T | 0 | repaired\n", Files.readString(dataFile));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "todo \u2003", "deadline \u2003 /by Sunday", "deadline report /by \u2003",
        "event \u2003 /from Monday /to Tuesday", "event meeting /from \u2003 /to Tuesday",
        "event meeting /from Monday /to \u2003", "rename 1 \u2003", "snooze 2 /by \u2003"
    })
    void getResponse_unicodeBlankDetails_preservesLoadableFile(String command) throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        TaskStorage storage = new TaskStorage(dataFile);
        Kafka kafka = new Kafka(storage);
        kafka.getResponse("todo keep me");
        kafka.getResponse("deadline report /by Friday");
        String originalFile = Files.readString(dataFile);

        assertTrue(kafka.getResponse(command).isError());
        assertEquals(originalFile, Files.readString(dataFile));
        assertEquals(2, storage.load().size());
    }

    /**
     * Simulates a temporary save failure without depending on operating system permissions.
     */
    private static class FailingStorage extends TaskStorage {
        private boolean shouldFailNextSave;

        FailingStorage(Path filePath) {
            super(filePath);
        }

        void failNextSave() {
            shouldFailNextSave = true;
        }

        @Override
        public void save(TaskList tasks) throws KafkaException {
            if (shouldFailNextSave) {
                shouldFailNextSave = false;
                throw new KafkaException("Simulated save failure");
            }
            super.save(tasks);
        }
    }

    @Test
    void corruptedFileRecovery_invalidAnswerThenYes_repromptsAndContinues() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "invalid saved task");

        String output = runKafka("maybe\n Y \ntodo recovered\nbye\n");

        assertTrue(output.contains("Please enter yes or no."));
        assertEquals(2, countOccurrences(output, "Overwrite it with an empty task list"));
        assertEquals(List.of("T | 0 |  recovered"), Files.readAllLines(dataFile));
    }

    @ParameterizedTest
    @ValueSource(strings = {" N \n", ""})
    void corruptedFileRecovery_refusalOrEndOfInput_preservesFile(String input) throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "invalid saved task");

        String output = runKafka(input);

        assertEquals("invalid saved task", Files.readString(dataFile));
        assertTrue(output.contains("Your task data was not changed."));
        assertFalse(output.contains("Starting with an empty list."));
    }

    @ParameterizedTest
    @MethodSource("invalidInputsAndExpectedErrors")
    void invalidInputShowsOnlyItsMatchingError(String invalidInput, String expectedError) {
        String output = runKafka(invalidInput + "\nbye\n");

        assertEquals(1, countOccurrences(output, expectedError),
                "The matching error should appear exactly once");
        for (String otherError : INPUT_ERROR_MESSAGES) {
            if (!otherError.equals(expectedError)) {
                assertFalse(output.contains(otherError),
                        "The output should not contain the unrelated error: " + otherError);
            }
        }
        assertFalse(output.contains("I've added this task"));
    }

    /**
     * Supplies bad commands alongside the one error each command should produce.
     *
     * @return Invalid commands and their matching user-facing errors.
     */
    private static Stream<Arguments> invalidInputsAndExpectedErrors() {
        return Stream.of(
                Arguments.of("todo", INPUT_ERROR_MESSAGES.get(0)),
                Arguments.of("deadline /by Friday", INPUT_ERROR_MESSAGES.get(1)),
                Arguments.of("deadline return book", INPUT_ERROR_MESSAGES.get(2)),
                Arguments.of("deadline return book /by", INPUT_ERROR_MESSAGES.get(3)),
                Arguments.of("event /from 2pm /to 3pm", INPUT_ERROR_MESSAGES.get(4)),
                Arguments.of("event meeting /to 3pm", INPUT_ERROR_MESSAGES.get(5)),
                Arguments.of("event meeting /from /to 3pm", INPUT_ERROR_MESSAGES.get(6)),
                Arguments.of("mark abc", INPUT_ERROR_MESSAGES.get(7)),
                Arguments.of("mark 0", INPUT_ERROR_MESSAGES.get(8)),
                Arguments.of("mark 1", INPUT_ERROR_MESSAGES.get(9)),
                Arguments.of("todo compare A | B", INPUT_ERROR_MESSAGES.get(10)));
    }

    /**
     * Counts non-overlapping appearances of one value in some text.
     *
     * @param text Complete text to search.
     * @param value Value whose appearances should be counted.
     * @return Number of non-overlapping appearances.
     */
    private static int countOccurrences(String text, String value) {
        int count = 0;
        int position = 0;
        while ((position = text.indexOf(value, position)) >= 0) {
            count++;
            position += value.length();
        }
        return count;
    }

    /**
     * Runs one isolated Kafka session using temporary storage and captured output.
     *
     * @param input Newline-separated commands for the session.
     * @return Everything Kafka printed during that session.
     */
    private String runKafka(String input) {
        capturedOutput.reset();
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        TaskStorage taskStorage = new TaskStorage(temporaryDirectory.resolve("tasks.txt"));
        new Kafka(taskStorage).run();
        return capturedOutput.toString(StandardCharsets.UTF_8);
    }
}
