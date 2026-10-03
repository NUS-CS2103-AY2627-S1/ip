package verity.ui;

import java.time.LocalDate;
import java.util.List;

import verity.task.Task;
import verity.task.TaskList;

/**
 * Formats user-facing messages about tasks.
 */
final class TaskMessageFormatter {

    String getTaskAddedMessage(Task task, int taskCount) {
        return Ui.HORIZONTAL_LINE
                + "     Got it. I've added this task:\n"
                + "       " + task.getStatus() + "\n"
                + "     Now you have " + taskCount
                + " tasks in the list.\n"
                + Ui.HORIZONTAL_LINE;
    }

    String getTaskMarkedMessage(Task task) {
        return Ui.HORIZONTAL_LINE
                + "\n"
                + "Nice! I've marked this task as done:\n\n"
                + task.getStatus() + "\n"
                + Ui.HORIZONTAL_LINE;
    }

    String getTaskUnmarkedMessage(Task task) {
        return Ui.HORIZONTAL_LINE
                + "\n"
                + "Ok, I've marked this task as not done yet:\n\n"
                + task.getStatus() + "\n"
                + Ui.HORIZONTAL_LINE;
    }

    String getTaskDeletedMessage(Task task, int taskCount) {
        return Ui.HORIZONTAL_LINE
                + "\n"
                + "Noted. I've removed this task:\n\n"
                + task.getStatus() + "\n"
                + Ui.HORIZONTAL_LINE + "\n"
                + "Now you have " + taskCount
                + " tasks in the list.\n\n"
                + Ui.HORIZONTAL_LINE;
    }

    String getTaskListMessage(TaskList taskList) {
        StringBuilder response = new StringBuilder(Ui.HORIZONTAL_LINE)
                .append("\n")
                .append("    Here are the tasks in your list:\n\n");

        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            response.append("    ")
                    .append(i + 1)
                    .append(".")
                    .append(task.getStatus())
                    .append("\n");
            appendTaskMetadata(response, task, "        ");
        }

        return response.append(Ui.HORIZONTAL_LINE).toString();
    }

    String getTasksOnMessage(LocalDate date, List<Task> matchingTasks) {
        StringBuilder response = new StringBuilder(Ui.HORIZONTAL_LINE)
                .append("\n")
                .append("    Tasks on ")
                .append(date)
                .append(":\n\n");

        for (Task task : matchingTasks) {
            response.append("    ")
                    .append(task.getStatus())
                    .append("\n");
            appendTaskMetadata(response, task, "        ");
        }

        if (matchingTasks.isEmpty()) {
            response.append("    There are no tasks on this date.\n");
        }

        return response.append(Ui.HORIZONTAL_LINE).toString();
    }

    String getMatchingTasksMessage(List<Task> matchingTasks) {
        StringBuilder response = new StringBuilder(Ui.HORIZONTAL_LINE)
                .append("\n")
                .append("    Here are the matching tasks "
                        + "in your list:\n\n");

        for (int i = 0; i < matchingTasks.size(); i++) {
            Task task = matchingTasks.get(i);
            response.append("    ")
                    .append(i + 1)
                    .append(".")
                    .append(task.getStatus())
                    .append("\n");
            appendTaskMetadata(response, task, "        ");
        }

        if (matchingTasks.isEmpty()) {
            response.append("    There are no matching tasks.\n");
        }

        return response.append(Ui.HORIZONTAL_LINE).toString();
    }

    private void appendTaskMetadata(StringBuilder response, Task task, String indent) {
        if (!task.getClientIds().isEmpty()) {
            response.append(indent)
                    .append("Clients: ")
                    .append(String.join(", ", task.getClientIds()))
                    .append("\n");
        }
        for (String note : task.getFormerClientNotes()) {
            response.append(indent).append("Note: ").append(note).append("\n");
        }
    }
}
