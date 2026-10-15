package duke;

import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

/** Handles all text displayed to the user. */
public class Ui {

    private static final String BANNER = """
      _                                                   _       _
     | |__   ___ _ __ _ __ ___   ___  ___       _ __ ___ (_)_ __ (_)
     | '_ \\ / _ \\ '__| '_ ` _ \\ / _ \\/ __|_____| '_ ` _ \\| | '_ \\| |
     | | | |  __/ |  | | | | | |  __/\\__ \\_____| | | | | | | | | | |
     |_| |_|\\___|_|  |_| |_| |_|\\___||___/     |_| |_| |_|_|_| |_|_|
""";
    private static final String INDENT = "     ";
    private static final String DIVIDER = "    ____________________________________________________________";

    /** Prints the banner and greeting framed by dividers. */
    public void showGreeting() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println(INDENT + "Greetings! I'm Hermes, your task messenger.");
        System.out.println(INDENT + "What shall I carry for you today?");
        System.out.println(DIVIDER);
    }

    /** Prints a message framed by dividers. */
    public void showMessage(String message) {
        System.out.println(DIVIDER);
        System.out.println(INDENT + message);
        System.out.println(DIVIDER);
    }

    /** Prints the stored tasks and their completion status. */
    public void showTaskList(List<Task> tasks) {
        System.out.println(DIVIDER);
        System.out.println(INDENT + "Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            showNumberedTask(i + 1, tasks.get(i));
        }
        System.out.println(DIVIDER);
    }

    /** Prints tasks whose descriptions contain the supplied keyword. */
    public void showMatchingTasks(List<Task> tasks, String keyword) {
        System.out.println(DIVIDER);
        System.out.println(INDENT + "Here are the matching tasks:");
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getDescription()
                        .toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .forEach(index -> showNumberedTask(index + 1, tasks.get(index)));
        System.out.println(DIVIDER);
    }

    /** Prints the confirmation shown after marking a task as done. */
    public void showMarkedTask(Task task) {
        showMessage("Dispatch complete! This task is now done:\n" + INDENT
                + "  [X] " + task.getDescription());
    }

    /** Prints the confirmation shown after marking a task as not done. */
    public void showUnmarkedTask(Task task) {
        showMessage("Message received. This task is active again:\n" + INDENT
                + "  [ ] " + task.getDescription());
    }

    /** Prints the confirmation shown after deleting a task. */
    public void showDeletedTask(Task task, int remainingTaskCount) {
        showMessage("Message withdrawn. I've removed this task:\n" + INDENT + "  ["
                + task.getTypeIcon() + "][" + task.getStatusIcon() + "] "
                + task.getDisplayText() + "\n" + INDENT + "Now you have "
                + remainingTaskCount + " tasks in the list.");
    }

    /** Prints the confirmation shown after adding a task. */
    public void showAddedTask(Task task, int taskCount) {
        showMessage("Message delivered! I've added this task:\n" + INDENT + "  ["
                + task.getTypeIcon() + "][ ] " + task.getDisplayText() + "\n\n"
                + INDENT + "Now you have " + taskCount + " tasks in the list.");
    }

    /** Prints a framed error without terminating the chatbot. */
    public void showError(String message) {
        showMessage("DELIVERY FAILED: " + message);
    }

    /** Prints a numbered task within an existing message frame. */
    private void showNumberedTask(int number, Task task) {
        System.out.println(INDENT + number + ".[" + task.getTypeIcon() + "]["
                + task.getStatusIcon() + "] " + task.getDisplayText());
    }
}
