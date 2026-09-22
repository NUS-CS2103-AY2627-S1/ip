package mono.ui;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import mono.task.Task;

/**
 * Formats Mono's conversation responses and sends them to an output destination.
 */
public class ConversationUi {
    /** Separator used to frame every response shown to the user. */
    private static final String RESPONSE_SEPARATOR =
            "____________________________________________________________";

    private final Consumer<String> output;

    /** Creates a console-based conversation user interface. */
    public ConversationUi() {
        this(System.out::print);
    }

    /**
     * Creates a conversation user interface that sends responses to a callback.
     *
     * @param output destination for complete response messages
     */
    public ConversationUi(Consumer<String> output) {
        this.output = Objects.requireNonNull(output, "output");
    }

    /** Prints Mono's greeting. */
    public void showGreeting() {
        String banner = "███╗   ███╗ ██████╗ ███╗   ██╗ ██████╗ \n"
                + "████╗ ████║██╔═══██╗████╗  ██║██╔═══██╗\n"
                + "██╔████╔██║██║   ██║██╔██╗ ██║██║   ██║\n"
                + "██║╚██╔╝██║██║   ██║██║╚██╗██║██║   ██║\n"
                + "██║ ╚═╝ ██║╚██████╔╝██║ ╚████║╚██████╔╝\n"
                + "╚═╝     ╚═╝ ╚═════╝ ╚═╝  ╚═══╝ ╚═════╝ ";
        emit(RESPONSE_SEPARATOR + "\n"
                + banner + "\nHello! I'm Mono.\nWhat can I do for you?\n"
                + RESPONSE_SEPARATOR + "\n");
    }

    /** Prints Mono's farewell. */
    public void showExit() {
        emit(RESPONSE_SEPARATOR + "\n"
                + "Bye. Hope to see you again soon!\n"
                + RESPONSE_SEPARATOR + "\n");
    }

    /**
     * Prints the response for an added task.
     *
     * @param task task that was added
     * @param count number of tasks after the addition
     */
    public void showTaskAdded(Task task, int count) {
        emit(RESPONSE_SEPARATOR + "\n"
                + "Got it. I've added this task:\n" + task + "\nNow you have " + count
                + " tasks in the list.\n" + RESPONSE_SEPARATOR + "\n");
    }

    /**
     * Prints the response for a deleted task.
     *
     * @param task task that was deleted
     * @param count number of tasks after the deletion
     */
    public void showTaskDeleted(Task task, int count) {
        emit(RESPONSE_SEPARATOR + "\n"
                + "Noted. I've removed this task:\n" + task + "\nNow you have " + count
                + " tasks in the list.\n" + RESPONSE_SEPARATOR + "\n");
    }

    /**
     * Prints all tasks with one-based IDs.
     *
     * @param tasks tasks to display
     */
    public void showTaskList(List<Task> tasks) {
        StringBuilder response = new StringBuilder(
                RESPONSE_SEPARATOR + "\n"
                        + "Here are the tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            response.append(i + 1).append(".").append(tasks.get(i)).append("\n");
        }
        response.append(RESPONSE_SEPARATOR).append("\n");
        emit(response.toString());
    }

    /**
     * Prints the response for marking a task done.
     *
     * @param task task that was marked as completed
     */
    public void showTaskMarkedDone(Task task) {
        emit(RESPONSE_SEPARATOR + "\n"
                + "Nice! I've marked this task as done:\n" + task
                + "\n" + RESPONSE_SEPARATOR + "\n");
    }

    /**
     * Prints the response for unmarking a task.
     *
     * @param task task that was marked as incomplete
     */
    public void showTaskUnmarked(Task task) {
        emit(RESPONSE_SEPARATOR + "\n"
                + "OK, I've marked this task as not done yet:\n" + task
                + "\n" + RESPONSE_SEPARATOR + "\n");
    }

    /** Sends one complete response to the configured output destination. */
    private void emit(String response) {
        this.output.accept(response);
    }
}
