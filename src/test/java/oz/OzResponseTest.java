package oz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


/**
 * Tests the response generation logic in {@link Oz} that drives GUI interactions.
 */
public class OzResponseTest {

    /** Temporary folder for test storage file. */
    @TempDir
    Path temporaryFolder;

    /** Chatbot instance under test. */
    private Oz oz;

    /**
     * Sets up an Oz instance backed by a temporary file before each test.
     */
    @BeforeEach
    public void setUp() {
        Path storageFile = temporaryFolder.resolve("test_tasks.txt");
        this.oz = new Oz(storageFile.toString());
    }

    @Test
    public void getResponse_emptyOrBlankInput_promptsUser() {
        CommandResult empty = this.oz.getResponse("");
        assertEquals("Please enter a command.", empty.message());
        assertEquals(ResponseType.BLANK, empty.type());

        CommandResult blank = this.oz.getResponse("   ");
        assertEquals("Please enter a command.", blank.message());
        assertEquals(ResponseType.BLANK, blank.type());

        CommandResult nullInput = this.oz.getResponse(null);
        assertEquals("Please enter a command.", nullInput.message());
        assertEquals(ResponseType.BLANK, nullInput.type());
    }

    @Test
    public void getResponse_unknownCommand_returnsErrorMessage() {
        CommandResult response = this.oz.getResponse("unknownCommand");
        assertEquals("Confound it! Unknown command. Check your blueprint syntax.", response.message());
        assertEquals(ResponseType.ERROR, response.type());
    }

    @Test
    public void getResponse_byeCommand_returnsFarewellMessage() {
        CommandResult response = this.oz.getResponse("bye");
        assertEquals("Farewell! Back to my contraptions. *oink*", response.message());
        assertEquals(ResponseType.BYE, response.type());
        assertTrue(this.oz.isExit());
    }

    @Test
    public void isExit_defaultState_returnsFalse() {
        assertFalse(this.oz.isExit());
        this.oz.getResponse("list");
        assertFalse(this.oz.isExit());
    }

    @Test
    public void getResponse_todoCommand_taskAddedAndReported() {
        CommandResult response = this.oz.getResponse("todo borrow book");
        assertTrue(response.message().contains("*Snort* Added to the list:"));
        assertTrue(response.message().contains("[T][ ] borrow book"));
        assertTrue(response.message().contains("Now you have 1 tasks in the list."));
        assertEquals(ResponseType.ADD, response.type());
    }

    @Test
    public void getResponse_todoCommandWithEmptyDescription_returnsErrorMessage() {
        CommandResult response = this.oz.getResponse("todo ");
        assertEquals("Confound it! The description of a todo cannot be empty.", response.message());
        assertEquals(ResponseType.ERROR, response.type());
    }

    @Test
    public void getResponse_deadlinesCommand_taskAddedAndReported() {
        CommandResult response = this.oz.getResponse("deadline return book /by 2026-12-31");
        assertTrue(response.message().contains("*Snort* Added to the list:"));
        assertTrue(response.message().contains("[D][ ] return book"));
        assertTrue(response.message().contains("Dec 31 2026"));
        assertEquals(ResponseType.ADD, response.type());
    }

    @Test
    public void getResponse_eventCommand_taskAddedAndReported() {
        CommandResult response = this.oz.getResponse(
                "event project meeting /from 2026-10-10 14:00 /to 2026-10-10 16:00");
        assertTrue(response.message().contains("*Snort* Added to the list:"));
        assertTrue(response.message().contains("[E][ ] project meeting"));
        assertEquals(ResponseType.ADD, response.type());
    }

    @Test
    public void getResponse_recurringCommand_seriesAddedAndReported() {
        CommandResult response = this.oz.getResponse(
                "recurring project meeting /on 2026-10-02 /start 1400 /end 1500 "
                        + "/every 1 week /until 2026-12-31");

        assertEquals(ResponseType.ADD, response.type());
        assertEquals("*Snort* Added to the list:\n"
                + "[R] project meeting (from: Oct 02 2026, 2pm to: Oct 02 2026, 3pm; "
                + "repeats: every 1 week until Dec 31 2026)\n"
                + "Now you have 1 tasks in the list.", response.message());
    }

