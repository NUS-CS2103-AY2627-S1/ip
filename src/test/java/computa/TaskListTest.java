package computa;

import org.junit.jupiter.api.Test;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;

class TaskListTest {
    @Test void tasksCanBeAddedRetrievedAndDeleted() {
        TaskList list = new TaskList();
        Todo task = new Todo("one");
        list.addTask(task);
        assertEquals(task, list.getTask(0));
        assertEquals(task, list.deleteTask(0));
        assertEquals(0, list.getSize());
    }

    @Test void recurringRulesAreHiddenFromVisibleTasks() {
        TaskList list = new TaskList();
        list.addTask(new RecurringTask("weekly", DayOfWeek.MONDAY));
        list.addTask(new Todo("ordinary"));
        assertEquals(1, list.getVisibleSize());
        assertEquals("ordinary", list.getVisibleTask(0).getDescription());
    }

    @Test void generatesMondayTaskWhenOpenedTuesday() {
        TaskList list = new TaskList();
        list.addTask(new RecurringTask("weekly", DayOfWeek.MONDAY));
        assertTrue(list.generateRecurringTasks(LocalDate.of(2026, 9, 15)));
        assertEquals(2, list.getSize());
        assertEquals(LocalDate.of(2026, 9, 14),
                ((RecurringTask) list.getVisibleTask(0)).getOccurrenceDate());
    }

    @Test void doesNotDuplicateSameWeekAndRemovesCompletedOldInstance() {
        TaskList list = new TaskList();
        list.addTask(new RecurringTask("weekly", DayOfWeek.MONDAY));
        list.generateRecurringTasks(LocalDate.of(2026, 9, 15));
        ((RecurringTask) list.getVisibleTask(0)).changeStatusIcon();
        assertTrue(list.generateRecurringTasks(LocalDate.of(2026, 9, 22)));
        assertEquals(2, list.getSize());
    }

    @Test void incompleteOldInstanceIsKept() {
        TaskList list = new TaskList();
        list.addTask(new RecurringTask("weekly", DayOfWeek.MONDAY));
        list.generateRecurringTasks(LocalDate.of(2026, 9, 15));
        list.generateRecurringTasks(LocalDate.of(2026, 9, 22));
        assertEquals(3, list.getSize());
        assertEquals(2, list.getVisibleSize());
    }
}
