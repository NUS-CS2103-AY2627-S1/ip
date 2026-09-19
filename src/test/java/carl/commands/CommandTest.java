package carl.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import carl.exceptions.CarlCommandException;
import carl.task.Deadline;
import carl.task.Event;
import carl.task.Task;
import carl.task.TaskList;
import carl.task.TaskManager;
import carl.task.Todo;
import carl.ui.Ui;

public class CommandTest {
    private static final LocalDateTime OCTOBER_FIRST_NOON =
            LocalDateTime.of(2026, 10, 1, 12, 0);

    @TempDir
    private Path temporaryDirectory;

    private Ui ui;
    private TaskManager storage;

    @BeforeEach
    public void setUp() {
        ui = new Ui();
        storage = new TaskManager(temporaryDirectory.resolve("save.txt"));
    }

    @Test
    public void addCommands_validTasks_addAndPersistEveryTaskType() throws Exception {
        TaskList tasks = new TaskList(List.of());

        CommandResult todoResult = new TodoCommand("buy milk")
                .onRun(ui, storage, tasks, "todo buy milk");
        CommandResult deadlineResult = new DeadlineCommand("submit report", OCTOBER_FIRST_NOON)
                .onRun(ui, storage, tasks, "deadline submit report");
        CommandResult eventResult = new EventCommand("workshop", OCTOBER_FIRST_NOON,
                OCTOBER_FIRST_NOON.plusHours(2)).onRun(ui, storage, tasks, "event workshop");

        assertEquals(3, tasks.getTasksLeft());
        assertTrue(todoResult.message().contains("buy milk"));
        assertTrue(deadlineResult.message().contains("submit report"));
        assertTrue(eventResult.message().contains("workshop"));
        assertEquals(tasks.toSaveString(), Files.readString(temporaryDirectory.resolve("save.txt")));
    }

    @Test
    public void targetedCommands_validIndex_markUnmarkAndDeleteTask() throws Exception {
        TaskList tasks = new TaskList(List.of(new Todo("buy milk")));

        CommandResult marked = new MarkCommand(1).onRun(ui, storage, tasks, "mark 1");
        CommandResult unmarked = new UnmarkCommand(1).onRun(ui, storage, tasks, "unmark 1");
        CommandResult deleted = new DeleteCommand(1).onRun(ui, storage, tasks, "delete 1");

        assertTrue(marked.message().contains("Checked off"));
        assertTrue(unmarked.message().contains("unmarked"));
        assertTrue(deleted.message().contains("removed"));
        assertTrue(tasks.getAllTasks().isEmpty());
    }

    @Test
    public void queryCommands_existingTasks_returnMatchingReadableResults() throws Exception {
        Deadline deadline = new Deadline("Alpha report", OCTOBER_FIRST_NOON);
        Event event = new Event("Bravo workshop", OCTOBER_FIRST_NOON.minusHours(1),
                OCTOBER_FIRST_NOON.plusHours(1));
        Todo todo = new Todo("Zulu errand");
        TaskList tasks = new TaskList(List.of(todo, event, deadline));

        String listMessage = new ListCommand().onRun(ui, storage, tasks, "list").message();
        String findMessage = new FindCommand("bravo").onRun(ui, storage, tasks, "find bravo").message();
        String dueMessage = new DueCommand(LocalDate.of(2026, 10, 1))
                .onRun(ui, storage, tasks, "due 2026-10-01").message();
        String sortMessage = new SortCommand().onRun(ui, storage, tasks, "sort").message();

        assertTrue(listMessage.contains("Zulu errand"));
        assertTrue(findMessage.contains("Bravo workshop"));
        assertFalse(findMessage.contains("Alpha report"));
        assertTrue(dueMessage.contains("Alpha report"));
        assertTrue(dueMessage.contains("Bravo workshop"));
        assertTrue(sortMessage.indexOf("Alpha report") < sortMessage.indexOf("Zulu errand"));
    }

    @Test
    public void informationalCommands_run_returnExpectedFlagsAndMessages() throws Exception {
        TaskList tasks = new TaskList(List.of());

        CommandResult help = new HelpCommand().onRun(ui, storage, tasks, "help");
        ByeCommand byeCommand = new ByeCommand();
        CommandResult bye = byeCommand.onRun(ui, storage, tasks, "bye");

        assertTrue(help.message().contains("`deadline`"));
        assertFalse(help.isExited());
        assertTrue(bye.isExited());
    }

    @Test
    public void modifyingCommand_duplicateTask_throwsAndPreservesOriginalList() throws Exception {
        Task original = new Todo("buy milk");
        TaskList tasks = new TaskList(List.of(original));

        assertThrows(CarlCommandException.class, () ->
                new TodoCommand("BUY MILK").onRun(ui, storage, tasks, "todo BUY MILK"));
        assertEquals(List.of(original), tasks.getAllTasks());
    }

    @Test
    public void commandResultFactories_setCorrectFlags() {
        CommandResult success = CommandResult.createSuccess("ok");
        CommandResult exit = CommandResult.createExit("bye");
        CommandResult error = CommandResult.createError("bad");

        assertEquals(new CommandResult("ok", false, false), success);
        assertEquals(new CommandResult("bye", true, false), exit);
        assertEquals(new CommandResult("bad", false, true), error);
    }
}
