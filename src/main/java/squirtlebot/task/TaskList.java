package squirtlebot.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.IntStream;

/**
 * Contains currently active tasks created by the user.
 */
public class TaskList extends ArrayList<Task> {

    /**
     * Constructs an empty task list.
     */
    public TaskList() {
        super();
    }

    /**
     * Constructs a task list containing objects specified by {@code tasks}.
     *
     * @param tasks collection whose elements are to be placed into this list.
     */
    public TaskList(Collection<Task> tasks) {
        super(tasks);
    }

    /**
     * Returns the string representation of {@link Task} objects contained within this collection.
     * String representation of contained {@link Task} objects are separated by a newline character.
     */
    @Override
    public String toString() {
        String formattedTasks = IntStream.range(0, size()).boxed()
                .map(x -> String.format("%d. %s", x + 1, get(x)))
                .reduce("", (x, y) -> x + y + "\n");

        return "\t" + formattedTasks.trim();
    }
}