    @Test
    public void getResponse_invalidRecurringCommands_returnErrorsWithoutAddingTasks() {
        String[] invalidCommands = {
            "recurring meeting /on 2026-10-02 /start 1400 /end 1500 /every week",
            "recurring meeting /on 2026-10-02 /start 1400 /end 1500 /every 0 weeks",
            "recurring meeting /on 2026-10-02 /start 1500 /end 1400 /every 1 week",
            "recurring meeting /on 2026-10-02 /start 1400 /end 1500 /every 1 month",
            "recurring meeting /on 2026-10-02 /start 1400 /end 1500 "
                    + "/every 1 week /until 2026-10-01"
        };

        for (String command : invalidCommands) {
            CommandResult response = this.oz.getResponse(command);
            assertEquals(ResponseType.ERROR, response.type(), command);
            assertTrue(response.message().startsWith("Confound it! "), command);
        }
        assertEquals("Here is the master task list:", this.oz.getResponse("list").message());
    }

    @Test
    public void getResponse_recurringOccurrence_markAndUnmarkRequiresDate() {
        this.oz.getResponse("recurring meeting /on 2026-10-02 /start 1400 /end 1500 "
                + "/every 1 week");

        assertEquals("Confound it! Please specify which occurrence to mark. "
                + "Use: mark <number> /on <date>.", this.oz.getResponse("mark 1").message());
        assertEquals("Confound it! Please specify which occurrence to unmark. "
                + "Use: unmark <number> /on <date>.", this.oz.getResponse("unmark 1").message());

        CommandResult marked = this.oz.getResponse("mark 1 /on 2026-10-09");
        assertEquals(ResponseType.CHANGE_MARK, marked.type());
        assertTrue(marked.message().contains(
                "[R][X] meeting (from: Oct 09 2026, 2pm to: Oct 09 2026, 3pm)"));
        assertTrue(this.oz.getResponse("mark 1 /on 2026-10-09").message()
                .contains("already marked as done"));

        CommandResult unmarked = this.oz.getResponse("unmark 1 /on 2026-10-09");
        assertEquals(ResponseType.CHANGE_MARK, unmarked.type());
        assertTrue(unmarked.message().contains(
                "[R][ ] meeting (from: Oct 09 2026, 2pm to: Oct 09 2026, 3pm)"));
    }

    @Test
    public void getResponse_onCommand_recurringOccurrenceUsesCalculatedDateAndStatus() {
        this.oz.getResponse("recurring meeting /on 2026-10-02 /start 1400 /end 1500 "
                + "/every 2 weeks /until 2026-10-30");
        this.oz.getResponse("mark 1 /on 2026-10-16");

        assertEquals("Tasks occurring on Oct 16 2026:\n"
                + "1. [R][X] meeting (from: Oct 16 2026, 2pm to: Oct 16 2026, 3pm)",
                this.oz.getResponse("on 2026-10-16").message());
        assertEquals("No tasks found for Oct 23 2026.",
                this.oz.getResponse("on 2026-10-23").message());
    }

    @Test
    public void getResponse_onArgumentForOrdinaryTask_returnsError() {
        this.oz.getResponse("todo read book");
        assertEquals("Confound it! The /on argument can only be used with recurring tasks.",
                this.oz.getResponse("mark 1 /on 2026-10-02").message());
    }

    @Test
    public void getResponse_markAndUnmarkCommands_updatesTaskStatus() {
        this.oz.getResponse("todo finish homework");
        CommandResult markResponse = this.oz.getResponse("mark 1");
        assertTrue(markResponse.message().contains("*Oink* Marked as done:"));
        assertTrue(markResponse.message().contains("[T][X] finish homework"));
        assertEquals(ResponseType.CHANGE_MARK, markResponse.type());

        CommandResult unmarkResponse = this.oz.getResponse("unmark 1");
        assertTrue(unmarkResponse.message().contains("*Snort* Marked as not done yet:"));
        assertTrue(unmarkResponse.message().contains("[T][ ] finish homework"));
        assertEquals(ResponseType.CHANGE_MARK, unmarkResponse.type());
    }

    @Test
    public void getResponse_deleteCommand_removesTask() {
        this.oz.getResponse("todo task to remove");
        CommandResult deleteResponse = this.oz.getResponse("delete 1");
        assertTrue(deleteResponse.message().contains("Scrapped! Removed task:"));
        assertTrue(deleteResponse.message().contains("[T][ ] task to remove"));
        assertTrue(deleteResponse.message().contains("Now you have 0 tasks in the list."));
        assertEquals(ResponseType.DELETE, deleteResponse.type());
    }

