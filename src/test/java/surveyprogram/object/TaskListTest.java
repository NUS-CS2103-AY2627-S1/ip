package surveyprogram.object;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import surveyprogram.processor.CommandResult;
import surveyprogram.processor.ReplyType;
import surveyprogram.processor.TaskCommandProcessor;

/** Tests task-list validation, mutation, filtering, and persistence integration. */
class TaskListTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void commands_saveFails_returnGuiErrorsAndRecoverOnNextChange() throws IOException {
        Path blockedParent = temporaryDirectory.resolve("blocked");
        Files.writeString(blockedParent, "not a directory");
        Path saveFile = blockedParent.resolve("tasks.txt");
        TaskList list = new TaskList(10, saveFile);
        TaskCommandProcessor processor = new TaskCommandProcessor(list);

        for (String command : new String[]{"todo work", "mark 1", "unmark 1", "delete 1"}) {
            CommandResult result = processor.process(command);
            assertEquals(ReplyType.ERROR, result.replyType());
            assertTrue(result.response().contains("YOUR CHANGES COULD NOT BE SAVED"));
            assertTrue(result.response().contains("THEY REMAIN IN THIS SESSION ONLY"));
            assertTrue(result.shouldContinue());
        }
        assertTrue(list.getList().isEmpty());
        processor.process("todo retained in memory");
        Files.delete(blockedParent);

        CommandResult recovered = processor.process("todo saved after recovery");

