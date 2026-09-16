package surveyprogram.object;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import surveyprogram.persistence.TaskStorage;

/**
 * Stores the application's tasks in a fixed-size array.
 */
public class TaskList {
    private int itemCount = 0;
    private final ArrayList<Item> items = new ArrayList<>();
    private final int capacity;
    private final Path saveFile;

    /**
     * Creates a task list that uses the default save file.
     *
     * @param capacity maximum number of tasks the list can contain
     */
    public TaskList(int capacity) {
        this(capacity, Path.of("data", "PERSIST.txt"));
    }

    /**
     * Creates a task list backed by a specific save file.
     *
     * @param capacity maximum number of tasks the list can contain
     * @param saveFile file used to load and save tasks
     */
    TaskList(int capacity, Path saveFile) {
        this.capacity = capacity;
        this.saveFile = saveFile;
    }

    /**
     * Returns an immutable snapshot of the stored tasks.
     *
     * @return stored tasks
     */
    public List<Item> getList() {
        return List.copyOf(items);
    }

    /**
     * Restores a task without saving it again.
     *
     * @param item task reconstructed from storage
     * @return {@code true} if the task was restored, or {@code false} if the list is full
     */
    public boolean restore(Item item) {
        if (itemCount >= capacity) {
            return false;
        }

        items.add(item);
        itemCount++;
        assert itemCount == items.size();
        return true;
    }

    /** Loads tasks from the save file, if it exists. */
    public void load() {
        TaskStorage.load(this, saveFile);
        assertConsistentState();
    }

    /** Returns success only after saving, or a visible warning that changes remain in memory. */
    private TaskListResult save(String response) {
        try {
            TaskStorage.save(this, saveFile);
            return success(response);
        } catch (IOException exception) {
            return error("BUT, YOUR CHANGES COULD NOT BE SAVED.\n"
                    + "THEY REMAIN IN THIS SESSION ONLY.\n"
                    + "CHECK FILE ACCESS AND DISK SPACE BEFORE MAKING ANOTHER CHANGE TO RETRY.");
        }
    }

    /**
     * Adds and saves a non-empty task when storage remains available.
     *
     * @param item task to add
     */
    public TaskListResult add(Item item) {
        assertConsistentState();
        if (itemCount >= capacity) {
            return error("BUT, THERE IS NO MORE MEMORY TO ALLOCATE.");
        }
        if (item.getName().trim().isEmpty()) {
            return error("IT IS BARREN AND CANNOT BE CREATED.");
        }

        items.add(item);
        itemCount++;
        assertConsistentState();
        return save("ORDER PROCESSED: " + item.toString().toUpperCase());
    }

    /**
     * Adds a task that becomes available after an existing task is complete.
     *
     * @param description dependent task description
     * @param parentIndex zero-based index of the parent task
     * @return result of validating and adding the dependent task
     */
    public TaskListResult addAfterTask(String description, int parentIndex) {
        if (parentIndex < 0 || parentIndex >= itemCount) {
            return error("BUT, IT WAS NEVER THERE IN THE FIRST PLACE.");
        }
        return add(new AfterTask(description, items.get(parentIndex)));
    }