    @Test
    public void getResponse_findCommand_returnsMatchingTasks() {
        this.oz.getResponse("todo read book");
        this.oz.getResponse("todo buy book");
        this.oz.getResponse("todo wash car");

        CommandResult findResponse = this.oz.getResponse("find book");
        assertTrue(findResponse.message().contains("Matching tasks located:"));
        assertTrue(findResponse.message().contains("read book"));
        assertTrue(findResponse.message().contains("buy book"));
        assertEquals(ResponseType.FIND, findResponse.type());
    }

    /** Verifies that invalid arguments are reported without changing the task list. */
    @Test
    public void getResponse_invalidArguments_returnsErrorWithoutAddingTasks() {
        String[] invalidCommands = {
            "list extra", "on", "on invalid-date", "find", "deadline return book",
            "deadline return book /by invalid-date", "event meeting",
            "event meeting /from 2026-10-11 /to 2026-10-10"
        };
        for (String command : invalidCommands) {
            CommandResult response = this.oz.getResponse(command);
            assertEquals(ResponseType.ERROR, response.type(), command);
            assertTrue(response.message().startsWith("Confound it! "), command);
        }
        assertEquals("Here is the master task list:", this.oz.getResponse("list").message());
    }

    /** Verifies task-number validation for all commands that select an existing task. */
    @Test
    public void getResponse_invalidTaskNumbers_preservesTask() {
        this.oz.getResponse("todo keep task");
        String[] commands = {"mark", "unmark", "delete"};
        String[] invalidNumbers = {"", "abc", "-1", "0", "2", "999999999999999999999"};
        for (String command : commands) {
            for (String number : invalidNumbers) {
                CommandResult response = this.oz.getResponse(command + " " + number);
                assertEquals(ResponseType.ERROR, response.type(), command + " " + number);
            }
        }
        assertEquals("Here is the master task list:\n1. [T][ ] keep task",
                this.oz.getResponse("list").message());
    }

    /** Verifies date filtering and local numbering when unrelated tasks precede the matches. */
    @Test
    public void getResponse_onCommand_listsOnlyTasksOccurringOnDate() {
        this.oz.getResponse("todo unrelated task");
        this.oz.getResponse("deadline return book /by 2026-10-10");
        this.oz.getResponse("event conference /from 2026-10-09 /to 2026-10-11");
        CommandResult response = this.oz.getResponse("  on  2026-10-10  ");
        assertEquals(ResponseType.LIST, response.type());
        assertEquals("Tasks occurring on Oct 10 2026:\n"
                + "1. [D][ ] return book (by: Oct 10 2026)\n"
                + "2. [E][ ] conference (from: Oct 09 2026 to: Oct 11 2026)", response.message());
        assertEquals("No tasks found for Jan 01 2027.",
                this.oz.getResponse("on 2027-01-01").message());
    }

    /** Verifies that every add command saves its task and reports the updated count. */
    @Test
    public void getResponse_addEachTaskType_persistsTasksAndReportsCounts() {
        String[] commands = {
            "todo read book",
            "deadline return book /by 2026-10-10",
            "event meeting /from 2026-10-10 1400 /to 2026-10-10 1600"
        };
        for (int i = 0; i < commands.length; i++) {
            CommandResult response = this.oz.getResponse(commands[i]);
            assertEquals(ResponseType.ADD, response.type());
            assertTrue(response.message().endsWith("Now you have " + (i + 1) + " tasks in the list."));
            Oz reloaded = new Oz(this.temporaryFolder.resolve("test_tasks.txt").toString());
            assertEquals(this.oz.getResponse("list").message(), reloaded.getResponse("list").message());
        }
    }

    /** Verifies ordering, numbering, whitespace, and empty results for keyword search. */
    @Test
    public void getResponse_findCommand_numbersMatchesWithoutTrailingNewline() {
        this.oz.getResponse("todo wash car");
        this.oz.getResponse("todo read book");
        this.oz.getResponse("todo buy book");
        assertEquals("Matching tasks located:\n"
                + "1. [T][ ] read book\n2. [T][ ] buy book",
                this.oz.getResponse("find book").message());
        CommandResult noMatches = this.oz.getResponse("find missing");
        assertEquals("No matching tasks found in the ledger.", noMatches.message());
        assertEquals(ResponseType.FIND, noMatches.type());
    }

