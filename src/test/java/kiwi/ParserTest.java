package kiwi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import kiwi.command.AddCommand;
import kiwi.command.Command;
import kiwi.command.DeleteCommand;
import kiwi.command.ExitCommand;
import kiwi.command.FindCommand;
import kiwi.command.FreeCommand;
import kiwi.command.ListCommand;
import kiwi.command.MarkCommand;
import kiwi.command.OnCommand;
import kiwi.command.UnmarkCommand;
import kiwi.task.Deadline;
import kiwi.task.Event;
import kiwi.task.TaskList;
import kiwi.task.Todo;

/**
 * Unit tests for {@link Parser#parse(String)}.
 */
class ParserTest {

    @TempDir
    Path tempDir;

    @Test
    void parse_bye_returnsExitCommand() throws KiwiException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    void parse_list_returnsListCommand() throws KiwiException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Nested
    class TodoCommand {

        @Test
        void parse_validTodo_returnsAddCommandAndAddsTask() throws KiwiException {
            Command command = Parser.parse("todo read book");
            assertInstanceOf(AddCommand.class, command);

            TaskList tasks = new TaskList();
            command.execute(tasks, new Ui(), new Storage(tempDir.resolve("kiwi.txt").toString()));

            assertEquals(1, tasks.size());
            assertInstanceOf(Todo.class, tasks.get(0));
            assertEquals("read book", tasks.get(0).getDescription());
        }