        assertEquals(ReplyType.TASK_CREATED, recovered.replyType());
        TaskList restored = new TaskList(10, saveFile);
        restored.load();
        assertEquals(2, restored.getList().size());
        assertEquals("retained in memory", restored.getList().getFirst().getName());
    }

    @Test
    void add_emptyAndOverCapacity_rejectsInvalidItems() {
        TaskList list = newList(1);

        TaskListResult emptyResult = list.add(new Todo("   "));
        list.add(new Todo("first"));
        TaskListResult fullResult = list.add(new Todo("second"));

        assertEquals(1, list.getList().size());
        assertEquals(TaskListResult.Status.ERROR, emptyResult.status());
        assertTrue(emptyResult.response().contains("IT IS BARREN AND CANNOT BE CREATED."));
        assertEquals(TaskListResult.Status.ERROR, fullResult.status());
        assertTrue(fullResult.response().contains("BUT, THERE IS NO MORE MEMORY TO ALLOCATE."));
    }

    @Test
    void restore_fullList_rejectsExtraItemAndSnapshotCannotBeModified() {
        TaskList list = newList(1);

        assertTrue(list.restore(new Todo("first")));
        assertFalse(list.restore(new Todo("second")));
        assertThrows(UnsupportedOperationException.class, () -> list.getList().add(new Todo("third")));
        assertEquals(1, list.getList().size());
    }

    @Test
    void addAfterTask_invalidParentIndexes_rejectsTask() {
        TaskList list = newList(2);
        list.add(new Todo("parent"));

        TaskListResult negativeResult = list.addAfterTask("child", -1);
        TaskListResult pastEndResult = list.addAfterTask("child", 1);

        assertEquals(TaskListResult.Status.ERROR, negativeResult.status());
        assertEquals(TaskListResult.Status.ERROR, pastEndResult.status());
        assertEquals(1, list.getList().size());
    }

    @Test
    void mark_validAndInvalidIndexes_updatesOnlyValidItem() {
        TaskList list = newList(2);
        list.add(new Todo("first"));

        list.mark(0, false);
        TaskListResult invalidResult = list.mark(-1, false);

        assertTrue(list.getList().getFirst().isDone());
        assertEquals(TaskListResult.Status.ERROR, invalidResult.status());
        assertTrue(invalidResult.response().contains("BUT, IT WAS NEVER THERE IN THE FIRST PLACE."));
    }

    @Test
    void delete_validAndInvalidIndexes_removesOnlyValidItem() {
        TaskList list = newList(2);
        list.add(new Todo("first"));
        list.add(new Todo("second"));

        list.delete(0);
        TaskListResult invalidResult = list.delete(5);

        assertEquals(1, list.getList().size());
        assertEquals("second", list.getList().getFirst().getName());
        assertEquals(TaskListResult.Status.ERROR, invalidResult.status());
        assertTrue(invalidResult.response().contains("BUT, IT WAS NEVER THERE IN THE FIRST PLACE."));
    }

    @Test
    void read_emptyAndPopulatedList_returnsExpectedNumbering() {
        TaskList list = newList(2);
        TaskListResult emptyResult = list.read();
        assertEquals(TaskListResult.Status.EMPTY, emptyResult.status());
        assertTrue(emptyResult.response().contains("BUT, THERE WAS NOTHING TO READ."));

        list.add(new Todo("first"));
        list.add(new Todo("second"));
        String output = list.read().response();

        assertTrue(output.contains("1.[T][ ] first"));
        assertTrue(output.contains("2.[T][ ] second"));
    }

    @Test
    void find_matchingMissingAndBlankKeywords_returnsExpectedResponses() {
        TaskList list = newList(3);
        list.add(new Todo("read book"));
        list.add(new Todo("buy groceries"));
        list.add(new Deadline("return BOOK", LocalDateTime.of(2026, 6, 6, 0, 0)));

        TaskListResult matchingResult = list.find("book");
        TaskListResult missingResult = list.find("phone");
        TaskListResult blankResult = list.find("   ");
        String matchingOutput = matchingResult.response();

        assertTrue(matchingOutput.contains("VERY WELL. HERE IS YOUR MATCHING LIST:"));
        assertTrue(matchingOutput.contains("1.[T][ ] read book"));
        assertTrue(matchingOutput.contains("2.[D][ ] return BOOK"));
        assertFalse(matchingOutput.contains("buy groceries"));
        assertEquals(TaskListResult.Status.SUCCESS, matchingResult.status());
        assertEquals(TaskListResult.Status.EMPTY, missingResult.status());
        assertTrue(missingResult.response().contains("BUT, THERE WAS NOTHING THAT CONFORMS TO THE TERM."));
        assertEquals(TaskListResult.Status.EMPTY, blankResult.status());
        assertTrue(blankResult.response().contains("BUT, THERE WAS NOTHING TO LOCATE."));
    }

    @Test
    void listByDate_mixedTasks_returnsOnlyMatchingDatedTasks() {
        TaskList list = newList(3);
        list.add(new Todo("undated"));
        list.add(new Deadline("due", LocalDateTime.of(2026, 8, 24, 12, 0)));
        list.add(new Event("trip", LocalDateTime.of(2026, 8, 23, 8, 0),
                LocalDateTime.of(2026, 8, 25, 18, 0)));

        String output = list.listByDate(LocalDate.of(2026, 8, 24)).response();

        assertFalse(output.contains("undated"));
        assertTrue(output.contains("due"));
        assertTrue(output.contains("trip"));
        assertTrue(list.listByDate(LocalDate.of(2026, 8, 26))
                .response().contains("NOTHING OF CONCERN"));
    }

    @Test
    void load_afterMutations_restoresAllTypesAndCompletionState() {
        Path saveFile = temporaryDirectory.resolve("nested/tasks.txt");
        TaskList original = new TaskList(5, saveFile);
        original.add(new Todo("todo"));
        original.add(new Deadline("deadline", LocalDateTime.of(2026, 8, 24, 17, 30)));
        original.add(new Event("event", LocalDateTime.of(2026, 8, 24, 9, 0),
                LocalDateTime.of(2026, 8, 25, 10, 0)));
        original.mark(1, false);

        TaskList restored = new TaskList(5, saveFile);
        restored.load();

        assertEquals(3, restored.getList().size());
        assertInstanceOf(Todo.class, restored.getList().get(0));
        assertInstanceOf(Deadline.class, restored.getList().get(1));
        assertInstanceOf(Event.class, restored.getList().get(2));
        assertTrue(restored.getList().get(1).isDone());
    }

    @Test
    void afterTask_parentCompletionAndReversal_controlsDependentTask() {
        TaskList list = newList(3);
        list.add(new Todo("parent"));
        list.addAfterTask("child", 0);

        TaskListResult lockedResult = list.mark(1, false);
        list.mark(0, false);
        list.mark(1, false);
        list.mark(0, true);

        assertEquals(TaskListResult.Status.ERROR, lockedResult.status());
        assertFalse(list.getList().get(0).isDone());
        assertFalse(list.getList().get(1).isDone());
    }

    @Test
    void delete_parent_removesAllDescendants() {
        TaskList list = newList(4);
        list.add(new Todo("parent"));
        list.addAfterTask("child", 0);
        list.addAfterTask("grandchild", 1);
        list.add(new Todo("unrelated"));

        TaskListResult result = list.delete(0);

        assertEquals(1, list.getList().size());
        assertEquals("unrelated", list.getList().getFirst().getName());
        assertTrue(result.response().contains("AS WITH ITS 2 CHILDREN."));
    }

    @Test
    void load_parentDependentTask_restoresRelationship() {
        Path saveFile = temporaryDirectory.resolve("after/tasks.txt");
        TaskList original = new TaskList(3, saveFile);
        original.add(new Todo("parent"));
        original.addAfterTask("child", 0);
        original.add(new Todo("later unrelated task"));

        TaskList restored = new TaskList(3, saveFile);
        restored.load();
        restored.mark(0, false);
        TaskListResult result = restored.mark(1, false);

        assertEquals("child", restored.getList().get(1).getName());
        assertEquals(TaskListResult.Status.SUCCESS, result.status());
        assertTrue(restored.getList().get(1).isDone());
    }

    private TaskList newList(int capacity) {
        return new TaskList(capacity, temporaryDirectory.resolve("tasks.txt"));
    }
}
