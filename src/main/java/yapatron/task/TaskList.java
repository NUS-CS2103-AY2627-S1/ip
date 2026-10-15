package yapatron.task;
import yapatron.YapException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class TaskList {

    private final List<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(List<Task> tasks) {
        assert tasks != null : "TaskList must be created with a task collection";
        for (Task task : tasks) {
            assert task != null : "TaskList must not contain null tasks";
        }
        this.tasks = tasks;
    }

    public List<Task> getTasks() {
        return this.tasks;
    }

    public void addTask(Task task) {
        assert task != null : "A null task must not be added to TaskList";
        tasks.add(task);
    }

    public int size() {
        return tasks.size();
    }

    public Task delete(int idx) throws YapException {
        if (idx < 0 || idx >= tasks.size()) {
            throw new YapException("There's no task with that number!");
        }
        int originalSize = tasks.size();
        Task deletedTask = tasks.remove(idx);
        assert tasks.size() == originalSize - 1 : "Deleting one task must reduce TaskList size by 1";

        return deletedTask;
    }

    public Task mark(int idx) throws YapException {
        if (idx < 0 || idx >= tasks.size()) {
            throw new YapException("There's no task with that number!");
        }

        Task t = tasks.get(idx);
        if (t.isDone()) {
            throw new YapException("Don't worry!! You've already done this!");
        }
        t.markAsDone();
        assert t.isDone() : "A task marked must be done";
            return t;
    }

    public Task unmark(int idx) throws YapException {
        if (idx < 0 || idx >= tasks.size()) {
            throw new YapException("There's no task with that number!");
        }

        Task t = tasks.get(idx);
        if (!t.isDone()) {
            throw new YapException("This task is already unmarked!");
        }
        t.unmark();
        assert !t.isDone() : "A task unmarked must be incomplete";
            return t;
    }

    public void printList() {
        System.out.println();
        System.out.println("Here's your current list!");
        System.out.println();
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
        System.out.println();
        System.out.println("Missing anything?");
    }

    /**
     * Finds tasks whose descriptions have the keyword
     *
     * @param word word to search for
     * @return List of matching tasks
     */
    public List<Task> find(String word) {
        String[] keywords = word.trim().toLowerCase(Locale.ROOT).split("\\s+");
        String keyword = word.toLowerCase(Locale.ROOT);

        return tasks.stream().
            filter(task -> {
                String description = task.getDesc().toLowerCase(Locale.ROOT);
                return Arrays.stream(keywords).allMatch(description::contains);
            })
        .collect(Collectors.toList());
    }

    /**
     * Checks whether a task with the same details already exists.
     * Completion status is ignored because it does not change the task's identity.
     *
     * @param candidate task to check for duplication
     * @return true if an equivalent task already exists, false otherwise
     */
    public boolean containsEquivalent(Task candidate) {
        assert candidate != null : "A candidate task must not be null";

        String candidateDetails = withoutCompletionStatus(candidate);

        return tasks.stream()
            .map(TaskList::withoutCompletionStatus)
            .anyMatch(candidateDetails::equals);
    }

    private static String withoutCompletionStatus(Task task) {
        // creates a version of a task without completion status to test for dupes
        return task.toFileFormat()
            .replaceFirst("\\| [0X] \\|", "| STATUS |");
    }

}




