package alpha.gui;

import alpha.AlphaException;
import alpha.TaskManager;
import alpha.task.Task;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/** Controls the main task-list view and translates UI actions into commands. */
public class MainWindow {
    @FXML
    private ListView<Task> taskListView;
    @FXML
    private TextField commandInput;
    @FXML
    private Label taskCountLabel;
    @FXML
    private Label feedbackLabel;

    private TaskManager taskManager;

    /** Initializes the task list cell factory after the FXML view is loaded. */
    @FXML
    public void initialize() {
        this.taskListView.setCellFactory(view -> new TaskCell(this::toggleTask, this::deleteTask));
    }

    /** Connects this controller to the shared task manager and displays its initial state. */
    public void setTaskManager(TaskManager taskManager) {
        assert taskManager != null : "The main window must have a task manager";
        this.taskManager = taskManager;
        this.refreshTasks();
        if (taskManager.getLoadingWarning() != null) {
            this.showFeedback(taskManager.getLoadingWarning(), "warning");
        }
    }

    /** Executes the command entered by the user. */
    @FXML
    private void handleCommand() {
        String input = this.commandInput.getText().trim();
        if (input.isEmpty()) {
            this.showFeedback("Please enter a command.", "error");
            return;
        }

        try {
            TaskManager.CommandResult result = this.taskManager.execute(input);
            this.showFeedback(result.getMessage(), "success");
            this.refreshTasks();
            this.commandInput.clear();
            if (result.isExit()) {
                Platform.exit();
            }
        } catch (AlphaException exception) {
            this.showFeedback("Moss says: " + exception.getMessage(), "error");
        }
    }

    /** Marks or unmarks the selected task. */
    private void toggleTask(int taskNumber) {
        assert taskNumber > 0 && taskNumber <= this.taskManager.getTasks().size()
                : "A task control must reference a displayed task";
        Task task = this.taskManager.getTasks().get(taskNumber - 1);
        this.runCommand(task.isDone() ? "unmark " : "mark ", taskNumber);
    }

    /** Deletes the selected task. */
    private void deleteTask(int taskNumber) {
        this.runCommand("delete ", taskNumber);
    }

    /** Executes a command that targets a numbered task. */
    private void runCommand(String command, int taskNumber) {
        this.commandInput.setText(command + taskNumber);
        this.handleCommand();
    }

    /** Replaces the list contents with the current tasks. */
    private void refreshTasks() {
        List<Task> tasks = this.taskManager.getTasks();
        this.taskListView.getItems().setAll(tasks);
        this.taskCountLabel.setText(tasks.size() + (tasks.size() == 1 ? " task" : " tasks"));
    }

    /** Updates the feedback message and applies a visual state that matches its meaning. */
    private void showFeedback(String message, String state) {
        this.feedbackLabel.getStyleClass().removeAll("success", "warning", "error");
        this.feedbackLabel.getStyleClass().add(state);
        this.feedbackLabel.setText(message);
    }
}
