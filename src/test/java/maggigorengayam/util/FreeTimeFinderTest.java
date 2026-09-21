package maggigorengayam.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import maggigorengayam.task.Event;
import maggigorengayam.task.Task;
import maggigorengayam.task.ToDo;
import maggigorengayam.util.FreeTimeFinder.FreeSlot;

public class FreeTimeFinderTest {
    private static final LocalDate DAY_1 = LocalDate.of(2019, 12, 2);
    private static final LocalTime WINDOW_START = LocalTime.of(9, 0);
    private static final LocalTime WINDOW_END = LocalTime.of(18, 0);

    @Test
    public void findNearestFreeSlot_emptyTaskList_returnsSlotStartingAtWindowStart() {
        Optional<FreeSlot> result = FreeTimeFinder.findNearestFreeSlot(
                List.of(), 240, WINDOW_START, WINDOW_END, DAY_1, WINDOW_START, 30);

        assertTrue(result.isPresent());
        assertEquals(DAY_1, result.get().date);
        assertEquals(LocalTime.of(9, 0), result.get().start);
        assertEquals(LocalTime.of(13, 0), result.get().end);
    }

    @Test
    public void findNearestFreeSlot_toDoOnly_neverBlocksTime() {
        List<Task> tasks = List.of(new ToDo("chill"));

        Optional<FreeSlot> result = FreeTimeFinder.findNearestFreeSlot(
                tasks, 240, WINDOW_START, WINDOW_END, DAY_1, WINDOW_START, 30);

        assertTrue(result.isPresent());
        assertEquals(LocalTime.of(9, 0), result.get().start);
    }

    @Test
    public void findNearestFreeSlot_singleEventBlockingMorning_findsGapAfterIt() {
        List<Task> tasks = List.of(
                new Event("meeting", DAY_1, LocalTime.of(9, 0), DAY_1, LocalTime.of(11, 0)));

        Optional<FreeSlot> result = FreeTimeFinder.findNearestFreeSlot(
                tasks, 240, WINDOW_START, WINDOW_END, DAY_1, WINDOW_START, 30);

        assertTrue(result.isPresent());
        assertEquals(DAY_1, result.get().date);
        assertEquals(LocalTime.of(11, 0), result.get().start);
        assertEquals(LocalTime.of(15, 0), result.get().end);
    }

    @Test
    public void findNearestFreeSlot_overlappingEvents_treatedAsOneMergedBlock() {
        List<Task> tasks = List.of(
                new Event("a", DAY_1, LocalTime.of(9, 0), DAY_1, LocalTime.of(12, 0)),
                new Event("b", DAY_1, LocalTime.of(11, 0), DAY_1, LocalTime.of(14, 0)));

        Optional<FreeSlot> result = FreeTimeFinder.findNearestFreeSlot(
                tasks, 240, WINDOW_START, WINDOW_END, DAY_1, WINDOW_START, 30);

        assertTrue(result.isPresent());
        assertEquals(LocalTime.of(14, 0), result.get().start);
        assertEquals(LocalTime.of(18, 0), result.get().end);
    }

    @Test
    public void findNearestFreeSlot_backToBackEvents_noFalseGapBetweenThem() {
        List<Task> tasks = List.of(
                new Event("a", DAY_1, LocalTime.of(9, 0), DAY_1, LocalTime.of(11, 0)),
                new Event("b", DAY_1, LocalTime.of(11, 0), DAY_1, LocalTime.of(17, 0)));

        Optional<FreeSlot> result = FreeTimeFinder.findNearestFreeSlot(
                tasks, 60, WINDOW_START, WINDOW_END, DAY_1, WINDOW_START, 30);

        assertTrue(result.isPresent());
        assertEquals(LocalTime.of(17, 0), result.get().start);
    }

    @Test
    public void findNearestFreeSlot_noGapToday_movesToNextDay() {
        List<Task> tasks = List.of(
                new Event("all day busy", DAY_1, LocalTime.of(9, 0), DAY_1, LocalTime.of(18, 0)));

        Optional<FreeSlot> result = FreeTimeFinder.findNearestFreeSlot(
                tasks, 240, WINDOW_START, WINDOW_END, DAY_1, WINDOW_START, 30);

        assertTrue(result.isPresent());
        assertEquals(DAY_1.plusDays(1), result.get().date);
        assertEquals(LocalTime.of(9, 0), result.get().start);
    }

    @Test
    public void findNearestFreeSlot_dateOnlyEvent_blocksWholeDayWindow() {
        List<Task> tasks = List.of(new Event("trip", DAY_1, null, DAY_1, null));

        Optional<FreeSlot> result = FreeTimeFinder.findNearestFreeSlot(
                tasks, 240, WINDOW_START, WINDOW_END, DAY_1, WINDOW_START, 30);

        assertTrue(result.isPresent());
        assertEquals(DAY_1.plusDays(1), result.get().date);
    }

    @Test
    public void findNearestFreeSlot_multiDayEvent_middleDayFullyBusyLastDayOnlyUntilToTime() {
        LocalDate day3 = DAY_1.plusDays(2);
        List<Task> tasks = List.of(
                new Event("trip", DAY_1, LocalTime.of(9, 0), day3, LocalTime.of(11, 0)));

        Optional<FreeSlot> result = FreeTimeFinder.findNearestFreeSlot(
                tasks, 240, WINDOW_START, WINDOW_END, DAY_1, WINDOW_START, 30);

        assertTrue(result.isPresent());
        assertEquals(day3, result.get().date);
        assertEquals(LocalTime.of(11, 0), result.get().start);
        assertEquals(LocalTime.of(15, 0), result.get().end);
    }

    @Test
    public void findNearestFreeSlot_searchStartLaterThanWindowStartToday_usesGivenStartTime() {
        Optional<FreeSlot> result = FreeTimeFinder.findNearestFreeSlot(
                List.of(), 30, WINDOW_START, WINDOW_END, DAY_1, LocalTime.of(17, 30), 30);

        assertTrue(result.isPresent());
        assertEquals(DAY_1, result.get().date);
        assertEquals(LocalTime.of(17, 30), result.get().start);
        assertEquals(LocalTime.of(18, 0), result.get().end);
    }

    @Test
    public void findNearestFreeSlot_searchStartAtWindowEnd_skipsTodayEntirely() {
        Optional<FreeSlot> result = FreeTimeFinder.findNearestFreeSlot(
                List.of(), 30, WINDOW_START, WINDOW_END, DAY_1, WINDOW_END, 30);

        assertTrue(result.isPresent());
        assertEquals(DAY_1.plusDays(1), result.get().date);
    }

    @Test
    public void findNearestFreeSlot_everyDayFullyBookedWithinHorizon_returnsEmpty() {
        List<Task> tasks = List.of(
                new Event("long trip", DAY_1, null, DAY_1.plusDays(5), null));

        Optional<FreeSlot> result = FreeTimeFinder.findNearestFreeSlot(
                tasks, 60, WINDOW_START, WINDOW_END, DAY_1, WINDOW_START, 3);

        assertFalse(result.isPresent());
    }
}
