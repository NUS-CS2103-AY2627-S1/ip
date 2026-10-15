package ayre;

import java.util.ArrayList;
import java.util.List;

import ayre.tasks.Task;

/**
 * Wrapper of an ArrayList of Tasks that acts as an intermediary in communication between
 * the other methods and the Tasks.
 */
public class TaskList {
    private static final int FIRST_TASK_INDEX = 1;

    private final ArrayList<Task> list;

    /**
     * Initializes the list.
     */
    public TaskList() {
        this.list = new ArrayList<>();
    }

    /**
     * Add a Task to the ArrayList.
     *
     * @param tsk Task to be added.
     * @return Context String for adding a Task.
     */
    public String addTask(Task tsk) {
        this.list.add(tsk);
        return "~ New mission added:\n" + tsk.toString();
    }

    /**
     * Finds all tasks containing the given String and returns them as a formatted String for easy printing.
     *
     * @param name Name to be matched against tasks in the list.
     * @return Result of the search.
     */
    public String findTasks(String name) {
        List<String> list = this.list.stream()
                                    .filter(task -> task.matchesName(name))
                                    .map(Task::toString)
                                    .toList();
        StringBuilder tasks = new StringBuilder();
        for (int i = FIRST_TASK_INDEX; i <= list.size(); i++) {
            tasks.append(i).append(". ").append(list.get(i - FIRST_TASK_INDEX)).append("\n");
        }
        return list.isEmpty()
                ? "~ Sorry, Raven. There were no hits for that search."
                : "~ These are the matching missions:\n"
                    + tasks + "~ Did you find what you were looking for, Raven?";
    }

    /**
     * Marks a Task in the ArrayList as complete.
     *
     * @param i Index of the Task.
     * @return Context String of marking a Task.
     */
    public String markTask(int i) {
        return this.list.get(i).markComplete();
    }

    /**
     * Unmarks a Task in the ArrayList as complete.
     *
     * @param i Index of the Task.
     * @return Context String of unmarking a Task.
     */
    public String unmarkTask(int i) {
        return this.list.get(i).unmarkComplete();
    }

    /**
     * Removes a Task from the ArrayList.
     *
     * @param i Index of the Task.
     * @return Context String of removing a Task.
     */
    public String delTask(int i) {
        String tsk = this.list.get(i).toString();
        this.list.remove(i);
        return "~ The mission has been dropped.\nDeleted: " + tsk;
    }

    /**
     * Sums the number of tasks not marked complete.
     *
     * @return Number of tasks not marked complete.
     */
    private int getUnmarkedTasks() {
        int count = 0;
        for (Task t : this.list) {
            if (!t.getComplete()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Getter for number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int getNumTasks() {
        return this.list.size();
    }

    /**
     * Converts the Tasks in the ArrayList into save file parse-friendly Strings.
     *
     * @return A List of the toLogString() of each Task in order.
     */
    public List<String> toLog() {
        return this.list.stream()
                .map(Task::toLogString)
                .toList();
    }

    @Override
    public String toString() {
        if (this.list.isEmpty()) {
            return "~ The mission log is empty. \"Time to get to work\"... Raven.";
        }
        StringBuilder tasks = new StringBuilder();
        for (int i = FIRST_TASK_INDEX; i <= this.list.size(); i++) {
            tasks.append(i).append(". ").append(this.list.get(i - FIRST_TASK_INDEX)).append("\n");
        }
        int numUnmarked = this.getUnmarkedTasks();
        String unmarkedInfo = (numUnmarked == 0)
                ? "~ There are no unmarked missions. Well done, Raven."
                : "~ You have " + numUnmarked + " pending mission"
                        + (numUnmarked == 1 ? "" : "s") + ". Let's do this.";
        return "~ Current missions:\n" + tasks + unmarkedInfo;
    }
}
