package spoon.parser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import spoon.command.Command;
import spoon.exception.*;
import spoon.task.Deadline;
import spoon.task.Event;
import spoon.task.Task;
import spoon.task.TaskList;
import spoon.task.ToDo;

public class ParserTest {

    @Nested
    class ParseInputTests {

        @Test
        public void parseInput_singleWord_returnsSingleElementArray() throws Exception {
            String[] test = Parser.parseInput("word");
            assertArrayEquals(new String[]{"word"}, test);
        }

        @Test
        public void parseInput_multipleWords_returnsTwoElementArray() throws Exception {
            String[] test1 = Parser.parseInput("two words");
            String[] test2 = Parser.parseInput("more than two words");
            assertArrayEquals(new String[]{"two", "words"}, test1);
            assertArrayEquals(new String[]{"more", "than two words"}, test2);
        }

        @Test
        public void parseInput_specialCharacter_throwsInvalidArgumentException() {
            assertThrows(InvalidArgumentException.class, () -> Parser.parseInput("|"));
            assertThrows(InvalidArgumentException.class, () -> Parser.parseInput("two|words"));
        }
    }

    @Nested
    class ParseCommandTests {

        @Test
        public void parseCommand_validInput_returnsCorrectEnum() throws Exception {
            assertEquals(Command.BYE, Parser.parseCommand("bye"));
            assertEquals(Command.LIST, Parser.parseCommand("list"));
            assertEquals(Command.MARK, Parser.parseCommand("mark 1"));
            assertEquals(Command.UNMARK, Parser.parseCommand("unmark 1"));
            assertEquals(Command.DELETE, Parser.parseCommand("delete 1"));
            assertEquals(Command.HELP, Parser.parseCommand("help"));
            assertEquals(Command.ON, Parser.parseCommand("on 01/01/0001"));
            assertEquals(Command.BY, Parser.parseCommand("by 01/01/0001"));
            assertEquals(Command.FIND, Parser.parseCommand("find word"));
            assertEquals(Command.TODO, Parser.parseCommand("todo task"));
            assertEquals(Command.DEADLINE, Parser.parseCommand("deadline task /by 01/01/0101 0101"));
            assertEquals(Command.EVENT, Parser.parseCommand("event task /from 01/01/0001 /to 02/01/0001"));
        }
    }

    @Nested
    class CheckEditTests {
        private TaskList testTaskList;

        @BeforeEach
        void setUp() throws DuplicateTaskException {
            testTaskList = new TaskList();
            testTaskList.add(new ToDo("Task 1"));
            testTaskList.add(new ToDo("Task 2"));
            testTaskList.add(new ToDo("Task 3"));
        }

        @Test
        void checkEdit_validIndex_returnsZeroBasedIndex() throws Exception {
            assertEquals(0, Parser.checkEdit("mark 1", testTaskList));
            assertEquals(2, Parser.checkEdit("unmark 3", testTaskList));
        }

        @Test
        void checkEdit_missingIndexArgument_throwsMissingArgumentException() {
            assertThrows(MissingArgumentException.class, () -> Parser.checkEdit("delete", testTaskList));
            assertThrows(MissingArgumentException.class, () -> Parser.checkEdit("mark   ", testTaskList));
        }

        @Test
        void checkEdit_nonNumericIndex_throwsInvalidFormatException() {
            assertThrows(InvalidFormatException.class, () -> Parser.checkEdit("unmark abc", testTaskList));
            assertThrows(InvalidFormatException.class, () -> Parser.checkEdit("delete one", testTaskList));
        }

        @Test
        void checkEdit_indexOutOfRange_throwsIndexOutOfRangeException() {
            assertThrows(IndexOutOfRangeException.class, () -> Parser.checkEdit("mark 0", testTaskList));
            assertThrows(IndexOutOfRangeException.class, () -> Parser.checkEdit("unmark 4", testTaskList));
            assertThrows(IndexOutOfRangeException.class, () -> Parser.checkEdit("delete -1", testTaskList));
        }
    }

    @Nested
    class CheckDateTests {

        @Test
        void checkDate_validDateFormat_returnsParsedLocalDate() throws Exception {
            LocalDate result = Parser.checkDate("on 01/01/0001");
            assertEquals(LocalDate.of(1, 1, 1), result);
        }

        @Test
        void checkDate_missingArgument_throwsMissingArgumentException() {
            assertThrows(MissingArgumentException.class, () -> Parser.checkDate("by"));
            assertThrows(MissingArgumentException.class, () -> Parser.checkDate("on   "));
        }
    }

