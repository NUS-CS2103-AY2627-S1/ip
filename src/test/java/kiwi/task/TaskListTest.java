package kiwi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import kiwi.KiwiException;

/**
 * Unit tests for {@link TaskList}.
 */
class TaskListTest {

    private TaskList taskList;

    @BeforeEach
    void setUp() {
        taskList = new TaskList();
        taskList.add(new Todo("first"), new Todo("second"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void isValidIndex_inRangeIndex_returnsTrue(int index) {
        assertTrue(taskList.isValidIndex(index));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 2, 10})
    void isValidIndex_outOfRangeIndex_returnsFalse(int index) {
        assertFalse(taskList.isValidIndex(index));
    }

    @Test
    void isValidIndex_emptyList_onlyRejectsNonNegativeIndices() {
        TaskList empty = new TaskList();
        assertFalse(empty.isValidIndex(0));
        assertFalse(empty.isValidIndex(-1));
    }

    @Test
    void add_increasesSizeAndAppendsTask() {
        taskList.add(new Todo("third"));
        assertEquals(3, taskList.size());
        assertEquals("third", taskList.get(2).getDescription());
    }

    @Test
    void add_multipleTasks_appendsAllInOrder() {
        TaskList list = new TaskList();
        list.add(new Todo("a"), new Todo("b"), new Todo("c"));
        assertEquals(3, list.size());
        assertEquals("a", list.get(0).getDescription());
        assertEquals("b", list.get(1).getDescription());
        assertEquals("c", list.get(2).getDescription());
    }

    @Test
    void delete_removesTaskAndReturnsIt() {
        Task removed = taskList.delete(0);
        assertEquals("first", removed.getDescription());
        assertEquals(1, taskList.size());
        assertEquals("second", taskList.get(0).getDescription());
    }

    @Test
    void markDone_marksTaskAtIndex() {
        taskList.markDone(1);
        assertTrue(taskList.get(1).isDone());
        assertFalse(taskList.get(0).isDone());
    }

    @Test
    void markNotDone_unmarksTaskAtIndex() {
        taskList.markDone(0);
        taskList.markNotDone(0);
        assertFalse(taskList.get(0).isDone());
    }

    @Test
    void find_matchingDescriptions_returnsThoseTasksInOrder() {
        taskList.add(new Todo("read book"));
        TaskList matches = taskList.find("first");

        assertEquals(1, matches.size());
        assertEquals("first", matches.get(0).getDescription());
        assertEquals(3, taskList.size());
    }

    @Test
    void find_substringInSeveralTasks_returnsAllMatches() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"),
                new Todo("project meeting"),
                new Deadline("return book", LocalDate.of(2019, 6, 6)),
                new Todo("borrow book"));

        TaskList matches = tasks.find("book");

        assertEquals(3, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return book", matches.get(1).getDescription());
        assertEquals("borrow book", matches.get(2).getDescription());
    }

    @Test
    void find_noMatches_returnsEmptyList() {
        TaskList matches = taskList.find("xyz");
        assertEquals(0, matches.size());
        assertEquals(2, taskList.size());
    }

    @Test
    void find_isCaseSensitive() {
        taskList.add(new Todo("Read Book"));
        assertEquals(0, taskList.find("book").size());
        assertEquals(1, taskList.find("Book").size());
    }

    @Test
    void containsSameDetails_sameTodoDescription_returnsTrue() {
        assertTrue(taskList.containsSameDetails(new Todo("first")));
        assertFalse(taskList.containsSameDetails(new Todo("third")));
    }

    @Test
    void containsSameDetails_deadlineSameDescriptionDifferentDate_returnsFalse() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", LocalDate.of(2019, 12, 2)));

        assertFalse(tasks.containsSameDetails(
                new Deadline("return book", LocalDate.of(2019, 12, 3))));
        assertTrue(tasks.containsSameDetails(
                new Deadline("return book", LocalDate.of(2019, 12, 2))));
    }