    /**
     * Returns all stored tasks in their insertion order.
     *
     * @return formatted task list, or a message when the list is empty
     */
    public TaskListResult read() {
        assertConsistentState();
        if (itemCount == 0) {
            return empty("BUT, THERE WAS NOTHING TO READ.");
        }
        String numberedItems = IntStream.range(0, itemCount)
                .mapToObj(index -> String.format("%d.%s", index + 1, items.get(index)))
                .collect(Collectors.joining(System.lineSeparator()));
        return success(numberedItems);
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword text to find in task descriptions
     * @return matching tasks or a message explaining why none can be shown
     */
    public TaskListResult find(String keyword) {
        if (keyword.isBlank()) {
            return empty("BUT, THERE WAS NOTHING TO LOCATE.");
        }

        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Item> matchingItems = items.stream()
                .filter(item -> item.getName().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
        if (matchingItems.isEmpty()) {
            return empty("BUT, THERE WAS NOTHING THAT CONFORMS TO THE TERM.");
        }
        String numberedItems = IntStream.range(0, matchingItems.size())
                .mapToObj(index -> String.format("%d.%s", index + 1, matchingItems.get(index)))
                .collect(Collectors.joining(System.lineSeparator()));
        return success("VERY WELL. HERE IS YOUR MATCHING LIST:\n" + numberedItems);
    }

    /**
     * Marks or unmarks the task at the supplied zero-based index.
     *
     * @param index zero-based index of the task
     * @param isReverse {@code true} to unmark the task, or {@code false} to mark it
     */
    public TaskListResult mark(int index, boolean isReverse) {
        assertConsistentState();
        if (index < 0 || index >= itemCount) {
            return error("BUT, IT WAS NEVER THERE IN THE FIRST PLACE.");
        }

        Item selectedItem = items.get(index);
        if (selectedItem instanceof AfterTask afterTask
                && !afterTask.isAvailable(LocalDateTime.now())) {
            return error("BUT, ITS CONDITIONS ARE NOT SATISFIED.");
        }

        String response = isReverse ? selectedItem.undo() : selectedItem.mark();
        if (isReverse && !selectedItem.isDone()) {
            unmarkDescendants(selectedItem);
        }
        return save(response);
    }

    /**
     * Removes and saves the task at a zero-based index.
     *
     * @param index zero-based index of the task to remove
     */
    public TaskListResult delete(int index) {
        assertConsistentState();
        if (index < 0 || index >= itemCount) {
            return error("BUT, IT WAS NEVER THERE IN THE FIRST PLACE.");
        }

        Item selectedItem = items.get(index);
        String deletedItem = selectedItem.toString();
        int originalCount = itemCount;
        removeWithDescendants(selectedItem);
        int deletedCount = originalCount - itemCount;
        assertConsistentState();
        String cascadeMessage = deletedCount > 1
                ? String.format("\nAS WITH ITS %d CHILDREN.", deletedCount - 1)
                : "";
        return save(deletedItem + "\nIT WAS AS IF IT WAS NEVER THERE\nAT ALL." + cascadeMessage);
    }

    /**
     * Prints deadlines and events that occur on a specified date.
     *
     * @param date date used to filter tasks
     */
    public TaskListResult listByDate(LocalDate date) {
        List<Item> matchingItems = items.stream().filter(item -> item.inRange(date)).toList();
        if (matchingItems.isEmpty()) {
            return empty("WELL, THERE IS NOTHING OF CONCERN ON THIS SPECIFIC DATE.");
        }

        String formattedItems = matchingItems.stream()
                .map(Item::toString)
                .collect(Collectors.joining(System.lineSeparator()));
        return success("WE SIT ON THE PRECIPICE OF THESE EVENTS:\n\n" + formattedItems);
    }

    private TaskListResult success(String response) {
        return new TaskListResult(response, TaskListResult.Status.SUCCESS);
    }

    private TaskListResult empty(String response) {
        return new TaskListResult(response, TaskListResult.Status.EMPTY);
    }

    private TaskListResult error(String response) {
        return new TaskListResult(response, TaskListResult.Status.ERROR);
    }

    /** Marks every descendant of a newly incomplete parent as incomplete. */
    private void unmarkDescendants(Item parent) {
        items.stream()
                .filter(item -> item instanceof AfterTask)
                .map(item -> (AfterTask) item)
                .filter(afterTask -> afterTask.getParent() == parent)
                .forEach(afterTask -> {
                    afterTask.setDone(false);
                    unmarkDescendants(afterTask);
                });
    }

    /** Removes a task and all tasks that transitively depend on it. */
    private void removeWithDescendants(Item parent) {
        List<Item> children = items.stream()
                .filter(item -> item instanceof AfterTask)
                .map(item -> (AfterTask) item)
                .filter(afterTask -> afterTask.getParent() == parent)
                .map(afterTask -> (Item) afterTask)
                .toList();
        children.forEach(this::removeWithDescendants);
        items.remove(parent);
        itemCount--;
    }

    /** Verifies that the stored task count agrees with the backing collection. */
    private void assertConsistentState() {
        assert itemCount == items.size()
                : "Item count must match the number of stored items";
    }
}
