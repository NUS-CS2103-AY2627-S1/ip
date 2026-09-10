package surveyprogram.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import surveyprogram.object.AfterTask;
import surveyprogram.object.Deadline;
import surveyprogram.object.Event;
import surveyprogram.object.Item;
import surveyprogram.object.TaskList;
import surveyprogram.object.TaskListResult;
import surveyprogram.object.Todo;

/** Tests command recognition, parsing, fallback behavior, and routing. */
class TaskCommandProcessorTest {
    @Test
    void process_byeWithWhitespaceAndMixedCase_returnsFalse() {
        RecordingList list = new RecordingList();
        TaskCommandProcessor processor = new TaskCommandProcessor(list);

        assertFalse(processor.process("  ByE  ").shouldContinue());
        assertTrue(list.addedItems.isEmpty());
    }

    @Test
    void process_supportedDeadlineFormats_createEquivalentDeadlines() {
        List<String> dateInputs = List.of(
                "2026-8-24 17:30", "2026-8-24 1730",
                "2026/8/24 17:30", "2026/8/24 1730",
                "24-8-2026 17:30", "24-8-2026 1730",
                "24/8/2026 17:30", "24/8/2026 1730");
        LocalDateTime expected = LocalDateTime.of(2026, 8, 24, 17, 30);

        for (String dateInput : dateInputs) {
            RecordingList list = new RecordingList();
            new TaskCommandProcessor(list)
                    .process("deadline submit report /by " + dateInput);

            Deadline deadline = assertInstanceOf(Deadline.class, list.addedItems.getFirst());
            assertEquals(expected, deadline.getBy(), dateInput);
        }
    }

    @Test
    void process_dateWithoutTime_defaultsToStartOfDay() {
        RecordingList list = new RecordingList();

        new TaskCommandProcessor(list).process("deadline submit /by 2026-8-24");

        Deadline deadline = assertInstanceOf(Deadline.class, list.addedItems.getFirst());
        assertEquals(LocalDateTime.of(2026, 8, 24, 0, 0), deadline.getBy());
    }

    @Test
    void process_pendingAfterDateWithoutTime_defaultsToStartOfDay() {
        RecordingList list = new RecordingList();

        new TaskCommandProcessor(list).process("pending publish /after 2026-9-24");

        AfterTask task = assertInstanceOf(AfterTask.class, list.addedItems.getFirst());
        assertEquals(LocalDateTime.of(2026, 9, 24, 0, 0), task.getReleaseAt());
    }

    @Test
    void process_pendingAfterTask_routesDescriptionAndOneBasedIndex() {
        RecordingList list = new RecordingList();

        new TaskCommandProcessor(list).process("pending return book /aftertask 3");

        assertEquals("return book", list.afterTaskDescription);
        assertEquals(2, list.parentIndex);
    }

    @Test
    void process_validEvent_parsesBothEndpoints() {
        RecordingList list = new RecordingList();

        new TaskCommandProcessor(list)
                .process("event workshop /from 2026-8-24 0900 /to 25-8-2026 17:30");

        Event event = assertInstanceOf(Event.class, list.addedItems.getFirst());
        assertEquals(LocalDateTime.of(2026, 8, 24, 9, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 8, 25, 17, 30), event.getTo());
    }

    @Test
    void process_invalidDatedCommands_fallBackToTodo() {
        RecordingList list = new RecordingList();
        TaskCommandProcessor processor = new TaskCommandProcessor(list);

        String deadlineOutput = processor.process("deadline submit /by someday").response();
        String eventOutput = processor.process("event meeting /from now").response();

        assertInstanceOf(Todo.class, list.addedItems.get(0));
        assertInstanceOf(Todo.class, list.addedItems.get(1));
        assertTrue(deadlineOutput.contains("YOU MUST BE"));
        assertTrue(eventOutput.contains("YOU MUST BE"));
    }

    @Test
    void process_dateCommand_routesValidDateAndRejectsInvalidDate() {
        RecordingList list = new RecordingList();
        TaskCommandProcessor processor = new TaskCommandProcessor(list);

        processor.process("date 24/8/2026");
        String invalidOutput = processor.process("date yesterday").response();

        assertEquals(LocalDate.of(2026, 8, 24), list.requestedDate);
        assertTrue(invalidOutput.contains("BUT, THE DATE IS INVALID."));
    }

    @Test
    void process_markUnmarkDelete_routesOneBasedIndexesAndRejectsNonNumbers() {
        RecordingList list = new RecordingList();
        TaskCommandProcessor processor = new TaskCommandProcessor(list);

        processor.process("mark 2");
        assertEquals(1, list.markedIndex);
        assertFalse(list.isReverseMark);
        processor.process("unmark 1");
        assertEquals(0, list.markedIndex);
        assertTrue(list.isReverseMark);
        processor.process("delete 3");
        assertEquals(2, list.deletedIndex);
        assertTrue(processor.process("delete no").response()
                .contains("BUT, IT IS INVALID."));
    }