    @Test
    public void getResponse_taskWithStorageDelimiter_returnsErrorMessage() {
        String expectedMessage = "Confound it! Task input cannot contain the '|' character "
                + "because it is reserved for storage.";

        CommandResult todoResponse = this.oz.getResponse("todo read | book");
        assertEquals(expectedMessage, todoResponse.message());
        assertEquals(ResponseType.ERROR, todoResponse.type());

        CommandResult deadlineResponse = this.oz.getResponse(
                "deadline return | book /by 2026-10-10");
        assertEquals(expectedMessage, deadlineResponse.message());
        assertEquals(ResponseType.ERROR, deadlineResponse.type());

        CommandResult eventResponse = this.oz.getResponse(
                "event team | sync /from 2026-10-10 1400 /to 2026-10-10 1500");
        assertEquals(expectedMessage, eventResponse.message());
        assertEquals(ResponseType.ERROR, eventResponse.type());

        CommandResult recurringResponse = this.oz.getResponse(
                "recurring team | meeting /on 2026-10-10 /start 1000 /end 1100 /every 1 week");
        assertEquals(expectedMessage, recurringResponse.message());
        assertEquals(ResponseType.ERROR, recurringResponse.type());
    }

    @Test
    public void getResponse_duplicateFlags_returnsErrorMessage() {
        CommandResult deadlineDuplicate = this.oz.getResponse(
                "deadline return book /by 2026-10-10 /by 2026-10-11");
        assertEquals("Confound it! Duplicate '/by' parameter detected.",
                deadlineDuplicate.message());
        assertEquals(ResponseType.ERROR, deadlineDuplicate.type());

        CommandResult eventDuplicateFrom = this.oz.getResponse(
                "event meeting /from 2026-10-10 1400 /from 2026-10-10 1500 /to 2026-10-10 1600");
        assertEquals("Confound it! Duplicate '/from' parameter detected.",
                eventDuplicateFrom.message());
        assertEquals(ResponseType.ERROR, eventDuplicateFrom.type());

        CommandResult eventDuplicateTo = this.oz.getResponse(
                "event meeting /from 2026-10-10 1400 /to 2026-10-10 1500 /to 2026-10-10 1600");
        assertEquals("Confound it! Duplicate '/to' parameter detected.",
                eventDuplicateTo.message());
        assertEquals(ResponseType.ERROR, eventDuplicateTo.type());
    }

    @Test
    public void getResponse_misplacedOrUnexpectedFlags_returnsErrorMessage() {
        CommandResult eventMisplaced = this.oz.getResponse(
                "event party /to 2026-10-10 1800 /from 2026-10-10 1400");
        assertTrue(eventMisplaced.message().contains("The '/from' parameter must precede '/to'"));
        assertEquals(ResponseType.ERROR, eventMisplaced.type());

        CommandResult todoUnexpected = this.oz.getResponse(
                "todo read book /by tomorrow");
        assertEquals("Confound it! The todo command does not accept parameter flags like /by, /from, or /to.",
                todoUnexpected.message());
        assertEquals(ResponseType.ERROR, todoUnexpected.type());

        CommandResult deadlineUnexpected = this.oz.getResponse(
                "deadline submit /by 2026-10-10 /from 1000");
        assertTrue(deadlineUnexpected.message().contains("Unexpected '/from' parameter in deadline command"));
        assertEquals(ResponseType.ERROR, deadlineUnexpected.type());
    }

    @Test
    public void getResponse_byeWithArguments_returnsErrorMessage() {
        CommandResult response = this.oz.getResponse("bye later");
        assertEquals("Confound it! The bye command does not take arguments.", response.message());
        assertEquals(ResponseType.ERROR, response.type());
    }

