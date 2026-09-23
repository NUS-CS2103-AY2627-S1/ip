package max.data;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;
import max.maxexception.InvalidTaskIDException;
import max.task.Deadline;
import max.task.Task;  

public class TaskList {
    private ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
        assert tasks != null : "A task list must always have a backing collection";
    }

    public TaskList(ArrayList<Task> loadedTasks) {
        assert loadedTasks != null : "Loaded tasks must provide a backing collection";
        this.tasks = loadedTasks;
    }

    public void add(Task task) {
        assert task != null : "The task list must not contain null entries";
        tasks.add(task);
    }

    public Task delete(int i) throws InvalidTaskIDException {
        if (i <= 0 || i > tasks.size()) {
            throw new InvalidTaskIDException();
        }
        Task removed = tasks.remove(i - 1);
        assert removed != null : "A valid task index must refer to a task";
        return removed;
    }

    public Task setDone(int i) throws InvalidTaskIDException {
        if (i <= 0 || i > tasks.size()) {
            throw new InvalidTaskIDException();
        }
        Task t = tasks.get(i - 1);
        assert t != null : "A valid task index must refer to a task";
        t.markAsDone();
        return t;
    }

    public Task setNotDone(int i) throws InvalidTaskIDException {
        if (i <= 0 || i > tasks.size()) {
            throw new InvalidTaskIDException();
        }
        Task t = tasks.get(i - 1);
        assert t != null : "A valid task index must refer to a task";
        t.notDone();
        return t;
    }

    public int size() {
        return tasks.size();
    }

    public ArrayList<Task> getAllTask() {
        return tasks;
    }

    /**
     * Orders deadline tasks from the earliest due date to the latest.
     *
     * Non-deadline tasks are placed after deadlines and retain their relative order.
     */
    public void sortByDeadline() {
        tasks.sort(Comparator.comparing(
                task -> task instanceof Deadline ? ((Deadline) task).getDate() : null,
                Comparator.nullsLast(Comparator.naturalOrder())));
    }

    public List<Task> getTasksOn(LocalDate date) {
        return tasks.stream().filter(task -> task.isOn(date))
                                                    .collect(Collectors.toList());
    }
   
    public List<Task> find(String keyword) {
        return tasks.stream()
                    .filter(task -> task.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
    }
}
