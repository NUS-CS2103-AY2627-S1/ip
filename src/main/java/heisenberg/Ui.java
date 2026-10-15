package heisenberg;

/** Formats the messages shown by the command-line and graphical interfaces. */
public class Ui {
    public void showWelcome() {
        System.out.println(getWelcomeMessage());
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public String getWelcomeMessage() {
        return """
                Welcome. I run a precise operation.
                Your tasks stay controlled and on schedule here.
                What shall we put on the formula today?""";
    }

    public String getTaskListMessage(TaskList taskList) {
        StringBuilder message = new StringBuilder("Current formula:");
        for (int taskNumber = 1; taskNumber <= taskList.size(); taskNumber++) {
            message.append(String.format("%n%d. %s", taskNumber, taskList.getTask(taskNumber)));
        }
        return message.toString();
    }

    public String getMatchingTasksMessage(TaskList matches) {
        StringBuilder message = new StringBuilder("No half measures. I isolated these matching tasks:");
        for (int taskNumber = 1; taskNumber <= matches.size(); taskNumber++) {
            message.append(String.format("%n%d. %s", taskNumber, matches.getTask(taskNumber)));
        }
        return message.toString();
    }

    public String getTaskAddedMessage(Task task, TaskList taskList) {
        return String.format("Let's cook. Reaction logged:%n%s%nThe formula now contains %d tasks.",
                task, taskList.size());
    }

    public String getTaskMarkedMessage(Task task) {
        return "Handled. One less loose end:\n" + task;
    }

    public String getTaskDeletedMessage(Task task, TaskList taskList) {
        return String.format("Tread lightly. Removed from the formula:%n%s%n%d tasks remain in the list.",
                task, taskList.size());
    }

    public String getTasksSortedMessage(TaskList taskList) {
        return "Deadlines calibrated from earliest to latest.\n" + getTaskListMessage(taskList);
    }

    public String getGoodbyeMessage() {
        return "Stay out of my territory. Lab shutdown complete.";
    }
}
