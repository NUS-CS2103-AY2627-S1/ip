package oreo.task;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import oreo.exception.OreoException;

/**
 * Stores the tasks entered during one run of the chatbot.
 */
public class TaskList {
    /** Maximum number of tasks that the fixed-capacity list can store. */
    private static final int MAX_TASK_COUNT = 100;

    /** The fixed-capacity array that holds tasks. */
    private final Task[] tasks;

    /** The number of tasks currently stored. */
    private int taskCount;

    /** Creates an empty task list. */
    public TaskList() {
        this("");
    }

    /**
     * Creates a task list containing the supplied tasks in the given order.
     *
     * @param initialTasks zero or more tasks to add to the list
     * @throws OreoException if more tasks than the list can hold are supplied
     */
    public TaskList(Task... initialTasks) {
        this.tasks = new Task[MAX_TASK_COUNT];
        for (Task task : initialTasks) {
            addTask(task);
        }
    }

    /**
     * Creates a task list based on the content from the storage
     *
     * @param content the storage string representation of the tasks
     */
    public TaskList(String content) {
        String[] tasksStr = content.split("\\R");
        this.tasks = new Task[MAX_TASK_COUNT];

        if (!content.isEmpty()) {
            try {
                for (String taskString : tasksStr) {
                    addTask(parseStoredTask(taskString));
                }
            } catch (Exception e) {
                throw new OreoException("Unable to load saved tasks.");
            }
        }
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to store
     * @throws OreoException if the task list is full
     */
    public void addTask(Task task) {
        assert task != null : "Only constructed tasks may be stored.";
        if (task == null) {
            throw new OreoException("A task cannot be empty.");
        }
        if (taskCount >= tasks.length) {
            throw new OreoException("Task list is full.");
        }
        if (Arrays.stream(tasks, 0, taskCount).anyMatch(task::hasSameDetails)) {
            throw new OreoException("An identical task already exists in the list.");
        }
        tasks[taskCount] = task;
        taskCount++;
        assert tasks[taskCount - 1] == task : "Adding a task must occupy the next list position.";
    }

    /**
     * Returns the number of tasks stored in this list.
     *
     * @return task count
     */
    public int getTaskCount() {
        return taskCount;
    }

    /**
     * Marks a task as completed.
     *
     * @param taskNumber one-based number of the task to mark
     * @return the marked task
     * @throws OreoException if the task number does not identify a stored task
     */
    public Task markTask(int taskNumber) {
        int taskIndex = getTaskIndex(taskNumber);
        tasks[taskIndex].markAsDone();
        return tasks[taskIndex];
    }

    /**
     * Marks a task as not completed.
     *
     * @param taskNumber one-based number of the task to unmark
     * @return the unmarked task
     * @throws OreoException if the task number does not identify a stored task
     */
    public Task unmarkTask(int taskNumber) {
        int taskIndex = getTaskIndex(taskNumber);
        tasks[taskIndex].markAsNotDone();
        return tasks[taskIndex];
    }

    /** Adds tags to the task identified by its one-based list number. */
    public Task addTags(int taskNumber, List<String> tags) {
        Task task = tasks[getTaskIndex(taskNumber)];
        task.addTags(tags);
        return task;
    }

    /** Removes tags from the task identified by its one-based list number. */
    public Task removeTags(int taskNumber, List<String> tags) {
        Task task = tasks[getTaskIndex(taskNumber)];
        task.removeTags(tags);
        return task;
    }

    /**
     * Deletes a task from the list
     *
     * @param taskNumber one-based number of the task to unmark
     * @return void
     * @throws OreoException if the task number does not identify a stored task
     */
    public Task deleteTask(int taskNumber) {
        int taskIndex = getTaskIndex(taskNumber);
        Task deletedTask = this.tasks[taskIndex];
        for (int i = taskIndex + 1; i < this.taskCount; ++i) {
            this.tasks[i - 1] = this.tasks[i];
        }
        this.tasks[taskCount - 1] = null;
        this.taskCount--;
        assert tasks[taskCount] == null : "The first unused array position must be empty after deletion.";
        return deletedTask;
    }

    /**
     * Converts a user-facing task number to an array index.
     *
     * @param taskNumber one-based number of the task
     * @return zero-based array index for the task
     * @throws OreoException if the task number does not identify a stored task
     */
    private int getTaskIndex(int taskNumber) {
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new OreoException("Please provide a valid task number.");
        }
        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < taskCount : "A valid task number must map inside the stored range.";
        return taskIndex;
    }

