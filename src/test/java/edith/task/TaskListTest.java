package edith.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void findMatchingIndices_multipleMatches_returnsOriginalIndices() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("exercise"));
        tasks.add(new ToDo("return BOOK"));

        assertEquals(List.of(0, 2), tasks.findMatchingIndices("book"));
    }

    @Test
    void findMatchingIndices_noMatches_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        assertEquals(List.of(), tasks.findMatchingIndices("exercise"));
    }

    @Test
    void hasDuplicate_sameTaskDetailsIgnoringCase_returnsTrue() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("Submit report", "June 6th"));

        assertTrue(tasks.hasDuplicate(new Deadline("submit REPORT", "june 6th")));
        assertFalse(tasks.hasDuplicate(new Deadline("submit report", "June 7th")));
    }

    @Test
    void sort_alphabeticalCriterion_ordersDescriptionsIgnoringCase() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("write report"));
        tasks.add(new ToDo("Buy groceries"));
        tasks.add(new ToDo("attend lecture"));

        tasks.sort(SortOrder.ALPHABETICAL);

        assertEquals("attend lecture", tasks.get(0).getDescription());
        assertEquals("Buy groceries", tasks.get(1).getDescription());
        assertEquals("write report", tasks.get(2).getDescription());
    }

    @Test
    void sort_descendingAlphabeticalCriterion_ordersDescriptionsFromZToA() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("write report"));
        tasks.add(new ToDo("Buy groceries"));
        tasks.add(new ToDo("attend lecture"));

        tasks.sort(SortOrder.ALPHABETICAL_DESCENDING);

        assertEquals("write report", tasks.get(0).getDescription());
        assertEquals("Buy groceries", tasks.get(1).getDescription());
        assertEquals("attend lecture", tasks.get(2).getDescription());
    }

    @Test
    void sort_dateCriterion_ordersDatedTasksAndPlacesUndatedTasksLast() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", "2026-12-10"));
        tasks.add(new ToDo("buy groceries"));
        tasks.add(new Event("project meeting", "2026-10-01", "2026-10-01T12:00"));
        tasks.add(new Deadline("return book", "next Friday"));

        tasks.sort(SortOrder.DATE);

        assertEquals("project meeting", tasks.get(0).getDescription());
        assertEquals("submit report", tasks.get(1).getDescription());
        assertEquals("buy groceries", tasks.get(2).getDescription());
        assertEquals("return book", tasks.get(3).getDescription());
    }

    @Test
    void sort_descendingDateCriterion_keepsUndatedTasksLast() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("earlier task", "2026-10-01"));
        tasks.add(new ToDo("undated task"));
        tasks.add(new Deadline("later task", "2026-12-10"));

        tasks.sort(SortOrder.DATE_DESCENDING);

        assertEquals("later task", tasks.get(0).getDescription());
        assertEquals("earlier task", tasks.get(1).getDescription());
        assertEquals("undated task", tasks.get(2).getDescription());
    }

    @Test
    void sort_dateCriterionWithNaturalDates_ordersByMonthDayAndTime() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("June deadline", "June 6th"));
        tasks.add(new Event("afternoon event", "June 6th 3pm", "6pm"));
        tasks.add(new Deadline("earlier deadline", "June 5th"));

        tasks.sort(SortOrder.DATE);

        assertEquals("earlier deadline", tasks.get(0).getDescription());
        assertEquals("June deadline", tasks.get(1).getDescription());
        assertEquals("afternoon event", tasks.get(2).getDescription());
    }

    @Test
    void sort_statusOrder_placesIncompleteTasksBeforeCompletedTasks() {
        TaskList tasks = new TaskList();
        ToDo completedTask = new ToDo("completed task");
        completedTask.markAsDone();
        tasks.add(completedTask);
        tasks.add(new ToDo("incomplete task"));

        tasks.sort(SortOrder.STATUS);

        assertEquals("incomplete task", tasks.get(0).getDescription());
        assertEquals("completed task", tasks.get(1).getDescription());
    }

    @Test
    void sort_addedCriterion_restoresOriginalInsertionOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("second alphabetically"));
        tasks.add(new ToDo("third alphabetically"));
        tasks.add(new ToDo("first alphabetically"));
        tasks.sort(SortOrder.ALPHABETICAL);

        tasks.sort(SortOrder.ADDED);

        assertEquals("second alphabetically", tasks.get(0).getDescription());
        assertEquals("third alphabetically", tasks.get(1).getDescription());
        assertEquals("first alphabetically", tasks.get(2).getDescription());
    }

    @Test
    void sort_descendingAddedCriterion_ordersNewestTaskFirst() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("oldest task"));
        tasks.add(new ToDo("middle task"));
        tasks.add(new ToDo("newest task"));

        tasks.sort(SortOrder.ADDED_DESCENDING);

        assertEquals("newest task", tasks.get(0).getDescription());
        assertEquals("middle task", tasks.get(1).getDescription());
        assertEquals("oldest task", tasks.get(2).getDescription());
    }
}
