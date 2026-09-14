package morgan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void sortByName_mixedCaseNames_sortedIgnoringCase() throws Exception {
        TaskList tasks = new TaskList(new ArrayList<>(List.of(
                new ToDo("write report"),
                new ToDo("Buy groceries"),
                new ToDo("attend meeting")
        )));

        tasks.sortByName();

        assertEquals("attend meeting", tasks.get(0).getName());
        assertEquals("Buy groceries", tasks.get(1).getName());
        assertEquals("write report", tasks.get(2).getName());
    }
}