    @Test
    public void getResponse_descriptiveIndexErrors_returnsDetailedMessages() {
        // Empty list
        CommandResult emptyListResponse = this.oz.getResponse("delete 1");
        assertEquals("Confound it! Your task list is empty. Add tasks before referencing them.",
                emptyListResponse.message());

        // Blank task number
        CommandResult blankNumberResponse = this.oz.getResponse("mark");
        assertEquals("Confound it! Please specify a task number.", blankNumberResponse.message());

        // Non-positive task numbers
        CommandResult zeroResponse = this.oz.getResponse("delete 0");
        assertEquals("Confound it! Task number must be a positive whole number starting from 1.",
                zeroResponse.message());
        CommandResult negativeResponse = this.oz.getResponse("delete -1");
        assertEquals("Confound it! Task number must be a positive whole number starting from 1.",
                negativeResponse.message());

        // Add one task
        this.oz.getResponse("todo single task");

        // Out of bounds
        CommandResult outOfBoundsResponse = this.oz.getResponse("delete 5");
        assertEquals("Confound it! Task number 5 does not exist. Please provide a number between 1 and 1.",
                outOfBoundsResponse.message());

        // Non-numeric input
        CommandResult nonNumericResponse = this.oz.getResponse("delete abc");
        assertEquals("Confound it! Please provide a valid whole number for the task index.",
                nonNumericResponse.message());
    }

    @Test
    public void getResponse_redundantStateTransitions_returnsError() {
        this.oz.getResponse("todo sample task");

        // Unmarking an already unmarked task
        CommandResult redundantUnmark = this.oz.getResponse("unmark 1");
        assertEquals("Confound it! Task 1 is not marked as done yet.", redundantUnmark.message());
        assertEquals(ResponseType.ERROR, redundantUnmark.type());

        // Mark it once
        CommandResult firstMark = this.oz.getResponse("mark 1");
        assertEquals(ResponseType.CHANGE_MARK, firstMark.type());

        // Marking an already marked task
        CommandResult redundantMark = this.oz.getResponse("mark 1");
        assertEquals("Confound it! Task 1 is already marked as done.", redundantMark.message());
        assertEquals(ResponseType.ERROR, redundantMark.type());
    }

    @Test
    public void getResponse_storageSaveFails_returnsErrorAndRollsBack() throws IOException {
        Path storageFile = this.temporaryFolder.resolve("readonly_tasks.txt");
        Files.createFile(storageFile);
        storageFile.toFile().setReadOnly();

        Oz readOnlyOz = new Oz(storageFile.toString());
        CommandResult response = readOnlyOz.getResponse("todo attempt task");
        assertEquals(ResponseType.ERROR, response.type());
        assertTrue(response.message().contains("Confound it!"));

        // Verify in-memory list rolled back to empty
        assertEquals("Here is the master task list:", readOnlyOz.getResponse("list").message());

        storageFile.toFile().setWritable(true);
    }

    @Test
    public void getResponse_unmarkRecurringOccurrenceOnNonRecurring_returnsErrorMessage() {
        this.oz.getResponse("todo read book");
        CommandResult response = this.oz.getResponse("unmark 1 /on 2026-10-15");
        assertEquals("Confound it! The /on argument can only be used with recurring tasks.",
                response.message());
        assertEquals(ResponseType.ERROR, response.type());
    }

    @Test
    public void getResponse_markAndUnmarkDuplicateOrUnexpectedFlags_returnsErrorMessage() {
        this.oz.getResponse("todo dummy task");

        CommandResult duplicateMark = this.oz.getResponse(
                "mark 1 /on 2026-10-10 /on 2026-10-11");
        assertEquals("Confound it! Duplicate '/on' parameter detected.", duplicateMark.message());
        assertEquals(ResponseType.ERROR, duplicateMark.type());

        CommandResult unexpectedMark = this.oz.getResponse("mark 1 /by 2026-10-10");
        assertTrue(unexpectedMark.message().contains("Unexpected 'flag' parameter in mark command"));
        assertEquals(ResponseType.ERROR, unexpectedMark.type());

        CommandResult duplicateUnmark = this.oz.getResponse(
                "unmark 1 /on 2026-10-10 /on 2026-10-11");
        assertEquals("Confound it! Duplicate '/on' parameter detected.", duplicateUnmark.message());
        assertEquals(ResponseType.ERROR, duplicateUnmark.type());

        CommandResult unexpectedUnmark = this.oz.getResponse("unmark 1 /from 2026-10-10");
        assertTrue(unexpectedUnmark.message().contains("Unexpected 'flag' parameter in unmark command"));
        assertEquals(ResponseType.ERROR, unexpectedUnmark.type());
    }

