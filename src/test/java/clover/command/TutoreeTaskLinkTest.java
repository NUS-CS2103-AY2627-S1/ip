package clover.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import clover.exception.CloverException;
import clover.storage.Storage;
import clover.task.TaskList;
import clover.tutoree.Tutoree;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/** Tests optional links from tasks to existing tutorees. */
class TutoreeTaskLinkTest {
    @TempDir
    Path tempDir;

    @Test
    void taskCommands_existingTutoree_allTaskTypesLinkToStoredName() throws CloverException {
        TaskList tasks = new TaskList();
        TutoreeList tutorees = tutoreesWithAlice();

        new ToDoCommand("prepare worksheet /for alice tan").execute(tasks, tutorees, new Ui(), storage());
        new DeadlineCommand("collect fee /by 2026-09-30 /for Alice Tan")
                .execute(tasks, tutorees, new Ui(), storage());
        new EventCommand("lesson /from 2026-09-20 /to 2026-09-21 /for Alice Tan")
                .execute(tasks, tutorees, new Ui(), storage());

        assertEquals("[T] [ ] prepare worksheet (for: Alice Tan)", tasks.get(0).toString());
        assertEquals("[D] [ ] collect fee (by: Sep 30 2026) (for: Alice Tan)", tasks.get(1).toString());
        assertEquals("[E] [ ] lesson (from: Sep 20 2026 to: Sep 21 2026) (for: Alice Tan)",
                tasks.get(2).toString());
    }

    @Test
    void todo_unknownTutoree_exceptionThrownWithoutAddingTask() {
        TaskList tasks = new TaskList();

        CloverException exception = assertThrows(CloverException.class, () -> new ToDoCommand(
                "prepare worksheet /for Alice Tan").execute(tasks, new TutoreeList(), new Ui(), storage()));

        assertEquals("No learning companion named \"Alice Tan\" is in the grove. Add them before linking a quest.",
                exception.getMessage());
        assertEquals(0, tasks.size());
    }

    private TutoreeList tutoreesWithAlice() {
        TutoreeList tutorees = new TutoreeList();
        tutorees.add(new Tutoree("Alice Tan", "12 Example Road", "50"));
        return tutorees;
    }

    private Storage storage() {
        return new Storage(tempDir.resolve("data/clover.txt"), tempDir.resolve("data/tutorees.txt"));
    }
}
