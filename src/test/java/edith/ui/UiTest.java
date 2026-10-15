package edith.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import edith.task.TaskList;
import edith.task.Todo;

/** Tests task-list responses shown to users. */
public class UiTest {
    @Test
    public void showTaskList_emptyList_showsCreationHint() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        ui.showTaskList(new TaskList());

        assertEquals(List.of("No tasks yet! Use todo, deadline, or event to create a new tasks"), output);
    }

    @Test
    public void showTaskList_withTask_showsNumberedList() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        ui.showTaskList(tasks);

        assertEquals(List.of("Here are the tasks in your list:", "1.[T][ ] read book"), output);
    }
}