    @Test
    void containsSameDetails_ignoresDoneStatus() {
        taskList.markDone(0);
        assertTrue(taskList.containsSameDetails(new Todo("first")));
    }

    @Test
    void findNearestFreeSlot_emptyList_returnsStartDateAtWorkDayStart() {
        TaskList empty = new TaskList();
        Optional<FreeSlot> slot = empty.findNearestFreeSlot(4, LocalDate.of(2019, 12, 1));

        assertTrue(slot.isPresent());
        assertEquals(LocalDate.of(2019, 12, 1), slot.get().getDate());
        assertEquals(LocalTime.of(8, 0), slot.get().getStart());
        assertEquals(LocalTime.of(12, 0), slot.get().getEnd());
    }

    @Test
    void findNearestFreeSlot_eventCoveringStart_returnsDayAfterEvent() throws KiwiException {
        TaskList tasks = new TaskList();
        tasks.add(new Event("conference",
                LocalDate.of(2019, 12, 1),
                LocalDate.of(2019, 12, 3)));

        Optional<FreeSlot> slot = tasks.findNearestFreeSlot(4, LocalDate.of(2019, 12, 1));

        assertTrue(slot.isPresent());
        assertEquals(LocalDate.of(2019, 12, 4), slot.get().getDate());
    }

    @Test
    void findNearestFreeSlot_deadlineAndTodoOnStart_doNotBlock() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", LocalDate.of(2019, 12, 1)));
        tasks.add(new Todo("read book"));

        Optional<FreeSlot> slot = tasks.findNearestFreeSlot(4, LocalDate.of(2019, 12, 1));

        assertTrue(slot.isPresent());
        assertEquals(LocalDate.of(2019, 12, 1), slot.get().getDate());
    }

    @Test
    void findNearestFreeSlot_gapBetweenEvents_returnsGapDay() throws KiwiException {
        TaskList tasks = new TaskList();
        tasks.add(new Event("first",
                LocalDate.of(2019, 12, 1),
                LocalDate.of(2019, 12, 2)));
        tasks.add(new Event("second",
                LocalDate.of(2019, 12, 4),
                LocalDate.of(2019, 12, 5)));

        Optional<FreeSlot> slot = tasks.findNearestFreeSlot(4, LocalDate.of(2019, 12, 1));

        assertTrue(slot.isPresent());
        assertEquals(LocalDate.of(2019, 12, 3), slot.get().getDate());
    }

    @Test
    void findNearestFreeSlot_doneEventStillBlocks() throws KiwiException {
        Event event = new Event("meeting",
                LocalDate.of(2019, 12, 1),
                LocalDate.of(2019, 12, 1));
        event.markAsDone();
        TaskList tasks = new TaskList();
        tasks.add(event);

        Optional<FreeSlot> slot = tasks.findNearestFreeSlot(4, LocalDate.of(2019, 12, 1));

        assertTrue(slot.isPresent());
        assertEquals(LocalDate.of(2019, 12, 2), slot.get().getDate());
    }

    @Test
    void findNearestFreeSlot_yearFullyBooked_returnsEmpty() throws KiwiException {
        TaskList tasks = new TaskList();
        tasks.add(new Event("long trip",
                LocalDate.of(2019, 1, 1),
                LocalDate.of(2021, 12, 31)));

        Optional<FreeSlot> slot = tasks.findNearestFreeSlot(4, LocalDate.of(2019, 12, 1));

        assertTrue(slot.isEmpty());
    }

    @Test
    void findNearestFreeSlot_fullWorkDay_endsAtSixPm() {
        TaskList empty = new TaskList();
        Optional<FreeSlot> slot = empty.findNearestFreeSlot(10, LocalDate.of(2019, 12, 1));

        assertTrue(slot.isPresent());
        assertEquals(LocalTime.of(18, 0), slot.get().getEnd());
    }
}
