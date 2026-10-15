package nova.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import nova.command.AddCommand;
import nova.command.Command;
import nova.command.DeleteCommand;
import nova.command.ExitCommand;
import nova.command.FindCommand;
import nova.command.HelpCommand;
import nova.command.ListCommand;
import nova.command.MarkCommand;
import nova.command.ShowOnDateCommand;
import nova.exception.NovaException;
import nova.storage.Storage;
import nova.task.Task;
import nova.task.TaskList;
import nova.task.TaskList.NumberedTask;
import nova.task.Todo;
import nova.ui.Ui;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_simpleCommands_returnsCorrectCommandTypes() throws NovaException {
        Command exitCommand = parser.parse("bye");

        assertInstanceOf(ExitCommand.class, exitCommand);
        assertTrue(exitCommand.isExit());
        assertInstanceOf(ListCommand.class, parser.parse("list"));
        assertFalse(parser.parse("list").isExit());
        assertInstanceOf(HelpCommand.class, parser.parse("help"));
        assertFalse(parser.parse("help").isExit());
    }

    @Test
    void parse_addCommands_executeAddsTasksWithParsedDetails() throws Exception {
        assertEquals("[T][ ] read book", parseAndExecuteAdd("todo read book").toString());
        assertEquals("[D][ ] submit report (by: Aug 24 2026)",
                parseAndExecuteAdd("deadline submit report /by 2026-08-24").toString());
        assertEquals("[E][ ] project meeting (from: 2pm to: 4pm)",
                parseAndExecuteAdd("event project meeting /from 2pm /to 4pm").toString());
    }

    @Test
    void parse_numberedCommands_executeUsesParsedTaskNumbersAndMarkState() throws Exception {
        Todo firstTask = new Todo("first");
        firstTask.markAsDone();
        Todo secondTask = new Todo("second");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask), new NoOpStorage());
        Ui ui = new NoOpUi();

        Command unmarkCommand = parser.parse("unmark 1");
        Command markCommand = parser.parse("mark 2");
        Command deleteCommand = parser.parse("delete 1");

        assertInstanceOf(MarkCommand.class, unmarkCommand);
        assertInstanceOf(MarkCommand.class, markCommand);
        assertInstanceOf(DeleteCommand.class, deleteCommand);
        unmarkCommand.execute(taskList, ui);
        markCommand.execute(taskList, ui);
        assertFalse(firstTask.isDone());
        assertTrue(secondTask.isDone());
        deleteCommand.execute(taskList, ui);
        assertEquals(List.of(secondTask), taskList.getTasks());
    }

    @Test
    void parse_dateSearch_executeUsesParsedDate() throws Exception {
        NoOpUi ui = new NoOpUi();
        Command command = parser.parse("on 2026-08-24");

        assertInstanceOf(ShowOnDateCommand.class, command);
        command.execute(new TaskList(List.of(), new NoOpStorage()), ui);

        assertEquals(LocalDate.of(2026, 8, 24), ui.shownDate);
    }

    @Test
    void parse_findCommand_executeUsesParsedKeyword() throws Exception {
        NoOpUi ui = new NoOpUi();
        Todo matchingTask = new Todo("read book");
        TaskList taskList = new TaskList(List.of(matchingTask, new Todo("write tests")),
                new NoOpStorage());
        Command command = parser.parse("find book");

        assertInstanceOf(FindCommand.class, command);
        command.execute(taskList, ui);

        assertEquals(1, ui.matchingTasks.size());
        assertEquals(1, ui.matchingTasks.get(0).getTaskNumber());
        assertEquals(matchingTask, ui.matchingTasks.get(0).getTask());
    }

    @Test
    void parse_unknownOrSimilarCommand_throwsUnknownCommandError() {
        String unknownCommandError = "I'm sorry, but I don't know what that means :-(";

        assertParseError("", unknownCommandError);
        assertParseError("listing", unknownCommandError);
        assertParseError("list extra", unknownCommandError);
        assertParseError("bye extra", unknownCommandError);
        assertParseError("help extra", unknownCommandError);
        assertParseError("help ", unknownCommandError);
        assertParseError(" help", unknownCommandError);
        assertParseError("Help", unknownCommandError);
        assertParseError("TODO read book", unknownCommandError);
    }

    @Test
    void parse_todoWithoutDescription_throwsDescriptionError() {
        assertParseError("todo", "The description of a todo cannot be empty.");
        assertParseError("todo   ", "The description of a todo cannot be empty.");
    }

    @Test
    void parse_findWithoutKeyword_throwsKeywordError() {
        String keywordError = "The keyword for a find command cannot be empty.";

        assertParseError("find", keywordError);
        assertParseError("find   ", keywordError);
    }

    @Test
    void parse_numberedCommandWithoutInteger_throwsTaskNumberError() {
        assertParseError("mark", "Please enter a task number, for example: mark 1");
        assertParseError("mark one", "Please enter a task number, for example: mark 1");
        assertParseError("unmark", "Please enter a task number, for example: unmark 1");
        assertParseError("delete 1.5", "Please enter a task number, for example: delete 1");
    }

    @Test
    void parse_malformedDeadline_throwsRelevantFormatOrDateError() {
        String formatError = "A deadline must follow: deadline DESCRIPTION /by yyyy-MM-dd";
        String dateError = "The deadline date must be a valid date in yyyy-MM-dd format.";

        assertParseError("deadline", formatError);
        assertParseError("deadline submit report", formatError);
        assertParseError("deadline /by 2026-08-24", formatError);
        assertParseError("deadline submit report /by", formatError);
        assertParseError("deadline submit report /by 2026-02-30", dateError);
        assertParseError("deadline submit report /by Aug 24 2026", dateError);
    }

    @Test
    void parse_malformedEvent_throwsRelevantFormatOrStartError() {
        String formatError = "An event must follow: event DESCRIPTION /from START /to END";

        assertParseError("event", formatError);
        assertParseError("event meeting /from 2pm", formatError);
        assertParseError("event /from 2pm /to 4pm", formatError);
        assertParseError("event meeting /from 2pm /to", formatError);
        assertParseError("event meeting /from  /to 4pm",
                "An event needs a start date or time after /from.");
    }

    @Test
    void parse_invalidDateSearch_throwsDateError() {
        String dateError = "The date must be a valid date in yyyy-MM-dd format.";

        assertParseError("on", dateError);
        assertParseError("on 2026-02-30", dateError);
        assertParseError("on Aug 24 2026", dateError);
    }

    private Task parseAndExecuteAdd(String input) throws Exception {
        TaskList taskList = new TaskList(List.of(), new NoOpStorage());
        Command command = parser.parse(input);
        assertInstanceOf(AddCommand.class, command);

        command.execute(taskList, new NoOpUi());
        return taskList.getTasks().get(0);
    }

    private void assertParseError(String input, String expectedMessage) {
        NovaException exception = assertThrows(NovaException.class, () -> parser.parse(input));
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static class NoOpStorage extends Storage {
        NoOpStorage() {
            super(Path.of("unused"));
        }

        @Override
        public void save(List<Task> tasks) throws IOException {
            // No persistence is needed for parser tests.
        }
    }

    private static class NoOpUi extends Ui {
        private LocalDate shownDate;
        private List<NumberedTask> matchingTasks;

        @Override
        public void showAddedTask(Task task, int taskCount) {
            // No output is needed for parser tests.
        }

        @Override
        public void showMarkedTask(Task task, boolean isMarked) {
            // No output is needed for parser tests.
        }

        @Override
        public void showDeletedTask(Task task, int taskCount) {
            // No output is needed for parser tests.
        }

        @Override
        public void showDeadlinesOn(LocalDate date) {
            shownDate = date;
        }

        @Override
        public void showMatchingTasks(List<NumberedTask> matchingTasks) {
            this.matchingTasks = List.copyOf(matchingTasks);
        }
    }
}