    /** Returns all tasks in the format used by the storage file. */
    public String storageStringRepresentation() {
        String lineSeparator = System.lineSeparator();
        return Arrays.stream(tasks, 0, taskCount)
                .map(Task::toStorageString)
                .collect(Collectors.joining(lineSeparator, "", taskCount == 0 ? "" : lineSeparator));
    }

    /**
     * Returns all tasks whose descriptions contain the supplied keyword, ignoring letter case.
     *
     * @param keyword text to search for in task descriptions
     * @return a numbered display of the matching tasks
     */
    public String findTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Task> matchingTasks = Arrays.stream(tasks, 0, taskCount)
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
        return formatNumberedTasks(" Here are the matching tasks in your list:", matchingTasks);
    }

    /** Returns every tag followed by the tasks currently associated with it. */
    public String listTags() {
        Map<String, List<Integer>> taskIndexesByTag = IntStream.range(0, taskCount)
                .boxed()
                .flatMap(index -> tasks[index].getTags().stream().map(tag -> Map.entry(tag, index)))
                .collect(Collectors.groupingBy(Map.Entry::getKey, TreeMap::new,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
        if (taskIndexesByTag.isEmpty()) {
            return " There are no tags in your list.";
        }

        String lineSeparator = System.lineSeparator();
        String tagGroups = taskIndexesByTag.entrySet().stream()
                .map(entry -> formatTagGroup(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(lineSeparator));
        return " Here are the tags and their tasks:" + lineSeparator + tagGroups;
    }

    /**
     * Returns a numbered display of every stored task and its completion status.
     *
     * @return the formatted task list
     */
    @Override
    public String toString() {
        return formatNumberedTasks(" Here are the tasks in your list:",
                Arrays.asList(tasks).subList(0, taskCount));
    }

    /** Returns a header followed by the supplied tasks, numbered from one. */
    private String formatNumberedTasks(String header, List<Task> tasksToFormat) {
        String lineSeparator = System.lineSeparator();
        return IntStream.range(0, tasksToFormat.size())
                .mapToObj(index -> " " + (index + 1) + "." + tasksToFormat.get(index))
                .collect(Collectors.joining(lineSeparator,
                        header + (tasksToFormat.isEmpty() ? "" : lineSeparator), ""));
    }

    /** Formats one tag and each task assigned to it using the task's list number. */
    private String formatTagGroup(String tag, List<Integer> taskIndexes) {
        String lineSeparator = System.lineSeparator();
        String numberedTasks = taskIndexes.stream()
                .map(index -> "  " + (index + 1) + "." + tasks[index])
                .collect(Collectors.joining(lineSeparator));
        return tag + lineSeparator + numberedTasks;
    }

    /** Reconstructs one task, including its optional tags, from a storage line. */
    private Task parseStoredTask(String taskString) {
        String[] fields = taskString.split(" \\| ", -1);
        if (fields.length < 3) {
            throw new OreoException("Invalid task in storage.");
        }

        Task task;
        int tagFieldIndex;
        if (Objects.equals(fields[0], "T")) {
            task = new Todo(fields[2]);
            tagFieldIndex = 3;
        } else if (Objects.equals(fields[0], "D") && fields.length >= 4) {
            task = new Deadline(fields[2], fields[3]);
            tagFieldIndex = 4;
        } else if (Objects.equals(fields[0], "E") && fields.length >= 5) {
            task = new Event(fields[2], fields[3], fields[4]);
            tagFieldIndex = 5;
        } else {
            throw new OreoException("Invalid task in storage.");
        }

        if (Objects.equals(fields[1], "X")) {
            task.markAsDone();
        } else if (!Objects.equals(fields[1], " ")) {
            throw new OreoException("Invalid task completion state in storage.");
        }
        if (fields.length == tagFieldIndex + 1) {
            addStoredTags(task, fields[tagFieldIndex]);
        } else if (fields.length != tagFieldIndex) {
            throw new OreoException("Invalid task tags in storage.");
        }
        return task;
    }

    /** Adds the optional labelled tag field stored with a task. */
    private void addStoredTags(Task task, String tagField) {
        String tagPrefix = "tags: ";
        if (!tagField.startsWith(tagPrefix)) {
            throw new OreoException("Invalid task tags in storage.");
        }
        String storedTags = tagField.substring(tagPrefix.length());
        if (storedTags.isEmpty()) {
            throw new OreoException("Invalid task tags in storage.");
        }
        task.addTags(List.of(storedTags.split(",")));
    }
}