    @Nested
    class CheckFindTests {

        @Test
        void checkFind_singleKeyword_returnsKeywordArray() throws Exception {
            String[] result = Parser.checkFind("find book");
            assertArrayEquals(new String[]{"book"}, result);
        }

        @Test
        void checkFind_multipleCommaSeparatedKeywords_returnsSplitTokens() throws Exception {
            String[] result = Parser.checkFind("find book, read, homework");
            assertArrayEquals(new String[]{"book", "read", "homework"}, result);
        }

        @Test
        void checkFind_missingKeyword_throwsMissingArgumentException() {
            assertThrows(MissingArgumentException.class, () -> Parser.checkFind("find"));
            assertThrows(MissingArgumentException.class, () -> Parser.checkFind("find   "));
        }
    }

    @Nested
    class CheckAddTests {

        @Test
        void checkAdd_validToDo_returnsToDoInstance() throws Exception {
            Task task = Parser.checkAdd("todo task");
            assertInstanceOf(ToDo.class, task);
        }

        @Test
        void checkAdd_validDeadline_returnsDeadlineInstance() throws Exception {
            Task task = Parser.checkAdd("deadline task /by 01/01/0001");
            assertInstanceOf(Deadline.class, task);
        }

        @Test
        void checkAdd_validEvent_returnsEventInstance() throws Exception {
            Task task = Parser.checkAdd("event task /from 01/01/0001 0001 /to 02/01/0001 0101");
            assertInstanceOf(Event.class, task);
        }

        @Test
        void checkAdd_emptyDescription_throwsMissingArgumentException() {
            assertThrows(MissingArgumentException.class, () -> Parser.checkAdd("todo"));
            assertThrows(MissingArgumentException.class, () -> Parser.checkAdd("deadline   "));
        }

        @Test
        void checkAdd_deadlineMissingDescriptionOrDelimiter_throwsMissingArgumentException() {
            // Missing description before /by
            assertThrows(MissingArgumentException.class, () -> Parser.checkAdd("deadline /by 01/01/0001"));
            // Missing date after /by
            assertThrows(MissingArgumentException.class, () -> Parser.checkAdd("deadline task /by "));
            // Missing /by flag
            assertThrows(MissingArgumentException.class, () -> Parser.checkAdd("deadline task 01/01/0001 0101"));
        }

        @Test
        void checkAdd_deadlineDuplicateDelimiter_throwsDuplicateArgumentException() {
            assertThrows(DuplicateArgumentException.class, () -> Parser.checkAdd(
                    "deadline task /by 01/01/0001 /by 02/01/0001"));
        }

        @Test
        void checkAdd_eventMissingComponents_throwsMissingArgumentException() {
            // Missing description before /from or /to
            assertThrows(MissingArgumentException.class, () -> Parser.checkAdd(
                    "event /from 01/01/0001 /to 01/01/0001"));
            assertThrows(MissingArgumentException.class, () -> Parser.checkAdd("event /from 01/01/0001 0101"));
            assertThrows(MissingArgumentException.class, () -> Parser.checkAdd("event /to 01/01/0001"));
            // Missing /from flag
            assertThrows(MissingArgumentException.class, () -> Parser.checkAdd(
                    "event task 01/01/0001 0101 /to 01/01/0001 0101"));
            // Missing date after /from
            assertThrows(MissingArgumentException.class, () -> Parser.checkAdd("event task /from /to 01/01/0001"));
            // Missing /to flag
            assertThrows(MissingArgumentException.class, () -> Parser.checkAdd("event task /from 01/01/0001 0101"));
            // Missing date after /to
            assertThrows(MissingArgumentException.class, () -> Parser.checkAdd("event task /from 01/01/0001 /to "));
        }

        @Test
        void checkAdd_eventDuplicateDelimiters_throwsDuplicateArgumentException() {
            // Duplicate /from flags
            assertThrows(DuplicateArgumentException.class, () -> Parser.checkAdd(
                    "event task /from 01/01/0001 /from 02/01/0001 /to 03/01/0001"));
            // Duplicate /to flags
            assertThrows(DuplicateArgumentException.class, () -> Parser.checkAdd(
                    "event task /from 01/01/0001 /to 02/01/0001 /to 03/01/0001"));
        }

        @Test
        void checkAdd_invalidCommand_throwsAssertionError() {
            assertThrows(AssertionError.class, () -> Parser.checkAdd("list tasks"));
        }
    }
}
