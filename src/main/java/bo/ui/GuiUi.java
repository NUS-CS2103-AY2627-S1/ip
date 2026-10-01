package bo.ui;

import java.util.ArrayList;
import java.util.List;

import bo.task.Task;

/**
 * Collects Bo's responses for display in the JavaFX interface.
 */
public class GuiUi extends Ui {
    private final List<GuiMessage> messages = new ArrayList<>();

    @Override
    public void showWelcome(String banner) {
        addMessage("Hi! I'm Bo. Ready to help you organize your tasks.",
                GuiMessage.MessageType.NORMAL);
    }

    @Override
    public void showError(String message) {
        addMessage("I got a little confused: " + message, GuiMessage.MessageType.ERROR);
    }

    @Override
    public void showLoadingError() {
        showError("I could not load your saved tasks, so I am starting with an empty list.");
    }

    @Override
    public void showTaskAdded(Task task, int taskCount) {
        addMessage("Got it - I added this task. You now have " + taskCount + " tasks:\n" + task,
                GuiMessage.MessageType.SUCCESS);
    }

    @Override
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            addMessage("Your task list is empty. A wonderfully clean slate.",
                    GuiMessage.MessageType.NORMAL);
            return;
        }
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            message.append(i + 1).append('.').append(tasks.get(i)).append('\n');
        }
        addMessage("Here is your task list:\n" + message.toString().stripTrailing(),
                GuiMessage.MessageType.NORMAL);
    }

    @Override
    public void showTaskMarked(Task task) {
        addMessage("Nice work - that task is off your plate:\n" + task,
                GuiMessage.MessageType.SUCCESS);
    }

    @Override
    public void showTaskUnmarked(Task task) {
        addMessage("No problem - I have returned this task to your list:\n" + task,
                GuiMessage.MessageType.SUCCESS);
    }

    @Override
    public void showTaskDeleted(Task task, int taskCount) {
        addMessage("Removed. It has left the task list peacefully:\n" + task,
                GuiMessage.MessageType.SUCCESS);
    }

    @Override
    public void showTaskSnoozed(Task task) {
        addMessage("Snoozed - I will leave this deadline alone for now:\n" + task,
                GuiMessage.MessageType.SUCCESS);
    }

    @Override
    public void showTaskRescheduled(Task task) {
        addMessage("Rescheduled - your future self will appreciate it:\n" + task,
                GuiMessage.MessageType.SUCCESS);
    }

    @Override
    public void showDeadlines(List<Integer> taskIndexes, List<Task> tasks, String heading) {
        addMessage("Here is what deserves your attention:\n"
                        + createIndexedTaskMessage(heading, taskIndexes, tasks),
                GuiMessage.MessageType.NORMAL);
    }

    @Override
    public void showMatchingTasks(List<Integer> taskIndexes, List<Task> tasks) {
        addMessage("I found these matches for you:\n"
                        + createIndexedTaskMessage("Matching tasks:", taskIndexes, tasks),
                GuiMessage.MessageType.NORMAL);
    }

    /**
     * Appends tasks using their zero-based indexes and one-based task numbers.
     *
     * @param taskIndexes The indexes of the tasks to append.
     * @param tasks The full task list.
     */
    private String createIndexedTaskMessage(String heading, List<Integer> taskIndexes,
                                            List<Task> tasks) {
        StringBuilder message = new StringBuilder(heading).append('\n');
        for (int taskIndex : taskIndexes) {
            message.append(taskIndex + 1).append('.').append(tasks.get(taskIndex)).append('\n');
        }
        return message.toString().stripTrailing();
    }

    /** Adds a normal response to the queue for the graphical interface. */
    private void addMessage(String message, GuiMessage.MessageType type) {
        messages.add(new GuiMessage(message, type));
    }

    /**
     * Returns and clears responses generated since the previous command.
     *
     * @return the pending typed responses
     */
    public List<GuiMessage> takeMessages() {
        List<GuiMessage> result = List.copyOf(messages);
        messages.clear();
        return result;
    }
}