    @Test
    void process_unknownAndListCommands_printExpectedResponses() {
        RecordingList list = new RecordingList();
        TaskCommandProcessor processor = new TaskCommandProcessor(list);

        assertTrue(processor.process("list").response().contains("VERY WELL. HERE IS YOUR LIST:"));
        assertTrue(list.isReadCalled);
        assertTrue(processor.process("unknown").response()
                .contains("WELL, THAT IS NO LONGER A COMMAND."));
    }

    @Test
    void process_findCommand_routesTrimmedKeyword() {
        RecordingList list = new RecordingList();
        TaskCommandProcessor processor = new TaskCommandProcessor(list);

        processor.process("find   book  ");

        assertEquals("book", list.findKeyword);
    }

    @Test
    void process_commands_assignsReplyTypesByOutcome() {
        RecordingList list = new RecordingList();
        TaskCommandProcessor processor = new TaskCommandProcessor(list);

        assertEquals(ReplyType.TASK_CREATED, processor.process("todo read").replyType());
        assertEquals(ReplyType.LIST, processor.process("list").replyType());
        assertEquals(ReplyType.FIND, processor.process("find read").replyType());
        assertEquals(ReplyType.MARK_COMPLETED, processor.process("mark 1").replyType());
        assertEquals(ReplyType.UNMARKED, processor.process("unmark 1").replyType());
        assertEquals(ReplyType.DELETED, processor.process("delete 1").replyType());
        assertEquals(ReplyType.DATE_QUERY, processor.process("date 2026-8-24").replyType());
        assertEquals(ReplyType.ERROR, processor.process("unknown").replyType());
        assertEquals(ReplyType.DEFAULT, processor.process("bye").replyType());
    }

    @Test
    void process_emptyAndInvalidOutcomes_overrideCommandReplyType() {
        TaskCommandProcessor emptyProcessor = new TaskCommandProcessor(new TaskList(10));

        assertEquals(ReplyType.EMPTY, emptyProcessor.process("find absent").replyType());
        assertEquals(ReplyType.EMPTY, emptyProcessor.process("date 2026-8-24").replyType());
        assertEquals(ReplyType.ERROR, emptyProcessor.process("todo ").replyType());
        assertEquals(ReplyType.ERROR, emptyProcessor.process("mark invalid").replyType());
    }

    @Test
    void process_missingSeparatorsAndArguments_returnsSpecificErrorsWithoutRouting() {
        RecordingList list = new RecordingList();
        TaskCommandProcessor processor = new TaskCommandProcessor(list);

        assertEquals(ReplyType.ERROR, processor.process("").replyType());
        assertEquals(ReplyType.ERROR, processor.process("todo").replyType());
        assertTrue(processor.process("date ").response().contains("NOT A DATE"));
        assertTrue(processor.process("delete ").response().contains("NOT SPECIFIED"));
        assertTrue(processor.process("pending child /aftertask nope").response().contains("INVALID"));
        assertTrue(list.addedItems.isEmpty());
    }

    /** Minimal list double that records processor calls without touching persistence. */
    private static class RecordingList extends TaskList {
        private final ArrayList<Item> addedItems = new ArrayList<>();
        private boolean isReadCalled;
        private int markedIndex = Integer.MIN_VALUE;
        private boolean isReverseMark;
        private int deletedIndex = Integer.MIN_VALUE;
        private LocalDate requestedDate;
        private String findKeyword;
        private String afterTaskDescription;
        private int parentIndex;

        RecordingList() {
            super(100);
        }

        @Override
        public TaskListResult add(Item item) {
            addedItems.add(item);
            return success("added");
        }

        @Override
        public TaskListResult addAfterTask(String description, int parentIndex) {
            afterTaskDescription = description;
            this.parentIndex = parentIndex;
            return success("added dependent task");
        }

        @Override
        public TaskListResult read() {
            isReadCalled = true;
            return success("list");
        }

        @Override
        public TaskListResult mark(int index, boolean isReverse) {
            markedIndex = index;
            isReverseMark = isReverse;
            return success("marked");
        }

        @Override
        public TaskListResult delete(int index) {
            deletedIndex = index;
            return success("deleted");
        }

        @Override
        public TaskListResult listByDate(LocalDate date) {
            requestedDate = date;
            return success("dated");
        }

        @Override
        public TaskListResult find(String keyword) {
            findKeyword = keyword;
            return success("found");
        }

        private TaskListResult success(String response) {
            return new TaskListResult(response, TaskListResult.Status.SUCCESS);
        }
    }
}