        @Test
        void parse_todoWithoutDescription_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("todo"));
            assertEquals("A todo needs a description — try: todo borrow book", exception.getMessage());
        }

        @Test
        void parse_todoWithLeadingAndExtraSpaces_addsTrimmedDescription() throws KiwiException {
            Command command = Parser.parse("  todo   read   book  ");
            TaskList tasks = new TaskList();
            command.execute(tasks, new Ui(), new Storage(tempDir.resolve("kiwi.txt").toString()));

            assertEquals("read book", tasks.get(0).getDescription());
        }

        @Test
        void parse_todoDescriptionWithSaveSeparator_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("todo a | b"));
            assertEquals("Task descriptions cannot contain \" | \".", exception.getMessage());
        }
    }

    @Nested
    class DeadlineCommand {

        @Test
        void parse_validDeadline_returnsAddCommandAndAddsTask() throws KiwiException {
            Command command = Parser.parse("deadline return book /by 2019-12-02");
            assertInstanceOf(AddCommand.class, command);

            TaskList tasks = new TaskList();
            command.execute(tasks, new Ui(), new Storage(tempDir.resolve("kiwi.txt").toString()));

            assertEquals(1, tasks.size());
            Deadline deadline = (Deadline) tasks.get(0);
            assertEquals("return book", deadline.getDescription());
            assertEquals(LocalDate.of(2019, 12, 2), deadline.getBy());
        }

        @Test
        void parse_deadlineWithoutBody_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("deadline"));
            assertEquals("A deadline needs details — try: deadline return book /by 2019-12-02",
                    exception.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "deadline return book",
            "deadline  /by 2019-12-02",
            "deadline return book /by "
        })
        void parse_incompleteDeadline_throwsKiwiException(String input) {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse(input));
            assertEquals(
                    "Deadlines need both a description and /by yyyy-MM-dd — "
                            + "e.g. deadline return book /by 2019-12-02",
                    exception.getMessage());
        }

        @Test
        void parse_deadlineWithExtraSpacesAroundBy_parsesDate() throws KiwiException {
            Command command = Parser.parse("deadline return book  /by  2019-12-02");
            TaskList tasks = new TaskList();
            command.execute(tasks, new Ui(), new Storage(tempDir.resolve("kiwi.txt").toString()));

            Deadline deadline = (Deadline) tasks.get(0);
            assertEquals(LocalDate.of(2019, 12, 2), deadline.getBy());
        }

        @Test
        void parse_deadlineWithDuplicateBy_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () ->
                    Parser.parse("deadline return book /by 2019-12-02 /by 2019-12-03"));
            assertEquals(
                    "/by appears more than once. Deadlines need both a description and /by yyyy-MM-dd — "
                            + "e.g. deadline return book /by 2019-12-02",
                    exception.getMessage());
        }

        @Test
        void parse_deadlineWithFromFlag_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () ->
                    Parser.parse("deadline return book /from 2019-12-02"));
            assertEquals(
                    "Unexpected /from. Deadlines need both a description and /by yyyy-MM-dd — "
                            + "e.g. deadline return book /by 2019-12-02",
                    exception.getMessage());
        }

        @Test
        void parse_deadlineWithExtraWordsAfterDate_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () ->
                    Parser.parse("deadline return book /by 2019-12-02 extra"));
            assertEquals(
                    "Please use only a yyyy-MM-dd date after /by — "
                            + "e.g. deadline return book /by 2019-12-02",
                    exception.getMessage());
        }

        @Test
        void parse_deadlineWithFreeFormByText_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () ->
                    Parser.parse("deadline do homework /by no idea :-p"));
            assertEquals("Please use a date as yyyy-MM-dd, e.g. 2019-12-02", exception.getMessage());
        }

        @Test
        void parse_deadlineWithMashedByFlag_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () ->
                    Parser.parse("deadline return book/by 2019-12-02"));
            assertEquals(
                    "Please put spaces around /by. Deadlines need both a description and /by yyyy-MM-dd — "
                            + "e.g. deadline return book /by 2019-12-02",
                    exception.getMessage());
        }
    }

    @Nested
    class EventCommand {

        @Test
        void parse_validEvent_returnsAddCommandAndAddsTask() throws KiwiException {
            Command command = Parser.parse("event meeting /from 2019-10-04 /to 2019-10-11");
            assertInstanceOf(AddCommand.class, command);

            TaskList tasks = new TaskList();
            command.execute(tasks, new Ui(), new Storage(tempDir.resolve("kiwi.txt").toString()));

            assertEquals(1, tasks.size());
            Event event = (Event) tasks.get(0);
            assertEquals("meeting", event.getDescription());
            assertTrue(event.occursOn(LocalDate.of(2019, 10, 7)));
        }

        @Test
        void parse_eventWithoutBody_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("event"));
            assertEquals("An event needs details — try: event meeting /from 2019-10-04 /to 2019-10-11",
                    exception.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "event meeting",
            "event  /from 2019-10-04 /to 2019-10-11",
            "event meeting /from 2019-10-04",
            "event meeting /from  /to 2019-10-11",
            "event meeting /from 2019-10-04 /to "
        })
        void parse_incompleteEvent_throwsKiwiException(String input) {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse(input));
            assertEquals(
                    "Events need /from and /to as yyyy-MM-dd — "
                            + "e.g. event meeting /from 2019-10-04 /to 2019-10-11",
                    exception.getMessage());
        }

        @Test
        void parse_eventWithToBeforeFrom_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () ->
                    Parser.parse("event meeting /to 2019-10-11 /from 2019-10-04"));
            assertEquals(
                    "Please put /from before /to. Events need /from and /to as yyyy-MM-dd — "
                            + "e.g. event meeting /from 2019-10-04 /to 2019-10-11",
                    exception.getMessage());
        }

        @Test
        void parse_eventWithDuplicateFrom_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () ->
                    Parser.parse("event meeting /from 2019-10-04 /from 2019-10-05 /to 2019-10-11"));
            assertEquals(
                    "/from appears more than once. Events need /from and /to as yyyy-MM-dd — "
                            + "e.g. event meeting /from 2019-10-04 /to 2019-10-11",
                    exception.getMessage());
        }

        @Test
        void parse_eventEndBeforeStart_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () ->
                    Parser.parse("event meeting /from 2019-10-11 /to 2019-10-04"));
            assertEquals("Event end date cannot be before the start date.", exception.getMessage());
        }
    }

    @Nested
    class OnCommandParsing {

        @Test
        void parse_validOnDate_returnsOnCommand() throws KiwiException {
            assertInstanceOf(OnCommand.class, Parser.parse("on 2019-12-02"));
        }

        @Test
        void parse_onWithoutDate_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("on"));
            assertEquals("Please give a date, e.g. on 2019-12-02", exception.getMessage());
        }
    }

    @Nested
    class FindCommandParsing {

        @Test
        void parse_findWithKeyword_returnsFindCommand() throws KiwiException {
            assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        }

        @Test
        void parse_findWithoutKeyword_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("find"));
            assertEquals("Please give a keyword to search for, e.g. find book", exception.getMessage());
        }

        @Test
        void parse_findWithOnlySpaces_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("find   "));
            assertEquals("Please give a keyword to search for, e.g. find book", exception.getMessage());
        }
    }

    @Nested
    class FreeCommandParsing {

        @Test
        void parse_freeHours_returnsFreeCommand() throws KiwiException {
            assertInstanceOf(FreeCommand.class, Parser.parse("free 4"));
        }

        @Test
        void parse_freeHoursWithFromDate_returnsFreeCommand() throws KiwiException {
            assertInstanceOf(FreeCommand.class, Parser.parse("free 4 /from 2019-12-01"));
        }

        @Test
        void parse_freeWithoutHours_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("free"));
            assertEquals("Please give a number of hours, e.g. free 4", exception.getMessage());
        }

        @Test
        void parse_freeNonNumericHours_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("free abc"));
            assertEquals("That duration doesn't look like a number: abc", exception.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {"free 0", "free -1"})
        void parse_freeNonPositiveHours_throwsKiwiException(String input) {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse(input));
            assertEquals("Please give a positive number of hours, e.g. free 4", exception.getMessage());
        }

        @Test
        void parse_freeHoursLongerThanWorkDay_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("free 11"));
            assertEquals(
                    "A free slot must fit in one work day (08:00-18:00, 10 hours).",
                    exception.getMessage());
        }

        @Test
        void parse_freeFromWithoutDate_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("free 4 /from"));
            assertEquals("Please give a start date, e.g. free 4 /from 2019-12-01",
                    exception.getMessage());
        }
    }

    @Nested
    class TaskNumberCommands {

        @Test
        void parse_markCommand_marksTaskAtOneBasedIndex() throws KiwiException {
            TaskList tasks = new TaskList();
            tasks.add(new Todo("read book"));

            Command command = Parser.parse("mark 1");
            assertInstanceOf(MarkCommand.class, command);
            command.execute(tasks, new Ui(), new Storage(tempDir.resolve("kiwi.txt").toString()));

            assertTrue(tasks.get(0).isDone());
        }

        @Test
        void parse_unmarkCommand_unmarksTaskAtOneBasedIndex() throws KiwiException {
            TaskList tasks = new TaskList();
            Todo todo = new Todo("read book");
            todo.markAsDone();
            tasks.add(todo);

            Command command = Parser.parse("unmark 1");
            assertInstanceOf(UnmarkCommand.class, command);
            command.execute(tasks, new Ui(), new Storage(tempDir.resolve("kiwi.txt").toString()));

            assertTrue(!tasks.get(0).isDone());
        }

        @Test
        void parse_deleteCommand_removesTaskAtOneBasedIndex() throws KiwiException {
            TaskList tasks = new TaskList();
            tasks.add(new Todo("first"), new Todo("second"));

            Command command = Parser.parse("delete 2");
            assertInstanceOf(DeleteCommand.class, command);
            command.execute(tasks, new Ui(), new Storage(tempDir.resolve("kiwi.txt").toString()));

            assertEquals(1, tasks.size());
            assertEquals("first", tasks.get(0).getDescription());
        }

        @ParameterizedTest
        @ValueSource(strings = {"mark", "unmark", "delete"})
        void parse_taskNumberCommandWithoutNumber_throwsKiwiException(String commandWord) {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse(commandWord));
            assertEquals("Please give a task number, e.g. " + commandWord + " 1", exception.getMessage());
        }

        @Test
        void parse_markWithNonNumericArgument_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("mark abc"));
            assertEquals("That task number doesn't look like a number: abc", exception.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {"mark 0", "mark -1", "delete 0"})
        void parse_nonPositiveTaskNumber_throwsKiwiException(String input) {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse(input));
            assertEquals("Task numbers start from 1.", exception.getMessage());
        }

        @Test
        void parse_markWithExtraArguments_throwsKiwiException() {
            KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("mark 1 2"));
            assertEquals("Please give only one task number, e.g. mark 1", exception.getMessage());
        }
    }

    @Test
    void parse_unknownCommand_throwsKiwiException() {
        KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("jump"));
        assertEquals(
                "Hmm, Kiwi doesn't recognize that. Try todo, deadline, event, on, find, free, list, "
                        + "mark, unmark, delete, or bye.",
                exception.getMessage());
    }

    @Test
    void parse_blankInput_throwsKiwiException() {
        KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("   "));
        assertEquals("Please type a command, e.g. todo borrow book", exception.getMessage());
    }

    @Test
    void parse_listWithExtraWords_throwsKiwiException() {
        KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("list extra"));
        assertEquals("list doesn't take extra words. Just type: list", exception.getMessage());
    }

    @Test
    void parse_byeWithExtraWords_throwsKiwiException() {
        KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("bye now"));
        assertEquals("bye doesn't take extra words. Just type: bye", exception.getMessage());
    }

    @Test
    void parse_onWithExtraWords_throwsKiwiException() {
        KiwiException exception = assertThrows(KiwiException.class, () -> Parser.parse("on 2019-12-02 extra"));
        assertEquals("on takes only a yyyy-MM-dd date, e.g. on 2019-12-02", exception.getMessage());
    }

    @Test
    void parse_duplicateTodo_throwsKiwiExceptionOnSecondAdd() throws KiwiException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("kiwi.txt").toString());
        Parser.parse("todo read book").execute(tasks, new Ui(), storage);

        Command duplicate = Parser.parse("todo read book");
        KiwiException exception = assertThrows(KiwiException.class, () ->
                duplicate.execute(tasks, new Ui(), storage));
        assertEquals("That task is already in your list.", exception.getMessage());
        assertEquals(1, tasks.size());
    }
}
