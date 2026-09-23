package sallman.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import sallman.SallmanException;
import sallman.Storage;
import sallman.TaskList;
import sallman.Ui;
import sallman.task.Deadline;
import sallman.task.Task;
import sallman.task.Todo;

/**
 * Tests that each command changes the list as asked, tells the user what it
 * did, saves what it changed, and refuses requests that would change nothing
 * without leaving a step behind for undo.
 */
public class CommandTest {

    @TempDir
    private Path folder;

    private TaskList tasks;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        ui = new Ui(false);
        storage = new Storage(folder.resolve("tasks.txt").toString());
    }

    /**
     * Adds todos through the add command, as a user would, and discards the replies.
     *
     * @param descriptions what each todo involves, added in order
     */
    private void addTodos(String... descriptions) throws SallmanException {
        for (String description : descriptions) {
            new AddCommand(new Todo(description)).execute(tasks, ui, storage);
        }
        ui.drainText();
    }

    /**
     * Returns what is currently saved, as it would be shown to the user.
     *
     * @return each saved task in its display form
     */
    private List<String> saved() throws SallmanException {
        return storage.load().stream().map(Task::toString).toList();
    }

    @Test
    public void add_newTask_addedConfirmedAndSaved() throws Exception {
        new AddCommand(new Todo("read book")).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertTrue(ui.drainText().contains("[T][ ] read book"));
        assertEquals(List.of("[T][ ] read book"), saved());
    }

    @Test
    public void add_sameTaskAgain_refusedAndNamesTheExistingOne() throws Exception {
        addTodos("read book");

        AddCommand duplicate = new AddCommand(new Todo("Read Book"));
        SallmanException e = assertThrows(SallmanException.class, () -> duplicate.execute(tasks, ui, storage));

        assertEquals("Great minds think alike! You already have that task, so I didn't add it again:",
                e.getMessage());
        assertEquals("  1.[T][ ] read book", e.toLines()[1]);
        assertEquals(1, tasks.size());
    }

    @Test
    public void add_refusedDuplicate_leavesNoUndoStep() throws Exception {
        addTodos("read book");
        int undoStepsBefore = tasks.getUndoCount();

        AddCommand duplicate = new AddCommand(new Todo("read book"));
        assertThrows(SallmanException.class, () -> duplicate.execute(tasks, ui, storage));

        assertEquals(undoStepsBefore, tasks.getUndoCount());
    }

    @Test
    public void delete_existingTask_removedReportedAndSaved() throws Exception {
        addTodos("read book", "buy milk");

        new DeleteCommand("1").execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        List<String> reply = ui.drainText().lines().toList();
        assertEquals("  [T][ ] read book", reply.get(1));
        assertEquals("You now have 1 task in your list.", reply.get(2));
        assertEquals(List.of("[T][ ] buy milk"), saved());
    }

    @Test
    public void delete_numberOutsideTheList_refusedAndListUnchanged() throws Exception {
        addTodos("read book");

        DeleteCommand delete = new DeleteCommand("5");
        assertThrows(SallmanException.class, () -> delete.execute(tasks, ui, storage));

        assertEquals(1, tasks.size());
    }

    @Test
    public void mark_taskAlreadyDone_refusedAndLeavesNoUndoStep() throws Exception {
        addTodos("read book");
        new MarkCommand(true, "1").execute(tasks, ui, storage);
        int undoStepsBefore = tasks.getUndoCount();

        MarkCommand markAgain = new MarkCommand(true, "1");
        SallmanException e = assertThrows(SallmanException.class, () -> markAgain.execute(tasks, ui, storage));

        assertEquals("Great news! Task 1 is already marked as done:", e.getMessage());
        assertEquals(undoStepsBefore, tasks.getUndoCount());
    }

    @Test
    public void unmark_doneTask_markedNotDoneAndSaved() throws Exception {
        addTodos("read book");
        new MarkCommand(true, "1").execute(tasks, ui, storage);
        ui.drainText();

        new MarkCommand(false, "1").execute(tasks, ui, storage);

        assertFalse(tasks.get(0).isDone());
        assertTrue(ui.drainText().startsWith("No problem! I've marked this task as not done yet:"));
        assertEquals(List.of("[T][ ] read book"), saved());
    }

    @Test
    public void unmark_taskNotDone_refused() throws Exception {
        addTodos("read book");

        MarkCommand unmark = new MarkCommand(false, "1");
        SallmanException e = assertThrows(SallmanException.class, () -> unmark.execute(tasks, ui, storage));

        assertTrue(e.getMessage().startsWith("It looks like task 1 isn't marked as done"));
    }

    @Test
    public void tag_newTags_attachedAndSaved() throws Exception {
        addTodos("read book");

        new TagCommand(true, "1 fun #books").execute(tasks, ui, storage);

        assertEquals(Set.of("fun", "books"), tasks.get(0).getTags());
        assertEquals(List.of("[T][ ] read book #fun #books"), saved());
    }

    @Test
    public void untag_existingTag_removedAndSaved() throws Exception {
        addTodos("read book");
        new TagCommand(true, "1 fun books").execute(tasks, ui, storage);

        new TagCommand(false, "1 books").execute(tasks, ui, storage);

        assertEquals(Set.of("fun"), tasks.get(0).getTags());
        assertEquals(List.of("[T][ ] read book #fun"), saved());
    }

    @Test
    public void tag_tagAlreadyThere_saysSoAndLeavesNoUndoStep() throws Exception {
        addTodos("read book");
        new TagCommand(true, "1 fun").execute(tasks, ui, storage);
        ui.drainText();
        int undoStepsBefore = tasks.getUndoCount();

        new TagCommand(true, "1 FUN").execute(tasks, ui, storage);

        assertTrue(ui.drainText().startsWith("It looks like that task already has every tag you named:"));
        assertEquals(undoStepsBefore, tasks.getUndoCount());
    }

    @Test
    public void undo_afterDelete_taskBackAndSaved() throws Exception {
        addTodos("read book");
        new DeleteCommand("1").execute(tasks, ui, storage);
        ui.drainText();

        new UndoCommand().execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertTrue(ui.drainText().startsWith("I apologise for any confusion!"));
        assertEquals(List.of("[T][ ] read book"), saved());
    }

    @Test
    public void undo_nothingToUndo_refused() {
        UndoCommand undo = new UndoCommand();

        assertThrows(SallmanException.class, () -> undo.execute(tasks, ui, storage));
    }

    @Test
    public void sort_byName_reorderedSavedAndUndoable() throws Exception {
        addTodos("zebra", "apple");
        int undoStepsBefore = tasks.getUndoCount();

        new SortCommand("name").execute(tasks, ui, storage);

        assertEquals("[T][ ] apple", tasks.get(0).toString());
        assertEquals(List.of("[T][ ] apple", "[T][ ] zebra"), saved());
        assertEquals(undoStepsBefore + 1, tasks.getUndoCount());
    }

    @Test
    public void sort_listAlreadyInThatOrder_showsItButLeavesNoUndoStep() throws Exception {
        // Otherwise undo after it would appear to do nothing, and repeated sorts
        // would push real changes out of the undo history.
        addTodos("apple", "zebra");
        int undoStepsBefore = tasks.getUndoCount();

        new SortCommand("name").execute(tasks, ui, storage);

        assertEquals(undoStepsBefore, tasks.getUndoCount());
        assertTrue(ui.drainText().startsWith("Here's a carefully sorted overview of your list, by name:"));
    }

    @Test
    public void sort_unknownOrder_refusedAndListUnchanged() throws Exception {
        addTodos("zebra", "apple");

        SortCommand sort = new SortCommand("sideways");
        assertThrows(SallmanException.class, () -> sort.execute(tasks, ui, storage));

        assertEquals("[T][ ] zebra", tasks.get(0).toString());
    }

    @Test
    public void find_matchingKeyword_showsOnlyTheMatches() throws Exception {
        addTodos("read book", "buy milk");

        new FindCommand("book").execute(tasks, ui, storage);

        assertEquals(List.of("I found some tasks that match \"book\":", "1.[T][ ] read book"),
                ui.drainText().lines().toList());
    }

    @Test
    public void find_thenDeleteTheNumberShown_removesTheTaskThatWasFound() throws Exception {
        addTodos("read book", "buy bread");
        new FindCommand("bread").execute(tasks, ui, storage);
        String shownNumber = ui.drainText().lines().toList().get(1).split("\\.")[0];

        new DeleteCommand(shownNumber).execute(tasks, ui, storage);

        assertEquals(List.of("[T][ ] read book"), saved());
    }

    @Test
    public void on_dateWithADeadline_showsIt() throws Exception {
        new AddCommand(new Deadline("return book", LocalDate.of(2019, 10, 15))).execute(tasks, ui, storage);
        ui.drainText();

        new OnCommand(LocalDate.of(2019, 10, 15)).execute(tasks, ui, storage);

        assertEquals("1.[D][ ] return book (by: Oct 15 2019)", ui.drainText().lines().toList().get(1));
    }

    @Test
    public void list_andExit_changeNothing() throws Exception {
        addTodos("read book");

        new ListCommand().execute(tasks, ui, storage);
        ExitCommand exit = new ExitCommand();
        exit.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertTrue(exit.isExit());
        assertFalse(new ListCommand().isExit());
    }
}
