package edith.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import edith.exception.EdithException;
import edith.task.TaskList;
import edith.task.Todo;
import edith.ui.Ui;

/** Tests tag changes and the decision to save only when a task changes. */
public class TagCommandTest {
    @Test
    public void tagCommand_mixedNewAndExistingTags_savesOnlyChangedTask() throws EdithException {
        Todo todo = new Todo("read book");
        todo.addTag("#Fun");
        TaskList tasks = new TaskList(List.of(todo));
        List<String> output = new ArrayList<>();
        AtomicInteger saveCount = new AtomicInteger();
        Ui ui = new Ui(output::add);
        TagCommand command = new TagCommand(1, List.of("#fun", "#school")) {
            @Override
            protected void saveTasks(TaskList ignored) {
                saveCount.incrementAndGet();
            }
        };
        TagCommand duplicateCommand = new TagCommand(1, List.of("#FUN", "#school")) {
            @Override
            protected void saveTasks(TaskList ignored) {
                saveCount.incrementAndGet();
            }
        };

        command.execute(tasks, ui);
        duplicateCommand.execute(tasks, ui);

        assertEquals(1, saveCount.get());
        assertEquals(List.of("Got it. I've added tags to this task:",
                "  [T][ ] read book [tags: #Fun #school]",
                "No tags were added; this task already has them:",
                "  [T][ ] read book [tags: #Fun #school]"), output);
    }

    @Test
    public void untagCommand_mixedPresentAndAbsentTags_savesOnlyChangedTask() throws EdithException {
        Todo todo = new Todo("read book");
        todo.addTag("#Fun");
        todo.addTag("#school");
        TaskList tasks = new TaskList(List.of(todo));
        List<String> output = new ArrayList<>();
        AtomicInteger saveCount = new AtomicInteger();
        Ui ui = new Ui(output::add);
        UntagCommand command = new UntagCommand(1, List.of("#FUN", "#missing")) {
            @Override
            protected void saveTasks(TaskList ignored) {
                saveCount.incrementAndGet();
            }
        };
        UntagCommand absentCommand = new UntagCommand(1, List.of("#missing")) {
            @Override
            protected void saveTasks(TaskList ignored) {
                saveCount.incrementAndGet();
            }
        };

        command.execute(tasks, ui);
        absentCommand.execute(tasks, ui);

        assertEquals(1, saveCount.get());
        assertEquals(List.of("Noted. I've removed tags from this task:",
                "  [T][ ] read book [tags: #school]",
                "No tags were removed; this task does not have them:",
                "  [T][ ] read book [tags: #school]"), output);
    }
}
