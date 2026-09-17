package nubish.utils;

/**
 * Handles all messages printed to the user.
 */
public class UI {
    private static final String RESPONSE = """
                    _________________________________________________
                    Nubish: %s
                    _________________________________________________
                    """;
    private String lastResponse;
    private boolean isLastResponseError;

    /**
     * Prints the greeting message shown when Nubish starts.
     */
    public void greet() {
        lastResponse = getGreeting();
        isLastResponseError = false;
        System.out.println(lastResponse);
    }

    /**
     * Returns the greeting message shown when Nubish starts.
     *
     * @return greeting message
     */
    public String getGreeting() {
        return """
                N   N U   U BBBB  III  SSSS H   H
                NN  N U   U B   B  I  S     H   H
                N N N U   U BBBB   I   SSS  HHHHH
                N  NN U   U B   B  I      S H   H
                N   N  UUU  BBBB  III SSSS  H   H
                Wazzup! I'm Nubish.
                What can I do for you?""";
    }

    /**
     * Returns the latest response produced by the UI.
     *
     * @return latest response text
     */
    public String getLastResponse() {
        return lastResponse;
    }

    /**
     * Returns whether the latest response is an error message.
     *
     * @return true if the latest response is an error message
     */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /**
     * Prints the farewell message shown when Nubish exits.
     */
    public void printBye() {
        printResponse("Sayonara. Hope to see you again soon!");
    }

    /**
     * Prints the formatted task list.
     *
     * @param list formatted list of tasks
     */
    public void printList(String list) {
        printResponse(list);
    }

    /**
     * Returns the formatted reminders for tasks due today.
     *
     * @param reminders formatted list of tasks due today
     * @return formatted reminders for tasks due today
     */
    public String getReminders(String reminders) {
        if (!reminders.isEmpty()) {
            return "REMINDER! These tasks are due today:\n" + reminders;
        }

        return "";
    }

    /**
     * Prints the formatted reminders for tasks due today.
     *
     * @param reminders formatted list of tasks due today
     */
    public void printReminders(String reminders) {
        printResponse("REMINDER! These tasks are due today:\n" + reminders);
    }

    /**
     * Prints a message confirming that a task was marked as done.
     *
     * @param task formatted task that was marked
     */
    public void printMark(String task) {
        String replyMark = String.format("""
                            Noice! I've marked this task as done:
                                %s\n
                            """, task);

        printResponse(replyMark);
    }

    /**
     * Prints a message confirming that a task was marked as not done.
     *
     * @param task formatted task that was unmarked
     */
    public void printUnmark(String task) {
        String replyUnmark = String.format("""
                            I've unmarked this task:
                                %s\n
                            """, task);

        printResponse(replyUnmark);
    }

    /**
     * Prints a message confirming that a todo task was added.
     *
     * @param input original todo command entered by the user
     * @param size number of tasks after adding the todo
     */
    public void printTodoAdded(String input, int size) {
        String replyTodo = String.format("""
                                todo task added: %s
                            Now you have %d tasks in the list.\n
                            """, input, size);
        printResponse(replyTodo);
    }

    /**
     * Prints a message confirming that a Deadline task was added.
     *
     * @param taskName description of the Deadline task
     * @param deadline Deadline entered by the user
     * @param size number of tasks after adding the Deadline
     */
    public void printDeadlineAdded(String taskName, String deadline, int size) {
        String replyDeadline = String.format("""
                                Added task: %s (by: %s)
                            Now you have %d tasks in the list\n
                            """, taskName, deadline, size);
        printResponse(replyDeadline);
    }

    /**
     * Prints a message confirming that an Event task was added.
     *
     * @param eventName description of the Event task
     * @param fromTime start date and time entered by the user
     * @param toTime end date and time entered by the user
     * @param size number of tasks after adding the Event
     */
    public void printEventAdded(String eventName, String fromTime, String toTime, int size) {
        String replyEvent = String.format("""
                                Added event: %s (From: %s, To: %s)
                            Now you have %d tasks in the list\n
                            """, eventName, fromTime, toTime, size);
        printResponse(replyEvent);
    }

    /**
     * Prints a message confirming that a task was deleted.
     *
     * @param task formatted task that was deleted
     * @param size number of tasks after deletion
     */
    public void printTaskDelete(String task, int size) {
        String replyDelete = String.format("""
                            Ok. I have removed this task:
                                %s
                            Now you have %d tasks in the list\n
                            """, task, size);

        printResponse(replyDelete);
    }

    /**
     * Prints the list of commands currently supported by Nubish.
     */
    public void printCommandList() {
        String cmd = """
                        WuUPS!!! No idea what you're saying bruh :-(
                        Here is a list of the current commands:
                        - todo
                        - deadline
                        - event
                        - mark
                        - unmark
                        - delete
                        - find\n
                        """;
        printResponse(cmd, true);
    }

    /**
     * Prints the list of tasks whose descriptions matched the search keyword.
     *
     * @param foundList formatted list of matching tasks
     */
    public void find(String foundList) {
        String found = String.format("""
                    Here are the matching tasks in your list:
                    %s
                """, foundList);
        printResponse(found);
    }

    /**
     * Prints an error message from Nubish command handling.
     *
     * @param message error message to show
     */
    public void showError(String message) {
        printResponse(message, true);
    }

    private void printResponse(String response) {
        printResponse(response, false);
    }

    private void printResponse(String response, boolean isError) {
        lastResponse = addTrailingLineBreak(response);
        isLastResponseError = isError;
        System.out.printf(RESPONSE, lastResponse);
    }

    private String addTrailingLineBreak(String response) {
        if (response.endsWith("\n")) {
            return response;
        }

        return response + "\n";
    }
}
