package com.fireinyu.themyth.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fireinyu.themyth.util.MythDateTime;

/**
 * Unit tests for {@link TaskOrder} sorting strategies.
 */
public class TaskOrderTest {

    /**
     * Tests that {@link TaskOrder#CREATED} sorts tasks in descending order of creation time.
     */
    @Test
    public void apply_sortsByCreatedDescending() {
        Task t1 = new TodoTask("Task 1");
        Task t2 = new TodoTask("Task 2");
        Task t3 = new TodoTask("Task 3");

        MythDateTime dt1 = MythDateTime.parse("2025-01-01-10-00-00");
        MythDateTime dt2 = MythDateTime.parse("2025-01-02-10-00-00");
        MythDateTime dt3 = MythDateTime.parse("2025-01-03-10-00-00");

        t1.setAccessTimes(dt1, dt1);
        t2.setAccessTimes(dt2, dt2);
        t3.setAccessTimes(dt3, dt3);

        List<Task> tasks = new ArrayList<>(List.of(t1, t3, t2));
        TaskOrder.CREATED.apply(tasks);

        assertEquals("Task 3", tasks.get(0).getDescription());
        assertEquals("Task 2", tasks.get(1).getDescription());
        assertEquals("Task 1", tasks.get(2).getDescription());
    }

    /**
     * Tests that {@link TaskOrder#MODIFIED} sorts tasks in descending order of last modified time.
     */
    @Test
    public void apply_sortsByModifiedDescending() {
        Task t1 = new TodoTask("Task 1");
        Task t2 = new TodoTask("Task 2");
        Task t3 = new TodoTask("Task 3");

        MythDateTime created = MythDateTime.parse("2025-01-01-10-00-00");
        MythDateTime mod1 = MythDateTime.parse("2025-01-05-10-00-00");
        MythDateTime mod2 = MythDateTime.parse("2025-01-07-10-00-00");
        MythDateTime mod3 = MythDateTime.parse("2025-01-06-10-00-00");

        t1.setAccessTimes(created, mod1);
        t2.setAccessTimes(created, mod2);
        t3.setAccessTimes(created, mod3);

        List<Task> tasks = new ArrayList<>(List.of(t1, t2, t3));
        TaskOrder.MODIFIED.apply(tasks);

        assertEquals("Task 2", tasks.get(0).getDescription());
        assertEquals("Task 3", tasks.get(1).getDescription());
        assertEquals("Task 1", tasks.get(2).getDescription());
    }
}