    @Test
    public void getResponse_todoUnexpectedFlags_returnsErrorMessage() {
        String expectedMessage = "Confound it! The todo command does not accept parameter "
                + "flags like /by, /from, or /to.";

        CommandResult fromResponse = this.oz.getResponse("todo read book /from 2pm");
        assertEquals(expectedMessage, fromResponse.message());
        assertEquals(ResponseType.ERROR, fromResponse.type());

        CommandResult toResponse = this.oz.getResponse("todo read book /to 4pm");
        assertEquals(expectedMessage, toResponse.message());
        assertEquals(ResponseType.ERROR, toResponse.type());

        CommandResult onResponse = this.oz.getResponse("todo read book /on 2026-10-10");
        assertEquals(expectedMessage, onResponse.message());
        assertEquals(ResponseType.ERROR, onResponse.type());
    }

    @Test
    public void getResponse_deadlineUnexpectedFlagsAndMissingFields_returnsErrorMessage() {
        CommandResult unexpectedTo = this.oz.getResponse(
                "deadline submit /by 2026-10-10 /to 1000");
        assertTrue(unexpectedTo.message().contains("Unexpected '/to' parameter in deadline command"));
        assertEquals(ResponseType.ERROR, unexpectedTo.type());

        CommandResult emptyDescription = this.oz.getResponse("deadline /by 2026-10-10");
        assertEquals(ResponseType.ERROR, emptyDescription.type());

        CommandResult missingBy = this.oz.getResponse("deadline return book /by");
        assertEquals(ResponseType.ERROR, missingBy.type());
    }

    @Test
    public void getResponse_eventMissingOrUnexpectedFlags_returnsErrorMessage() {
        CommandResult missingFrom = this.oz.getResponse(
                "event meeting /to 2026-10-10 1600");
        assertTrue(missingFrom.message().contains("Missing required '/from' parameter"));
        assertEquals(ResponseType.ERROR, missingFrom.type());

        CommandResult missingTo = this.oz.getResponse(
                "event meeting /from 2026-10-10 1400");
        assertTrue(missingTo.message().contains("Missing required '/to' parameter"));
        assertEquals(ResponseType.ERROR, missingTo.type());

        CommandResult unexpectedBy = this.oz.getResponse(
                "event meeting /from 2026-10-10 1400 /to 2026-10-10 1600 /by 2026-10-10");
        assertTrue(unexpectedBy.message().contains("Unexpected '/by' parameter in event command"));
        assertEquals(ResponseType.ERROR, unexpectedBy.type());
    }

    @Test
    public void getResponse_recurringDuplicateAndOrderFlags_returnsErrorMessage() {
        CommandResult dupOn = this.oz.getResponse(
                "recurring team sync /on 2026-10-02 /on 2026-10-09 /start 1400 /end 1500 /every 1 week");
        assertEquals("Confound it! Duplicate '/on' parameter detected.", dupOn.message());

        CommandResult dupStart = this.oz.getResponse(
                "recurring team sync /on 2026-10-02 /start 1400 /start 1500 /end 1500 /every 1 week");
        assertEquals("Confound it! Duplicate '/start' parameter detected.", dupStart.message());

        CommandResult dupEnd = this.oz.getResponse(
                "recurring team sync /on 2026-10-02 /start 1400 /end 1500 /end 1600 /every 1 week");
        assertEquals("Confound it! Duplicate '/end' parameter detected.", dupEnd.message());

        CommandResult dupEvery = this.oz.getResponse(
                "recurring team sync /on 2026-10-02 /start 1400 /end 1500 /every 1 week /every 2 weeks");
        assertEquals("Confound it! Duplicate '/every' parameter detected.", dupEvery.message());

        CommandResult dupUntil = this.oz.getResponse(
                "recurring team sync /on 2026-10-02 /start 1400 /end 1500 /every 1 week "
                        + "/until 2026-12-31 /until 2026-12-30");
        assertEquals("Confound it! Duplicate '/until' parameter detected.", dupUntil.message());

        CommandResult outOfOrder = this.oz.getResponse(
                "recurring team sync /start 1400 /on 2026-10-02 /end 1500 /every 1 week");
        assertTrue(outOfOrder.message().contains("Recurring parameters are out of order"));
    }

    @Test
    public void getResponse_deleteStorageSaveFails_restoresTaskAndReturnsError() throws IOException {
        this.oz.getResponse("todo task to delete");

        Path storageFile = temporaryFolder.resolve("test_tasks.txt");
        storageFile.toFile().setReadOnly();

        CommandResult response = this.oz.getResponse("delete 1");
        assertEquals(ResponseType.ERROR, response.type());
        assertTrue(response.message().contains("Confound it!"));

        storageFile.toFile().setWritable(true);

        // Verify that the task was restored into the task list upon rollback
        CommandResult listResponse = this.oz.getResponse("list");
        assertTrue(listResponse.message().contains("1. [T][ ] task to delete"));
    }

