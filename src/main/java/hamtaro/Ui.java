package hamtaro;

import java.util.Scanner;
import java.util.List;

import hamtaro.task.Task;
import hamtaro.task.TaskList;

public class Ui {
    private final Scanner scanner = new Scanner(System.in);

    private final String line = "____________________________________________________________";

    public String showWelcome() {

        String welcome = line
                + "    __  __                __                 \n"
                + "   / / / /___ _____ ___  / /_____ __________ \n"
                + "  / /_/ / __ `/ __ `__ \\/ __/ __ `/ ___/ __ \\\n"
                + " / __  / /_/ / / / / / / /_/ /_/ / /  / /_/ /\n"
                + "/_/ /_/\\__,_/_/ /_/ /_/\\__/\\__,_/_/   \\____/ \n"
                + "Squeak! I'm Hamtaro, your little task hamster!\n"
                + "What shall we get done today?\n"
                + line;

        return welcome;
    }

    /** Reads one complete command from standard input. */
    public String readCommand() {
        return scanner.nextLine();
    }

    public String showGoodbye() {

        String goodbye = "Squeak you later! Keep those tasks nice and tidy.\n";

        return goodbye;
    }

    public String showError(String message) {
        String error = "Oh no, a little hamster hiccup!\n" + message;

        return error;
    }

    public String showTaskAdded(Task task, int totalTasks) {

        String taskAdded =
                "Squeak! I tucked this task into your list:\n"
                + task.toString()
                + "Your task nest now has " + totalTasks + " task(s).\n";

        return taskAdded;
    }

    public String showTaskMarked(Task task) {

        String taskMarked = "All done! I did a happy wheel spin for this task:\n" + task.toString();

        return taskMarked;
    }

    public String showTaskUnmarked(Task task) {

        String taskUnmarked = "No worries! I put this task back in the to-do pile:\n"
                + task.toString();

        return taskUnmarked;
    }

    /** Displays a successful tag addition. */
    public String showTagAdded(Task task, String tag) {
        return "Squeak! I stuck the #" + tag + " label on this task:\n" + task ;
    }

    /** Displays a successful tag removal. */
    public String showTagRemoved(Task task, String tag) {
        return "I peeled the #" + tag + " label off this task:\n" + task ;
    }

    public String showTaskDeleted(Task task) {

        String deleted = "Into the hamster shredder it goes! I removed this task:\n"
                + task.toString();

        return deleted;
    }

    public String showTaskList(TaskList tasks) {

        String taskList = "";
        for (int i = 0; i < tasks.getSize(); i++) {
            taskList += (i + 1) + ". " + tasks.getTask(i).toString() + "\n";
        }

        return taskList;
    }

    /** Displays the tasks whose descriptions matched a find keyword. */
    public String showMatchingTasks(List<Task> matchingTasks) {
        showLine();
        if (matchingTasks.isEmpty()) {
            String noMatch = "No matching tasks found. The hamster sniffed everywhere!";
            return noMatch;
        } else {
            String match = "I found these little task treats for you:\n";
            for (int i = 0; i < matchingTasks.size(); i++) {
                match += (i + 1) + ". " + matchingTasks.get(i) + "\n";
            }
            return match;
        }
    }

    public String showLine() {
        return "____________________________________________________________";
    }
}
