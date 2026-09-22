package echo;

import java.util.ArrayList;

/**
 * Stores and manages Echo's tasks in their display order.
 */
public class TodoList {
    private ArrayList<Task> listOfTasks = new ArrayList<>();

    private static final String LINE = "\n" + "-".repeat(30) + "\n";
    private static final String ENDLINE = "\n" + "-".repeat(30);

    private Echo echo;

    public TodoList() {
    }

    /**
     * Creates a TodoList storing a reference to an Echo object
     *
     * @param echo the Echo object whose reference is to be stored in the newly created TodoList
     */
    public TodoList(Echo echo) {
        this.echo = echo;
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param item the task to add
     */
    public void addToList(Task item) {
        assert item != null;

        listOfTasks.add(item);

        // Adds the reverse of this action to the previousActions stack so it can be undone with undo
        this.echo.previousActions.push(() -> this.listOfTasks.remove(item));

        System.out.println(String.format("\nThere are now %d items in the list",
                listOfTasks.size()));
    }

    /**
     * Adds a task to the end of this list. Used when reading data from storage.
     *
     * @param item the task to add
     */
    public void addToListFromStorage(Task item) {
        assert item != null;

        listOfTasks.add(item);
    }

    /**
     * Marks the specified one-based task number as complete.
     *
     * @param taskNum the one-based number of the task to mark
     * @throws InvalidCommandException if the task number is outside this list
     */
    public void markList(int taskNum) throws InvalidCommandException {
        if (listOfTasks.size() < taskNum || taskNum < 1) {
            throw new InvalidCommandException();
        }
        Task targetTask = listOfTasks.get(taskNum - 1);
        if (Boolean.parseBoolean(targetTask.getStatus())) {
            return;
        }
        targetTask.mark();
        this.echo.previousActions.push(() -> targetTask.unmark());
    }

    /**
     * Marks the specified one-based task number as incomplete.
     *
     * @param taskNum the one-based number of the task to unmark
     * @throws InvalidCommandException if the task number is outside this list
     */
    public void unmarkList(int taskNum) throws InvalidCommandException {
        if (listOfTasks.size() < taskNum || taskNum < 1) {
            throw new InvalidCommandException();
        }
        Task targetTask = listOfTasks.get(taskNum - 1);
        if (!Boolean.parseBoolean(targetTask.getStatus())) {
            return;
        }
        targetTask.unmark();
        this.echo.previousActions.push(() -> targetTask.mark());
    }

    /**
     * Removes and returns the task at the specified one-based task number.
     *
     * @param taskNum the one-based number of the task to remove
     * @return the removed task
     * @throws InvalidCommandException if the task number is outside this list
     */
    public Task deleteTask(int taskNum) throws InvalidCommandException {
        if (listOfTasks.size() < taskNum || taskNum < 1) {
            throw new InvalidCommandException();
        }
        Task targetTask = listOfTasks.get(taskNum - 1);
        echo.previousActions.push(() -> {
            if (this.getSize() >= taskNum) {
                this.listOfTasks.add(taskNum - 1, targetTask);
                return;
            }
            this.listOfTasks.add(targetTask);
        });
        return this.listOfTasks.remove(taskNum - 1);
    }

    /**
     *
     * @param keyword the keyword used to find matching task descriptions
     * @return a TodoList that has all Tasks in the calling TodoList with
     * descriptions containing the keyword
     */
    public TodoList findSearchList(String keyword) {
        TodoList searchList = new TodoList();
        for (Task currentTask : this.listOfTasks) {
            if (currentTask.getDesc().contains(keyword)) {
                // addToListFromStorage is used here because there is no need to store runnables.
                searchList.addToListFromStorage(currentTask);
            }
        }
        return searchList;
    }

    /**
     * Returns the tasks in this list.
     *
     * @return the list that stores the tasks
     */
    public ArrayList<Task> getList() {
        return this.listOfTasks;
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index the zero-based position of the task
     * @return the task at the given index
     */
    public Task getTask(int index){
        return this.listOfTasks.get(index);
    }

    /**
     * Returns a numbered, formatted representation of this task list.
     *
     * @return the formatted task list
     */
    @Override
    public String toString() {
        int len = listOfTasks.size();

        String output = "";

        for (int i = 0; i < len; i ++) {
            Task currentTask = listOfTasks.get(i);
            output += String.format("%d. %s\n",
                    i + 1,
                    currentTask);
        }
        return LINE + output + ENDLINE;
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the task count
     */
    protected int getSize() {
        return listOfTasks.size();
    }
}
