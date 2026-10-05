package friday;

import java.util.List;

import friday.task.RepeatFrequency;
import friday.task.Task;
import friday.task.TaskList;

/**
 * Handles all text shown to the user.
 */
public class Ui {
    /**
     * Returns the welcome message.
     *
     * @return welcome message
     */
    public String getWelcome() {
        return getWelcome(new TaskList());
    }

    /**
     * Returns the welcome message with upcoming reminders when any exist.
     *
     * @param upcomingReminders tasks due soon
     * @return welcome message
     */
    public String getWelcome(TaskList upcomingReminders) {
        String banner = " _____ ____  ___ ____    _ __   __\n"
                + "|  ___|  _ \\|_ _|  _ \\  / \\ \\ / /\n"
                + "| |_  | |_) || || | | |/ _ \\ V /\n"
                + "|  _| |  _ < | || |_| / ___ \\| |\n"
                + "|_|   |_| \\_\\___|____/_/   \\_\\_|";

        return banner + "\n"
                + "\n"
                + "All systems are online, sir. How may I assist?"
                + getStartupReminderText(upcomingReminders);
    }

    /**
     * Shows the welcome message.
     */
    public void showWelcome() {
        System.out.println(getWelcome());
    }

    /**
     * Returns the goodbye message.
     *
     * @return goodbye message
     */
    public String getBye() {
        return "Powering down. I remain at your service, sir.";
    }

    /**
     * Shows the goodbye message.
     */
    public void showBye() {
        System.out.println(getBye());
    }

    /**
     * Returns the task list message.
     *
     * @param tasks tasks to show
     * @return task list message
     */
    public String getTaskList(TaskList tasks) {
        return getIndexedTaskList("Your current task register, sir:", tasks);
    }

    /**
     * Shows the task list.
     *
     * @param tasks tasks to show
     */
    public void showTaskList(TaskList tasks) {
        System.out.println(getTaskList(tasks));
    }

    /**
     * Returns the tasks that match a keyword search.
     *
     * @param tasks matching tasks to show
     * @return matching tasks message
     */
    public String getMatchingTasks(TaskList tasks) {
        return getIndexedTaskList("I found the following matching tasks, sir:", tasks);
    }

    /**
     * Returns the message shown after tasks are sorted.
     *
     * @param tasks sorted tasks
     * @return sorted task list message
     */
    public String getTasksSorted(TaskList tasks) {
        return getIndexedTaskList("Your tasks are now arranged chronologically, sir:", tasks);
    }

    /**
     * Returns the reminder list message.
     *
     * @param tasks tasks that have reminder dates
     * @return reminder list message
     */
    public String getReminders(TaskList tasks) {
        if (tasks.size() == 0) {
            return "Your schedule contains no dated reminders, sir.";
        }
        return getIndexedTaskList("Your reminders, arranged chronologically, sir:", tasks);
    }

    private String getIndexedTaskList(String heading, TaskList tasks) {
        List<Task> taskList = tasks.asList();
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < taskList.size(); i++) {
            response.append("\n")
                    .append(i + 1)
                    .append(". ")
                    .append(taskList.get(i));
        }
        return response.toString();
    }

    /**
     * Shows the tasks that match a keyword search.
     *
     * @param tasks matching tasks to show
     */
    public void showMatchingTasks(TaskList tasks) {
        System.out.println(getMatchingTasks(tasks));
    }

    /**
     * Returns the message for a newly added task.
     *
     * @param task task that was added
     * @param taskCount number of tasks in the list
     * @return task added message
     */
    public String getTaskAdded(Task task, int taskCount) {
        return "Certainly, sir. I have added this task:\n"
                + task + "\n"
                + "You now have " + formatTaskCount(taskCount) + " in the register.";
    }

    /**
     * Shows the message for a newly added task.
     *
     * @param task task that was added
     * @param taskCount number of tasks in the list
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(getTaskAdded(task, taskCount));
    }

    /**
     * Returns the message for a task that has been marked as done.
     *
     * @param task task that was marked as done
     * @return task marked message
     */
    public String getTaskMarked(Task task) {
        return "Consider it done, sir. I have marked this task as complete:\n" + task;
    }

    /**
     * Shows the message for a task that has been marked as done.
     *
     * @param task task that was marked as done
     */
    public void showTaskMarked(Task task) {
        System.out.println(getTaskMarked(task));
    }

    /**
     * Returns the message for a task that has been marked as not done.
     *
     * @param task task that was marked as not done
     * @return task unmarked message
     */
    public String getTaskUnmarked(Task task) {
        return "Understood, sir. I have returned this task to active status:\n" + task;
    }

    /**
     * Shows the message for a task that has been marked as not done.
     *
     * @param task task that was marked as not done
     */
    public void showTaskUnmarked(Task task) {
        System.out.println(getTaskUnmarked(task));
    }

    /**
     * Returns the message for a deleted task.
     *
     * @param task task that was deleted
     * @param taskCount number of tasks left in the list
     * @return task deleted message
     */
    public String getTaskDeleted(Task task, int taskCount) {
        return "The task has been removed, sir:\n"
                + task + "\n"
                + "You now have " + formatTaskCount(taskCount) + " in the register.";
    }

    /**
     * Returns the message for a task that has been marked as recurring.
     *
     * @param task task that was marked as recurring
     * @param repeatFrequency repeat frequency assigned to the task
     * @return recurring task message
     */
    public String getTaskRepeated(Task task, RepeatFrequency repeatFrequency) {
        return "Certainly, sir. This task will now repeat " + repeatFrequency.getText() + ":\n" + task;
    }

    /**
     * Shows the message for a deleted task.
     *
     * @param task task that was deleted
     * @param taskCount number of tasks left in the list
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(getTaskDeleted(task, taskCount));
    }

    /**
     * Returns an error message.
     *
     * @param message error message to show
     * @return formatted error message
     */
    public String getError(String message) {
        return "I am afraid I could not complete that request, sir.\n" + message;
    }

    /**
     * Shows an error message.
     *
     * @param message error message to show
     */
    public void showError(String message) {
        System.out.println(getError(message));
    }

    /**
     * Shows an error message for failed loading.
     */
    public void showLoadingError() {
        showError("The saved task file could not be read, so I have started with an empty register.");
    }

    /**
     * Returns the help message that lists available commands.
     *
     * @return help message
     */
    public String getHelp() {
        return "Available directives, sir:\n"
                + " 1. list - shows all tasks in the list\n"
                + " 2. todo <description> - adds a todo task\n"
                + " 3. deadline <description> /by <date> - adds a deadline task\n"
                + " 4. event <description> /from <date> /to <date> - adds an event task\n"
                + " 5. mark <task number> - marks a task as done\n"
                + " 6. unmark <task number> - marks a task as not done\n"
                + " 7. delete <task number> - deletes a task from the list\n"
                + " 8. find <keyword> - finds tasks that match the keyword\n"
                + " 9. sort - sorts dated tasks chronologically\n"
                + "10. repeat <task number> <frequency> - makes a task recurring\n"
                + "11. reminders - shows dated tasks sorted chronologically\n"
                + "12. help - shows this help message\n"
                + "13. bye - exits the application\n"
                + "14. edit <task number> <description> - changes a task description";
    }

    private String getStartupReminderText(TaskList upcomingReminders) {
        if (upcomingReminders.size() == 0) {
            return "";
        }
        return "\n\n" + getIndexedTaskList("Items requiring attention within 2 days, sir:", upcomingReminders);
    }

    private String formatTaskCount(int taskCount) {
        return taskCount + (taskCount == 1 ? " task" : " tasks");
    }
}
