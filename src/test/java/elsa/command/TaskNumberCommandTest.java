package elsa.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import elsa.ElsaException;
import elsa.storage.Storage;
import elsa.task.TaskList;
import elsa.task.Todo;
import elsa.ui.Ui;

/**
 * Tests {@link TaskNumberCommand}, the part shared by every command that acts on
 * a task the user numbered.
 *
 * <p>Marking, unmarking and deleting all reach the task the same way, so the
 * checks that a task with that number exists live here rather than three times
 * over. Testing them here says which class is at fault when one breaks, which
 * testing them through a subclass would not.
 *
 * <p>The class is abstract, so these tests go through the smallest concrete one
 * that can exist: it answers with the position it was given rather than doing
 * anything to the task, which is exactly what is being checked.
 */
public class TaskNumberCommandTest {

    /** A command that does nothing but report the position it worked out. */
    private static final class PositionReport extends TaskNumberCommand {
        private PositionReport(int number) {
            super(number, CommandType.MARK);
        }

        @Override
        public String execute(TaskList tasks, Ui ui, Storage storage) throws ElsaException {
            return String.valueOf(indexIn(tasks));
        }
    }

    private static TaskList twoTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        return tasks;
    }

    /** The user counts from 1 and the list counts from 0, so task 1 is position 0. */
    @Test
    public void indexIn_theFirstTask_givesPositionZero() throws ElsaException {
        assertEquals("0", new PositionReport(1).execute(twoTasks(), new Ui(), null));
    }

    @Test
    public void indexIn_theLastTask_givesTheLastPosition() throws ElsaException {
        assertEquals("1", new PositionReport(2).execute(twoTasks(), new Ui(), null));
    }

    /**
     * An empty list is told apart from a number out of range, because the two
     * need different advice: one user has to add a task first, the other has to
     * pick a different number.
     */
    @Test
    public void indexIn_emptyList_saysToAddATaskFirst() {
        PositionReport command = new PositionReport(1);
        TaskList none = new TaskList();
        Ui ui = new Ui();

        ElsaException thrown = assertThrows(ElsaException.class, () -> command.execute(none, ui, null));

        assertTrue(thrown.getMessage().contains("no tasks yet"), thrown.getMessage());
        assertTrue(thrown.getMessage().contains("todo <description>"),
                "the user is not told how to add one");
    }

    @Test
    public void indexIn_numberPastTheEnd_namesTheRangeThatWouldWork() {
        PositionReport command = new PositionReport(3);
        TaskList tasks = twoTasks();
        Ui ui = new Ui();

        ElsaException thrown = assertThrows(ElsaException.class, () -> command.execute(tasks, ui, null));

        assertTrue(thrown.getMessage().contains("There is no task 3"), thrown.getMessage());
        assertTrue(thrown.getMessage().contains("from 1 to 2"), thrown.getMessage());
    }

    /** Numbering starts at 1, so 0 and anything below it name no task. */
    @Test
    public void indexIn_zeroOrBelow_isRefusedLikeAnyOtherNumberOutOfRange() {
        PositionReport zero = new PositionReport(0);
        PositionReport negative = new PositionReport(-1);

        assertThrows(ElsaException.class, () -> zero.execute(twoTasks(), new Ui(), null));
        assertThrows(ElsaException.class, () -> negative.execute(twoTasks(), new Ui(), null));
    }

    /** One task is "1 task", and the range is then "from 1 to 1". */
    @Test
    public void indexIn_listOfOne_countsItInTheSingular() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        PositionReport command = new PositionReport(2);
        Ui ui = new Ui();

        ElsaException thrown = assertThrows(ElsaException.class, () -> command.execute(tasks, ui, null));

        assertTrue(thrown.getMessage().contains("You have 1 task,"), thrown.getMessage());
    }
}