    @Test
    public void getResponse_parseTaskIndexNegativeOverflow_returnsNonPositiveError() {
        this.oz.getResponse("todo sample");
        CommandResult response = this.oz.getResponse("delete -999999999999999999999");
        assertEquals("Confound it! Task number must be a positive whole number starting from 1.",
                response.message());
        assertEquals(ResponseType.ERROR, response.type());
    }

    @Test
    public void getResponse_markStorageSaveFails_restoresTaskDoneStateAndReturnsError() throws IOException {
        this.oz.getResponse("todo task to mark");

        Path storageFile = temporaryFolder.resolve("test_tasks.txt");
        storageFile.toFile().setReadOnly();

        CommandResult response = this.oz.getResponse("mark 1");
        assertEquals(ResponseType.ERROR, response.type());
        assertTrue(response.message().contains("Confound it!"));

        storageFile.toFile().setWritable(true);

        CommandResult listResponse = this.oz.getResponse("list");
        assertTrue(listResponse.message().contains("1. [T][ ] task to mark"));
    }

    @Test
    public void getResponse_unmarkStorageSaveFails_restoresTaskDoneStateAndReturnsError() throws IOException {
        this.oz.getResponse("todo task to unmark");
        this.oz.getResponse("mark 1");

        Path storageFile = temporaryFolder.resolve("test_tasks.txt");
        storageFile.toFile().setReadOnly();

        CommandResult response = this.oz.getResponse("unmark 1");
        assertEquals(ResponseType.ERROR, response.type());
        assertTrue(response.message().contains("Confound it!"));

        storageFile.toFile().setWritable(true);

        CommandResult listResponse = this.oz.getResponse("list");
        assertTrue(listResponse.message().contains("1. [T][X] task to unmark"));
    }

    @Test
    public void getResponse_markOccurrenceStorageSaveFails_restoresOccurrenceStateAndReturnsError()
            throws IOException {
        this.oz.getResponse("recurring sync /on 2026-10-02 /start 1400 /end 1500 /every 1 week");

        Path storageFile = temporaryFolder.resolve("test_tasks.txt");
        storageFile.toFile().setReadOnly();

        CommandResult response = this.oz.getResponse("mark 1 /on 2026-10-09");
        assertEquals(ResponseType.ERROR, response.type());
        assertTrue(response.message().contains("Confound it!"));

        storageFile.toFile().setWritable(true);

        CommandResult onResponse = this.oz.getResponse("on 2026-10-09");
        assertTrue(onResponse.message().contains("[R][ ] sync"));
    }

    @Test
    public void getResponse_unmarkOccurrenceStorageSaveFails_restoresOccurrenceStateAndReturnsError()
            throws IOException {
        this.oz.getResponse("recurring sync /on 2026-10-02 /start 1400 /end 1500 /every 1 week");
        this.oz.getResponse("mark 1 /on 2026-10-09");

        Path storageFile = temporaryFolder.resolve("test_tasks.txt");
        storageFile.toFile().setReadOnly();

        CommandResult response = this.oz.getResponse("unmark 1 /on 2026-10-09");
        assertEquals(ResponseType.ERROR, response.type());
        assertTrue(response.message().contains("Confound it!"));

        storageFile.toFile().setWritable(true);

        CommandResult onResponse = this.oz.getResponse("on 2026-10-09");
        assertTrue(onResponse.message().contains("[R][X] sync"));
    }

    @Test
    public void getResponse_recurringIntervalEdgeCases_returnsErrorMessage() {
        CommandResult nonNumeric = this.oz.getResponse(
                "recurring sync /on 2026-10-02 /start 1400 /end 1500 /every abc week");
        assertEquals(ResponseType.ERROR, nonNumeric.type());
        assertTrue(nonNumeric.message().contains("The recurrence interval must be a positive whole number."));

        CommandResult overflow = this.oz.getResponse(
                "recurring sync /on 2026-10-02 /start 1400 /end 1500 /every 99999999999999999 week");
        assertEquals(ResponseType.ERROR, overflow.type());
        assertTrue(overflow.message().contains("The recurrence interval must be a positive whole number."));
    }
}
